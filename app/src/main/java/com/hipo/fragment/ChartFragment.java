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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.hipo.lookie.R;
import com.hipo.model.pojo.AndroidCategoryChartVo;
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
    private ArrayList<Entry> yValsPie, yValsBar;
    private ArrayList<String> xValsPie, xValsBar;
    private Map<Integer, Integer> changePercentToMoney;
    private PieDataSet pDataSet;
    private boolean chartInitDone = true;
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
        GetChartCategoryThread chartCategoryThread = new GetChartCategoryThread(categoryHandler, yearMonth);
        chartCategoryThread.run();


        return view;
    }


    private void initChartSet(Message msg) {
        Log.d("CategoryListSum!", msg.obj.toString());
        categoryChartList = (List) msg.obj;
        if (chartInitDone) {
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
                if (getPercent(categoryChartList.get(i).getSum(), sum) > 0) {
                    Log.d("percentage", getPercent(categoryChartList.get(i).getSum(), sum) + "");
                    yValsPie.add(setYEntry(getPercent(categoryChartList.get(i).getSum(), sum), index));
                    xValsPie.add(index++, categoryChartList.get(i).getCategory());
                    changePercentToMoney.put(getPercent(categoryChartList.get(i).getSum(), sum), categoryChartList.get(i).getSum());
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
            }
        }

        Log.d("ChartSumVal", sum + "");
        chartInit(yValsPie, xValsPie);
        chartInitDone = false;

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
        return (val * 100) / sum;
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
            GetChartCategoryThread chartCategoryThread = new GetChartCategoryThread(categoryHandler, yearMonth);
            chartCategoryThread.run();
        } else {
            Log.d("else입니다", "^^");

        }

    }

    @OnItemSelected(R.id.chart_month_spinner)
    public void monthSpinnerSelected(AdapterView<?> parent, View view, int position, long id) {
        int yearMonth[] = new int[2];
        yearMonth[0] = Integer.parseInt(yearSpinner.getAdapter().getItem(yearSpinner.getSelectedItemPosition()).toString());
        yearMonth[1] = monthSpinner.getSelectedItemPosition() + 1;
        if (divChart[0]) {
            categoryHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    updatePieChart(msg);
                }
            };
            GetChartCategoryThread chartCategoryThread = new GetChartCategoryThread(categoryHandler, yearMonth);
            chartCategoryThread.run();
        } else {
            Log.d("else입니다", "^^");
        }
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
                xValsPie.clear();
                yValsPie.clear();
                for (int i = 0; i < categoryChartList.size(); i++) {
                    if (getPercent(categoryChartList.get(i).getSum(), sum) > 0) {
                        Log.d("percentage", getPercent(categoryChartList.get(i).getSum(), sum) + "");
                        yValsPie.add(setYEntry(getPercent(categoryChartList.get(i).getSum(), sum), index));
                        xValsPie.add(index++, categoryChartList.get(i).getCategory());
                        changePercentToMoney.put(getPercent(categoryChartList.get(i).getSum(), sum), categoryChartList.get(i).getSum());
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
            /*Log.d("pqpqVo", pq.poll() + "");

            PriorityQueue<AndroidCategoryChartVo> reversedPriorityQueue = new PriorityQueue<>(pq.size(), Collections.reverseOrder());
            reversedPriorityQueue.addAll(pq);
*/
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
        for (int i = 0; i < yValsPie.size(); i++) {
            sum = changePercentToMoney.get((int) yValsPie.get(i).getVal());
            yValsPie.get(i).setVal(sum);
            pDataSet.setValueFormatter(new com.hipo.utils.DefaultValueFormatter(0));
        }
        Log.d("chartdes", "p -> 원");
    }

    @Override
    public void onNothingSelected() {
        List<AndroidCategoryChartVo> tempList = new ArrayList<>();
        xValsPie.clear();
        yValsPie.clear();
        int sum = 0;
        for (int i = 0; i < categoryChartList.size(); i++) {
            if (categoryChartList.get(i).getSum() != 0) {
                sum += categoryChartList.get(i).getSum();
            }
        }
        for (int i = 0; i < categoryChartList.size(); i++) {
            if (getPercent(categoryChartList.get(i).getSum(), sum) > 0) {
                tempList.add(categoryChartList.get(i));
            }
        }
        for (int i = 0; i < tempList.size(); i++) {
            Log.d("percentage", getPercent(tempList.get(i).getSum(), sum) + "");
            yValsPie.add(setYEntry(getPercent(tempList.get(i).getSum(), sum), i));
            xValsPie.add(tempList.get(i).getCategory());
            changePercentToMoney.put(getPercent(tempList.get(i).getSum(), sum), tempList.get(i).getSum());
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

    private void barChartInit() {

    }


    private void getBarChartData(int yearMonth[]) {
        limitHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d("nowCheckingBarChartData", msg.obj.toString());
            }
        };
        GetBarChartThread barChartThread=new GetBarChartThread(limitHandler,yearMonth);
        barChartThread.start();
    }

}
