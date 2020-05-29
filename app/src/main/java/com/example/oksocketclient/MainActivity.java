package com.example.oksocketclient;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.oksocketclient.bean.Face;
import com.example.oksocketclient.utils.DTPickerUtils;
import com.example.oksocketclient.utils.OkSocketUtils;
import com.example.oksocketclient.utils.PicUtils;
import com.example.oksocketclient.view.CommonDialog;
import com.wildma.pictureselector.PictureSelector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoginStatus, View.OnClickListener, CommonDialog.OnCenterItemClickListener {

    private static final String TAG = "MainActivity";
    ImageView faceImg;
    EditText nameEt;
    EditText idcardEt;
    EditText onlyIdEt;
    RadioButton typeWhiteRb;
    RadioButton typeBlackRb;
    RadioButton typeEmplooyRb;
    RadioButton typeStrangerRb;
    RadioButton allowSystemRb;
    RadioButton banSystemRb;
    RadioButton allowDoorRb;
    RadioButton banDoorRb;
    TextView startdateTv;
    TextView starttimeTv;
    TextView enddateTv;
    TextView endtimeTv;
    Button confrimAddFace;
    private OkSocketUtils okSocketUtils;
    private MainFragment mainFragment;
    private LinearLayout linearLayout;
    private ImageView onlineState;
    private boolean isOnline;
    private Context context;
    private CommonDialog addMsgDialog;
    private String picturePath = null;

    private Calendar calendar;
    private ActivityManager manager ;
    private int heapSize ;
    private int maxHeapSize ;
    // manafest.xml   android:largeHeap="true"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        context = this;
        calendar= Calendar.getInstance(Locale.CHINA);
        onlineState = findViewById(R.id.online_state);
        findViewById(R.id.msg_add).setOnClickListener(this);
        findViewById(R.id.deviceSet).setOnClickListener(this);
        findViewById(R.id.whiteList).setOnClickListener(this);
        findViewById(R.id.clinettag).setOnClickListener(this);
        isOnline = getIntent().getExtras().getBoolean("isSuccessful");
        onlineState.setSelected(isOnline);
        okSocketUtils = OkSocketUtils.getInstace();
//        mainFragment=new MainFragment();
//        getSupportFragmentManager().beginTransaction().replace(R.id.linearlayout, mainFragment).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        okSocketUtils.setLoginStatusListener(this);
        manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        heapSize = manager.getMemoryClass();
        maxHeapSize = manager.getLargeMemoryClass();
        Log.e(TAG, "onResume: "+heapSize+"------"+maxHeapSize );
    }

    @Override
    public void onClick(View view) {
        if (isOnline) {
            switch (view.getId()) {
                case R.id.msg_add:
                    addMsg();
                    break;
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
        } else {
            Toast.makeText(context, "设备已离线 请重连后再试！", Toast.LENGTH_SHORT).show();
        }
    }

    private void addMsg() {
        final String[] items = {"设备配置", "人员名单"};
        new AlertDialog.Builder(context)
                .setTitle("请选择")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAddMsgDialog(i);
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }


    private void showAddMsgDialog(int i) {
        int layoutResID = 0;
        int[] listenedItem = null;
        if (i == 0) {
            layoutResID = R.layout.layout_add_deviceset;
            listenedItem = new int[]{R.id.device_id};
        } else if (i == 1) {
            layoutResID = R.layout.layout_add_face;
            listenedItem = new int[]{R.id.face_img, R.id.startdate_tv, R.id.starttime_tv, R.id.enddate_tv, R.id.endtime_tv, R.id.confrim_add_face};
        }
        if (layoutResID != 0 && listenedItem != null) {
            addMsgDialog = new CommonDialog(context, layoutResID, listenedItem);
            View view = addMsgDialog.getContentView();
            initDialogViews(view,i);
        }
        if (addMsgDialog != null) {
            addMsgDialog.show();
            addMsgDialog.setOnCenterItemClickListener(this);
        }
    }


    private void initDialogViews(View view,int i){
        if (i==0){

        }else if (i==1){
            faceImg=view.findViewById(R.id.face_img);
            confrimAddFace=view.findViewById(R.id.confrim_add_face);
            startdateTv=view.findViewById(R.id.startdate_tv);
            startdateTv.setText(calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH));
            starttimeTv=view.findViewById(R.id.starttime_tv);
            starttimeTv.setText(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
            enddateTv=view.findViewById( R.id.enddate_tv);
            enddateTv.setText(calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH));
            endtimeTv=view.findViewById( R.id.endtime_tv);
            endtimeTv.setText(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
            nameEt=view.findViewById(R.id.name_et);
            idcardEt=view.findViewById(R.id.idcard_et);
            typeWhiteRb=view.findViewById(R.id.type_white_rb);
            typeBlackRb=view.findViewById(R.id.type_black_rb);
            typeEmplooyRb=view.findViewById(R.id.type_emplooy_rb);
            typeStrangerRb=view.findViewById(R.id.type_stranger_rb);
            allowSystemRb=view.findViewById(R.id.allow_system_rb);
            banSystemRb=view.findViewById(R.id.ban_system_rb);
            allowDoorRb=view.findViewById(R.id.allow_door_rb);
            banDoorRb=view.findViewById(R.id.ban_door_rb);
        }
    }

    @Override
    public void OnCenterItemClick(CommonDialog dialog, View view) {

        switch (view.getId()) {
            case R.id.face_img:
                /**
                 * create()方法参数一是上下文，在activity中传activity.this，在fragment中传fragment.this。参数二为请求码，用于结果回调onActivityResult中判断
                 * selectPicture()方法参数分别为 是否裁剪、裁剪后图片的宽(单位px)、裁剪后图片的高、宽比例、高比例。都不传则默认为裁剪，宽200，高200，宽高比例为1：1。
                 */
                PictureSelector
                        .create(MainActivity.this, PictureSelector.SELECT_REQUEST_CODE)
                        .selectPicture(false, 200, 200, 1, 1);
                break;
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
            case R.id.confrim_add_face:
                //人员属性
                type=null;//重置
                if (typeWhiteRb.isChecked()){
                    type="1";
                }else if (typeBlackRb.isChecked()){
                    type="0";
                }else if (typeEmplooyRb.isChecked()){
                    type="4";
                }else if (typeStrangerRb.isChecked()){
                    type="-1";
                }
                if (judgementNull()){
                    addFaceMsg();
                    addMsgDialog.dismiss();
                }
                break;

        }
    }

    //判空
    private boolean judgementNull(){

        if (picturePath==null){
            Toast.makeText(context, "请拍照！", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(nameEt.getText().toString())){
            Toast.makeText(context, "请输入姓名！", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(idcardEt.getText().toString())){
            Toast.makeText(context, "请输入身份证号！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(type)){
            Toast.makeText(context, "请选择人员属性！", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private String type=null;
    private void addFaceMsg(){
        Face face=new Face();
        face.setName(nameEt.getText().toString());
        face.setFace_pic(PicUtils.getImageToBase64(picturePath));
        face.setIdcard_num(idcardEt.getText().toString());
        face.setStartdate(startdateTv.getText().toString());
        face.setStarttime(starttimeTv.getText().toString());
        face.setEnddate(enddateTv.getText().toString());
        face.setEndtime(endtimeTv.getText().toString());
        face.setType(type);
        //人员系统权限
        if (allowSystemRb.isChecked()){
            face.setSystem_permissions("1");
        }else if (banSystemRb.isChecked()){
            face.setSystem_permissions("0");
        }
        //人员门禁权限
        if (allowDoorRb.isChecked()){
            face.setIsbanallow(true);
        }else if (banDoorRb.isChecked()){
            face.setIsbanallow(false);
        }

        OkSocketUtils.getInstace().clineSendData(2006,face);

//        Log.e(TAG, "addFaceMsg: "+face.toString() );
    }

    //设备配置
    public void deviceSet() {
        Bundle bundle = new Bundle();
        bundle.putInt("cmd", 1001);
        startActivityForResult(new Intent(context, MsgListActivity.class).putExtras(bundle), 1);
    }

    public void whiteList() {

        final String[] items = {"白名单", "黑名单", "员工", "陌生人"};
        new AlertDialog.Builder(context)
                .setTitle("请选择")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, items[i], Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        if (items[i].equals("白名单")) {//1
                            bundle.putInt("cmd", 2001);
                        } else if (items[i].equals("黑名单")) {//0
                            bundle.putInt("cmd", 2002);
                        } else if (items[i].equals("员工")) {//4
                            bundle.putInt("cmd", 2003);
                        } else if (items[i].equals("陌生人")) {//-1
                            bundle.putInt("cmd", 2004);
                        }
                        startActivityForResult(new Intent(context, MsgListActivity.class).putExtras(bundle), 1);
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }

    //客户端tag
    public void clinettag() {
        Bundle bundle = new Bundle();
        bundle.putInt("cmd", 3001);
        startActivityForResult(new Intent(context, MsgListActivity.class).putExtras(bundle), 1);
    }

    @Override
    public void getLoginStatus(boolean isSuccessful) {
        isOnline = isSuccessful;
        onlineState.setSelected(isOnline);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*结果回调*/
        if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
            if (data != null) {
                 picturePath = data.getStringExtra(PictureSelector.PICTURE_PATH);
                faceImg.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                /*使用 Glide 加载图片，由于裁剪后的图片地址是相同的，所以不能从缓存中加载*/
                /*RequestOptions requestOptions = RequestOptions
                        .circleCropTransform()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true);
                Glide.with(this).load(picturePath).apply(requestOptions).into(mIvImage);*/
            }
        }
        if (requestCode == 1 && resultCode == 2) {
            isOnline = data.getExtras().getBoolean("isSuccessful");
            onlineState.setSelected(isOnline);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        okSocketUtils.disConnect();
    }


}
