package com.example.oksocketclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oksocketclient.utils.ConstantUtils;
import com.example.oksocketclient.utils.DeviceAddressUtils;
import com.example.oksocketclient.utils.ExeCommand;
import com.example.oksocketclient.utils.OkSocketUtils;

import static java.lang.Thread.sleep;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private OkSocketUtils okSocketUtils;
    private EditText ipEditText;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        okSocketUtils = OkSocketUtils.getInstace();
        ipEditText = findViewById(R.id.ip_edit);
        progressDialog = new ProgressDialog(this);
        //设置ProgressDialog 是否可以按返回键取消；
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setTitle("正在加载 请稍后...");

        ipEditText.setText(DeviceAddressUtils.getIpAddress(this));

    }

    public void loginClick(View view) {
        progressDialog.show();
        okSocketUtils.mConnect(ipEditText.getText().toString(), ConstantUtils.PORT);
        okSocketUtils.setLoginStatusListener(new LoginStatus() {
            @Override
            public void getLoginStatus(boolean isSuccessful) {
                if (isSuccessful) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("isSuccessful", isSuccessful));
                    progressDialog.dismiss();
                } else {
                    Log.e(TAG, "setLoginStatusListener: 连接失败");
                    Toast.makeText(LoginActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        runCommand(ipEditText.getText().toString());
    }

    private void runCommand(String string) {
        ExeCommand cmd = new ExeCommand(false).run(string, 60000);
        while (cmd.isRunning()) {
            try {
                sleep(1000);
            } catch (Exception e) {

            }
            String buf = cmd.getResult();
            //do something
            Log.e(TAG, "runCommand: " + buf);
        }
    }

}
