package com.hipo.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.gson.Gson;
import com.hipo.callback.CalendarToDialog;
import com.hipo.callback.ReflashListData;
import com.hipo.lookie.R;
import com.hipo.model.NetworkTask2;
import com.hipo.model.pojo.AddedListVo;
import com.hipo.model.pojo.ListVo;
import com.hipo.utils.AddedListVoFunction;
import com.hipo.utils.ConvertListVo;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyDialogFragment extends DialogFragment implements CalendarToDialog {

    private AddedListVo listVo;
    private boolean btnBoolean = false, convertingDone = false;
    private AddedListVoFunction listFunction;
    private StringBuilder dateSb;
    private ReflashListData reflashData;
    private int distin = 0;
    private TimePickerDialog dialog;
    private NetworkTask2 task2;
    private boolean voCheck = false;
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
    @BindView(R.id.won_text)
    TextView wonText;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        reflashData = (ReflashListData) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            distin = 2;
            listVo = (AddedListVo) bundle.getSerializable("AddedListVo");
            Log.d("잘가저왓니?", listVo.toString());
            dateSb = new StringBuilder();
            reflashData = (ReflashListData) getTargetFragment();
        } else {
            distin = 1;
            listVo = new AddedListVo();
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_my_dialog, null);
        builder.setView(v);
        ButterKnife.bind(this, v);
        init(listVo);

        if (distin == 1) {
            builder.setTitle("직접입력하기");
        }
        if (distin == 2) {

            builder.setTitle("상세정보");
            dateText.setClickable(false);
            timeText.setClickable(false);
            listFunction = new AddedListVoFunction();
        }
        return builder.create();
    }

    @OnClick(R.id.modify_btn)
    public void onButtonClick(View view) {
        if (distin == 1) {
            if (!(dateText.getText().toString().equals("년.월") || timeText.getText().toString().equals("시:분") || moneyEdit.getText().toString().equals("") || nameEdit.getText().toString().equals(""))) {

                String money = convertForForm(moneyEdit.getText().toString());
                if (convertingDone = true) {
                    listVo.setDay(dateText.getText() + " " + timeText.getText());

                    listVo.setMoney(money);
                    listVo.setPaid(moneySpinner.getSelectedItem().toString());
                    listVo.setCategory(categorySpinner.getSelectedItem().toString());
                    listVo.setName(nameEdit.getText().toString());
                    listVo.setLocationX("o");
                    listVo.setLocationY("o");
                    listVo.setOperations("-");
                    listVo.setBank("없음");
                    listVo.setListId("0");
                    listVo.setId(Profile.getCurrentProfile().getId());
                    listVo.setDate_day("0");
                    listVo.setDate_ym("0");

                    task2 = new NetworkTask2(Profile.getCurrentProfile().getId(), 6);
                    Gson gson = new Gson();
                    ListVo vo = new ConvertListVo().converting(listVo);
                    Log.d("converted", vo.toString());
                    String listVoJson = gson.toJson(vo, ListVo.class);
                    Map<String, String> params = new HashMap<>();
                    params.put("addVo", listVoJson);
                    task2.execute(params);
                    Log.d("addVo", vo.toString());
                    voCheck = true;
                    dismiss();
                }
            }

        } else if (distin == 2) {
            if (btnBoolean) {
                listVo.setMoney(convertForForm(moneyEdit.getText().toString()));
                if (convertingDone == false) {
                    return;
                }
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
                moneyText.setText(backConvert(listVo.getMoney()));

                paidText.setText(listVo.getPaid());
                categoryText.setText(listVo.getCategory());
                dateText.setClickable(false);
                timeText.setClickable(false);

                moneySpinner.setVisibility(View.INVISIBLE);
                categorySpinner.setVisibility(View.INVISIBLE);
                btnBoolean = false;
                voCheck = true;
                sendUpdate();
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
                moneyEdit.setText(backConvert(listVo.getMoney()));

                moneySpinner.setVisibility(View.VISIBLE);
                categorySpinner.setVisibility(View.VISIBLE);

                wonText.setVisibility(View.VISIBLE);
                dateText.setClickable(true);
                timeText.setClickable(true);
                voCheck = true;
                btnBoolean = true;
            }
        }
    }

    public void init(AddedListVo listVo) {
        if (distin == 1) {
            moneyEdit.setVisibility(View.VISIBLE);
            moneyText.setVisibility(View.INVISIBLE);

            moneySpinner.setVisibility(View.VISIBLE);
            paidText.setVisibility(View.INVISIBLE);

            nameEdit.setVisibility(View.VISIBLE);
            nameText.setVisibility(View.INVISIBLE);

            categorySpinner.setVisibility(View.VISIBLE);
            categoryText.setVisibility(View.INVISIBLE);

            wonText.setVisibility(View.VISIBLE);
            dateText.setText("년.월");
            timeText.setText("시:분");

        } else if (distin == 2) {
            nameText.setText(listVo.getName());
            moneyText.setText(listVo.getMoney());
            paidText.setText(listVo.getPaid());
            categoryText.setText(listVo.getCategory());
            dateText.setText(listVo.getDate_ym() + "-" + listVo.getDate_day());
            timeText.setText(listVo.getTime());
        }

    }

    @OnClick(R.id.date_text)
    public void dateClickView(View v) {
        Log.d("a", "ok");
        CalendarFragment calendarFragment = new CalendarFragment();
        if (distin == 2) {
            Bundle bundle = new Bundle();
            bundle.putString("date", listVo.getDate_day());
            calendarFragment.setArguments(bundle);
        }
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
        if (distin == 2) {
            int times[] = listFunction.hourMin(listVo);
            dialog = new TimePickerDialog(getContext(), listener, times[0], times[1], true);
        }
        dialog = new TimePickerDialog(getContext(), listener, 0, 0, true);
        dialog.show();
    }

    @Override
    public void dateCallback(String[] dateArr) {
        for (int i = 0; i < dateArr.length; i++) {
            Log.d("Checking time", dateArr[i]);
        }
        dateText.setText(dateArr[0] + "-" + dateArr[1] + "-" + dateArr[2]);

    }

    public int checkSelection(String select, int div) {
        Log.d("select", select);
        String arr[];
        if (div == 1) {
            arr = getResources().getStringArray(R.array.paid_spinner);
        } else {
            arr = getResources().getStringArray(R.array.category_spinner);

        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(select)) {
                Log.d("arr" + i, arr[i]);
                return i;
            }
        }

        return 0;
    }

    public void sendUpdate() {
        task2 = new NetworkTask2(listVo.getId(), 5);
        Map<String, String> params = new HashMap<>();
        Gson gson = new Gson();
        dateSb.append(dateText.getText());
        dateSb.append(" ");
        dateSb.append(timeText.getText());
        listVo.setDay("");
        listVo.setDay(dateSb.toString());
        dateSb.setLength(0);
        String listVoJson = gson.toJson(new ConvertListVo().converting(listVo));
        params.put("updateListVo", listVoJson);
        task2.execute(params);
    }

    public String convertForForm(String money) {
        try {
            Integer.parseInt(money);
            this.convertingDone = true;
        } catch (Exception e) {
            this.convertingDone = false;
            Toast.makeText(getContext(), "숫자만 입력해주세요", Toast.LENGTH_SHORT).show();
            Log.d("숫자만입력", "알림주기");
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < money.length(); i++) {
            sb.append(money.charAt(i));
            if ((money.length() - (i + 1)) % 3 == 0) {
                if (money.length() - 1 == i) {

                } else {
                    sb.append(",");
                }
            }
        }
        sb.append("원");
        return sb.toString();
    }

    public String backConvert(String money) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < money.length(); i++) {
            if (!(String.valueOf(money.charAt(i)).equals(",") || (String.valueOf(money.charAt(i)).equals("원")))) {
                sb.append(money.charAt(i));
            }
        }

        return sb.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("ddddx뒤진다", "fasdf");
        Log.d("listVo", listVo.toString());
        reflashData.reflash(this.listVo);

    }
}
