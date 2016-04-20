package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.administrator.read.R;

import java.util.List;
import java.util.zip.Inflater;

import bean.Books;
import util.BitmapCache;

/**
 * Created by Administrator on 2016/4/8.
 */
public class MainBookListAdapter extends BaseAdapter {

    private Context context;
    private List<Books> list;
    private RequestQueue mQueue;

    public MainBookListAdapter(Context context, List<Books> list) {
        this.context = context;
        this.list = list;
        mQueue = Volley.newRequestQueue(context);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.mainlistviewitem, null);
            vh.iv = (NetworkImageView) convertView.findViewById(R.id.imageview);
            vh.tv_author = (TextView) convertView.findViewById(R.id.item_author);
            vh.tv_title = (TextView) convertView.findViewById(R.id.title);
            vh.tv_chapter = (TextView) convertView.findViewById(R.id.item_chapter);
            vh.tv_cost = (TextView) convertView.findViewById(R.id.item_cost);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tv_author.setText(list.get(position).getAuthor());
        vh.tv_cost.setText( "全本" + list.get(position).getTotalMoney() + "元");
        vh.tv_title.setText(list.get(position).getTitle());
        vh.tv_chapter.setText("更新至" + list.get(position).getChapterCount() + "章");
        ImageLoader imageLoader = new ImageLoader(mQueue,BitmapCache.instance());

        vh.iv.setDefaultImageResId(R.drawable.default_big_icon);
        vh.iv.setErrorImageResId(R.drawable.default_big_icon);
        vh.iv.setImageUrl(list.get(position).getCover(), imageLoader);
        return convertView;
    }

}

class ViewHolder {
    NetworkImageView iv;
    TextView tv_title, tv_author, tv_chapter,tv_cost;
}