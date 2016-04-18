package com.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.read.R;

import adapter.CatalogAdapter;
import bean.BookDetail;
import bean.CleanableEditText;
import bean.MyEvent;
import de.greenrobot.event.EventBus;
import util.BitmapCache;
import util.ParserJson;
import util.SetListHeight;

public class DetailActivity extends Activity {

    private TextView tv_auth, tv_theme, tv_process, tv_price, tv_depict, tv_title;
    private ImageView iv;
    private ListView catalog;
    private Button bt_favor, bt_buy;

    private String path = "http://api.manyanger.com:8101/novel/novelDetail.htm?id=";
    private int id;
    private RequestQueue mQueue;

    private BookDetail bd;

    private ImageView iv_back, iv_search, search_search, search_back;
    private TextView title;
    private CleanableEditText edittext;
    private LinearLayout ll_search, ll;
    private ScrollView sv;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean favorFlag = false;

    private RelativeLayout rl_progress;
    private LinearLayout ll_detail;
    private ImageView iv_progress;
    private Animation animation;

    private Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_detail);
        preferences=getSharedPreferences("shelf", Context.MODE_PRIVATE);
        editor=preferences.edit();
        mQueue = Volley.newRequestQueue(this);
        id = getIntent().getExtras().getInt("ID");
        path = path + id;
        initView();
        setProgress();
        downloadJson();

    }

    public void setProgress(){
        animation = AnimationUtils.loadAnimation(this, R.anim.progress);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        iv_progress.setAnimation(animation);
        animation.startNow();
    }

    private void initView() {

        bt = (Button) findViewById(R.id.footer);
        rl_progress = (RelativeLayout) findViewById(R.id.rl_progress);
        ll_detail = (LinearLayout) findViewById(R.id.ll_detail);
        iv_progress = (ImageView) findViewById(R.id.iv_progress);

        tv_auth = (TextView) findViewById(R.id.tv_auth);
        tv_theme = (TextView) findViewById(R.id.tv_theme);
        tv_process = (TextView) findViewById(R.id.tv_process);
        tv_depict = (TextView) findViewById(R.id.depict);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv = (ImageView) findViewById(R.id.imageview);
        catalog = (ListView) findViewById(R.id.catlog);
        bt_favor = (Button) findViewById(R.id.favor_btn);
        bt_buy = (Button) findViewById(R.id.buy_btn);
        sv = (ScrollView) findViewById(R.id.scrollView);

        title = (TextView) findViewById(R.id.title);
        title.setText("图书详情");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.this.finish();
            }
        });

        iv_search.setVisibility(View.INVISIBLE);

        int pre = Integer.parseInt(preferences.getString("" + id , "-1" ));
        if(pre == id){
            bt_favor.setText("已收藏");
            favorFlag = true;
        }

        bt_favor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favorFlag == true){
                    editor.remove(id + "");
                    editor.commit();
                    Toast.makeText(DetailActivity.this,"已取消收藏",Toast.LENGTH_SHORT).show();
                    bt_favor.setText("加入书架");

                }else{
                    editor.putString(""+id , id+"");
                    editor.commit();
                    Toast.makeText(DetailActivity.this,"已收藏",Toast.LENGTH_SHORT).show();
                    bt_favor.setText("已收藏");
                }
                favorFlag = !favorFlag;
                EventBus.getDefault().post(new MyEvent());
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this,MoreActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("ID",id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    private void downloadJson() {
        StringRequest stringRequest = new StringRequest(path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        bd = ParserJson.parserJson(response);
                        tv_theme.setText(bd.getTheme());
                        tv_process.setText(bd.getWords() + "字(" + bd.getProcess() + ")");
                        tv_depict.setText(bd.getDepict());
                        tv_auth.setText(bd.getAuthor());
                        tv_title.setText(bd.getTitle());
                        downloadImg();
                        catalog.setAdapter(new CatalogAdapter(bd.getBooklist(), DetailActivity.this));
                        SetListHeight.setListViewHeight(catalog);
                        sv.smoothScrollTo(0, 20);
                        rl_progress.setVisibility(View.GONE);
                        iv_progress.clearAnimation();
                        catalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(DetailActivity.this,ReadActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("ID", bd.getBooklist().get(position).getId_cha());
                                bundle.putString("title", bd.getBooklist().get(position).getTitle_cha());
                                bundle.putInt("position", position);
                                bundle.putInt("count", bd.getBooklist().size());
                                intent.putExtras(bundle);
                                startActivityForResult(intent, 2);
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(DetailActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(stringRequest);

    }

    private void downloadImg() {
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(iv,
                R.drawable.default_big_icon, R.drawable.default_big_icon);
        ImageLoader imageLoader = new ImageLoader(mQueue, BitmapCache.instance());
        imageLoader.get(bd.getCover(), listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){
            int position = data.getExtras().getInt("position");
            if(requestCode == 2 && resultCode == 1){
                Intent intent = new Intent(DetailActivity.this,ReadActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", bd.getBooklist().get(position).getTitle_cha());
                bundle.putInt("ID", bd.getBooklist().get(position).getId_cha());
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
            }
        }

    }

}
