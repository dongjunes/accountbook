package com.hipo.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.hipo.callback.SettingDataCallback;
import com.hipo.component.activity.LoginActivity;
import com.hipo.component.service.MyService;
import com.hipo.lookie.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    private SettingDataCallback callback;

    public SettingFragment() {
        // Required empty public constructor
    }


    private static final String ARG_SECTION_NUMBER = "section_number";

    public static SettingFragment newInstance(int sectionNumber) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback=(SettingDataCallback)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.loginbtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                AccessToken.setCurrentAccessToken(null);
                startActivity(i);
                callback.stopService();
                Toast.makeText(getContext(), "다시 로그인해주세요", Toast.LENGTH_SHORT);
                getActivity().finish();
            }
        });
        return view;
    }

}