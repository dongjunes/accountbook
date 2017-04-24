package com.hipo.lookie;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class TabbedActivity extends AppCompatActivity implements ListDataCallback {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        UserVo vo = null;
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
        init(vo);

    }

    @Override
    public void test(int i) {
        Log.d("testCallback", i + "");
    }

    public class NetworkTask2 extends AsyncTask<Map<String, String>, Integer, String> {
        private String id;
        private int receive = 0;
        private UserVo userVo = null;

        public NetworkTask2(String id, int receive) {
            this.id = id;
            this.receive = receive;
        }

        @Override
        protected String doInBackground(Map<String, String>... maps) { // 내가 전송하고 싶은 파라미터

// Http 요청 준비 작업
            HttpClient.Builder http;
            if (receive == 1) {
                http = new HttpClient.Builder("POST", "http://192.168.1.14:8088/account-book/android/" + id + "/join");

            } else {
                http = new HttpClient.Builder("POST", "http://192.168.1.14:8088/account-book/android/" + id + "/login");
            }
// Parameter 를 전송한다.
            http.addAllParameters(maps[0]);


//Http 요청 전송
            HttpClient post = http.create();
            post.request();

// 응답 상태코드 가져오기
            int statusCode = post.getHttpStatusCode();

// 응답 본문 가져오기
            String body = post.getBody();

            return body;
        }

        @Override
        protected void onPostExecute(String s) {
            if (receive == 1) {

            } else {
                Log.d("ReceiveDataPOST", s);
            }
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private UserVo vo;

        public SectionsPagerAdapter(FragmentManager fm, UserVo vo) {
            super(fm);
            this.vo = vo;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return ListFragment.newInstance(position + 1, vo);
                case 1:
                    return MapFragment.newInstance(position + 2);
                case 2:
                    return ChartFragment.newInstance(position + 3);
                case 3:
                    return SettingFragment.newInstance(position + 4);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
                case 3:
                    return "SECTION 4";

            }
            return null;
        }
    }
}
