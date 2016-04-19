package com.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.read.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import adapter.OrderListAdapter;
import bean.OrderMessage;
import util.ParserJson;

public class OrderedActivity extends Activity {

    private String path = "http://192.168.0.160:8080/order/getUserOrders.htm?userId=";
    private int userID;
    private PullToRefreshListView plv;
    private RelativeLayout rl_progress,rl_layout;
    private ImageView iv_progress,iv_back;
    private TextView tv_no_order;
    private Animation animation;
    private List<OrderMessage> list = new ArrayList<>();
    private RequestQueue mQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered);
        userID = getIntent().getExtras().getInt("userID");
        path = path + userID;
        initView();

    }

    private void initView(){
        plv = (PullToRefreshListView) findViewById(R.id.lv_ordered);
        rl_progress = (RelativeLayout) findViewById(R.id.rl_progress);
        iv_progress = (ImageView) findViewById(R.id.iv_progress);
        tv_no_order = (TextView) findViewById(R.id.tv_no_order);
        rl_layout = (RelativeLayout) findViewById(R.id.layout);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderedActivity.this.finish();
            }
        });
        animation = AnimationUtils.loadAnimation(this,R.anim.progress);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        iv_progress.setAnimation(animation);
        animation.start();
        plv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        plv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }
        });
        getData();
    }

    private void getData(){
        mQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        rl_progress.setVisibility(View.GONE);
                        list.clear();
                        list.addAll(ParserJson.parserOrderedJson(response));
                        if(list.size() == 0){
                            tv_no_order.setVisibility(View.VISIBLE);
                        }else {
                            plv.setVisibility(View.VISIBLE);
                            plv.setAdapter(new OrderListAdapter(OrderedActivity.this, list));
                        }
                    }
                },
          new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(OrderedActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(stringRequest);


    }
}
