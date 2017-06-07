package com.hipo.fragment;


import android.graphics.Color;
import android.os.Build;
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
import com.hipo.utils.GetChartCategoryThread;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChartFragment extends Fragment implements OnChartValueSelectedListener {

    private Handler categoryHandler;
    private PieData data;
    private List<AndroidCategoryChartVo> categoryChartList;
    private ArrayList<Entry> yvalues;
    private ArrayList<String> xVals;
    private Map<Integer, Integer> changePercentToMoney;
    private PieDataSet dataSet;
    private Integer sum;
    private Message msg;
    private boolean chartInitDone = true;
    @BindView(R.id.chart_year_spinner)
    Spinner yearSpinner;
    @BindView(R.id.chart_month_spinner)
    Spinner monthSpinner;
    @BindView(R.id.chart_assortment_spinner)
    Spinner assortmentSpinner;
    @BindView(R.id.pie_chart)
    PieChart pieChart;

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
        changePercentToMoney = new HashMap<>();
        int yearMonth[] = settingDefalutYearMonth();
        categoryHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                initChartSet(msg);
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
            yvalues = new ArrayList<>();
            xVals = new ArrayList<>();
        }
        this.sum = 0;

        int index = 0;
        for (int i = 0; i < categoryChartList.size(); i++) {
            if (categoryChartList.get(i).getSum() != 0) {
                this.sum += categoryChartList.get(i).getSum();
            }
        }
        for (int i = 0; i < categoryChartList.size(); i++) {
            if (getPercent(categoryChartList.get(i).getSum(), sum) > 0) {
                Log.d("percentage", getPercent(categoryChartList.get(i).getSum(), sum) + "");
                yvalues.add(setYEntry(getPercent(categoryChartList.get(i).getSum(), sum), index));
                xVals.add(index++, categoryChartList.get(i).getCategory());
                changePercentToMoney.put(getPercent(categoryChartList.get(i).getSum(), sum), categoryChartList.get(i).getSum());
            }
        }
        dataSet = new PieDataSet(yvalues, "Election Results");
        data = new PieData(xVals, dataSet);
        Log.d("ChartSumVal", sum + "");
        if (chartInitDone) {
            chartInit(yvalues, xVals);
            chartInitDone = false;
        }

    }

    private void chartInit(ArrayList<Entry> yvalues, ArrayList<String> xVals) {
        dataSet = new PieDataSet(yvalues, "");
        data = new PieData(xVals, dataSet);
        pieChart.setDescription("높은금액순으로 10개까지 표시됩니다.");
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
        dataSet.setColors(colorList());

    }

    private Entry setYEntry(Integer val, int i) {
        return new Entry(val, i);

    }

    private ArrayList<Integer> colorList() {
        final int[] MY_COLORS = {Color.rgb(80, 208, 255), Color.rgb(80, 255, 241), Color.rgb(80, 255, 117),
                Color.rgb(191, 80, 255), Color.rgb(255, 80, 212), Color.rgb(255, 80, 80), Color.rgb(253, 255, 80),
                Color.rgb(255, 195, 80), Color.rgb(204, 255, 80), Color.rgb(255, 80, 245)};
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
        categoryHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                updateChart(msg);
            }
        };
        GetChartCategoryThread chartCategoryThread = new GetChartCategoryThread(categoryHandler, yearMonth);
        chartCategoryThread.run();

    }

    @OnItemSelected(R.id.chart_month_spinner)
    public void monthSpinnerSelected(AdapterView<?> parent, View view, int position, long id) {
        int yearMonth[] = new int[2];
        yearMonth[0] = Integer.parseInt(yearSpinner.getAdapter().getItem(yearSpinner.getSelectedItemPosition()).toString());
        yearMonth[1] = monthSpinner.getSelectedItemPosition() + 1;
        categoryHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                updateChart(msg);
            }
        };
        GetChartCategoryThread chartCategoryThread = new GetChartCategoryThread(categoryHandler, yearMonth);
        chartCategoryThread.run();

    }

    private void updateChart(Message msg) {
        categoryChartList = (List<AndroidCategoryChartVo>) msg.obj;
        int sum = 0;
        xVals.clear();
        yvalues.clear();
        dataSet.clear();
        int index = 0;
        for (int i = 0; i < categoryChartList.size(); i++) {
            if (categoryChartList.get(i).getSum() != 0) {
                sum += categoryChartList.get(i).getSum();
            }
        }
        try {
            for (int i = 0; i < categoryChartList.size(); i++) {
                if (getPercent(categoryChartList.get(i).getSum(), sum) > 0) {
                    Log.d("percentage", getPercent(categoryChartList.get(i).getSum(), sum) + "");
                    yvalues.add(setYEntry(getPercent(categoryChartList.get(i).getSum(), sum), index));
                    xVals.add(index++, categoryChartList.get(i).getCategory());
                    changePercentToMoney.put(getPercent(categoryChartList.get(i).getSum(), sum), categoryChartList.get(i).getSum());
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
        yearMonth[0] = Integer.parseInt(yearSpinner.getAdapter().getItem(position).toString());
        yearMonth[1] = monthSpinner.getSelectedItemPosition() + 1;

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Integer sum = 0;
        for (int i = 0; i < yvalues.size(); i++) {
            sum = changePercentToMoney.get(Integer.valueOf((int) yvalues.get(i).getVal()));
            yvalues.get(i).setVal(sum);
            dataSet.setValueFormatter(new com.hipo.utils.DefaultValueFormatter(0));
        }
        Log.d("chartdes", "p -> 원");
    }

    @Override
    public void onNothingSelected() {
        List<AndroidCategoryChartVo> tempList = new ArrayList<>();
        xVals.clear();
        yvalues.clear();
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
            yvalues.add(setYEntry(getPercent(tempList.get(i).getSum(), sum), i));
            xVals.add(tempList.get(i).getCategory());
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


}
