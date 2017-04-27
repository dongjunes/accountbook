package com.hipo.model;

import android.os.AsyncTask;
import android.util.Log;

import com.hipo.model.pojo.UserVo;
import com.hipo.network.HttpClient;

import java.util.Map;

/**
 * Created by dongjune on 2017-04-25.
 */

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
            Log.d("회원가입하고싶어요", "ㅎㅎㅎ");
            http = new HttpClient.Builder("POST", "http://192.168.1.14:8088/account-book/android/" + id + "/join");
        } else if (receive == 3) {
            http = new HttpClient.Builder("POST", "http://192.168.1.14:8088/account-book/android/" + id + "/addList");
        }else if(receive==4){
            http = new HttpClient.Builder("POST", "http://192.168.1.14:8088/account-book/android/" + id + "/getList");
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