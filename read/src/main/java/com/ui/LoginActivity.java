package com.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.read.R;
import com.example.administrator.read.ReadApplication;

import org.json.JSONException;
import org.json.JSONObject;

import bean.CleanableEditText;
import bean.LoginMessage;
import de.greenrobot.event.EventBus;

public class LoginActivity extends Activity {



    private Button bt_login,bt_register;
    private CleanableEditText et_username,et_password;

    private String path = "http://api.manyanger.com:8101/manyanger/login.htm?username=%s&password=%s";
    private RequestQueue mQueue;

    private ImageView iv_back;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mQueue = Volley.newRequestQueue(this);
        preferences=getSharedPreferences("login", Context.MODE_PRIVATE);
        editor=preferences.edit();
        init();
    }



    private void init(){
        bt_login = (Button) findViewById(R.id.button_login);
        bt_register = (Button) findViewById(R.id.button_register);
        et_username = (CleanableEditText) findViewById(R.id.login_username);
        et_password = (CleanableEditText) findViewById(R.id.login_password);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();
                path = String.format(path, username, password);
                StringRequest stringRequest = new StringRequest(path,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jo = new JSONObject(response);
                                    Log.i("lll",jo.getString("BackMessage"));
                                    String username = jo.getJSONObject("Member").getString("username");
                                    int userID = jo.getJSONObject("Member").getInt("id");
                                    if (jo.getString("BackMessage").equals("成功")) {
                                        Toast.makeText(LoginActivity.this, "欢迎回来　\n" + username, Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().post(new LoginMessage(username,userID));
                                        /**
                                         * 发送消息给fragmentmine
                                         * */
                                        ReadApplication.getInstance().setFlag(true);
                                        LoginActivity.this.finish();

                                    } else {
                                        Toast.makeText(LoginActivity.this, "用户名或者密码错误", Toast.LENGTH_SHORT).show();
                                        et_password.setText("");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(LoginActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                    }
                });
                mQueue.add(stringRequest);
            }
        });

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistActivity.class);
                startActivity(intent);
            }
        });

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });

    }
}
