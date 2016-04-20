package ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.administrator.read.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import adapter.MainBookListAdapter;
import bean.Books;
import util.ParserJson;

public class SearchActivity extends Activity{

    private PullToRefreshListView listview;
    private TextView title,tv_search_none;
    private RelativeLayout iv_search, iv_back;

    private List<Books> list;
    private String path = "http://api.manyanger.com:8101/novel/novelList.htm?pageNo=%d&keyWord=";

    private int currentPage = 0;
    private RequestQueue mQueue;

    private String lastJson = "";
    private MainBookListAdapter adapter;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                downloadJson(msg.getData().getString("response"));
            }else if(msg.what == 1){
                Toast.makeText(getApplicationContext(),"已无更多",Toast.LENGTH_SHORT).show();
            }else if(msg.what == 2){
                addData(msg.getData().getString("response"));
            }
            listview.onRefreshComplete();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        mQueue = Volley.newRequestQueue(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String a = null;
                try {
                    a = download(path);
                    if(lastJson.equals(a)){
                        handler.sendEmptyMessage(1);
                    }else{
                        lastJson = a;
                        Message msg = new Message();
                        msg.what = 0;
                        Bundle bundle = new Bundle();
                        bundle.putString("response", a);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private void initView() {
        listview = (PullToRefreshListView) findViewById(R.id.listview);
        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                currentPage++;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String a = null;
                        try {
                            a = download(path);
                            if (lastJson.equals(a)) {
                                handler.sendEmptyMessage(1);
                            } else {
                                lastJson = a;
                                Message msg = new Message();
                                msg.what = 2;
                                Bundle bundle = new Bundle();
                                bundle.putString("response", a);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        iv_search = (RelativeLayout) findViewById(R.id.iv_search);
        tv_search_none = (TextView) findViewById(R.id.tv_search_none);
        iv_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    SearchActivity.this.finish();
                }
                return true;
            }
        });
        iv_search.setVisibility(View.INVISIBLE);
        list = new ArrayList<>();
        title = (TextView) findViewById(R.id.title);
        title.setText("搜索结果");
    }

    private void downloadJson(String json){
                        list.addAll(ParserJson.parserClassifyJson(json));
                        if(list.size() == 0){
                            tv_search_none.setVisibility(View.VISIBLE);
                        }
                        adapter = new MainBookListAdapter(SearchActivity.this, list);
                        listview.setAdapter(adapter);
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent();
                                intent.setClass(SearchActivity.this, DetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("ID", list.get(position).getId());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    }

    private void addData(String json){
        list.addAll(ParserJson.parserClassifyJson(json));
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
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private String download(String path)throws IOException{
        path = String.format(path,currentPage);
        path = path + getIntent().getExtras().getString("search");
        try {
            path = new String(path.getBytes(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(path)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);
        return response.body().string();
    }


}
