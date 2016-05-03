package ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.BlackCatlogAdapter;
import bean.BatteryView;
import bean.BookDetailList;
import bean.ReadView;

public class ReadActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    //    private RelativeLayout iv_back;
    private String title;
    private int id, catlogID;
    private TextView tv_title;
    private RequestQueue mQueue;
    private String path = "http://api.manyanger.com:8101/novel/novelRead.htm?chapterId=", catlogpath = "http://api.manyanger.com:8101/novel/chapterList.htm?id=";
    private int p, count;
    private boolean isDA = true;

    private MViewPager vp;
    private Animation animation;
    private RelativeLayout rl_progress;
    private ImageView iv_progress;
    private RelativeLayout rl_read, iv_back;
    private String ss;
    private List<ReadView> list = new ArrayList<>();
    private boolean isEnd = false, isHead = false; // vp 判断在哪一页

    private PagerAdapter adapter;
    private LinearLayout rl_toolbar;
    private RelativeLayout rl_top, rl_catlog;
    private Button bt_last, bt_next, bt_catlog, bt_light, bt_setting, bt_progress;
    private SeekBar sb_pg, sb_light;
    private TextView tv_progress;
    private int mSize = 0;
    private boolean bt_status = true; // toolbar两个按钮状态
    private int currentLight;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Animation animation_top_show, animation_button_show, animation_top_dismiss, animation_button_dismiss;

    private boolean isShow = false; // toolbar 是否顯示

    private Button bt_cg, bt_mk;
    private ImageView iv_cancle;

    //目录
    private ListView lv_cg;
    private List<BookDetailList> catlogList = new ArrayList<>();
    private boolean hasNext = false;
    private int catlogPage = 0;
    private boolean cgIsShow = false;

    //书签
    private ListView lv_mk;
