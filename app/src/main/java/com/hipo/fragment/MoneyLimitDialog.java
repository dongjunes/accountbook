package com.hipo.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hipo.lookie.R;
import com.hipo.model.GetCategoryListThread;
import com.hipo.model.UpdateLimitValueThread;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dongjune on 2017-05-23.
 */

public class MoneyLimitDialog extends DialogFragment {

    private Handler categoryListHandler;
    private boolean doneUpdate = false;
    @BindView(R.id.category_limit_spinner)
    Spinner categorySpinner;
    @BindView(R.id.category_limit_edit)
    EditText limitEdit;
    @BindView(R.id.category_limit_select)
    Button btn;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_limit_dialog, null);
        builder.setView(v);
        ButterKnife.bind(this, v);
        setSpinnerAdapter();
        return builder.create();
    }

    public void setSpinnerAdapter() {
        categoryListHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ArrayList<String> adapterArray = (ArrayList) msg.obj;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, adapterArray);
                categorySpinner.setAdapter(adapter);
            }
        };
        GetCategoryListThread categoryListThread = new GetCategoryListThread(categoryListHandler);
        categoryListThread.start();
    }

    @OnClick(R.id.category_limit_select)
    public void onClick(View v) {
        String money = limitEdit.getText().toString();
        int limit = checkInteger(money);
        String category = categorySpinner.getSelectedItem().toString();
        UpdateLimitValueThread limitThread = new UpdateLimitValueThread(category, limit);
        limitThread.start();
        if (doneUpdate) {
            dismiss();
        }
    }

    public int checkInteger(String money) {
        try {
            int limit = Integer.parseInt(money);
            doneUpdate = true;
            return limit;
        } catch (Exception e) {
            Toast.makeText(getContext(), "숫자만 입력해 주세요", Toast.LENGTH_SHORT).show();
            limitEdit.setText("");
        }
        return -1;
    }

}
