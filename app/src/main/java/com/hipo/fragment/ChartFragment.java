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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hipo.lookie.R;
import com.hipo.model.pojo.AndroidCategoryChartVo;
import com.hipo.utils.DecimalRemover;
import com.hipo.utils.GetChartCategoryThread;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
                List<AndroidCategoryChartVo> categoryChartList = (List) msg.obj;
                ArrayList<PieEntry> yvalues = new ArrayList<>();
                ArrayList<String> xVals = new ArrayList<>();
                for (int i = 0; i < categoryChartList.size(); i++) {
                    yvalues.add(setYEntry(categoryChartList.get(i).getSum(), i));
                    xVals.add(i, categoryChartList.get(i).getCategory());
                }
                chartInit(yvalues, xVals);
            }
        };
        GetChartCategoryThread chartCategoryThread = new GetChartCategoryThread(categoryHandler);
        chartCategoryThread.run();

        return view;
    }

    private void chartInit(ArrayList<PieEntry> yvalues, ArrayList<String> xVals) {
        PieDataSet dataSet = new PieDataSet(yvalues, "Election Results");
        data = new PieData(dataSet);
        Description d=new Description();
        d.setText("dede");
        pieChart.setDescription(d);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.setHoleRadius(30f);

        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);

        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        data.setValueFormatter(new DecimalRemover(new DecimalFormat("###,###,###")));
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(0, Color.BLUE);
        colors.add(1, Color.YELLOW);
        colors.add(2, Color.GREEN);


        dataSet.setColors(colors);
    }

    private PieEntry setYEntry(Integer val, int i) {
        return new PieEntry(val, i);

    }

    private ArrayList<Integer> colorList() {
        ArrayList<Integer> colors = new ArrayList<>();
        return null;
    }

}
