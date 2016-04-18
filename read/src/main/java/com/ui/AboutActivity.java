package com.ui;

import android.app.Activity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.read.R;

import adbanner.BaseWebActivity;

public class AboutActivity extends Activity {

    private GridView mGridView;
    private MAdapter mAdapter;
    List<GridData> mList = new ArrayList<GridData>();
    private ImageView iv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mList.add(new GridData("和阅读", R.drawable.about1, "http://www.cmread.com/client"));
        mList.add(new GridData("和动漫", R.drawable.about2, "http://dm.10086.cn/H/o/3470181/543156/mv00;jsessionid=F51B9582FBD8D23A4494AC49CA271A10?a=/apphall/viewAppSoftwareDetail&NAMESPACE_543156clientid=wapclient_enjoy&NAMESPACE_543156columnID=298022"));
        mList.add(new GridData("应用汇", R.drawable.about3, "http://www.appchina.com/"));
        mList.add(new GridData("聚美优品", R.drawable.about4, "http://sh.jumei.com/"));
        mList.add(new GridData("搜狗市场", R.drawable.about5, "http://app.sogou.com/"));
        mGridView = (GridView) findViewById(R.id.gridView1);
        mAdapter = new MAdapter();
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = mList.get(position).url;
                String title = "";
                /*
				 * if (TextUtils.isEmpty(url)) {
				 * holder.imageView.setEnabled(false); return; }
				 */
                Bundle bundle = new Bundle();

                bundle.putString("url", url);
                bundle.putString("title", title);
                Intent intent = new Intent(getApplicationContext(), BaseWebActivity.class);
                intent.putExtras(bundle);

                AboutActivity.this.startActivity(intent);
            }
        });
        iv = (ImageView) findViewById(R.id.iv_back);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.finish();
            }
        });
    }

        class MAdapter extends BaseAdapter {

            @Override
            public int getCount() {
                return mList.size();
            }

            @Override
            public Object getItem(int position) {
                return mList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if(convertView == null || convertView.getTag() == null) {
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.more_item, null);
                    holder.image = (ImageView) convertView.findViewById(R.id.imageView1);
                    holder.text = (TextView) convertView.findViewById(R.id.textView1);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.image.setImageResource(mList.get(position).imageId);
                holder.text.setText(mList.get(position).text);
                return convertView;
            }

        }

        class ViewHolder {
            ImageView image;
            TextView text;
        }

        class GridData {
            String text;
            int imageId;
            String url;
            public GridData(String text, int imageId, String url) {
                super();
                this.text = text;
                this.imageId = imageId;
                this.url = url;
            }

        }


}
