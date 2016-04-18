package com.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.read.R;

import java.util.ArrayList;
import java.util.List;

import adapter.MainBookListAdapter;
import bean.Books;
import util.ParserJson;
import util.SetListHeight;

public class SearchActivity extends Activity {

    private ListView listview;
    private TextView title,tv_search_none;
    private ImageView iv_search, iv_back;

    private List<Books> list;
    private String path = "http://api.manyanger.com:8101/novel/novelList.htm?pageNo=0&keyWord=";

    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        mQueue = Volley.newRequestQueue(this);
        path = path + getIntent().getExtras().getString("search");
        downloadJson();
    }

    private void initView() {
        listview = (ListView) findViewById(R.id.listview);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        tv_search_none = (TextView) findViewById(R.id.tv_search_none);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.this.finish();
            }
        });
        iv_search.setVisibility(View.INVISIBLE);
        list = new ArrayList<>();
        title = (TextView) findViewById(R.id.title);
        title.setText("搜索结果");
    }

    private void downloadJson(){
        StringRequest stringRequest = new StringRequest(path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        list.addAll(ParserJson.parserClassifyJson(response));
                        if(list.size() == 0){
                            tv_search_none.setVisibility(View.VISIBLE);
                        }
                        listview.setAdapter(new MainBookListAdapter(SearchActivity.this, list));
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent();
                                intent.setClass(SearchActivity.this, DetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("ID", list.get(position).getId());
                                startActivity(intent);
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SearchActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(stringRequest);

    }
}
