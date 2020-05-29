package com.example.oksocketclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oksocketclient.bean.DeviceSet;
import com.example.oksocketclient.bean.Face;
import com.example.oksocketclient.bean.IpSave;
import com.example.oksocketclient.utils.DTPickerUtils;
import com.example.oksocketclient.utils.MyAdapter;
import com.example.oksocketclient.utils.OkSocketUtils;
import com.example.oksocketclient.utils.PicUtils;
import com.example.oksocketclient.view.AlertDialog;
import com.example.oksocketclient.view.CommonDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MsgListActivity extends AppCompatActivity implements CommonDialog.OnCenterItemClickListener, ServerData {
    private static final String TAG = "MsgListActivity";
    private OkSocketUtils okSocketUtils;
    private  List<DeviceSet> deviceSetList =new ArrayList<>();
    private  List<Face> faceList =new ArrayList<>();
    private List<IpSave>ipSaves=new ArrayList<>();
    private ListView listView;
    private DeviceSetAdapter adapter;
    private MyAdapter myAdapter1;
    private CommonDialog detailMsgDialog;
    private int cmd;

    private Face face;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_set);
        init();
    }

    private void init() {
        listView=findViewById(R.id.listview);
        progressDialog=new ProgressDialog(this);
        //设置ProgressDialog 是否可以按返回键取消；
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setTitle("正在加载 请稍后...");
        progressDialog.show();
        okSocketUtils=OkSocketUtils.getInstace();
        okSocketUtils.setLoginStatusListener(new LoginStatus() {
            @Override
            public void getLoginStatus(boolean isSuccessful) {
                Log.e(TAG, "setLoginStatusListener: "+isSuccessful );
                if (!isSuccessful){
                    setResult(2,new Intent().putExtra("isSuccessful",isSuccessful));
                    finish();
                    Toast.makeText(MsgListActivity.this, "设备已离线！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cmd=getIntent().getExtras().getInt("cmd");
        okSocketUtils.clineSendData(cmd,null);
        okSocketUtils.setServerDataListener(this);
    }

    private EditText nameEditText,idcardEditText,onlyidEditText,typeEditText;
    private Spinner typeSpinner,systemSpinner,doorSpinner;
    private TextView startdateTv,starttimeTv,enddateTv,endtimeTv;
    private String[]TYPE=new String[]{"陌生人","黑名单","白名单","员工"};
    private String[]PERMISSION=new String[]{"禁止","允许","未设置"};
    private String type,system_permission;
    private boolean door_permission;
    private List typeList;
    private ArrayAdapter<String> typeSpinnerAdapter,systemSpinnerAdapter,doorSpinnerAdapter;
    private Calendar calendar;
    private void showCommonDialog(Face obj){
        calendar= Calendar.getInstance(Locale.CHINA);
        detailMsgDialog =new CommonDialog(this,R.layout.layout_psersonmsg,
                new int[]{R.id.face_img,R.id.name,R.id.idcard,R.id.only_id,R.id.type,R.id.revise,
                        R.id.startdate_tv,R.id.starttime_tv, R.id.enddate_tv, R.id.endtime_tv});
        View view= detailMsgDialog.getContentView();
        ImageView imageView=view.findViewById(R.id.face_img);
        imageView.setImageBitmap(PicUtils.getBitmapFromByte(obj.getFace_pic()));
        nameEditText=view.findViewById(R.id.name);
        nameEditText.setText(obj.getName());
        idcardEditText=view.findViewById(R.id.idcard);
        idcardEditText.setText(obj.getIdcard_num());
        onlyidEditText=view.findViewById(R.id.only_id);
        onlyidEditText.setText(obj.getOnly_id());
        typeEditText=view.findViewById(R.id.type);
        typeEditText.setText(obj.getType());

        startdateTv=view.findViewById(R.id.startdate_tv);

        starttimeTv=view.findViewById(R.id.starttime_tv);

        enddateTv=view.findViewById( R.id.enddate_tv);

        endtimeTv=view.findViewById( R.id.endtime_tv);


        if (TextUtils.isEmpty(obj.getStartdate())){
            startdateTv.setText("请选择");
        }else {
            startdateTv.setText(obj.getStartdate());
        }
        if (TextUtils.isEmpty(obj.getStarttime())){
            starttimeTv.setText("请选择");
        }else {
            starttimeTv.setText(obj.getStarttime());
        }
        if (TextUtils.isEmpty(obj.getEnddate())){
            enddateTv.setText("请选择");
        }else {
            enddateTv.setText(obj.getEnddate());
        }
        if (TextUtils.isEmpty(obj.getEndtime())){
            endtimeTv.setText("请选择");
        }else {
            endtimeTv.setText(obj.getEndtime());
        }

        typeSpinner=view.findViewById(R.id.spinner_type);
        systemSpinner=view.findViewById(R.id.spinner_system);
        doorSpinner=view.findViewById(R.id.spinner_door);

        setTypeSpinnerMsg(TYPE,obj,typeSpinner);
        setSystemSpinnerMsg(PERMISSION,obj,systemSpinner);
        setDoorSpinnerMsg(PERMISSION,obj,doorSpinner);

        face=new Face();
        face=obj;
        if(detailMsgDialog !=null){
            detailMsgDialog.show();
            detailMsgDialog.setOnCenterItemClickListener(this);
        }
    }


    //信息删除dialog
    private void showConfrimDeleteDialog(final MyAdapter myAdapter, final int position, final long id, final int cmd){
        new AlertDialog(this)
                .builder()
                .setTitle("是否删除？")
                .setMsg("删除后无法恢复")
                .setCancelable(false)
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myAdapter.remove(position);
                        if (cmd==2001||cmd==2002||cmd==2003||cmd==2004){
                            okSocketUtils.clineSendData(2007,id);
                        }else if (cmd==3001){
                            okSocketUtils.clineSendData(3002,id);
                        }
                        //删除完毕重新请求数据进行刷新
                        okSocketUtils.clineSendData(cmd,null);
                        progressDialog.show();
                    }
                })
                .show();
    }


    public static <T> List<T> jsonToList(String json, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for(final JsonElement elem : array){
            list.add(new Gson().fromJson(elem, cls));
        }
        return list;
    }

    @Override
    public void OnCenterItemClick(CommonDialog dialog, View view) {
        switch (view.getId()){
            case R.id.startdate_tv:
                DTPickerUtils.showDatePickerDialog(this,5,startdateTv,calendar);
                break;
            case R.id.starttime_tv:
                DTPickerUtils.showTimePickerDialog(this,5,starttimeTv,calendar);
                break;
            case R.id.enddate_tv:
                DTPickerUtils.showDatePickerDialog(this,5,enddateTv,calendar);
                break;
            case R.id.endtime_tv:
                DTPickerUtils.showTimePickerDialog(this,5,endtimeTv,calendar);
                break;
            case R.id.revise:
//                Face face=new Face();
                face.setName(nameEditText.getText().toString());
                face.setIdcard_num(idcardEditText.getText().toString());
                face.setOnly_id(onlyidEditText.getText().toString());
                face.setType(type);
                face.setSystem_permissions(system_permission);
                face.setIsbanallow(door_permission);
                face.setStartdate(startdateTv.getText().toString());
                face.setStarttime(starttimeTv.getText().toString());
                face.setEnddate(enddateTv.getText().toString());
                face.setEndtime(endtimeTv.getText().toString());
//                face.setFace_pic(null);
//                Log.e(TAG, "OnCenterItemClick: "+face.toString() );
                okSocketUtils.clineSendData(2005,face);
                //修改完毕重新请求数据进行刷新
                okSocketUtils.clineSendData(cmd,null);
                dialog.dismiss();
                progressDialog.show();
                break;
        }
    }

    @Override
    public void getServerData(String str) {
        progressDialog.dismiss();

        Log.e(TAG, "getServerData: 收到----" );
//                Log.e(TAG, "getServerData: "+str);
        JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
        final int cmd = jsonObject.get("cmd").getAsInt();
        String strdata = jsonObject.get("data").getAsString();
        if (cmd==1001){//设备配置
            deviceSetList.clear();
            deviceSetList = jsonToList(strdata, DeviceSet.class);
            myAdapter1 = new MyAdapter<DeviceSet>(deviceSetList,R.layout.item_deviceset) {
                @Override
                public void bindView(ViewHolder holder, DeviceSet obj) {
//                            holder.setImageResource(R.id.img_icon,obj.getaIcon());
                    holder.setText(R.id.device_id,"设备id："+obj.getDevice_id());
                    holder.setText(R.id.device_banallow,"门禁权限："+obj.isDevice_banallow());
                    holder.setText(R.id.ismain,"是否为主设备："+obj.isIsmain());
                }
            };
        }else if (cmd==2001||cmd==2002||cmd==2003||cmd==2004){//人员名单
            faceList.clear();
            faceList = jsonToList(strdata, Face.class);
            Collections.reverse(faceList);//倒序
            myAdapter1 = new MyAdapter<Face>(faceList,R.layout.item_face) {
                @Override
                public void bindView(ViewHolder holder, final Face obj) {
                    final int position= holder.getItemPosition();
                    holder.setOnLongClickListener(R.id.item_face_layout, new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            Toast.makeText(MsgListActivity.this, "长按了第"+position+"个", Toast.LENGTH_SHORT).show();

                            showConfrimDeleteDialog(myAdapter1,position,faceList.get(position).getId(),cmd);
                            return false;
                        }
                    });
                    holder.setOnClickListener(R.id.image, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MsgListActivity.this, "点击了第"+position+"个", Toast.LENGTH_SHORT).show();
                            showCommonDialog(obj);
                        }
                    });
                    holder.setImageResource(R.id.image,PicUtils.getBitmapFromByte(obj.getFace_pic()));
                    holder.setText(R.id.only_id,"唯一id："+obj.getOnly_id());
                    holder.setText(R.id.name,"姓名："+obj.getName());
                    holder.setText(R.id.idcard_tv,"身份证号："+obj.getIdcard_num());
                    holder.setText(R.id.type,"属性："+obj.getType());
                }
            };
        }else if (cmd==3001){//保存的客户端ip以及tag
            ipSaves.clear();
            ipSaves=jsonToList(strdata,IpSave.class);
            myAdapter1=new MyAdapter<IpSave>(ipSaves,R.layout.item_deviceset) {
                @Override
                public void bindView(ViewHolder holder, IpSave obj) {
                    final int position= holder.getItemPosition();
                    holder.setOnClickListener(R.id.item_deviceset, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MsgListActivity.this, "点击了第"+position+"个", Toast.LENGTH_SHORT).show();
                        }
                    });
                    holder.setOnLongClickListener(R.id.item_deviceset, new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            Toast.makeText(MsgListActivity.this, "长按了第"+position+"个", Toast.LENGTH_SHORT).show();
                            showConfrimDeleteDialog(myAdapter1,position,ipSaves.get(position).getId(),cmd);
                            return false;
                        }
                    });
                    holder.setText(R.id.device_banallow,"设备IP："+obj.getIp());
                    holder.setText(R.id.device_id,"设备tag："+obj.getClinettag());
                    holder.setVisibility(R.id.ismain,View.GONE);
                }
            };
        }
        if (myAdapter1!=null)
            listView.setAdapter(myAdapter1);
    }

    //人员属性
    private void setTypeSpinnerMsg(final String[]strings, Face face, Spinner spinner){
        typeSpinnerAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,strings);
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(typeSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (strings[i].equals("陌生人")){
                    type="-1";
                }else if (strings[i].equals("黑名单")){
                    type="0";
                }else if (strings[i].equals("白名单")){
                    type="1";
                }else if (strings[i].equals("员工")){
                    type="4";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

            if (!TextUtils.isEmpty(face.getType())){
                if (face.getType().equals("-1")){//陌生人
                    spinner.setSelection(0);
                    type="-1";
                }else if (face.getType().equals("0")){//黑名单
                    spinner.setSelection(1);
                    type="0";
                }else if (face.getType().equals("1")){//白名单
                    spinner.setSelection(2);
                    type="1";
                }else if (face.getType().equals("4")){//员工
                    spinner.setSelection(3);
                    type="4";
                }
            }
    }

    private void setSystemSpinnerMsg(final String[]strings, Face face, Spinner spinner){
        systemSpinnerAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,strings);
        systemSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(systemSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (strings[i].equals("禁止")){
                    system_permission="0";
                }else if (strings[i].equals("允许")){
                    system_permission="1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (!TextUtils.isEmpty(face.getSystem_permissions())){
            if (face.getSystem_permissions().equals("0")){//禁止
                spinner.setSelection(0);
                system_permission="0";
            }else if (face.getSystem_permissions().equals("1")){//允许
                spinner.setSelection(1);
                system_permission="1";
            }
        }else {
            spinner.setSelection(2);
        }
    }

    private void setDoorSpinnerMsg(final String[]strings, Face face, Spinner spinner){
        doorSpinnerAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,strings);
        doorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(doorSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 if (strings[i].equals("禁止")){
                    door_permission=false;
                }else if (strings[i].equals("允许")){
                    door_permission=true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (!TextUtils.isEmpty(face.isIsbanallow()+"")){
            if (face.isIsbanallow()){
                spinner.setSelection(1);
                door_permission=true;
            }else {
                spinner.setSelection(0);
                door_permission=false;
            }
        }else {
            spinner.setSelection(2);
        }
    }


}
