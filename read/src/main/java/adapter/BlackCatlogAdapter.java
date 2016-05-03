package adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.read.R;

import java.util.List;

import bean.BookDetailList;

/**
 * Created by Administrator on 2016/4/26.
 */
public class BlackCatlogAdapter extends BaseAdapter{


        private List<BookDetailList> list;

        private Context context;

        public BlackCatlogAdapter(List<BookDetailList> list, Context context){
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
            BlackCatalogViewHolder vh;
            if(convertView == null){
                vh = new BlackCatalogViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.catalogitem,null);
                vh.tv_title = (TextView) convertView.findViewById(R.id.title);
                vh.tv_flag = (TextView) convertView.findViewById(R.id.flag);
                convertView.setTag(vh);
            }else{
                vh = (BlackCatalogViewHolder) convertView.getTag();
            }
            vh.tv_title.setText(list.get(position).getTitle_cha());
            vh.tv_flag.setVisibility(View.GONE);
            if(list.get(position).isFlag()){
                vh.tv_title.setTextColor(context.getResources().getColor(R.color.blue));
            }else
            vh.tv_title.setTextColor(Color.WHITE);

            return convertView;
        }
    class BlackCatalogViewHolder{
        TextView tv_title,tv_flag;

    }

}
