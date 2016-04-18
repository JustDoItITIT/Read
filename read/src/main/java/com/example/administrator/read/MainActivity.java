package com.example.administrator.read;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.ui.FragmentClassify;
import com.ui.FragmentLogin;
import com.ui.FragmentMain;
import com.ui.FragmentShelf;
import com.ui.SearchActivity;

import bean.CleanableEditText;
import bean.ProgressEvent;
import de.greenrobot.event.EventBus;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private ImageView ll_main, ll_bookshelf, ll_classify, ll_more;
    private ViewPager vp;
    private List<Fragment> list;

    private ImageView iv_back, iv_search, search_search, search_back;
    private TextView title;
    private CleanableEditText edittext;
    private LinearLayout ll_search;
    private RelativeLayout rl_layout;


    /**
     * 加载
     */
    private LinearLayout main;
    private RelativeLayout rl_progress;
    private ImageView iv_progress;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        EventBus.getDefault().register(this);
        initView();
        ll_main.performClick();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {

        main = (LinearLayout) findViewById(R.id.ll_main);
        rl_progress = (RelativeLayout) findViewById(R.id.rl_progress);
        iv_progress = (ImageView) findViewById(R.id.iv_progress);


        title = (TextView) findViewById(R.id.title);
        title.setText("首页");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_back.setVisibility(View.GONE);

        search_back = (ImageView) findViewById(R.id.search_back);
        edittext = (CleanableEditText) findViewById(R.id.search_edittext);
        search_search = (ImageView) findViewById(R.id.search_search);
        ll_search = (LinearLayout) findViewById(R.id.search_layout);
        rl_layout = (RelativeLayout) findViewById(R.id.layout);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.animation_search);
                rl_layout.setVisibility(View.GONE);
                ll_search.setVisibility(View.VISIBLE);
                ll_search.setAnimation(animation);
                animation.startNow();
            }
        });

        search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_layout.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.animation_search_out);
                ll_search.setAnimation(animation);
                animation.startNow();
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_search.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        });
        search_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("search", edittext.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ll_main = (ImageView) findViewById(R.id.item_main);
        ll_bookshelf = (ImageView) findViewById(R.id.item_bookshelf);
        ll_classify = (ImageView) findViewById(R.id.item_classify);
        ll_more = (ImageView) findViewById(R.id.item_more);
        vp = (ViewPager) findViewById(R.id.viewPager);
        list = new ArrayList<>();
        list.add(new FragmentMain());
        list.add(new FragmentShelf());
        list.add(new FragmentClassify());
        list.add(new FragmentLogin());
        ll_main.setOnClickListener(this);
        ll_bookshelf.setOnClickListener(this);
        ll_classify.setOnClickListener(this);
        ll_more.setOnClickListener(this);
        vp.setAdapter(new adapter.PagerAdapter(getSupportFragmentManager(), list));
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        ll_main.performClick();
                        break;
                    case 1:
                        ll_bookshelf.performClick();
                        break;
                    case 2:
                        ll_classify.performClick();
                        break;
                    case 3:
                        ll_more.performClick();
                        break;
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }


        });
        vp.setOffscreenPageLimit(4);

        animation = AnimationUtils.loadAnimation(this, R.anim.progress);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        iv_progress.setAnimation(animation);
        animation.startNow();
    }

    @Override
    public void onClick(View v) {
        reset();
        switch (v.getId()) {
            case R.id.item_main:
                vp.setCurrentItem(0);
                ll_main.setImageResource(R.drawable.ic_recommand_pressed);
                title.setText("首页");
                break;
            case R.id.item_bookshelf:
                vp.setCurrentItem(1);
                ll_bookshelf.setImageResource(R.drawable.ic_bookshelf_pressed);
                title.setText("本地书架");
                break;
            case R.id.item_classify:
                vp.setCurrentItem(2);
                ll_classify.setImageResource(R.drawable.ic_hot_pressed);
                title.setText("分类阅读");
                iv_search.setVisibility(View.GONE);
                break;
            case R.id.item_more:
                vp.setCurrentItem(3);
                ll_more.setImageResource(R.drawable.ic_personal_pressed);
                title.setText("个人界面");
                iv_search.setVisibility(View.GONE);
                break;
            default:
                break;
        }


    }

    public void reset() {
        ll_main.setImageResource(R.drawable.ic_recommand_nor);
        ll_bookshelf.setImageResource(R.drawable.ic_bookshelf_nor);
        ll_classify.setImageResource(R.drawable.ic_hot_nor);
        ll_more.setImageResource(R.drawable.ic_personal_nor);
        iv_search.setVisibility(View.VISIBLE);
    }


    public void onEventMainThread(ProgressEvent event) {
        ll_main.setVisibility(View.VISIBLE);
        rl_progress.setVisibility(View.GONE);
        iv_progress.clearAnimation();
    }


}
