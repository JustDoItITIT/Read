package ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.read.R;

import org.json.JSONException;
import org.json.JSONObject;

import bean.CleanableEditText;

public class RegistActivity extends Activity {

    private Button bt_reginst;
    private RelativeLayout iv_back;
    private CleanableEditText et_username,et_phone,et_password;

    private RequestQueue mQueue;
    private String path = "http://api.manyanger.com:8101/manyanger/register.htm?email=%s&password=%s&phone=%s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        mQueue = Volley.newRequestQueue(this);
        initView();

    }


    private void initView(){
        bt_reginst = (Button) findViewById(R.id.regist_button);
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        et_username = (CleanableEditText) findViewById(R.id.regist_username);
        et_password = (CleanableEditText) findViewById(R.id.regist_password);
        et_phone = (CleanableEditText) findViewById(R.id.regist_phone);
        iv_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    RegistActivity.this.finish();
                }
                return true;
            }
        });
        bt_reginst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path = String.format(path, et_username.getText().toString(), et_password.getText().toString(), et_phone.getText().toString());
                StringRequest stringRequest = new StringRequest(path,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jo = new JSONObject(response);
                                    if (jo.getString("BackMessage").equals("成功")) {
                                        Toast.makeText(RegistActivity.this, "注册成功，您的用户名是：\n" + jo.getJSONObject("Member").getString("username"), Toast.LENGTH_LONG).show();
                                        RegistActivity.this.finish();
                                    } else {
                                        Toast.makeText(RegistActivity.this, "该邮箱已被注册", Toast.LENGTH_SHORT).show();
                                        et_password.setText("");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(RegistActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                    }
                });
                mQueue.add(stringRequest);
            }
        });
    }
}
