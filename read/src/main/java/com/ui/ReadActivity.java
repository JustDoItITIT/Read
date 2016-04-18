package com.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.read.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ReadActivity extends Activity {

    private ImageView iv_back;
    private String title;
    private int id;
    private TextView tv_title, mBookContent;
    private RequestQueue mQueue;
    private String path = "http://api.manyanger.com:8101/novel/novelRead.htm?chapterId=";

    private Button bt_last,bt_next;
    private int position,count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        mQueue = Volley.newRequestQueue(this);
        title = getIntent().getExtras().getString("title");
        id = getIntent().getExtras().getInt("ID");
        position = getIntent().getExtras().getInt("position");
        count = getIntent().getExtras().getInt("count");
        path = path + id;
        init();
        getData();
    }

    private void init(){
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.chTitle);
        mBookContent = (TextView) findViewById(R.id.book_content);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadActivity.this.finish();
            }
        });
        tv_title.setText(title);

        bt_last = (Button) findViewById(R.id.bt_last);
        bt_next = (Button) findViewById(R.id.bt_next);

        bt_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position == 0){
                    Toast.makeText(ReadActivity.this,"已是第一章",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position - 1);
                    intent.putExtras(bundle);
                    setResult(1, intent);
                    ReadActivity.this.finish();
                }
            }
        });

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(position == count - 1){
                        Toast.makeText(ReadActivity.this,"已是最后一章",Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position + 1);
                        intent.putExtras(bundle);
                        setResult(1, intent);
                        ReadActivity.this.finish();
                    }
            }
        });

    }

    private void getData(){
        StringRequest stringRequest = new StringRequest(path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            mBookContent.setText(Html.fromHtml(jo.getJSONObject("chapter").getString("content")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        mQueue.add(stringRequest);


    }
}
