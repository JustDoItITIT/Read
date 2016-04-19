package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.read.R;

import java.util.List;

import bean.BookDetailList;

/**
 * Created by Administrator on 2016/4/11.
 */
public class CatalogAdapter extends BaseAdapter {

    private List<BookDetailList> list;

    private Context context;

    public CatalogAdapter(List<BookDetailList> list, Context context){
        this.list = list;
        this.context = context;
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
        CatalogViewHolder vh;
        if(convertView == null){
            vh = new CatalogViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.catalogitem,null);
            vh.tv_title = (TextView) convertView.findViewById(R.id.title);
            vh.tv_flag = (TextView) convertView.findViewById(R.id.flag);
            convertView.setTag(vh);
        }else{
           vh = (CatalogViewHolder) convertView.getTag();
        }
        vh.tv_title.setText(list.get(position).getTitle_cha());

        if(position == 0){
            vh.tv_flag.setVisibility(View.VISIBLE);
            vh.tv_flag.setText("免费阅读");
        }else if(list.get(position).isFlag()){
            vh.tv_flag.setText("已购买");
            vh.tv_flag.setVisibility(View.VISIBLE);
        }else{
            vh.tv_flag.setVisibility(View.GONE);
        }
        return convertView;
    }



}

class CatalogViewHolder{
    TextView tv_title,tv_flag;

}
