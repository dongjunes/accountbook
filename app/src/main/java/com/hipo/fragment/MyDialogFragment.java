package com.hipo.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.hipo.callback.CalendarToDialog;
import com.hipo.lookie.R;
import com.hipo.model.pojo.ListVo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyDialogFragment extends DialogFragment implements CalendarToDialog {

    private ListVo listVo;
    private boolean btnBoolean = false;
    @BindView(R.id.modify_btn)
    Button modifyBtn;
    @BindView(R.id.time_text)
    TextView timeText;
    @BindView(R.id.paid_text)
    TextView paidText;
    @BindView(R.id.date_text)
    TextView dateText;
    @BindView(R.id.category_text)
    TextView categoryText;
    @BindView(R.id.name_text)
    TextView nameText;
    @BindView(R.id.money_text)
    TextView moneyText;
    @BindView(R.id.name_edit)
    EditText nameEdit;
    @BindView(R.id.money_edit)
    EditText moneyEdit;
    @BindView(R.id.money_spinner)
    Spinner moneySpinner;
    @BindView(R.id.category_spinner)
    Spinner categorySpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        listVo = (ListVo) bundle.getSerializable("listVo");
        Log.d("잘가저왓니?", listVo.toString());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_my_dialog, null);
        builder.setView(v);
        builder.setTitle("상세정보");
        ButterKnife.bind(this, v);

        init(listVo);
        dateText.setClickable(false);
        timeText.setClickable(false);
        return builder.create();
    }

    @OnClick(R.id.modify_btn)
    public void onButtonClick(View view) {
        if (btnBoolean) {
            listVo.setMoney(moneyEdit.getText().toString());
            listVo.setName(nameEdit.getText().toString());
            listVo.setPaid((String) moneySpinner.getSelectedItem());
            listVo.setCategory((String) categorySpinner.getSelectedItem());
            nameText.setVisibility(View.VISIBLE);
            moneyText.setVisibility(View.VISIBLE);
            paidText.setVisibility(View.VISIBLE);
            categoryText.setVisibility(View.VISIBLE);

            nameEdit.setVisibility(View.INVISIBLE);
            nameText.setText(listVo.getName());

            moneyEdit.setVisibility(View.INVISIBLE);
            moneyText.setText(listVo.getMoney());

            paidText.setText(listVo.getPaid());
            categoryText.setText(listVo.getCategory());
            dateText.setClickable(false);
            timeText.setClickable(false);

            moneySpinner.setVisibility(View.INVISIBLE);
            categorySpinner.setVisibility(View.INVISIBLE);
            btnBoolean = false;


            //서버에 전송


        } else {
            moneySpinner.setSelection(checkSelection(listVo.getPaid(), 1));
            moneySpinner.setSelection(checkSelection(listVo.getCategory(), 2));
            nameText.setVisibility(View.INVISIBLE);
            moneyText.setVisibility(View.INVISIBLE);
            paidText.setVisibility(View.INVISIBLE);
            categoryText.setVisibility(View.INVISIBLE);

            nameEdit.setVisibility(View.VISIBLE);
            nameEdit.setText(listVo.getName());

            moneyEdit.setVisibility(View.VISIBLE);
            moneyEdit.setText(listVo.getMoney());

            moneySpinner.setVisibility(View.VISIBLE);
            categorySpinner.setVisibility(View.VISIBLE);

            dateText.setClickable(true);
            timeText.setClickable(true);

            btnBoolean = true;
        }

    }

    public void init(ListVo listVo) {
        nameText.setText(listVo.getName());
        moneyText.setText(listVo.getMoney());
        paidText.setText(listVo.getPaid());
        categoryText.setText(listVo.getCategory());
        dateText.setText(listVo.getDate_ym() + "." + listVo.getDate_day());
        timeText.setText(listVo.getTime());

    }

    @OnClick(R.id.date_text)
    public void dateClickView(View v) {
        Log.d("a", "ok");
        CalendarFragment calendarFragment = new CalendarFragment();
        Bundle bundle = new Bundle();
        bundle.putString("date", listVo.getDate_day());
        calendarFragment.setArguments(bundle);
        FragmentManager fm = getFragmentManager();
        calendarFragment.setTargetFragment(this, 0);
        calendarFragment.show(fm, "tags");

    }

    @OnClick(R.id.time_text)
    public void timeClickView(View v) {
        Log.d("b", "ok");
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (minute < 10) {
                    listVo.setTime(hourOfDay + ":" + "0" + minute);
                    Log.d("timecheck", listVo.getTime());
                } else {
                    listVo.setTime(hourOfDay + ":" + minute);
                }
                timeText.setText(listVo.getTime());
            }
        };
        int times[] = listVo.hourMin();
        TimePickerDialog dialog = new TimePickerDialog(getContext(), listener, times[0], times[1], true);
        dialog.show();
    }

    @Override
    public void dateCallback(String[] dateArr) {
        for (int i = 0; i < dateArr.length; i++) {
            Log.d("Checking time", dateArr[i]);
        }
        dateText.setText(dateArr[0] + "-" + dateArr[1] + "." + dateArr[2]);

    }

    public int checkSelection(String select, int div) {
        Log.d("select",select);
        String arr[];
        if (div == 1) {
            arr = getResources().getStringArray(R.array.paid_spinner);
        } else {
            arr = getResources().getStringArray(R.array.category_spinner);

        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(select)) {
                Log.d("arr"+i,arr[i]);
                return i;
            }
        }

        return 0;
    }

}
