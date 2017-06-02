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
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.hipo.lookie.R;
import com.hipo.model.pojo.AndroidCategoryChartVo;
import com.hipo.utils.DecimalRemover;
import com.hipo.utils.GetChartCategoryThread;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


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
    private boolean chartChange = true;
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

        categoryHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                initChartSet(msg);
            }
        };
        GetChartCategoryThread chartCategoryThread = new GetChartCategoryThread(categoryHandler);
        chartCategoryThread.run();

        return view;
    }

    private void initChartSet(Message msg) {
        Log.d("CategoryListSum!", msg.obj.toString());
        categoryChartList = (List) msg.obj;
        yvalues = new ArrayList<>();
        xVals = new ArrayList<>();
        sum = 0;
        for (int i = 0; i < categoryChartList.size(); i++) {
            if (categoryChartList.get(i).getSum() != 0) {
                this.sum += categoryChartList.get(i).getSum();
            }
        }
        for (int i = 0; i < categoryChartList.size(); i++) {
            if (getPercent(categoryChartList.get(i).getSum(), sum) > 0) {
                Log.d("percentage", getPercent(categoryChartList.get(i).getSum(), sum) + "");
                yvalues.add(setYEntry(getPercent(categoryChartList.get(i).getSum(), sum), i));
                xVals.add(i, categoryChartList.get(i).getCategory());
                changePercentToMoney.put(getPercent(categoryChartList.get(i).getSum(), sum), categoryChartList.get(i).getSum());
            }
        }
        Log.d("ChartSumVal", sum + "");
        chartInit(yvalues, xVals);
    }

    private void chartInit(ArrayList<Entry> yvalues, ArrayList<String> xVals) {
        dataSet = new PieDataSet(yvalues, "Election Results");
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
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(0, Color.CYAN);
        colors.add(1, Color.YELLOW);
        colors.add(2, Color.GREEN);
        colors.add(3, Color.MAGENTA);

        dataSet.setColors(colors);

    }

    private Entry setYEntry(Integer val, int i) {
        return new Entry(val, i);

    }

    private ArrayList<Integer> colorList() {
        ArrayList<Integer> colors = new ArrayList<>();
        return null;
    }

    private Integer getPercent(Integer val, Integer sum) {
        return (val * 100) / sum;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Integer sum = 0;

        if (chartChange) {
            for (int i = 0; i < yvalues.size(); i++) {
                sum = changePercentToMoney.get(Integer.valueOf((int) yvalues.get(i).getVal()));
                yvalues.get(i).setVal(sum);
                dataSet.setValueFormatter(new com.hipo.utils.DefaultValueFormatter(0));
            }
            chartChange = false;
        } else {//TODO 고민해보자 convert to percentage chart see
            for (int i = 0; i < categoryChartList.size(); i++) {
                if (getPercent(categoryChartList.get(i).getSum(), this.sum) > 0) {
                    yvalues.add(setYEntry(getPercent(categoryChartList.get(i).getSum(), this.sum), i));
                    dataSet.setValueFormatter(new PercentFormatter());
                }
            }
            chartChange = true;
        }
    }

    @Override
    public void onNothingSelected() {

    }
}
