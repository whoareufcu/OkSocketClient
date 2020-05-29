package com.example.oksocketclient.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.oksocketclient.R;

public class CommonDialog extends Dialog implements View.OnClickListener {

    //在构造方法里提前加载了样式
    private Context context;//上下文
    private int layoutResID;//布局文件id
    private int[] listenedItem;//监听的控件id
    private View contentView;
    private float withScale;
    private float heightScale;
    public CommonDialog(Context context,int layoutResID, int[] listenedItem){
        super(context, R.style.CommonDialog);//加载dialog的样式
        this.context = context;
        this.layoutResID = layoutResID;
        this.listenedItem = listenedItem;
        contentView= LayoutInflater.from(context).inflate(layoutResID,null,false);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //提前设置Dialog的一些样式
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);//设置dialog显示居中
        dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);//设置动画效果
//        setContentView(layoutResID);

        setContentView(contentView);

        WindowManager windowManager = ((Activity)context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (display.getWidth()*0.8);// 设置dialog宽度为屏幕的0.8
        lp.height= (int) (display.getHeight()*0.7);
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);//点击外部Dialog消失
        //遍历控件id添加点击注册
        for(int id:listenedItem){
            findViewById(id).setOnClickListener(this);
        }
    }

    private OnCenterItemClickListener listener;
    public interface OnCenterItemClickListener {
        void OnCenterItemClick(CommonDialog dialog, View view);
    }
    //很明显我们要在这里面写个接口，然后添加一个方法
    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
        this.listener = listener;
    }


    public View getContentView(){
        return contentView;
    }

    @Override
    public void onClick(View v) {
//        dismiss();//注意：我在这里加了这句话，表示只要按任何一个控件的id,弹窗都会消失，不管是确定还是取消。
        listener.OnCenterItemClick(this,v);
    }
}