//    private TextView nomark;
//    private Button markManager;
//    private RelativeLayout iv_mark;
//    private SharedPreferences mPreferences;
//    private SharedPreferences.Editor mEditor;

    //setting
    private LinearLayout ll_setting;
    private RelativeLayout rl_small, rl_big, rl_bg1, rl_bg2, rl_bg3, rl_bg4,bg, rl_seekbar;
    private Button bt_comfirm;
    private TextView eWord;
    private int currentHint;
    private String mResponse;
    private int mColor;

    //mode
    private ImageView iv_mode;
    private boolean mode = false;

    private float downPostionX = 0;

    //msg head bt
    private TextView tv_title_msg, tv_chapter_msg, tv_time_msg;
    private BatteryView bw;
    float x = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                mSize = list.size();
                rl_read.setVisibility(View.GONE);
                rl_progress.setVisibility(View.GONE);
                setVp();
                initSb();
                sb_light.setVisibility(View.INVISIBLE);
                sb_pg.setVisibility(View.VISIBLE);
                tv_progress.setVisibility(View.VISIBLE);
                tv_chapter_msg.setText("1/" + list.size() + "页");
                switch (mColor) {
                    case 0:
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).setBackgroundColor(getResources().getColor(R.color.bg_1));
                        }
                        break;
                    case 1:
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).setBackgroundResource(R.drawable.readset_reader_bg_1);
                        }
                        break;
                    case 2:
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).setBackgroundColor(getResources().getColor(R.color.bg_3));
                        }
                        break;
                    case 3:
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).setBackgroundColor(getResources().getColor(R.color.bg_4));
                        }
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏

        setContentView(R.layout.activity_read);
        preferences = getSharedPreferences("read_setting", Context.MODE_PRIVATE);
        editor = preferences.edit();

        currentLight = preferences.getInt("light", 80);
        setScreenBrightness(this, currentLight);
        currentHint = preferences.getInt("hint", 17);
        mColor = preferences.getInt("color", 2);
        mode = preferences.getBoolean("mode", false);
        mQueue = Volley.newRequestQueue(this);
        vp = (MViewPager) findViewById(R.id.read_vp);
        initView();
        title = getIntent().getExtras().getString("title");
        id = getIntent().getExtras().getInt("ID");
        catlogID = getIntent().getExtras().getInt("catlogId");
        p = getIntent().getExtras().getInt("position");
        count = getIntent().getExtras().getInt("count");
        isDA = getIntent().getExtras().getBoolean("detailactivity", false);
        path = path + id;
        catlogpath = catlogpath + catlogID;
        init();
        getData();
        getCatlog();
    }

    private void setCatlogList() {
        lv_cg.setSelection(p);

        lv_cg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                setResult(1, intent);
                ReadActivity.this.finish();
            }
        });
    }

    private void getCatlog() {

        StringRequest stringRequest = new StringRequest(catlogpath + "&pageNo=" + catlogPage,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            JSONObject joc = jo.getJSONObject("chapterList");
                            JSONArray ja = joc.getJSONArray("result");
                            hasNext = jo.getJSONObject("chapterList").getBoolean("next");
                            for (int i = 0; i < ja.length(); i++) {
                                BookDetailList bdl = new BookDetailList();
                                bdl.setTitle_cha(ja.getJSONObject(i).getString("title"));
                                bdl.setId_cha(ja.getJSONObject(i).getInt("id"));
                                catlogList.add(bdl);
                            }
                            catlogPage++;
                            if (hasNext) {
                                getCatlog();
                            } else {
                                catlogList.get(p).setFlag(true);
                                lv_cg.setAdapter(new BlackCatlogAdapter(catlogList, ReadActivity.this));
                                setCatlogList();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        mQueue.add(stringRequest);
    }

    private void getNewView(int size) {
        rl_progress.setVisibility(View.VISIBLE);
        list.clear();
        rl_read.setVisibility(View.VISIBLE);
        newView(mResponse, size);
    }

    private void newView(final String res, final int size) {
        final ReadView tv = new ReadView(this);
        tv.setText(res);
        tv.setTextSize(size);
        tv.setTextColor(getResources().getColor(R.color.text_color));
        rl_read.removeAllViews();
        rl_read.addView(tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    isShow = false;
                    rl_top.clearAnimation();
                    rl_toolbar.clearAnimation();
                    rl_top.setAnimation(animation_top_dismiss);
                    animation_top_dismiss.startNow();
                    rl_toolbar.setAnimation(animation_button_dismiss);
                    animation_button_dismiss.startNow();
                    iv_mode.setVisibility(View.GONE);
                    animation_top_dismiss.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            rl_top.setVisibility(View.GONE);
                            rl_toolbar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    isShow = true;
                    rl_top.setVisibility(View.VISIBLE);
                    rl_toolbar.setVisibility(View.VISIBLE);
                    iv_mode.setVisibility(View.VISIBLE);
                    rl_top.setAnimation(animation_top_show);
                    animation_top_show.startNow();
                    rl_toolbar.setAnimation(animation_button_show);
                    animation_button_show.startNow();

                }
                if (cgIsShow) {
                    iv_cancle.performClick();
                }

            }
        });
        tv.setPadding(10, getResources().getDimensionPixelSize(R.dimen.top_margin), 10, getResources().getDimensionPixelSize(R.dimen.top_margin));
        list.add(tv);
        //在ReadView绘制完成后才能获得其中的内容
        tv.post(new Runnable() {

                    @Override
                    public void run() {
                        //如果当前页面中的字数不等于总字数时
                        if (!(tv.getCharNum() == res.length())) {
                            //截掉已经显示的内容
                            ss = res.substring(tv.getCharNum());
                            //继续添加
                            newView(ss, size);

                        } else if (tv.getCharNum() == res.length()) {
                            setAdapter();
                            vp.setAdapter(adapter);
                            rl_read.setVisibility(View.GONE);
                            rl_progress.setVisibility(View.GONE);
                            mSize = list.size();
                            initSb();
                        }
                    }
                }
        );

    }

    private void setAdapter() {
        adapter = new android.support.v4.view.PagerAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                if (list.get(position).getParent() != null) {
                    RelativeLayout rl = (RelativeLayout) (list.get(position).getParent());
                    rl.removeAllViews();
                }
                container.addView(list.get(position));
                return list.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                if (list.size() > 2)
                    container.removeView(list.get(position));
            }


        };
    }

    private void initView() {
        tv_chapter_msg = (TextView) findViewById(R.id.tv_chapter_msgbottom);
        tv_time_msg = (TextView) findViewById(R.id.tv_time_msgbottom);
        tv_title_msg = (TextView) findViewById(R.id.tv_title_msgtop);
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        if (minute < 10) {
            tv_time_msg.setText(hour + ":0" + minute);
        } else {
            tv_time_msg.setText(hour + ":" + minute);
        }
        bw = (BatteryView) findViewById(R.id.battery);
        rl_progress = (RelativeLayout) findViewById(R.id.rl_progress);
        iv_progress = (ImageView) findViewById(R.id.iv_progress);
        animation = AnimationUtils.loadAnimation(ReadActivity.this, R.anim.progress);
        rl_read = (RelativeLayout) findViewById(R.id.rl_read);
        iv_back = (RelativeLayout) findViewById(R.id.iv_back);
        rl_catlog = (RelativeLayout) findViewById(R.id.catlog_layout);
        rl_catlog.setVisibility(View.GONE);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        iv_progress.setAnimation(animation);
        animation.startNow();
        rl_toolbar = (LinearLayout) findViewById(R.id.rl_toolbar);
        rl_top = (RelativeLayout) findViewById(R.id.top);
        bt_last = (Button) findViewById(R.id.last);
        bt_next = (Button) findViewById(R.id.next);
        bt_catlog = (Button) findViewById(R.id.catlog);
        bt_light = (Button) findViewById(R.id.light);
        bt_setting = (Button) findViewById(R.id.setting);
        bt_progress = (Button) findViewById(R.id.pg);
        sb_pg = (SeekBar) findViewById(R.id.seekbar_pg);
        sb_light = (SeekBar) findViewById(R.id.seekbar_light);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        bt_last.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        bt_catlog.setOnClickListener(this);
        bt_light.setOnClickListener(this);
        bt_progress.setOnClickListener(this);
        bt_setting.setOnClickListener(this);
        animation_top_show = AnimationUtils.loadAnimation(this, R.anim.animation_top_show);
        animation_button_show = AnimationUtils.loadAnimation(this, R.anim.animation_bottom_show);
        animation_top_dismiss = AnimationUtils.loadAnimation(this, R.anim.animation_top_dismiss);
        animation_button_dismiss = AnimationUtils.loadAnimation(this, R.anim.animation_bottom_dismiss);
        bt_cg = (Button) findViewById(R.id.catlog_catlog);
        bt_mk = (Button) findViewById(R.id.catlog_marks);
        iv_cancle = (ImageView) findViewById(R.id.catlog_cancle);
        lv_cg = (ListView) findViewById(R.id.listview_catlog);
        lv_mk = (ListView) findViewById(R.id.listview_mark);
        bt_cg.setOnClickListener(this);
        bt_mk.setOnClickListener(this);
        bt_cg.performClick();
        ll_setting = (LinearLayout) findViewById(R.id.ll_setting);
        rl_small = (RelativeLayout) findViewById(R.id.small);
        rl_big = (RelativeLayout) findViewById(R.id.big);
        rl_bg1 = (RelativeLayout) findViewById(R.id.bg_1);
        rl_bg2 = (RelativeLayout) findViewById(R.id.bg_2);
        rl_bg3 = (RelativeLayout) findViewById(R.id.bg_3);
        rl_bg4 = (RelativeLayout) findViewById(R.id.bg_4);
        rl_seekbar = (RelativeLayout) findViewById(R.id.rl_seekbar);
        rl_big.setOnTouchListener(this);
        rl_small.setOnTouchListener(this);
        ll_setting.setOnTouchListener(this);
        rl_bg1.setOnTouchListener(this);
        rl_bg2.setOnTouchListener(this);
        rl_bg3.setOnTouchListener(this);
        rl_bg4.setOnTouchListener(this);
        bg = (RelativeLayout) findViewById(R.id.bg);

        bt_comfirm = (Button) findViewById(R.id.bt_comfirm);
        bt_comfirm.setOnClickListener(this);
        eWord = (TextView) findViewById(R.id.ex_word);
        eWord.setTextSize(currentHint);

        iv_mode = (ImageView) findViewById(R.id.mode);
        iv_mode.setOnClickListener(this);
        iv_mode.setVisibility(View.GONE);

        if (mode) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setBackgroundColor(getResources().getColor(android.R.color.black));
            }
            iv_mode.setImageResource(R.drawable.readset_title_mode_2);
        }
        iv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 *
                 *
                 * */
                cgIsShow = false;
                rl_catlog.clearAnimation();
                rl_catlog.setAnimation(animation_button_dismiss);
                animation_button_dismiss.startNow();
                animation_button_dismiss.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        rl_catlog.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

