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
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hipo.lookie.R;
import com.hipo.utils.GetChartCategoryThread;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChartFragment extends Fragment {

    private Handler categoryHandler;
    private PieData data;
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

        categoryHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d("CategoryListSum!", msg.obj.toString());
            }
        };
        GetChartCategoryThread chartCategoryThread = new GetChartCategoryThread(categoryHandler);
        chartCategoryThread.start();

        chartInit();
        return view;
    }

    private void chartInit() {
        ArrayList<Entry> yvalues = new ArrayList<Entry>();
        setYEntry(yvalues);
        PieDataSet dataSet = new PieDataSet(yvalues, "Election Results");

        ArrayList<String> xVals = new ArrayList<String>();
        setXEntry(xVals);

        data = new PieData(xVals, dataSet);

        pieChart.setDescription("This is Pie Chart");

        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.setHoleRadius(30f);

        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);

        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
    }

    private void setYEntry(ArrayList<Entry> yvalues) {
        yvalues.add(new Entry(1f, 0));
        yvalues.add(new Entry(2f, 1));
        yvalues.add(new Entry(3f, 2));
        yvalues.add(new Entry(4f, 3));
        yvalues.add(new Entry(5f, 4));
        yvalues.add(new Entry(6f, 5));
    }

    private void setXEntry(ArrayList<String> xVals) {
        xVals.add("January");
        xVals.add("February");
        xVals.add("March");
        xVals.add("April");
        xVals.add("May");
        xVals.add("June");
    }
}
