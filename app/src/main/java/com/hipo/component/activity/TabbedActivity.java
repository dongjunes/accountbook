package com.hipo.component.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.hipo.callback.SettingDataCallback;
import com.hipo.component.service.MyService;
import com.hipo.model.NetworkTask2;
import com.hipo.utils.SectionsPagerAdapter;
import com.hipo.callback.ListDataCallback;
import com.hipo.lookie.R;
import com.hipo.model.pojo.UserVo;

import java.util.HashMap;
import java.util.Map;

public class TabbedActivity extends AppCompatActivity implements ListDataCallback, SettingDataCallback {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        exchangeServer();
        startService(vo);
        init(vo);

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
    }

    @Override
    public void test(int i) {
        Log.d("testCallback", i + "");
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
}
