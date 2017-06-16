package com.hipo.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hipo.lookie.R;
import com.hipo.utils.AddedListVoFunction;
import com.hipo.model.GetPaidSumThread;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

/**
 * Created by dongjune on 2017-05-23.
 */

public class CheckMoneyDialog extends DialogFragment {

    private Handler paidSumHandler;
    private GetPaidSumThread paidSumThread;
    private boolean done = true;
    @BindView(R.id.check_money_text)
    TextView textView;
    @BindView(R.id.check_year_spinner)
    Spinner yearSpinner;
    @BindView(R.id.check_month_spinner)
    Spinner monthSpinner;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.check_money_dialog_fragment, null);

        builder.setView(v);
        ButterKnife.bind(this, v);

        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        dialog.show();
        int[] yearMonth = settingDefalutYearMonth();
        if (done) {
            paidSumHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    setUI(msg);
                }
            };
            paidSumThread = new GetPaidSumThread(paidSumHandler, yearMonth);
            paidSumThread.start();
            done = false;
        }
        return dialog;
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

    public void setUI(Message msg) {
        String sum = "";
        try {
            String sumMessage = msg.obj.toString();
            sum = AddedListVoFunction.convertForForm(sumMessage);
        } catch (Exception e) {
            Toast.makeText(getContext(), "사용금액이 없습니다.", Toast.LENGTH_SHORT).show();
            sum = "0";
        }
        textView.setText(sum);
    }

    @OnItemSelected(R.id.check_year_spinner)
    public void yearSpinnerSelected(AdapterView<?> parent, View view, int position, long id) {
        int yearMonth[] = new int[2];
        yearMonth[0] = Integer.parseInt(yearSpinner.getAdapter().getItem(position).toString());
        yearMonth[1] = monthSpinner.getSelectedItemPosition() + 1;
        paidSumHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                setUI(msg);
            }
        };
        paidSumThread = new GetPaidSumThread(paidSumHandler, yearMonth);
        paidSumThread.start();
    }

    @OnItemSelected(R.id.check_month_spinner)
    public void monthSpinnerSelected(AdapterView<?> parent, View view, int position, long id) {
        int yearMonth[] = new int[2];
        yearMonth[0] = Integer.parseInt(yearSpinner.getAdapter().getItem(yearSpinner.getSelectedItemPosition()).toString());
        yearMonth[1] = monthSpinner.getSelectedItemPosition() + 1;
        paidSumHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                setUI(msg);
            }
        };
        paidSumThread = new GetPaidSumThread(paidSumHandler, yearMonth);
        paidSumThread.start();
    }


}
