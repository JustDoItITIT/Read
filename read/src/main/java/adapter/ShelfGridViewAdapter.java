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
import com.android.volley.toolbox.Volley;
import com.example.administrator.read.R;

import java.util.List;

import bean.ShelfBook;
import util.BitmapCache;

/**
 * Created by Administrator on 2016/4/12.
 */
public class ShelfGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<ShelfBook> list;
    private RequestQueue mQueue;

    public ShelfGridViewAdapter(Context context ,List<ShelfBook> list){
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
        SViewHolder vh;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.shelfgridviewitem,null);
            vh = new SViewHolder();
            vh.tv = (TextView) convertView.findViewById(R.id.textview_shelf);
            vh.iv = (ImageView) convertView.findViewById(R.id.imageview_shelf);
            convertView.setTag(vh);
        }else{
            vh = (SViewHolder) convertView.getTag();
        }
        vh.tv.setText(list.get(position).getTitle());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(vh.iv,
                R.drawable.default_big_icon, R.drawable.default_big_icon);
        ImageLoader imageLoader = new ImageLoader(mQueue, BitmapCache.instance());
        imageLoader.get(list.get(position).getCover(), listener);
        return convertView;
    }
}
class SViewHolder{
    TextView tv;
    ImageView iv;
}