//        nomark = (TextView) findViewById(R.id.nomark);
//        markManager = (Button) findViewById(R.id.markmanager);
//        iv_mark = (RelativeLayout) findViewById(R.id.iv_mark);

    }


    private void reset() {
        bt_cg.setTextColor(getResources().getColor(R.color.blue));
        bt_mk.setTextColor(getResources().getColor(R.color.blue));
        lv_cg.setVisibility(View.GONE);
        lv_mk.setVisibility(View.GONE);
    }

    private void initSb() {
        float num = (float) 100 / mSize;
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String s = df.format(num);
        sb_pg.setMax(mSize - 1);
        sb_pg.setProgress(0);
        tv_progress.setText(s + "%");
        sb_pg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vp.setCurrentItem(progress);
                float num = (float) (progress + 1) * 100 / mSize;
                DecimalFormat df = new DecimalFormat("0.00");//格式化小数
                String s = df.format(num);
                tv_progress.setText(s + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_light.setMax(215);
        sb_light.setProgress(currentLight);
        sb_light.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentLight = progress + 40;
                setScreenBrightness(ReadActivity.this, currentLight);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.chTitle);

        iv_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ReadActivity.this.finish();
                }
                return true;
            }
        });
        tv_title.setText(title);
        tv_title.setTextSize(20);
        tv_title_msg.setText(title);
    }

    private void getData() {
        StringRequest stringRequest = new StringRequest(path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            String a = Html.fromHtml(jo.getJSONObject("chapter").getString("content")).toString();
                            getView(a);
                            mResponse = a;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        mQueue.add(stringRequest);


    }

    private void getView(String s) {
        addReadView(s, currentHint);
    }

    private void addReadView(final String s, final int textSize) {
        Log.i("ssss", "" + s);
        final ReadView tv = new ReadView(this);
        tv.setText(s);
        tv.setTextSize(textSize);
        if (mode) {
            tv.setTextColor(getResources().getColor(R.color.text_color_night));
        } else {
            tv.setTextColor(getResources().getColor(R.color.text_color));
        }
        rl_read.removeAllViews();
        rl_read.addView(tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    isShow = false;
                    rl_top.clearAnimation();
                    rl_toolbar.clearAnimation();
                    rl_top.setAnimation(animation_top_dismiss);
                    animation_top_dismiss.startNow();
                    rl_toolbar.setAnimation(animation_button_dismiss);
                    animation_button_dismiss.startNow();
                    iv_mode.setVisibility(View.GONE);
                    animation_top_dismiss.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            rl_top.setVisibility(View.GONE);
                            rl_toolbar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    isShow = true;
                    rl_top.setVisibility(View.VISIBLE);
                    rl_toolbar.setVisibility(View.VISIBLE);
                    iv_mode.setVisibility(View.VISIBLE);
                    rl_top.setAnimation(animation_top_show);
                    animation_top_show.startNow();
                    rl_toolbar.setAnimation(animation_button_show);
                    animation_button_show.startNow();

                }
                if (cgIsShow) {
                    iv_cancle.performClick();
                }

            }
        });
        tv.setPadding(10, getResources().getDimensionPixelSize(R.dimen.top_margin), 10, getResources().getDimensionPixelSize(R.dimen.top_margin));
        list.add(tv);
        //在ReadView绘制完成后才能获得其中的内容
        tv.post(new Runnable() {

                    @Override
                    public void run() {
                        //如果当前页面中的字数不等于总字数时
                        if (!(tv.getCharNum() == s.length())) {
                            //截掉已经显示的内容
                            ss = s.substring(tv.getCharNum());
                            //继续添加
                            addReadView(ss, textSize);

                        } else if (tv.getCharNum() == s.length()) {
                            handler.sendEmptyMessage(0);
                        }
                    }
                }
        );

    }

    private void setVp() {
        adapter = new android.support.v4.view.PagerAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                if (list.get(position).getParent() != null) {
                    RelativeLayout rl = (RelativeLayout) (list.get(position).getParent());
                    rl.removeAllViews();
                }
                vp.setObjectForPosition(list.get(position), position);
                container.addView(list.get(position));
                return list.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                if (list.size() > 2)
                    container.removeView(list.get(position));
            }


        };
        vp.setAdapter(adapter);


        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
                if (position == list.size() - 1 && isEnd) {
                    jumpTo(false);
                } else if (position == list.size() - 1 && !isEnd) {
                    isEnd = true;
                } else {
                    isEnd = false;
                }
