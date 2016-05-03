package ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.Map;

import adapter.CatalogAdapter;
import bean.BookDetailList;

public class MoreActivity extends Activity {

    private RelativeLayout iv_back;
    private ListView lv_more;
    private List<BookDetailList> list;

    private String path = "http://api.manyanger.com:8101/novel/chapterList.htm?id=%d&pageNo=%d";
    private int id;
    private RequestQueue mQueue;
    private List<String> results = new ArrayList<>();
    private SharedPreferences sp_record;
    private SharedPreferences.Editor ed_record;
    private boolean buyed = false;
    private List<String> list_id = new ArrayList<>();
    private String result;
    private String name;

    private int currentPage = 0;
    private CatalogAdapter adapter;
    private  boolean flag = true;
    private int performClick = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        id = getIntent().getExtras().getInt("ID");
        performClick = getIntent().getExtras().getInt("performClick",6);
        sp_record = getSharedPreferences("buy_record", Context.MODE_PRIVATE);
        ed_record = sp_record.edit();
        mQueue = Volley.newRequestQueue(this);
        init();
        downloadData();
    }

    private void performClick(){
        if(performClick == 6){

        }else if(performClick >= 0 && performClick < 6){
            jump(performClick);
        }
    }

    private void init(){
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        lv_more = (ListView) findViewById(R.id.more_listview);
        list = new ArrayList<>();
        adapter = new CatalogAdapter(list, MoreActivity.this);
        lv_more.setAdapter(adapter);

        iv_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    MoreActivity.this.finish();
                }
                return true;
            }
        });
        /**
         * 获取sp中的数据 遍历书本ID 查看购买记录
         * 改变按钮
         * */

        Map<String,?> map = sp_record.getAll();
        for (String key : map.keySet()) {
            list_id.add(key);
        }
        name = id + "";
        for (int i = 0 ; i < list_id.size() ; i ++ ){
            if(name.equals(list_id.get(i))){
                result = sp_record.getString(name,"null");
                if("all".equals(result)){
                    buyed = true;
                }else{
                    String[] a = result.split(",");
                    for (int j = 0 ; j < a.length ; j ++)
                    {
                        results.add(a[j]);
                    }
                    results.remove(0);
                }
            }
        }}

    private void downloadData(){
        String p = String.format(path,id,currentPage);
        StringRequest stringRequest = new StringRequest(p,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            JSONArray ja = jo.getJSONObject("chapterList").getJSONArray("result");
                            flag =  jo.getJSONObject("chapterList").getBoolean("next");
                            for (int i = 0;i < ja.length() ;i++){
                                BookDetailList bdl = new BookDetailList();
                                bdl.setTitle_cha(ja.getJSONObject(i).getString("title"));
                                bdl.setId_cha(ja.getJSONObject(i).getInt("id"));
                            list.add(bdl);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        currentPage ++;
                        if(flag){
                            downloadData();
                        }else{
                            for(int i = 0 ; i < results.size();i ++){
                                int a = Integer.parseInt(results.get(i));
                                setFlag(a,adapter);
                            }
                            for (int i = 0 ; i < list_id.size() ; i ++ ){
                                if(name.equals(list_id.get(i))){
                                    result = sp_record.getString(name,"null");
                                    if("all".equals(result)){
                                        for (int j = 0 ; j < list.size();j++){
                                            list.get(j).setFlag(true);
                                        }
                                    }
                                }
                            }
                            Log.i("size",list.size() + "");
                            Log.i("position",performClick + "");
                            performClick();
                        }
                        adapter.notifyDataSetChanged();

                        lv_more.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                couldJump(position);

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

    public void couldJump(final int position){
        {
            /**
             * 第一章免费 后面收费
             * */
            if(buyed){
                jump(position);
            }
            else if(position <= 2){
                jump(position);
            }
            else{
                boolean flag = false;
                if(results.size() > 0){
                    for (int i = 0 ; i < results.size() ; i ++){
                        int p = Integer.parseInt(results.get(i));
                        if(p == position){
                            jump(position);
                            flag = true;
                        }
                    }
                }
                if(!flag){
                    ConfirmDialog.Builder builder = new ConfirmDialog.Builder(MoreActivity.this);
                    builder.setMessage("确定购买本章节内容吗？");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            StringBuilder sb = new StringBuilder();
                            sb.append(result);
                            sb.append("," + position);

                            String b[] = sb.toString().split(",");
                            results.clear();
                            for (int j = 0; j < b.length; j++) {
                                results.add(b[j]);
                            }
                            results.remove(0);
                            for(int i = 0 ; i < results.size();i ++){
                                int a = Integer.parseInt(results.get(i));
                                setFlag(a,adapter);
                            }
                            Log.i("fuck",results.toString());
                            ed_record.putString(name, sb.toString());
                            ed_record.commit();
                            jump(position);
                        }
                    });

                    builder.setNegativeButton("取消",
                            new android.content.DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    builder.create().show();
                }}

        }
    }

    public void jump(int position){
        Intent intent = new Intent(MoreActivity.this, ReadActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", list.get(position).getTitle_cha());
        bundle.putInt("ID", list.get(position).getId_cha());
        bundle.putInt("position", position);
        bundle.putInt("count", list.size());
        bundle.putInt("catlogId",id);
        intent.putExtras(bundle);
        startActivityForResult(intent, 2);
    }

    public void setFlag(int position,CatalogAdapter adapter){
        list.get(position).setFlag(true);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 2 && resultCode == 1 && data != null){
            int position = data.getExtras().getInt("position");
            couldJump(position);
        }
    }
}
