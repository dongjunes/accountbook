package com.hipo.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.hipo.callback.ListDataCallback;
import com.hipo.lookie.R;
import com.hipo.model.RecyclerAdapter;
import com.hipo.model.pojo.ListVo;
import com.hipo.model.pojo.UserVo;
import com.hipo.utils.GetListDataThread;
import com.hipo.utils.SortingThread;

import java.util.List;

/**
 * Created by dongjune on 2017-04-20.
 */

public class ListFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListDataCallback dataCallback;
    private RecyclerView recyclerView = null;
    private Spinner spinner;
    private Handler listHandler, sortHandler;
    private SortingThread sort;

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
//        Log.d("도착지UserVo", vo.toString());
        init(view);

        listHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                List<ListVo> list = (List<ListVo>) msg.obj;
                Log.d("List!!List", list.toString());
                recyclerView.setAdapter(new RecyclerAdapter(getContext(), list, R.layout.recycler_item));
                setSpinnerEvent(list);
            }
        };
        GetListDataThread listThread = new GetListDataThread(listHandler, vo.getId());
        listThread.start();

        dataCallback.test(1);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dataCallback = (ListDataCallback) activity;
    }

    private void init(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        spinner = (Spinner) v.findViewById(R.id.spinner);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setSpinnerEvent(final List<ListVo> list) {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:

                        break;
                    case 2:
                        sortHandler=new Handler(){
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                final List<ListVo> listVo=(List<ListVo>)msg.obj;
                                recyclerView.setAdapter(new RecyclerAdapter(getContext(), listVo, R.layout.recycler_item));
                            }
                        };
                        sort = new SortingThread(2, list, sortHandler);
                        sort.start();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
