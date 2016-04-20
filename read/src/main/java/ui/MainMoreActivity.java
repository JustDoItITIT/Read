package ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
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

public class MainMoreActivity extends Activity {

    private String path = "http://api.manyanger.com:8101/novel/indexList.htm?type=%d&pageNo=%d";
    private TextView tv_title;
    private RelativeLayout iv_back;
    private PullToRefreshListView lv;
    private List<Books> list = new ArrayList<>();
    private RequestQueue mQueue;
    private int currentPage = 0;
    private String title;
    private int type = 0;

    private String lastJson = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_more);
        title = getIntent().getExtras().getString("title");
        type = getIntent().getExtras().getInt("type");
        init();
        getData();
        lv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                currentPage++;
                getData();
            }
        });
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.title);
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        lv = (PullToRefreshListView) findViewById(R.id.prListView);
        mQueue = Volley.newRequestQueue(this);
        tv_title.setText(title);
        iv_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    MainMoreActivity.this.finish();
                }
                return true;
            }
        });
    }


    private void getData() {
        path = String.format(path, type, currentPage);
        StringRequest stringRequest = new StringRequest(path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(lastJson.equals(response)){
                            Toast.makeText(getApplicationContext(), "已无更多", Toast.LENGTH_SHORT).show();
                            lv.onRefreshComplete();
                        }else {
                            lastJson = response;
                            list.addAll(ParserJson.parserMoreJson(response));
                            lv.setAdapter(new MainBookListAdapter(MainMoreActivity.this, list));
                            lv.onRefreshComplete();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "未知错误", Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(stringRequest);
    }

}
