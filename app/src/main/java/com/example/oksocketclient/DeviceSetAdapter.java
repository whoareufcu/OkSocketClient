package com.example.oksocketclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.oksocketclient.bean.DeviceSet;
import com.example.oksocketclient.bean.Face;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeviceSetAdapter<T> extends BaseAdapter {

    private Context context;
    private List<T> list;

    public DeviceSetAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view==null){
            view = LayoutInflater.from(context).inflate(R.layout.item_deviceset, viewGroup, false);
            viewHolder=new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
//        viewHolder.deviceId.setText("设备id："+list.get(i).getDevice_id());
//        viewHolder.deviceBanallow.setText("门禁权限："+list.get(i).isDevice_banallow());
//        viewHolder.ismain.setText("是否为主设备："+list.get(i).isIsmain());
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.device_banallow)
        TextView deviceBanallow;
        @Bind(R.id.ismain)
        TextView ismain;
        @Bind(R.id.device_id)
        TextView deviceId;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