//                vp.setjAction(new MViewPager.JAction() {
//                    @Override
//                    public void jumpAction() {
                        if (position == 0 && isHead) {
                            Log.i("isHead", isHead + "");
                            jumpTo(true);
                        } else if (position == 0 && !isHead) {
                            isHead = true;
                        } else {
                            isHead = false;
                        }
//                    }
//                });
            }

            @Override
            public void onPageSelected(int position) {
                tv_chapter_msg.setText(vp.getCurrentItem() + 1 + "/" + list.size() + "页");
                sb_pg.setProgress(position);
//                if(position == list.size() - 1){
//                    isEnd = true;
//                }else{
//                    isEnd = false;
//                }
//                if(position == 0){
//                    isHead = true;
//                }else{
//                    isHead = false;
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void jumpTo(boolean flag) {
        // true --> last  false -->next
        if (flag) {
            if (p == 0) {
                Toast.makeText(ReadActivity.this, "已是第一章", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("position", p - 1);
                intent.putExtras(bundle);
                setResult(1, intent);
                ReadActivity.this.finish();
            }
        } else {
            if (p == count - 1) {
                Toast.makeText(ReadActivity.this, "已是最后一章", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("position", p + 1);
                intent.putExtras(bundle);
                setResult(1, intent);
                ReadActivity.this.finish();
            }
        }
    }

    private void setPg() {
        rl_seekbar.setVisibility(View.VISIBLE);
        ll_setting.setVisibility(View.GONE);
        bt_status = true;
        bt_last.setText("上一章");
        bt_next.setText("下一章");
        sb_light.setVisibility(View.INVISIBLE);
        sb_pg.setVisibility(View.VISIBLE);
        tv_progress.setVisibility(View.VISIBLE);
    }

    private void setLight() {
        rl_seekbar.setVisibility(View.VISIBLE);
        ll_setting.setVisibility(View.GONE);
        bt_status = false;
        bt_last.setText("亮度-");
        bt_next.setText("亮度+");
        sb_light.setVisibility(View.VISIBLE);
        sb_pg.setVisibility(View.INVISIBLE);
        tv_progress.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.last:
                if (bt_status)
                    jumpTo(true);
                else {
                    currentLight = currentLight - 20;
                    if (currentLight < 40) {
                        currentLight = 40;
                    }
                    setScreenBrightness(this, currentLight);
                    sb_light.setProgress(currentLight - 40);
                }
                break;
            case R.id.next:
                if (bt_status)
                    jumpTo(false);
                else {
                    currentLight = currentLight + 20;
                    if (currentLight > 255) {
                        currentLight = 255;
                    }
                    setScreenBrightness(this, currentLight);
                    sb_light.setProgress(currentLight - 40);
                }
                break;
            case R.id.catlog:
                cgIsShow = true;
                rl_catlog.setVisibility(View.VISIBLE);
                rl_catlog.clearAnimation();
                rl_catlog.setAnimation(animation_button_show);
                animation_button_show.startNow();
                break;
            case R.id.setting:
                rl_seekbar.setVisibility(View.GONE);
                ll_setting.setVisibility(View.VISIBLE);
                break;
            case R.id.pg:
                setPg();
                break;
            case R.id.light:
                setLight();
                break;
            case R.id.catlog_catlog:
                reset();
                lv_cg.setVisibility(View.VISIBLE);
                bt_mk.setTextColor(Color.WHITE);
                break;
            case R.id.catlog_marks:
                reset();
                lv_mk.setVisibility(View.VISIBLE);
                bt_cg.setTextColor(Color.WHITE);
                break;
            case R.id.bt_comfirm:
                getNewView(currentHint);
                break;
            case R.id.mode:
                if (mode) {
                    switch (mColor) {
                        case 0:
                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).setBackgroundColor(getResources().getColor(R.color.bg_1));
                            }
                            break;
                        case 1:
                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).setBackgroundResource(R.drawable.readset_reader_bg_1);
                            }
                            break;
                        case 2:
                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).setBackgroundColor(getResources().getColor(R.color.bg_3));
                            }
                            break;
                        case 3:
                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).setBackgroundColor(getResources().getColor(R.color.bg_4));
                            }
                            break;
                    }
                    setScreenBrightness(this, currentLight);
                    iv_mode.setImageResource(R.drawable.readset_title_mode_1);
                    sb_light.setProgress(currentLight - 40);
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setTextColor(getResources().getColor(R.color.text_color));
                    }
                    setAdapter();
                    vp.setAdapter(adapter);
                } else {
                    iv_mode.setImageResource(R.drawable.readset_title_mode_2);
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setBackgroundColor(getResources().getColor(android.R.color.black));
                    }
                    setScreenBrightness(this, 40);
                    sb_light.setProgress(0);
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setTextColor(getResources().getColor(R.color.text_color_night));
                    }
                    setAdapter();
                    vp.setAdapter(adapter);
                }
                mode = !mode;
                break;
            default:
                break;
        }
    }

    public static int getScreenBrightness(Activity activity) {
        int value = 0;
        ContentResolver cr = activity.getContentResolver();
        try {
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {

        }
        return value;
    }

    public static void setScreenBrightness(Activity activity, int value) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.screenBrightness = value / 255f;
        activity.getWindow().setAttributes(params);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.small:
                    currentHint = currentHint - 2;
                    if (currentHint >= 12) {
                        eWord.setTextSize(currentHint);
                    } else {
                        currentHint = 12;
                        eWord.setTextSize(currentHint);
                    }
                    break;
                case R.id.big:
                    currentHint = currentHint + 2;
                    if (currentHint <= 30) {
                        eWord.setTextSize(currentHint);
                    } else {
                        currentHint = 30;
                        eWord.setTextSize(currentHint);
                    }
                    break;
                case R.id.bg_1:
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setBackgroundColor(getResources().getColor(R.color.bg_1));
                    }
