package com.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.read.R;
import com.example.administrator.read.ReadApplication;

import bean.LoginMessage;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/4/12.
 */
public class FragmentLogin extends Fragment {

    private View view;
    private LinearLayout ll_login, ll_unlogin;
    private Button bt_login, bt_regist, bt_order, bt_esc, bt_about;
    private TextView tv_username;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Boolean isLogined = false;
    private int userID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, null);
        EventBus.getDefault().register(this);
        preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = preferences.edit();
        initView();
        return view;
    }

    private void initView() {
        bt_order = (Button) view.findViewById(R.id.bt_orders);
        bt_esc = (Button) view.findViewById(R.id.bt_esc);
        bt_about = (Button) view.findViewById(R.id.bt_about);

        ll_login = (LinearLayout) view.findViewById(R.id.layout_logined);
        ll_unlogin = (LinearLayout) view.findViewById(R.id.layout_unlogin);
        bt_login = (Button) view.findViewById(R.id.login_button);
        bt_regist = (Button) view.findViewById(R.id.regist_button);
        tv_username = (TextView) view.findViewById(R.id.tv_username);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        bt_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegistActivity.class);
                startActivity(intent);
            }
        });
        bt_esc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialog.Builder builder = new ConfirmDialog.Builder(getContext());
                builder.setMessage("确定要退出登陆吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ReadApplication.getInstance().setFlag(false);
                        ll_login.setVisibility(View.GONE);
                        ll_unlogin.setVisibility(View.VISIBLE);
                    }
                });

                builder.setNegativeButton("取消",
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.create().show();
            }
        });


        isLogined = ReadApplication.getInstance().getFlag();
        if (isLogined) {
            tv_username.setText("用戶名  ： " + preferences.getString("username", "null"));
            userID = preferences.getInt("userID", 0);
            ll_login.setVisibility(View.VISIBLE);
            ll_unlogin.setVisibility(View.GONE);
        } else {
            ll_unlogin.setVisibility(View.VISIBLE);
        }

        bt_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
        bt_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("userID", userID);
                intent.putExtras(bundle);
                intent.setClass(getActivity(), OrderedActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(LoginMessage event) {
        tv_username.setText(event.getUsername());
        ll_login.setVisibility(View.VISIBLE);
        ll_unlogin.setVisibility(View.GONE);
        userID = event.getUserID();
    }
}












