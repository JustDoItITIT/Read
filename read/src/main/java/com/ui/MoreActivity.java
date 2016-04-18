package com.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.read.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapter.CatalogAdapter;
import bean.BookDetailList;

public class MoreActivity extends Activity {

    private ImageView iv_back;
    private ListView lv_more;
    private List<BookDetailList> list;

    private String path = "http://api.manyanger.com:8101/novel/chapterList.htm?id=";
    private int id;
    private RequestQueue mQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        id = getIntent().getExtras().getInt("ID");
        mQueue = Volley.newRequestQueue(this);
        init();
        downloadData();
    }

    private void init(){
        iv_back = (ImageView) findViewById(R.id.iv_back);
        lv_more = (ListView) findViewById(R.id.more_listview);
        list = new ArrayList<>();
        path = path + id;
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreActivity.this.finish();
            }
        });
    }

    private void downloadData(){
        StringRequest stringRequest = new StringRequest(path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            JSONArray ja = jo.getJSONObject("chapterList").getJSONArray("result");
                            for (int i = 0;i < ja.length() ;i++){
                                BookDetailList bdl = new BookDetailList();
                                bdl.setTitle_cha(ja.getJSONObject(i).getString("title"));
                                bdl.setId_cha(ja.getJSONObject(i).getInt("id"));
                            list.add(bdl);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        lv_more.setAdapter(new CatalogAdapter(list,MoreActivity.this));
                        lv_more.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MoreActivity.this,ReadActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("title", list.get(position).getTitle_cha());
                                bundle.putInt("ID", list.get(position).getId_cha());
                                bundle.putInt("position", position);
                                bundle.putInt("count", list.size());
                                intent.putExtras(bundle);
                                startActivityForResult(intent, 2);
                            }
                        });
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MoreActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
            }
        }
        );
        mQueue.add(stringRequest);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int position = data.getExtras().getInt("position");
        if(requestCode == 2 && resultCode == 1){
            Intent intent = new Intent(MoreActivity.this,ReadActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("title", list.get(position).getTitle_cha());
            bundle.putInt("ID", list.get(position).getId_cha());
            bundle.putInt("position", position);
            intent.putExtras(bundle);
            startActivityForResult(intent, 2);
        }

    }
}
