package com.hipo.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hipo.lookie.R;
import com.hipo.model.pojo.ListVo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyDialogFragment extends DialogFragment {

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
        return builder.create();
    }

    @OnClick(R.id.modify_btn)
    public void onButtonClick(View view) {
        if (btnBoolean) {
            nameText.setVisibility(View.VISIBLE);
            moneyText.setVisibility(View.VISIBLE);
            paidText.setVisibility(View.VISIBLE);
            categoryText.setVisibility(View.VISIBLE);

            nameEdit.setVisibility(View.INVISIBLE);
            nameText.setText(listVo.getName());

            moneyEdit.setVisibility(View.INVISIBLE);
            moneyText.setText(listVo.getMoney());

            moneySpinner.setVisibility(View.INVISIBLE);
            categorySpinner.setVisibility(View.INVISIBLE);
            btnBoolean = false;
        } else {
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

}
