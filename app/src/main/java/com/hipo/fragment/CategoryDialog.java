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

import com.facebook.Profile;
import com.hipo.lookie.R;
import com.hipo.model.network.NetworkTask2;
import com.hipo.model.GetCategoryListThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dongjune on 2017-05-23.
 */

public class CategoryDialog extends DialogFragment {

    private Handler categoryListHandler;

    @BindView(R.id.category_add_btn)
    Button categoryAddBtn;
    @BindView(R.id.category_edit)
    EditText categoryEdit;
    @BindView(R.id.category_delete_spinner)
    Spinner categoryDeleteSpinner;
    @BindView(R.id.category_delete_btn)
    Button deleteBtn;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_category_dialog, null);
        builder.setView(v);
        ButterKnife.bind(this, v);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(0, "1");
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
                categoryDeleteSpinner.setAdapter(adapter);
            }
        };
        GetCategoryListThread categoryListThread = new GetCategoryListThread(categoryListHandler);
        categoryListThread.start();
    }

    @OnClick(R.id.category_add_btn)
    public void onClickCategoryAddButton(View v) {
        String category = categoryEdit.getText().toString();
        sharingServer(category);
        Toast.makeText(getContext(), "카테고리가 추가되었습니다.", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    public void sharingServer(String category) {
        try {
            NetworkTask2 task2 = new NetworkTask2(Profile.getCurrentProfile().getId(), 9);
            Map<String, String> params = new HashMap<>();
            params.put("category", category);
            task2.execute(params);

        } catch (Exception e) {
            Toast.makeText(getContext(), "카테고리 추가오류", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.category_delete_btn)
    public void OnClickDeleteBtn(View v) {
        String category = categoryDeleteSpinner.getSelectedItem().toString();
        NetworkTask2 task2 = new NetworkTask2(Profile.getCurrentProfile().getId(), 11);
        Map<String, String> params = new HashMap<>();
        params.put("category", category);
        task2.execute(params);
        setSpinnerAdapter();
    }

}
