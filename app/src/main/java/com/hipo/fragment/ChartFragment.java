package com.hipo.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.hipo.lookie.R;
import com.hipo.model.pojo.AndroidCategoryChartVo;
import com.hipo.model.pojo.GraphVo;
import com.hipo.utils.AddedListVoFunction;
import com.hipo.utils.DecimalRemover;
import com.hipo.utils.GetBarChartThread;
import com.hipo.utils.GetChartCategoryThread;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChartFragment extends Fragment implements OnChartValueSelectedListener {

    private Handler categoryHandler, limitHandler;
    private PieData data;
    private List<AndroidCategoryChartVo> categoryChartList;
    private ArrayList<Entry> yValsPie;
    private ArrayList<BarEntry> yValsBar1, yValsBar2;
    private ArrayList<IBarDataSet> barDataSets;
    private ArrayList<String> xValsPie, xValsBar;
    private Map<Integer, Integer> changePercentToMoney;
    private PieDataSet pDataSet;
    private boolean pieChartInitDone = true, barChartinitDone = true;
    private boolean[] divChart = new boolean[2];
    @BindView(R.id.chart_year_spinner)
    Spinner yearSpinner;
    @BindView(R.id.chart_month_spinner)
    Spinner monthSpinner;
    @BindView(R.id.chart_assortment_spinner)
    Spinner assortmentSpinner;
    @BindView(R.id.pie_chart)
    PieChart pieChart;
    @BindView(R.id.Consumption_bar_chart)
    BarChart barChart;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static ChartFragment newInstance(int sectionNumber) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_chart, container, false);

        ButterKnife.bind(this, view);

        setDefaultAssortSpinner();
        divChart[0] = true;
        visibleChart(divChart);
        changePercentToMoney = new HashMap<>();
        int yearMonth[] = settingDefalutYearMonth();
        categoryHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                initChartSet(msg);
                Log.d("ChartFragmentChecking", "okay");

            }
        };
        GetChartCategoryThread chartCategoryThread = new GetChartCategoryThread(categoryHandler, yearMonth, getContext());
        chartCategoryThread.run();


        return view;
    }


    private void initChartSet(Message msg) {
        Log.d("CategoryListSum!", msg.obj.toString());
        categoryChartList = (List) msg.obj;
        if (pieChartInitDone) {
            yValsPie = new ArrayList<>();
            xValsPie = new ArrayList<>();
        }
        int sum = 0;

        int index = 0;
        for (int i = 0; i < categoryChartList.size(); i++) {
            if (categoryChartList.get(i).getSum() != 0) {
                sum += categoryChartList.get(i).getSum();
            }
        }
        if (categoryChartList.size() <= 5) {
            for (int i = 0; i < categoryChartList.size(); i++) {
                if (categoryChartList.get(i).getSum() != 0) {
                    if (getPercent(categoryChartList.get(i).getSum(), sum) > 0) {
                        Log.d("percentage", getPercent(categoryChartList.get(i).getSum(), sum) + "");
                        yValsPie.add(setYEntry(getPercent(categoryChartList.get(i).getSum(), sum), index));
                        xValsPie.add(index++, categoryChartList.get(i).getCategory());
                        changePercentToMoney.put(getPercent(categoryChartList.get(i).getSum(), sum), categoryChartList.get(i).getSum());
                    }
                }
            }
        } else {
            xValsPie.clear();
            yValsPie.clear();
            //5개 이상일 경우 처리
            PriorityQueue<AndroidCategoryChartVo> pq = new PriorityQueue<>();
            for (int i = 0; i < categoryChartList.size(); i++) {
                if (getPercent(categoryChartList.get(i).getSum(), sum) > 0) {
                    pq.add(categoryChartList.get(i));
                    Log.d("pqpqADDED", categoryChartList.get(i).getSum() + "");
                }
            }

            AndroidCategoryChartVo vo;
            for (int i = 0; i < 5; i++) {
                vo = pq.poll();
                yValsPie.add(setYEntry(getPercent(vo.getSum(), sum), index));
                xValsPie.add(index++, vo.getCategory());
                changePercentToMoney.put(getPercent(vo.getSum(), sum), vo.getSum());
                Log.d("pqpqVo", vo + "");
                Log.d("pqpqMapCheck", changePercentToMoney.toString());
            }
        }

        Log.d("ChartSumVal", sum + "");
        chartInit(yValsPie, xValsPie);
        pieChartInitDone = false;

        data.notifyDataChanged();
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }

    private void chartInit(ArrayList<Entry> yValsPie, ArrayList<String> xValsPie) {
        pDataSet = new PieDataSet(yValsPie, "");
        data = new PieData(xValsPie, pDataSet);
        pDataSet.setSliceSpace(0);
        pieChart.setDescription("높은금액순으로 5개까지 표시됩니다.");
        pieChart.setDescriptionTextSize(10.0f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.setHoleRadius(30f);
        pieChart.setOnChartValueSelectedListener(this);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        data.setValueFormatter(new DecimalRemover(new DecimalFormat("###,###,###")));
        pDataSet.setColors(colorList());
        pieChart.invalidate();
    }

    private Entry setYEntry(Integer val, int i) {
        return new Entry(val, i);

    }

    private ArrayList<Integer> colorList() {
        final int[] MY_COLORS = {Color.rgb(80, 208, 255), Color.rgb(80, 255, 241), Color.rgb(80, 255, 117),
                Color.rgb(191, 80, 255), Color.rgb(255, 80, 212)};
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : MY_COLORS) colors.add(c);
        return colors;
    }

    private Integer getPercent(Integer val, Integer sum) {
        try {
            return (val * 100) / sum;
        } catch (Exception e) {
            Log.d("ChartFragment", "카테고리의 값이 0");
            return 0;
        }
    }

    @OnItemSelected(R.id.chart_year_spinner)
    public void yearSpinnerSelected(AdapterView<?> parent, View view, int position, long id) {
        int yearMonth[] = new int[2];
        yearMonth[0] = Integer.parseInt(yearSpinner.getAdapter().getItem(position).toString());
        yearMonth[1] = monthSpinner.getSelectedItemPosition() + 1;
        if (divChart[0]) {
            categoryHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    updatePieChart(msg);
                }
            };
            GetChartCategoryThread chartCategoryThread = new GetChartCategoryThread(categoryHandler, yearMonth,getContext());
            chartCategoryThread.run();
        } else {
            Log.d("else입니다", "^^");
            getBarChartData(yearMonth);
        }

    }

    @OnItemSelected(R.id.chart_month_spinner)
    public void monthSpinnerSelected(AdapterView<?> parent, View view, int position, long id) {
        int yearMonth[] = new int[2];
        yearMonth[0] = Integer.parseInt(yearSpinner.getAdapter().getItem(yearSpinner.getSelectedItemPosition()).toString());
        yearMonth[1] = monthSpinner.getSelectedItemPosition() + 1;
        if (divChart[0]) {
            getPieChartData(yearMonth);
        } else {
            getBarChartData(yearMonth);
            Log.d("else입니다", "^^");
        }
    }

    private void getPieChartData(int yearMonth[]) {
        categoryHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                updatePieChart(msg);
            }
        };
        GetChartCategoryThread chartCategoryThread = new GetChartCategoryThread(categoryHandler, yearMonth,getContext());
        chartCategoryThread.run();
    }

    private void updatePieChart(Message msg) {
        categoryChartList = (List<AndroidCategoryChartVo>) msg.obj;
        int sum = 0;
        xValsPie.clear();
        yValsPie.clear();
        int index = 0;
        for (int i = 0; i < categoryChartList.size(); i++) {
            if (categoryChartList.get(i).getSum() != 0) {
                sum += categoryChartList.get(i).getSum();
            }
        }
        try {
            if (categoryChartList.size() <= 5) {
                for (int i = 0; i < categoryChartList.size(); i++) {
                    if (getPercent(categoryChartList.get(i).getSum(), sum) > 0) {
                        Log.d("percentage3", getPercent(categoryChartList.get(i).getSum(), sum) + "");
                        yValsPie.add(setYEntry(getPercent(categoryChartList.get(i).getSum(), sum), index));
                        xValsPie.add(index++, categoryChartList.get(i).getCategory());
                        changePercentToMoney.put(getPercent(categoryChartList.get(i).getSum(), sum), categoryChartList.get(i).getSum());
                    }
                }
            } else {
                //5개 이상일 경우 처리
                PriorityQueue<AndroidCategoryChartVo> pq = new PriorityQueue<>();
                for (int i = 0; i < categoryChartList.size(); i++) {
                    if (getPercent(categoryChartList.get(i).getSum(), sum) > 0) {
                        pq.add(categoryChartList.get(i));
                        Log.d("pqpqADDED", categoryChartList.get(i).getSum() + "");
                    }
                }

                AndroidCategoryChartVo vo;
                for (int i = 0; i < 5; i++) {
                    vo = pq.poll();
                    yValsPie.add(setYEntry(getPercent(vo.getSum(), sum), index));
                    xValsPie.add(index++, vo.getCategory());
                    changePercentToMoney.put(getPercent(vo.getSum(), sum), vo.getSum());
                    Log.d("pqpqVo", vo + "");
                }
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        data.notifyDataChanged();
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }

    @OnItemSelected(R.id.chart_assortment_spinner)
    public void assortmentSpinnerSelected(AdapterView<?> parent, View view, int position, long id) {
        int yearMonth[] = new int[2];
        yearMonth[0] = Integer.parseInt(yearSpinner.getAdapter().getItem(yearSpinner.getSelectedItemPosition()).toString());
        yearMonth[1] = monthSpinner.getSelectedItemPosition() + 1;

        switch (position) {
            case 0:
                divChart[0] = true;
                divChart[1] = false;
                visibleChart(divChart);
                break;
            case 1:
                divChart[0] = false;
                divChart[1] = true;
                getBarChartData(yearMonth);

                visibleChart(divChart);
                break;
        }
    }

    private void visibleChart(boolean[] divChart) {
        int piv = -1;
        for (int i = 0; i < divChart.length; i++) {
            if (divChart[i]) {
                piv = i;
                break;
            }
        }
        switch (piv) {
            case -1:
                Log.d("chartVisible", "오류입니다.");
                break;
            case 0:
                pieChart.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.INVISIBLE);
                break;
            case 1:
                pieChart.setVisibility(View.INVISIBLE);
                barChart.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public void onValueSelected(Entry e, int pDataSetIndex, Highlight h) {
        int sum = 0;
        Log.d("yValCheck", yValsPie.toString());
        Log.d("SetIndex??", pDataSetIndex + "");
        try {

            for (int i = 0; i < yValsPie.size(); i++) {
                sum = changePercentToMoney.get(Integer.valueOf((int) yValsPie.get(i).getVal()));
                yValsPie.get(i).setVal(sum);
                pDataSet.setValueFormatter(new com.hipo.utils.DefaultValueFormatter(0));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.d("chartdes", "p -> 원");
    }

    @Override
    public void onNothingSelected() {
        List<AndroidCategoryChartVo> tempList = new ArrayList<>();
        xValsPie.clear();
        yValsPie.clear();
        int sum = 0;
        int count = 0;
        Log.e("ErrorChecking", categoryChartList.toString());
        for (int i = 0; i < categoryChartList.size(); i++) {
            if (categoryChartList.get(i).getSum() != 0) {
                sum += categoryChartList.get(i).getSum();
            }
        }

        for (int i = 0; i < categoryChartList.size(); i++) {
            if (getPercent(categoryChartList.get(i).getSum(), sum) > 0) {
                tempList.add(count++, categoryChartList.get(i));
            }
        }
        if (tempList.size() <= 5) {
            for (int i = 0; i < tempList.size(); i++) {
                Log.d("percentage", getPercent(tempList.get(i).getSum(), sum) + "");
                yValsPie.add(setYEntry(getPercent(tempList.get(i).getSum(), sum), i));
                xValsPie.add(tempList.get(i).getCategory());
                changePercentToMoney.put(getPercent(tempList.get(i).getSum(), sum), tempList.get(i).getSum());
            }

        } else {
            PriorityQueue<AndroidCategoryChartVo> pq = new PriorityQueue<>();
            for (int i = 0; i < categoryChartList.size(); i++) {
                if (getPercent(categoryChartList.get(i).getSum(), sum) > 0) {
                    Log.d("InsertPQ?", categoryChartList.get(i) + "");
                    pq.add(categoryChartList.get(i));
                    Log.d("pqpqADDEDNotSelect", categoryChartList.get(i).getSum() + "");
                }
            }
            Log.d("PQNULLCHECK", pq.toString());
            int index = 0;
            AndroidCategoryChartVo vo;
            for (int i = 0; i < 5; i++) {
                vo = pq.poll();
                Log.d("CheckingPQ", vo + "");
                yValsPie.add(setYEntry(getPercent(vo.getSum(), sum), index));
                xValsPie.add(index++, vo.getCategory());
                changePercentToMoney.put(getPercent(vo.getSum(), sum), vo.getSum());
                Log.d("notingSelectpqpqVo!!@!@", vo + "");
                Log.d("pqpqMapCheck", changePercentToMoney.toString());
            }

        }
        data.setValueFormatter(new PercentFormatter());
        data.setValueFormatter(new DecimalRemover(new DecimalFormat("###,###,###")));

    }

    public int[] settingDefalutYearMonth() {
        int timeArr[] = AddedListVoFunction.convertDateToInt(AddedListVoFunction.inDate);
        Log.d("timeArr!", timeArr[0] + "," + timeArr[1]);
        int yearMonth[] = new int[2];
        yearMonth[0] = timeArr[5];
        yearMonth[1] = timeArr[1];
        setYearSpinner(yearMonth);
        setMonthSpinner(yearMonth);
        return yearMonth;
    }

    public void setYearSpinner(int yearMonth[]) {
        for (int i = 0; i < yearSpinner.getAdapter().getCount(); i++) {
            if (String.valueOf(yearMonth[0]).equals(yearSpinner.getItemAtPosition(i).toString())) {
                yearSpinner.setSelection(i);
                break;
            }
        }
    }

    public void setMonthSpinner(int yearMonth[]) {
        for (int i = 0; i < monthSpinner.getAdapter().getCount(); i++) {
            if (String.valueOf(yearMonth[1]).equals(monthSpinner.getItemAtPosition(i).toString())) {
                monthSpinner.setSelection(i);
                break;
            }
        }
    }

    private void setDefaultAssortSpinner() {
        assortmentSpinner.setSelection(0);
    }

    private void barChartInit(List<GraphVo> barList) {
        barValInit(barChartinitDone);
        setBarChartData(barList);
        BarData bData = new BarData(xValsBar, barDataSets);
        barChart.setData(bData);
        barChart.setDescription("");
        barChart.animateXY(2000, 2000);
        barChart.invalidate();
    }

    private void barValInit(boolean barChartinitDone) {
        if (barChartinitDone) {
            xValsBar = new ArrayList<>();
            yValsBar2 = new ArrayList<>();
            yValsBar1 = new ArrayList<>();
            barDataSets = new ArrayList<>();
            this.barChartinitDone = false;
        } else {
            xValsBar.clear();
            yValsBar2.clear();
            yValsBar1.clear();
            barDataSets.clear();
        }
    }

    private void setBarChartData(List<GraphVo> barList) {
        List<GraphVo> pointBarList = new ArrayList<>();
        GraphVo vo = null;
        int count = 0;
        for (int i = 0; i < barList.size(); i++) {
            vo = barList.get(i);
            if (!(vo.getMl() == 0 || vo.getLsum() == 0)) {
                pointBarList.add(vo);
                yValsBar1.add(getEntry(vo.getMl(), count));
                yValsBar2.add(getEntry(vo.getLsum(), count));
                xValsBar.add(vo.getCategory());
                count++;
            }
        }
        Log.d("CheckingPointVo", pointBarList.toString());

        BarDataSet barDataSet1 = new BarDataSet(yValsBar1, "금액한도");
        BarDataSet barDataSet2 = new BarDataSet(yValsBar2, "사용금액");
        barDataSet1.setColor(Color.rgb(80, 208, 255));
        barDataSet2.setColor(Color.rgb(80, 255, 241));
        barDataSet1 = barDataFormatSet(barDataSet1);
        barDataSet2 = barDataFormatSet(barDataSet2);
        barDataSets.add(barDataSet1);
        barDataSets.add(barDataSet2);

        //barChart.getXAxis().setEnabled(false);
        //barChart.getAxisLeft().setEnabled(false);
        //barChart.getAxisRight().setEnabled(false);

    }

    private BarDataSet barDataFormatSet(BarDataSet data) {
        data.setValueFormatter(new com.hipo.utils.DefaultValueFormatter(0));
        data.setValueTextSize(8);
        return data;
    }

    private BarEntry getEntry(Integer val, int i) {
        return new BarEntry(val, i);
    }

    private void getBarChartData(int yearMonth[]) {
        limitHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    Log.d("nowCheckingBarChartData", msg.obj.toString());
                    barChartInit((List<GraphVo>) msg.obj);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "카테고리한도를 추가하시면 차트이용이 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        GetBarChartThread barChartThread = new GetBarChartThread(limitHandler, yearMonth);
        barChartThread.start();

    }

}
