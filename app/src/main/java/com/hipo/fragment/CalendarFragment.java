package com.hipo.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hipo.callback.CalendarToDialog;
import com.hipo.lookie.R;
import com.hipo.model.GridViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

import static com.hipo.lookie.R.array.list_month;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends DialogFragment {

    @BindView(R.id.gridview)
    GridView gridView;
    @BindView(R.id.date)
    TextView dateText;
    @BindView(R.id.month_spinner)
    Spinner monthSpinner;
    @BindView(R.id.year_spinner)
    Spinner yearSpinner;

    private CalendarToDialog callback;
    private GridViewAdapter adapter;
    private ArrayList<String> dateList;
    private Calendar calendar;
    private int month, year;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_calendar, null);
        ButterKnife.bind(this, v);
        yearSpinner.setSelection(1);
        countingDate(month, year);
        callback = (CalendarToDialog) getTargetFragment();
        builder.setView(v);

        return builder.create();
    }

    @OnItemClick(R.id.gridview)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String arr[] = new String[3];
        Log.d("gridview", parent.getAdapter().getItem(position) + " 일 ");
        arr[0] = String.valueOf(year);
        arr[1] = String.valueOf(month);
        arr[2] = (String) parent.getAdapter().getItem(position);

        callback.dateCallback(arr);
        getDialog().dismiss();
    }


    @OnItemSelected(R.id.month_spinner)
    public void monthSpinnerEvnet(AdapterView<?> parent, View view, int position, long id) {
        switch (yearSpinner.getSelectedItemPosition()) {
            case 0:
                year = 2016;
                break;
            case 1:
                year = 2017;
        }
        month = position + 1;
        countingDate(month, year);
    }

    @OnItemSelected(R.id.year_spinner)
    public void yearSpinnerEvnet(AdapterView<?> parent, View view, int position, long id) {
        month = monthSpinner.getSelectedItemPosition() + 1;
        switch (position) {
            case 0:
                year = 2016;
                Log.d("year month", year + " " + month);
                countingDate(month, year);
                break;
            case 1:
                year = 2017;
                Log.d("year month", year + " " + month);
                countingDate(month, year);
                break;

        }
    }


    private void countingDate(int month, int year) {

        Long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat yearFormet = new SimpleDateFormat("yyyy", Locale.KOREA);
        SimpleDateFormat monthFormet = new SimpleDateFormat("MM", Locale.KOREA);
        calendar = Calendar.getInstance();
        if (month == 0) {
            month = (calendar.get(Calendar.MONTH) + 1);
        }
        if (year == 0) {
            year = (calendar.get(Calendar.YEAR));
            Log.d("year", year + "");
        }

        dateText.setText(year + "/" + month);
        dateList = new ArrayList<String>();
        dateList.add("일");
        dateList.add("월");
        dateList.add("화");
        dateList.add("수");
        dateList.add("목");
        dateList.add("금");
        dateList.add("토");

        calendar.set(year, month - 1, 1);

        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);

        for (int i = 1; i < dayNum; i++) {
            dateList.add("");
        }
        //setCalendarDate(calendar.get(Calendar.MONTH) + 1);
        setCalendarDate(calendar.get(Calendar.MONTH));
        monthSpinner.setSelection(month - 1);
        adapter = new GridViewAdapter(getContext(), dateList);

        gridView.setAdapter(adapter);
    }

    private void setCalendarDate(int month) {
        calendar.set(Calendar.MONTH, month - 1);
        for (int i = 0; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dateList.add("" + (i + 1));
        }
    }
}
