package ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
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

import adapter.MainBookListAdapter;
import bean.Books;
import util.ParserJson;

public class ClassifyActivity extends Activity {

    private TextView tv_title;
    private RelativeLayout iv_search, iv_back;
    private PullToRefreshListView listview;

    private String path = "http://api.manyanger.com:8101/novel/novelList.htm?pageNo=%d&theme=";
    private int id;
    private RequestQueue mQueue;
    private List<Books> list;
    private int currentPage = 0;
    private MainBookListAdapter adapter;

    private String lastJson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);
        initView();
        mQueue = Volley.newRequestQueue(this);
        tv_title.setText(getIntent().getExtras().getString("title"));
        id = getIntent().getExtras().getInt("ID");
        path = path + id;
        downLoadJson();
        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listview.setRefreshingLabel("加载中");
        listview.setReleaseLabel("松开手指加载更多");
        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                currentPage++;
                addView();
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("ID", list.get(position - 1 ).getId());
                intent.putExtras(bundle);
                intent.setClass(ClassifyActivity.this, DetailActivity.class);
                ClassifyActivity.this.startActivity(intent);
            }
        });

        iv_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ClassifyActivity.this.finish();
                }

                return true;
            }
        });

    }

    public void initView() {
        tv_title = (TextView) findViewById(R.id.title);
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        iv_search = (RelativeLayout) findViewById(R.id.iv_search);
        listview = (PullToRefreshListView) findViewById(R.id.listiew);
        list = new ArrayList<>();
        iv_search.setVisibility(View.GONE);
    }

    private void downLoadJson() {
        StringRequest stringRequest = new StringRequest(String.format(path, currentPage),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        lastJson = response;
                        list.addAll(ParserJson.parserClassifyJson(response));
                        adapter = new MainBookListAdapter(ClassifyActivity.this, list);
                        listview.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ClassifyActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
            }
        }
        );
        mQueue.add(stringRequest);

    }

    private void addView() {
        StringRequest stringRequest = new StringRequest(String.format(path, currentPage),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!lastJson.equals(response)) {
                            lastJson = response;
                            list.addAll(ParserJson.parserClassifyJson(response));
                            adapter.notifyDataSetChanged();
                            listview.onRefreshComplete();
                        }else{
                            Toast.makeText(ClassifyActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                            listview.onRefreshComplete();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ClassifyActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
            }
        }
        );
        mQueue.add(stringRequest);

    }

}
