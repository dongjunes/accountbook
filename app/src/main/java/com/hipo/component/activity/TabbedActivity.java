package com.hipo.component.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hipo.callback.ReflashListData;
import com.hipo.callback.SettingDataCallback;
import com.hipo.component.service.MyService;
import com.hipo.fragment.ListFragment;
import com.hipo.fragment.MyDialogFragment;
import com.hipo.model.NetworkTask2;
import com.hipo.model.pojo.AddedListVo;
import com.hipo.utils.SectionsPagerAdapter;
import com.hipo.callback.ListDataCallback;
import com.hipo.lookie.R;
import com.hipo.model.pojo.UserVo;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabbedActivity extends AppCompatActivity implements SettingDataCallback, ReflashListData {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private NetworkTask2 task2;
    private SharedPreferences pref;
    private UserVo vo = null;
    @BindView(R.id.add_icon)
    ImageView addIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        exchangeServer();
        startService(vo);
        init(vo);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.add_icon)
    public void addIconClick(View v) {
        Log.d("click!!!", "addicon");
        MyDialogFragment dialogFragment = new MyDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        dialogFragment.show(fm, "tags");
    }

    private void exchangeServer() {
        Intent i = getIntent();
        if (i.getBooleanExtra("loginDone", false)) {
            String userId = i.getStringExtra("userId");
            Log.d("로그인된상태", userId);
            task2 = new NetworkTask2(userId, 2);
            Map<String, String> params = new HashMap<String, String>();
            try {
                String result = task2.execute(params).get();
                Gson gson = new Gson();
                vo = gson.fromJson(result, UserVo.class);
                Log.d("vovovo", vo.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            vo = (UserVo) getIntent().getSerializableExtra("userVo");
            Log.d("로그인 안된상태", vo.toString());

            task2 = new NetworkTask2(vo.getId(), 1);
            Map<String, String> params = new HashMap<String, String>();
            params.put("id", vo.getId());
            params.put("gender", vo.getGender());
            params.put("name", vo.getName());
            params.put("email", vo.getEmail());
            params.put("password", vo.getPassword());
            params.put("age", vo.getAge());

            task2.execute(params);
        }
    }

    private void startService(UserVo vo) {
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        if (getPreferences() == false) {
            intent.putExtra("userVo", vo);
            Log.d("TabActivity", "servicesss : " + vo.toString());
            startService(intent);
            setPreferences(true);
        }

    }

    private void init(UserVo vo) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), vo);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.mipmap.list);
        tabLayout.getTabAt(1).setIcon(R.mipmap.loc);
        tabLayout.getTabAt(2).setIcon(R.mipmap.graph);
        tabLayout.getTabAt(3).setIcon(R.mipmap.setting);
    }


    private boolean getPreferences() {
        pref = getSharedPreferences("serviceBool", MODE_PRIVATE);
        return pref.getBoolean("boolean", false);
    }

    // 값 저장하기
    private void setPreferences(boolean bool) {
        pref = getSharedPreferences("serviceBool", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("boolean", bool);
        editor.commit();
    }

    @Override
    public void stopService() {
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        if (getPreferences() == true) {
            stopService(intent);
            setPreferences(false);
        }
    }

    @Override
    public void reflash(AddedListVo vo) {
        Log.d("새로고침", "어떡하냐");
        mSectionsPagerAdapter.reflashList();
    }
}