//                    bg.setBackgroundColor(getResources().getColor(R.color.bg_1));
                    mColor = 0;
                    break;
                case R.id.bg_2:
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setBackgroundResource(R.drawable.readset_reader_bg_1);
                    }
//                    bg.setBackgroundResource(R.drawable.readset_reader_bg_1);
                    mColor = 1;
                    break;
                case R.id.bg_3:
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setBackgroundColor(getResources().getColor(R.color.bg_3));
                    }
//                    bg.setBackgroundColor(getResources().getColor(R.color.bg_3));
                    mColor = 2;
                    break;
                case R.id.bg_4:
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setBackgroundColor(getResources().getColor(R.color.bg_4));
                    }
//                    bg.setBackgroundColor(getResources().getColor(R.color.bg_4));
                    mColor = 3;
                    break;
            }
        }
        return true;
    }

    private void register() {
        registerReceiver(batteryChangedReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(timeChangeReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    private void unregister() {
        unregisterReceiver(batteryChangedReceiver);
        unregisterReceiver(timeChangeReceiver);
    }

    // 接受广播
    private BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                int power = level * 100 / scale;
                bw.setPower(power);
            }
        }
    };

    private BroadcastReceiver timeChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                if (minute < 10) {
                    tv_time_msg.setText(hour + ":0" + minute);
                } else {
                    tv_time_msg.setText(hour + ":" + minute);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putInt("light", currentLight);
        editor.putInt("hint", currentHint);
        editor.putInt("color", mColor);
        editor.putBoolean("mode", mode);
        editor.commit();
        unregister();
    }
}