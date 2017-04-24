package com.hipo.lookie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                                vo.setEmail(object.get("email") + "");
                                vo.setGender(object.get("gender") + "");
                                vo.setAge(object.getJSONObject("age_range").get("max") + "");
                                Log.d("userVo", vo.toString());
                            } catch (JSONException e) {

                            }
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
}
