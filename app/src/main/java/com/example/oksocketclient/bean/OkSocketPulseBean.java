package com.example.oksocketclient.bean;


import android.util.Log;

import com.example.oksocketclient.utils.ConstantUtils;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class OkSocketPulseBean implements IPulseSendable {
    private String str = "pulse";
    private static final String TAG = "OkSocketPulseBean";
    public OkSocketPulseBean() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", ConstantUtils.PILSE);
            jsonObject.put("data"," ");
            str = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public byte[] parse() {

        byte[] body = str.getBytes(Charset.defaultCharset());
        Log.e(TAG, "parse: "+body.length );
        ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(body.length);
        bb.put(body);
        return bb.array();
    }
}