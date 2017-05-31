package com.hipo.component.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.hipo.lookie.R;
import com.hipo.model.pojo.UserVo;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkpermission();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        AccessToken token = AccessToken.getCurrentAccessToken();

        if (AccessToken.getCurrentAccessToken() != null) {
            Log.d("LoginActivity login", "o : " + token.getUserId());
            Intent i = getIntent();
            i.putExtra("userId", token.getUserId());
            i.putExtra("loginDone", true);
            startActivity(i);
            finish();
        } else {
            Log.d("LoginActivity login", "x");
            LoginButton loginButton = (LoginButton) findViewById(R.id.loginbtn);
            loginButton.setReadPermissions("public_profile");

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d("LOGIN result", "1");
                    GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.d("LOGIN result", "2");
                            Log.d("LOGIN result", object.toString());
                            UserVo vo = new UserVo();
                            try {
                                vo.setId(object.get("id") + "");
                                vo.setName(object.get("name") + "");
                                vo.setPassword(vo.getId());
                                if (object.get("gender").equals(null)) {
                                    vo.setGender("성별미설정");
                                } else {
                                    vo.setGender(object.get("gender") + "");
                                }
                                vo.setAge(object.getJSONObject("age_range").get("max") + "");
                                if (vo.getAge().equals("")) {
                                    vo.setAge(object.getJSONObject("age_range").get("min") + "");

                                } else {
                                    Log.d("나이가 없습니다 ", "ㅎㅎ");
                                    vo.setAge("0");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d("LOGINuserVo", vo.toString());

                            Intent i = getIntent();
                            i.putExtra("userVo", vo);
                            i.putExtra("loginDone", false);
                            startActivity(i);
                            finish();
                        }
                    });

                    Bundle param = new Bundle();
                    param.putString("fields", "id,name,email,gender,age_range");
                    graphRequest.setParameters(param);
                    graphRequest.executeAsync();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {
                    Log.d("error message", error.getMessage());
                }
            });
        }


    }

    public Intent getIntent() {
        Intent intent = new Intent(this, TabbedActivity.class);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d("LOGIN result", "A");
    }

    public void checkpermission() {
         /* 사용자의 OS 버전이 마시멜로우 이상인지 체크한다. */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("permissionCheck", "1");
                    /* 사용자 단말기의 권한 중 "전화걸기" 권한이 허용되어 있는지 체크한다.
                    *  int를 쓴 이유? 안드로이드는 C기반이기 때문에, Boolean 이 잘 안쓰인다.
                    */
            int permissionResultSMS = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
            int permissionResultGPS = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

                    /* CALL_PHONE의 권한이 없을 때 */
            // 패키지는 안드로이드 어플리케이션의 아이디다.( 어플리케이션 구분자 )
            if (permissionResultSMS == PackageManager.PERMISSION_DENIED || permissionResultGPS == PackageManager.PERMISSION_DENIED) {

                Log.d("permissionCheck", "2");
                        /* 사용자가 CALL_PHONE 권한을 한번이라도 거부한 적이 있는 지 조사한다.
                        * 거부한 이력이 한번이라도 있다면, true를 리턴한다.
                        */
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)) {
                    Log.d("permissionCheck", "3");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    finish();

                }

                //최초로 권한을 요청할 때
                else {
                    // CALL_PHONE 권한을 Android OS 에 요청한다.
                    requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                }

            }
                    /* CALL_PHONE의 권한이 있을 때 */
            else {
                Log.d("permissionCheck", "success");
            }

        }
                /* 사용자의 OS 버전이 마시멜로우 이하일 떄 */
        else {
            Log.d("마쉬멜로 이하버전", ",");
        }
        Log.d("permissionCheck", "Method Check");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {

            /* 요청한 권한을 사용자가 "허용"했다면 인텐트를 띄워라
                내가 요청한 게 하나밖에 없기 때문에. 원래 같으면 for문을 돈다.*/
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("체크성공ㅇㅇㅇ", "");
                }
            } else {
                Toast.makeText(LoginActivity.this, "권한 요청을 거부했습니다.", Toast.LENGTH_SHORT).show();
            }

        }
    }

}

