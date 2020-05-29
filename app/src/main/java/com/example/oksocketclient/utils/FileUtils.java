/*
 * Copyright 2018 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.oksocketclient.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by YanZhenjie on 2018/6/9.
 */
public class FileUtils {

    private static final String TAG = "FileUtils";


    public static void saveConfig(boolean isCovered, String password) {
        File file = new File(Environment.getExternalStorageDirectory(),
                "config.xml");
        Log.e(TAG, file.getAbsolutePath());

        if (!isCovered && file.exists()) {
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            // 获取xml序列化器
            XmlSerializer xs = Xml.newSerializer();
            xs.setOutput(fos, "utf-8");
            //生成xml头
            xs.startDocument("utf-8", true);
            //添加xml根节点
            xs.startTag(null, "config");

            xs.startTag(null, "password");
            xs.text(password);
            xs.endTag(null, "password");
            xs.endTag(null, "config");
            //生成xml头
            xs.endDocument();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getPassword() {
        String password = "";
        try {
            File path = new File(Environment.getExternalStorageDirectory(),
                    "config.xml");
            FileInputStream fis = new FileInputStream(path);

            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(fis, "utf-8");

            int eventType = parser.getEventType(); // 获得事件类型

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName(); // 获得当前节点的名称

                switch (eventType) {
                    case XmlPullParser.START_TAG: // 当前等于开始节点 <person>
                        if ("config".equals(tagName)) { // <config>
                        } else if ("password".equals(tagName)) { // <name>
                            password = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG: // </persons>
                        if ("config".equals(tagName)) {
                            Log.i(TAG, "password---" + password);
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next(); // 获得下一个事件类型
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        Log.e("pass", password);
        return password;
    }

    /**
     * 保存方法
     */
    public static void saveBitmap(Bitmap bm, String picName) {
        Log.e(TAG, "保存图片");
        File f = new File(Environment.getExternalStorageDirectory(), picName);
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //保存文本，重写
    public static void saveTxtFile(String path, String txt) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(txt.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return * @Description: 根据图片地址转换为base64编码字符串     * @Author:     * @CreateTime:
     */
//    public static String getImageStr(String imgFile) {
//        InputStream inputStream = null;
//        byte[] data = null;
//        try {
//            inputStream = new FileInputStream(imgFile);
//            data = new byte[inputStream.available()];
//            inputStream.read(data);
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }        // 加密
//        BASE64Encoder encoder = new BASE64Encoder();
//        return encoder.encode(data);
//    }

    //判断文件是否存在
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;

    }

}