package com.hipo.lookie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by dongjune on 2017-04-20.
 */

public class ListFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListDataCallback dataCallback;

    public static ListFragment newInstance(int sectionNumber, UserVo vo) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable("UserVo", vo);
        //Log.d("중간과정확인",vo.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_list, container, false);
        Bundle b = getArguments();
        UserVo vo = (UserVo) b.getSerializable("UserVo");
        Log.d("도착지UserVo", vo.toString());
        dataCallback.test(1);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dataCallback = (ListDataCallback) activity;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
