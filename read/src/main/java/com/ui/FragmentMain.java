package com.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
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

import adapter.MainBookListAdapter;
import adbanner.ImagePagerAdapter;
import bannerview.ViewFlow;
import bean.Banner;
import bean.Books;
import bean.ProgressEvent;
import de.greenrobot.event.EventBus;
import util.ParserJson;
import util.SetListHeight;

/**
 * Created by Administrator on 2016/4/8.
 */
public class FragmentMain extends Fragment implements View.OnClickListener {

    private View view;
    private ListView lv_recommend, lv_competitive, lv_update;
    private Button bt_recommend, bt_competitive, bt_update;
    private String path = "http://api.manyanger.com:8101/novel/index.htm";
    private String banner_url = "http://api.manyanger.com:8101/novel/getAd.htm?type=Bookshelf&num=5";
    private RequestQueue mQueue;
    private List<Books> list_recommend, list_competitive, list_update;
    private List<ImageView> list_iv;
    private List<Banner> list_banner;

    private ViewFlow mViewFlow;
    private ArrayList<String> imageUrlList = new ArrayList<String>();
    ArrayList<String> linkUrlArray = new ArrayList<String>();
    ArrayList<String> titleList = new ArrayList<String>();


    private ScrollView sv;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentmain, null);
        init();
        initView();
        mQueue = Volley.newRequestQueue(getContext());
        downloadJson();
        return view;
    }

    private void initView() {
        mViewFlow = (ViewFlow) view.findViewById(R.id.banner);
        lv_recommend = (ListView) view.findViewById(R.id.listview_recommend);
        lv_competitive = (ListView) view.findViewById(R.id.listview_competitive);
        lv_update = (ListView) view.findViewById(R.id.listview_update);
        bt_recommend = (Button) view.findViewById(R.id.bt_recommend);
        bt_competitive = (Button) view.findViewById(R.id.bt_competitive);
        bt_update = (Button) view.findViewById(R.id.bt_update);
        bt_recommend.setOnClickListener(this);
        bt_competitive.setOnClickListener(this);
        bt_update.setOnClickListener(this);

        sv = (ScrollView) view.findViewById(R.id.scrollView);

        setViewPagerAdapter();
    }

    private void initBanner(ArrayList<String> imageUrlList) {

        mViewFlow.setAdapter(new ImagePagerAdapter(getContext(), imageUrlList,
                linkUrlArray, titleList).setInfiniteLoop(true));
        mViewFlow.setmSideBuffer(imageUrlList.size()); // 实际图片张数，

        mViewFlow.setTimeSpan(4500);
        mViewFlow.setSelection(imageUrlList.size() * 1000); // 设置初始位置
        mViewFlow.startAutoFlowTimer(); // 启动自动播放
    }

    private void init() {
        list_recommend = new ArrayList<>();
        list_competitive = new ArrayList<>();
        list_update = new ArrayList<>();
        list_iv = new ArrayList<>();
        list_banner = new ArrayList<>();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_recommend:

                break;
            case R.id.bt_competitive:
                break;
            case R.id.bt_update:
                break;
            default:
                break;

        }
    }

    private void downloadJson() {
        StringRequest stringRequest = new StringRequest(path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        list_recommend.addAll(ParserJson.parserJson(response, "recommendNovelList"));
                        list_competitive.addAll(ParserJson.parserJson(response, "featuredNovelList"));
                        list_update.addAll(ParserJson.parserJson(response, "latestNovelList"));
                        lv_recommend.setAdapter(new MainBookListAdapter(getContext(), list_recommend));
                        lv_competitive.setAdapter(new MainBookListAdapter(getContext(), list_competitive));
                        lv_update.setAdapter(new MainBookListAdapter(getContext(), list_update));
                        SetListHeight.setListViewHeight(lv_update);
                        SetListHeight.setListViewHeight(lv_competitive);
                        SetListHeight.setListViewHeight(lv_recommend);
                        sv.smoothScrollTo(0, 20);
                        EventBus.getDefault().post(new ProgressEvent());
                        lv_competitive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putInt("ID", list_competitive.get(position).getId());
                                intent.putExtras(bundle);
                                intent.setClass(getContext(), DetailActivity.class);
                                getContext().startActivity(intent);
                            }
                        });
                        lv_recommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putInt("ID", list_recommend.get(position).getId());
                                intent.putExtras(bundle);
                                intent.setClass(getContext(), DetailActivity.class);
                                getContext().startActivity(intent);
                            }
                        });
                        lv_update.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putInt("ID", list_update.get(position).getId());
                                intent.putExtras(bundle);
                                intent.setClass(getContext(), DetailActivity.class);
                                getContext().startActivity(intent);
                            }
                        });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(), "未知错误", Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(stringRequest);

    }


    public void setViewPagerAdapter() {

        final RequestQueue mQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(banner_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray ja = new JSONObject(response).getJSONArray("adList");
                            for (int i = 0; i < ja.length(); i++) {
                                Banner banner = new Banner();
                                banner.setImage(ja.getJSONObject(i).getString("image"));
                                banner.setUrl(ja.getJSONObject(i).getString("url"));
                                list_banner.add(banner);
                                imageUrlList.add(ja.getJSONObject(i).getString(
                                        "image"));
                                linkUrlArray.add(ja.getJSONObject(i).getString(
                                        "url"));
                                titleList.add("");
                            }
                            initBanner(imageUrlList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                }

                , new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(), "未知错误", Toast.LENGTH_SHORT).show();
            }
        }

        );
        mQueue.add(stringRequest);


    }

}
