package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.read.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import bean.OrderMessage;

/**
 * Created by Administrator on 2016/4/19.
 */
public class OrderListAdapter extends BaseAdapter {

    private Context context;
    private List<OrderMessage> list;


    public OrderListAdapter(Context context , List<OrderMessage> list){
        this.context = context;
        this.list = list;
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
        OrderViewHolder vh;
        if(convertView == null){
            vh = new OrderViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.ordered_item,null);
            vh.tv_cost = (TextView) convertView.findViewById(R.id.tv_cost);
            vh.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            vh.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            vh.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
            convertView.setTag(vh);
        }else{
            vh = (OrderViewHolder) convertView.getTag();
        }
        vh.tv_cost.setText(list.get(position).getGoodPrice() + "元");
        if("1".equals(list.get(position).getOrderType())){
            vh.tv_type.setText("全本购买");
        }else if("2".equals(list.get(position).getOrderType())){
            vh.tv_type.setText("章节购买");
        }
        vh.tv_status.setText(list.get(position).getOrderStatus());

        String dateString =list.get(position).getOrder_time();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
        TimeZone tz = TimeZone.getTimeZone("GMT+8");
        sdf.setTimeZone(tz);
        Date s;
        try {
            s = sdf.parse(dateString);
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            vh.tv_time.setText(sdf.format(s));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return convertView;
    }

    class OrderViewHolder{
        TextView tv_time,tv_type,tv_status,tv_cost;
    }

}
