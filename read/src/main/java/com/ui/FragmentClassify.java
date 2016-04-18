package com.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
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

import adapter.GridViewAdapter;
import bean.GVItem;
import util.ParserJson;

/**
 * Created by Administrator on 2016/4/11.
 */
public class FragmentClassify extends Fragment{

    private GridView gv;
    private List<GVItem> list;
    private String path = "http://api.manyanger.com:8101/novel/novelTheme.htm";
    private RequestQueue mQueue;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.classifyfragment,null);
        gv = (GridView) view.findViewById(R.id.gridview);
        list = new ArrayList<>();
        mQueue = Volley.newRequestQueue(getContext());
        downLoadJson();
        return view;
    }

    private void downLoadJson(){
        StringRequest stringRequest = new StringRequest(path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        list.addAll(ParserJson.parserGVJson(response));
                        gv.setAdapter(new GridViewAdapter(getContext(), list));
                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putInt("ID",list.get(position).getId());
                                bundle.putString("title", list.get(position).getName());
                                intent.putExtras(bundle);
                                intent.setClass(getActivity(),ClassifyActivity.class);
                                startActivity(intent);
                            }
                        });

                    }
                }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(getContext(), "未知错误", Toast.LENGTH_SHORT).show();
                        }
        }
                    );
                    mQueue.add(stringRequest);

    }
}
