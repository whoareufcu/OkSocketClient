package com.example.oksocketclient;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.oksocketclient.utils.OkSocketUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MainFragment";
    private OkSocketUtils okSocketUtils;
    private AlertDialog alertDialog1;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        context=getContext();
        view.findViewById(R.id.deviceSet).setOnClickListener(this);
        view.findViewById(R.id.whiteList).setOnClickListener(this);
        view.findViewById(R.id.clinettag).setOnClickListener(this);
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.deviceSet:
                deviceSet();
                break;
            case R.id.whiteList:
                whiteList();
                break;
            case R.id.clinettag:
                clinettag();
                break;
        }

    }


    //设备配置
    public void deviceSet() {
        Bundle bundle=new Bundle();
        bundle.putInt("cmd",1001);
        startActivity(new Intent(context, MsgListActivity.class).putExtras(bundle));
    }

    public void whiteList() {

        final String[] items = {"白名单", "黑名单", "员工", "陌生人"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("请选择");
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(context, items[i], Toast.LENGTH_SHORT).show();
                Bundle bundle=new Bundle();
                if (items[i].equals("白名单")){
                    bundle.putInt("cmd",1002);
                }else if (items[i].equals("黑名单")){
                    bundle.putInt("cmd",1003);
                }else if (items[i].equals("员工")){
                    bundle.putInt("cmd",1004);
                }else if (items[i].equals("陌生人")){
                    bundle.putInt("cmd",1005);
                }
                startActivity(new Intent(context, MsgListActivity.class).putExtras(bundle));
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = alertBuilder.create();
        alertDialog1.show();
    }

    //客户端tag
    public void clinettag() {
        Bundle bundle=new Bundle();
        bundle.putInt("cmd",1006);
        startActivity(new Intent(context, MsgListActivity.class).putExtras(bundle));
    }
}
