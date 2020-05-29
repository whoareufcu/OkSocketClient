///*
// * Copyright 2018 Yan Zhenjie.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.example.oksocketclient;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.util.Log;
//
//import com.codeos.utils.NetworkUtils;
//import com.codeos.utils.SharedPreferencesUtils;
//import com.google.gson.Gson;
//import com.jndv.andserver.component.LoginInterceptor;
//import com.jndv.andserver.model.CodeEvent;
//import com.jndv.andserver.util.FileUtils;
//import com.jndv.andserver.util.iputil.IPSetUtils;
//import com.jndv.bean.AndFaceList;
//import com.jndv.faceidentify.FaceIdentify;
//import com.jndv.faceidentify.facepass.FasspassUtil;
//import com.jndv.faceidentify.facepass.SettingVar;
//import com.jndv.faceidentify.facepass.SqlUtil;
//import com.jndv.greendao.FaceListEntity;
//import com.jndv.greendao.PlateListEntity;
//import com.jndv.kplayertest.MyApplication;
//import com.jndv.utils.ConstUtil;
//import com.jndv.utils.GreendaoUtil;
//import com.jndv.utils.LogUtil;
//import com.yanzhenjie.andserver.annotation.GetMapping;
//import com.yanzhenjie.andserver.annotation.PostMapping;
//import com.yanzhenjie.andserver.annotation.RequestMapping;
//import com.yanzhenjie.andserver.annotation.RequestParam;
//import com.yanzhenjie.andserver.annotation.ResponseBody;
//import com.yanzhenjie.andserver.annotation.RestController;
//import com.yanzhenjie.andserver.http.HttpRequest;
//import com.yanzhenjie.andserver.http.HttpResponse;
//import com.yanzhenjie.andserver.http.cookie.Cookie;
//import com.yanzhenjie.andserver.http.multipart.MultipartFile;
//import com.yanzhenjie.andserver.http.session.Session;
//import com.yanzhenjie.andserver.util.MediaType;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import megvii.facepass.FacePassException;
//import megvii.facepass.FacePassHandler;
//import megvii.facepass.types.FacePassAddFaceResult;
//import megvii.facepass.types.FacePassConfig;
//
//@RestController
//@RequestMapping()
//class TestController {
//
//    /**
//     * 登录
//     *
//     * @param request
//     * @param response
//     * @param password
//     * @return
//     */
//    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    String login(HttpRequest request, HttpResponse response,
//                 @RequestParam(name = "password") String password) {
//
//        String pass = FileUtils.getPassword();
//        if (pass.equals(password)) {
//            //登录成功，保存cookie和session
//            Session session = request.getValidSession();
//            session.setAttribute(LoginInterceptor.LOGIN_ATTRIBUTE, true);
//
//            Cookie cookie = new Cookie("password", password);
//            cookie.setMaxAge(60 * 10);//有效期
//            response.addCookie(cookie);
//
//            response.sendRedirect("set.html");
//            return "Login 成功";
//        } else {
//
//            response.sendRedirect("login.html");
//            return "Login failed.";
//        }
//
//    }
//
//    /**
//     * 获取登录密码
//     *
//     * @return
//     */
//    @GetMapping(path = "/getPassword")
//    String getPassword() {
//        Map<String, String> map = new HashMap<>();
//        map.put("password", FileUtils.getPassword());
//        return new Gson().toJson(map);
//
//    }
//
//    @PostMapping(path = "/resetPassword")
//    String resetPassword(HttpResponse response,
//                         @RequestParam(name = "newPassword") String newPassword
//    ) {
//
//        if (response.getStatus() == 200) {
//            FileUtils.saveConfig(true, newPassword);
//            response.sendRedirect("login.html");
//        }
//
//        return newPassword;
//    }
//
//    @GetMapping(path = "/getSet")
//    String getSet() {
//        Map<String, String> map = new HashMap<>();
//        map.put(ConstUtil.KEY_DEVICE_ID, SharedPreferencesUtils.getString(ConstUtil.KEY_DEVICE_ID, NetworkUtils.getMacAddress() == null ? ConstUtil.DEFAULT_DEVICES_ID : NetworkUtils.getMacAddress().replace(":", "")));
//        map.put(ConstUtil.KEY_EXIT_NO, SharedPreferencesUtils.getString(ConstUtil.KEY_EXIT_NO, ConstUtil.DEFAULT_ROAD_NUM));
//        map.put(ConstUtil.KEY_SERVERS_IP, SharedPreferencesUtils.getString(ConstUtil.KEY_SERVERS_IP, ConstUtil.DEFAULT_SERVERS_IP));
//        map.put(ConstUtil.KEY_SERVERS_PORT, SharedPreferencesUtils.getString(ConstUtil.KEY_SERVERS_PORT, ConstUtil.DEFAULT_SERVERS_PORT));
//        //人脸1
//        map.put(ConstUtil.KEY_SERVERS_ACCOUNT_FACE_1, SharedPreferencesUtils.getString(ConstUtil.KEY_SERVERS_ACCOUNT_FACE_1, ConstUtil.DEFAULT_SERVERS_ACCOUNT));
//        map.put(ConstUtil.KEY_SERVERS_PASSWORD_FACE_1, SharedPreferencesUtils.getString(ConstUtil.KEY_SERVERS_PASSWORD_FACE_1, ConstUtil.DEFAULT_SERVERS_PASSWORD));
//        map.put(ConstUtil.KEY_SERVERS_URL_FACE_1, SharedPreferencesUtils.getString(ConstUtil.KEY_SERVERS_URL_FACE_1, ConstUtil.DEFAULT_RTSP_ADDRESS_FACE_1));
//        map.put(ConstUtil.KEY_SERVERS_URL_TYPE_FACE_1, SharedPreferencesUtils.getInt(ConstUtil.KEY_SERVERS_URL_TYPE_FACE_1, 1) + "");
//        map.put(ConstUtil.KEY_SERVERS_MIN_SIZE_FACE_1, SharedPreferencesUtils.getInt(ConstUtil.KEY_SERVERS_MIN_SIZE_FACE_1, ConstUtil.DEFAULT_MIN_FACE_SIZE) + "");
//        //人脸2
//        map.put(ConstUtil.KEY_SERVERS_ACCOUNT_FACE_2, SharedPreferencesUtils.getString(ConstUtil.KEY_SERVERS_ACCOUNT_FACE_2, ConstUtil.DEFAULT_SERVERS_ACCOUNT));
//        map.put(ConstUtil.KEY_SERVERS_PASSWORD_FACE_2, SharedPreferencesUtils.getString(ConstUtil.KEY_SERVERS_PASSWORD_FACE_2, ConstUtil.DEFAULT_SERVERS_PASSWORD));
//        map.put(ConstUtil.KEY_SERVERS_URL_FACE_2, SharedPreferencesUtils.getString(ConstUtil.KEY_SERVERS_URL_FACE_2, ConstUtil.DEFAULT_RTSP_ADDRESS_FACE_2));
//        map.put(ConstUtil.KEY_SERVERS_URL_TYPE_FACE_2, SharedPreferencesUtils.getInt(ConstUtil.KEY_SERVERS_URL_TYPE_FACE_2, 1) + "");
//        map.put(ConstUtil.KEY_SERVERS_MIN_SIZE_FACE_2, SharedPreferencesUtils.getInt(ConstUtil.KEY_SERVERS_MIN_SIZE_FACE_2, ConstUtil.DEFAULT_MIN_FACE_SIZE) + "");
//
//        //车牌1
//        map.put(ConstUtil.KEY_SERVERS_ACCOUNT_PLATE_1, SharedPreferencesUtils.getString(ConstUtil.KEY_SERVERS_ACCOUNT_PLATE_1, ConstUtil.DEFAULT_SERVERS_ACCOUNT));
//        map.put(ConstUtil.KEY_SERVERS_PASSWORD_PLATE_1, SharedPreferencesUtils.getString(ConstUtil.KEY_SERVERS_PASSWORD_PLATE_1, ConstUtil.DEFAULT_SERVERS_PASSWORD));
//        map.put(ConstUtil.KEY_SERVERS_URL_PLATE_1, SharedPreferencesUtils.getString(ConstUtil.KEY_SERVERS_URL_PLATE_1, ConstUtil.DEFAULT_RTSP_ADDRESS_PLATE_1));
//        map.put(ConstUtil.KEY_SERVERS_URL_TYPE_PLATE_1, SharedPreferencesUtils.getInt(ConstUtil.KEY_SERVERS_URL_TYPE_PLATE_1, 1) + "");
//        //车牌2
//        map.put(ConstUtil.KEY_SERVERS_ACCOUNT_PLATE_2, SharedPreferencesUtils.getString(ConstUtil.KEY_SERVERS_ACCOUNT_PLATE_2, ConstUtil.DEFAULT_SERVERS_ACCOUNT));
//        map.put(ConstUtil.KEY_SERVERS_PASSWORD_PLATE_2, SharedPreferencesUtils.getString(ConstUtil.KEY_SERVERS_PASSWORD_PLATE_2, ConstUtil.DEFAULT_SERVERS_PASSWORD));
//        map.put(ConstUtil.KEY_SERVERS_URL_PLATE_2, SharedPreferencesUtils.getString(ConstUtil.KEY_SERVERS_URL_PLATE_2, ConstUtil.DEFAULT_RTSP_ADDRESS_PLATE_2));
//        map.put(ConstUtil.KEY_SERVERS_URL_TYPE_PLATE_2, SharedPreferencesUtils.getInt(ConstUtil.KEY_SERVERS_URL_TYPE_PLATE_2, 1) + "");
//
//
//        //大值
//        map.put("faceX1", SharedPreferencesUtils.getInt(ConstUtil.KEY_RANGE_X_FACE_1) + "");
//        map.put("faceY1", SharedPreferencesUtils.getInt(ConstUtil.KEY_RANGE_Y_FACE_1) + "");
//        map.put("faceW1", SharedPreferencesUtils.getInt(ConstUtil.KEY_RANGE_W_FACE_1, 1920) + "");
//        map.put("faceH1", SharedPreferencesUtils.getInt(ConstUtil.KEY_RANGE_H_FACE_1, 1080) + "");
//        map.put("faceX2", SharedPreferencesUtils.getInt(ConstUtil.KEY_RANGE_X_FACE_2) + "");
//        map.put("faceY2", SharedPreferencesUtils.getInt(ConstUtil.KEY_RANGE_Y_FACE_2) + "");
//        map.put("faceW2", SharedPreferencesUtils.getInt(ConstUtil.KEY_RANGE_W_FACE_2, 1920) + "");
//        map.put("faceH2", SharedPreferencesUtils.getInt(ConstUtil.KEY_RANGE_H_FACE_2, 1080) + "");
//
//
//        int[] videoWidths = MyApplication.plateIdentifyActivity.getVideoWidth();
//        if (videoWidths[0] == 0) {
//            videoWidths[0] = 1920;
//        }
//        if (videoWidths[1] == 0) {
//            videoWidths[1] = 1920;
//        }
//        map.put("faceXSW1", videoWidths[0] + "");
//        map.put("faceXSW2", videoWidths[1] + "");
//
//        map.put(ConstUtil.KEY_LOCAL_IP_ADDRESS, SharedPreferencesUtils.getString(ConstUtil.KEY_LOCAL_IP_ADDRESS, ""));
//        map.put(ConstUtil.KEY_LOCAL_NETMASK, SharedPreferencesUtils.getString(ConstUtil.KEY_LOCAL_NETMASK, ""));
//        map.put(ConstUtil.KEY_LOCAL_GATEWAY, SharedPreferencesUtils.getString(ConstUtil.KEY_LOCAL_GATEWAY, ""));
//        map.put(ConstUtil.KEY_LOCAL_DNS1, SharedPreferencesUtils.getString(ConstUtil.KEY_LOCAL_DNS1, ""));
//        map.put(ConstUtil.KEY_LOCAL_DNS2, SharedPreferencesUtils.getString(ConstUtil.KEY_LOCAL_DNS2, ""));
//
//        String mainIp = SharedPreferencesUtils.getString(ConstUtil.MAIN_DEVICE_IP, ConstUtil.DEFAULT_MAIN_DEVICE_IP);
//        String localIp = IPSetUtils.getIpAddress();
//
//        boolean isMain = mainIp.equals(localIp);
//
//        map.put("deviceType", isMain ? "1" : "2");
//        map.put("mainIp", mainIp);
//        Log.e("getset", new Gson().toJson(map));
//        return new Gson().toJson(map);
//
//    }
//
//    //保存人脸识别区数据
//    @PostMapping(path = "/setFaceOCR")
//    String setFaceOCR(HttpResponse response,
//                      @RequestParam(name = "faceX1") String faceX1,
//                      @RequestParam(name = "faceY1") String faceY1,
//                      @RequestParam(name = "faceH1") String faceH1,
//                      @RequestParam(name = "faceW1") String faceW1,
//                      @RequestParam(name = "faceX2") String faceX2,
//                      @RequestParam(name = "faceY2") String faceY2,
//                      @RequestParam(name = "faceW2") String faceW2,
//                      @RequestParam(name = "faceH2") String faceH2
//
//    ) {
//        EventBus.getDefault().post(new CodeEvent(7, "获取最新分辨率"));
//        if (response.getStatus() == 200) {
//            //大值
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_X_FACE_1, Integer.parseInt(faceX1) / 2 * 2);
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_Y_FACE_1, Integer.parseInt(faceY1) / 2 * 2);
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_W_FACE_1, Integer.parseInt(faceW1) / 2 * 2);
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_H_FACE_1, Integer.parseInt(faceH1) / 2 * 2);
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_X_FACE_2, Integer.parseInt(faceX2) / 2 * 2);
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_Y_FACE_2, Integer.parseInt(faceY2) / 2 * 2);
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_W_FACE_2, Integer.parseInt(faceW2) / 2 * 2);
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_H_FACE_2, Integer.parseInt(faceH2) / 2 * 2);
//
//            int[] videoWidths = MyApplication.plateIdentifyActivity.getVideoWidth();
//            if (videoWidths[0] == 0) {
//                videoWidths[0] = 1920;
//            }
//            if (videoWidths[1] == 0) {
//                videoWidths[1] = 1920;
//            }
//
//            int face_times_1 = videoWidths[0] / 320;
//            int face_times_2 = videoWidths[1] / 320;
//            if (face_times_1 == 0) {
//                face_times_1 = 3;
//            }
//            if (face_times_2 == 0) {
//                face_times_2 = 3;
//            }
//            LogUtil.e("AndServerSample face_times_1:" + face_times_1 + ",,face_times_2" + face_times_2);
//            //小值
//            SharedPreferencesUtils.saveInt("pos_x_faceidt_1", (Integer.parseInt(faceX1) / face_times_1) / 2 * 2);
//            SharedPreferencesUtils.saveInt("pos_y_faceidt_1", (Integer.parseInt(faceY1) / face_times_1) / 2 * 2);
//            SharedPreferencesUtils.saveInt("width_faceidt_1", (Integer.parseInt(faceW1) / face_times_1) / 2 * 2);
//            SharedPreferencesUtils.saveInt("height_faceidt_1", (Integer.parseInt(faceH1) / face_times_1) / 2 * 2);
//            SharedPreferencesUtils.saveInt("pos_x_faceidt_2", (Integer.parseInt(faceX2) / face_times_2) / 2 * 2);
//            SharedPreferencesUtils.saveInt("pos_y_faceidt_2", (Integer.parseInt(faceY2) / face_times_2) / 2 * 2);
//            SharedPreferencesUtils.saveInt("width_faceidt_2", (Integer.parseInt(faceW2) / face_times_2) / 2 * 2);
//            SharedPreferencesUtils.saveInt("height_faceidt_2", (Integer.parseInt(faceH2) / face_times_2) / 2 * 2);
//            SharedPreferencesUtils.saveBoolean(" saved_regioin_faceidt", true);
//            SharedPreferencesUtils.saveBoolean(" saved_regioin_plateidt", true);
//
//            EventBus.getDefault().post(new CodeEvent(1, "修改设置数据"));
//            return "保存成功";
//        }
//
//        return "保存失败";
//
//    }
//
//    //保存服务器数据
//    @PostMapping(path = "/setServer")
//    String setServer(HttpResponse response,
//                     @RequestParam(name = "deviceId") String deviceId,
//                     @RequestParam(name = "exitNo") String exitNo,
//                     @RequestParam(name = "serverIp") String serverIp,
//                     @RequestParam(name = "serverPort") String serverPort,
//                     @RequestParam(name = "faceServerAccount1") String faceServerAccount1,
//                     @RequestParam(name = "faceServerPassword1") String faceServerPassword1,
//                     @RequestParam(name = "faceUrlType1") String faceUrlType1,
//                     @RequestParam(name = "faceUrl1") String faceUrl1,
//                     @RequestParam(name = "faceSize1") String faceSize1,
//                     @RequestParam(name = "faceServerAccount2") String faceServerAccount2,
//                     @RequestParam(name = "faceServerPassword2") String faceServerPassword2,
//                     @RequestParam(name = "faceUrlType2") String faceUrlType2,
//                     @RequestParam(name = "faceUrl2") String faceUrl2,
//                     @RequestParam(name = "faceSize2") String faceSize2,
//
//                     @RequestParam(name = "carServerAccount1") String carServerAccount1,
//                     @RequestParam(name = "carServerPassword1") String carServerPassword1,
//                     @RequestParam(name = "carUrlType1") String carUrlType1,
//                     @RequestParam(name = "carUrl1") String carUrl1,
//
//                     @RequestParam(name = "carServerAccount2") String carServerAccount2,
//                     @RequestParam(name = "carServerPassword2") String carServerPassword2,
//                     @RequestParam(name = "carUrlType2") String carUrlType2,
//                     @RequestParam(name = "carUrl2") String carUrl2,
//
//                     @RequestParam(name = "deviceType") String deviceType,
//                     @RequestParam(name = "mainIp") String mainIp
//    ) {
//
//        if (response.getStatus() == 200) {
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_DEVICE_ID, deviceId.replaceAll(" ", ""));
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_EXIT_NO, exitNo.replaceAll(" ", ""));
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_SERVERS_IP, serverIp.replaceAll(" ", ""));
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_SERVERS_PORT, serverPort.replaceAll(" ", ""));
//
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_SERVERS_ACCOUNT_FACE_1, faceServerAccount1.replaceAll(" ", ""));
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_SERVERS_PASSWORD_FACE_1, faceServerPassword1.replaceAll(" ", ""));
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_SERVERS_URL_TYPE_FACE_1, Integer.valueOf(faceUrlType1));
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_SERVERS_URL_FACE_1, faceUrl1.replaceAll(" ", ""));
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_SERVERS_MIN_SIZE_FACE_1, Integer.valueOf(faceSize1));
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_SERVERS_ACCOUNT_FACE_2, faceServerAccount2.replaceAll(" ", ""));
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_SERVERS_PASSWORD_FACE_2, faceServerPassword2.replaceAll(" ", ""));
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_SERVERS_URL_TYPE_FACE_2, Integer.valueOf(faceUrlType2));
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_SERVERS_URL_FACE_2, faceUrl2.replaceAll(" ", ""));
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_SERVERS_MIN_SIZE_FACE_2, Integer.valueOf(faceSize2));
//
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_SERVERS_ACCOUNT_PLATE_1, carServerAccount1.replaceAll(" ", ""));
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_SERVERS_PASSWORD_PLATE_1, carServerPassword1.replaceAll(" ", ""));
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_SERVERS_URL_TYPE_PLATE_1, Integer.valueOf(carUrlType1));
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_SERVERS_URL_PLATE_1, carUrl1.replaceAll(" ", ""));
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_SERVERS_ACCOUNT_PLATE_2, carServerAccount2.replaceAll(" ", ""));
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_SERVERS_PASSWORD_PLATE_2, carServerPassword2.replaceAll(" ", ""));
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_SERVERS_URL_TYPE_PLATE_2, Integer.valueOf(carUrlType2));
//            SharedPreferencesUtils.saveString(ConstUtil.KEY_SERVERS_URL_PLATE_2, carUrl2.replaceAll(" ", ""));
//
//            SharedPreferencesUtils.saveBoolean("startup", true);
//
//            EventBus.getDefault().post(new CodeEvent(2, "修改设置数据"));
//
//            SharedPreferencesUtils.saveInt("pos_x_faceidt_1", 0);
//            SharedPreferencesUtils.saveInt("pos_y_faceidt_1", 0);
//            SharedPreferencesUtils.saveInt("width_faceidt_1", 100);
//            SharedPreferencesUtils.saveInt("height_faceidt_1", 100);
//            SharedPreferencesUtils.saveInt("pos_x_faceidt_2", 0);
//            SharedPreferencesUtils.saveInt("pos_y_faceidt_2", 0);
//            SharedPreferencesUtils.saveInt("width_faceidt_2", 100);
//            SharedPreferencesUtils.saveInt("height_faceidt_2", 100);
//
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_X_FACE_1, 0);
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_Y_FACE_1, 0);
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_W_FACE_1, 300);
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_H_FACE_1, 300);
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_X_FACE_2, 0);
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_Y_FACE_2, 0);
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_W_FACE_2, 300);
//            SharedPreferencesUtils.saveInt(ConstUtil.KEY_RANGE_H_FACE_2, 300);
//
//            /*SharedPreferencesUtils.saveInt(ConstUtil.VIDEO_URL_TYPE, Integer.valueOf(urlType));
//            SharedPreferencesUtils.saveInt(ConstUtil.MIN_FACE_SIZE, Integer.valueOf(faceSize));
//*/
//            if (deviceType.equals("2")) {
//                SharedPreferencesUtils.saveString(ConstUtil.MAIN_DEVICE_IP, mainIp);
//                SharedPreferencesUtils.saveBoolean("isMain", false);
//            } else if (deviceType.equals("1")) {
//                String localIp = IPSetUtils.getIpAddress();
//                SharedPreferencesUtils.saveString(ConstUtil.MAIN_DEVICE_IP, localIp);
//                SharedPreferencesUtils.saveBoolean("isMain", true);
//            }
//
//            System.exit(0);
//            android.os.Process.killProcess(android.os.Process.myPid());
//
//            return "保存成功";
//        }
//
//        return "保存失败";
//
//    }
//
//    //保存设备数据
//    @PostMapping(path = "/setDevice")
//    @ResponseBody
//    String setDevice(
//            @RequestParam(name = "type") String type,
//            @RequestParam(name = "localIpAddress", required = false) String localIpAddress,
//            @RequestParam(name = "localNetmask", required = false) String localNetmask,
//            @RequestParam(name = "localGateway", required = false) String localGateway,
//            @RequestParam(name = "localDns1", required = false) String localDns1,
//            @RequestParam(name = "localDns2", required = false) String localDns2,
//            HttpResponse response) {
//
//        LogUtil.e("localIpAddress" + localIpAddress);
//        if (response.getStatus() == 200) {
//            LogUtil.e("ip response.getStatus() 200");
//            if (type.equals("1")) {//切换成动态ip
//                EventBus.getDefault().post(new CodeEvent(4, "修改设置数据"));
//            } else if (type.equals("2")) {//设置静态ip
//                //静态ip地址
//                SharedPreferencesUtils.saveString(ConstUtil.KEY_LOCAL_IP_ADDRESS, localIpAddress.replaceAll(" ", ""));
//                SharedPreferencesUtils.saveString(ConstUtil.KEY_LOCAL_NETMASK, localNetmask.replaceAll(" ", ""));
//                SharedPreferencesUtils.saveString(ConstUtil.KEY_LOCAL_GATEWAY, localGateway.replaceAll(" ", ""));
//                SharedPreferencesUtils.saveString(ConstUtil.KEY_LOCAL_DNS1, localDns1.replaceAll(" ", ""));
//                SharedPreferencesUtils.saveString(ConstUtil.KEY_LOCAL_DNS2, localDns2.replaceAll(" ", ""));
//
//                EventBus.getDefault().post(new CodeEvent(5, "修改设置数据"));
//            }
//            return "保存成功";
//        }
//
//        return "保存失败";
//
//    }
//
//    @GetMapping(path = "/getFacePic")
//    String getFacePic() {
//        Map<String, String> map = new HashMap<>();
//        LogUtil.e("07x33 111111111111");
//        // String picName = MyApplication.plateIdentifyActivity.getPic();
//        String facePic1 = MyApplication.plateIdentifyActivity.getfacePic(1);
//        String facePic2 = MyApplication.plateIdentifyActivity.getfacePic(2);
//        LogUtil.e("07x33 222222222222");
//        LogUtil.e("07x33 33333333333");
//        map.put("pic1", facePic1);
//        map.put("pic2", facePic2);
//        LogUtil.e("07x33 444444444444444");
//        return new Gson().toJson(map);
//
//    }
//
//    @GetMapping(path = "/requestPic")
//    void requestPic() {
//        EventBus.getDefault().post(new CodeEvent(6, "获取图片"));
//    }
//
//    @PostMapping(path = "/onFacepassAuth")
//    String onFacepassAuth() {
//        FaceIdentify.initSDK(MyApplication.getInstances());
//        Map<String, String> map = new HashMap<>();
//        map.put("isAuthorized", FacePassHandler.isAuthorized() + "");
////        map.put("isAuthorized", "false");
//        LogUtil.e("onFacepassAuth::" + FacePassHandler.isAuthorized() + "xxxxx" + new Gson().toJson(map));
//        return new Gson().toJson(map);
//
//    }
//
//    @PostMapping(path = "/faceIn")
//    String faceInput(HttpRequest request, HttpResponse response,
//                     @RequestParam(name = "file") MultipartFile file,
//                     @RequestParam(name = "name") String name,
//                     @RequestParam(name = "idcard") String idcard,
//                     @RequestParam(name = "flag") String flag) {
//        Session session = request.getValidSession();
//        session.setAttribute(LoginInterceptor.LOGIN_ATTRIBUTE, true);
//        FaceListEntity user = new FaceListEntity();
//        user.setID(UUID.randomUUID().toString());
//        user.setName(name);
//        user.setIDCard(idcard);
//        user.setFlag(flag);
//        try {
//            user.setHeadImg(file.getBytes());
//
//
//            if (file != null && file.getBytes().length > 0) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(file.getBytes(), 0, file.getBytes().length);
//                FacePassAddFaceResult result = null;
//                FacePassConfig config = FasspassUtil.getConfig();
//                if (config == null) {
//                    return "facepass初始化失败";
//                }
//                FacePassHandler handler = new FacePassHandler(config);
//                try {
//                    result = handler.addFace(bitmap);
//                } catch (FacePassException e) {
//                    e.printStackTrace();
//                }
//                if (result != null) {
//                    Log.e("result", result.result + "++");
//                    if (result.result == 0) {
//                        byte[] faceToken = result.faceToken;
//                        if (faceToken == null || faceToken.length == 0) {
//                            return "人脸参数不正确";
//                        }
//                        boolean b = handler.bindGroup(SettingVar.GROUP_NAME, faceToken);
//                        if (b) {
//                            //sqLdm.update("Face","faceToken","ID", Arrays.toString(faceToken),users.get(i).getID());
//
//                            new SqlUtil("").update(new String(faceToken), user.getID() + "");
//                            //user.setHeadByte(null);
//                            GreendaoUtil.getInstance().insertFace(user);
//
//                        }
//                        return "人脸插入成功";
//                    } else if (result.result == 1) {
//                        return "没有检测到人脸";
//                    } else if (result.result == 2) {
//                        return "检测到人脸，但是没有通过质量判断";
//                    }
//                }
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (FacePassException e) {
//            e.printStackTrace();
//        }
//
//        return "人脸插入失败";
//    }
//
//    @PostMapping(path = "/plateIn")
//    String plateInput(HttpRequest request, HttpResponse response,
//                      @RequestParam(name = "carnum") String carnum,
//                      @RequestParam(name = "pinpai") String pinpai,
//                      @RequestParam(name = "flag") String flag) {
//        if (response.getStatus() == 200) {
//            PlateListEntity entity = new PlateListEntity();
//            entity.setCarNum(carnum);
//            entity.setPinPai(pinpai);
//            entity.setFlag(flag);
//            entity.setID(UUID.randomUUID().toString());
//            GreendaoUtil.getInstance().insertPlate(entity);
//            return "车牌插入成功";
//        }
//
//        return "车牌插入失败";
//    }
//
//    @PostMapping(path = "/faceQuery")
//    String faceQuery(HttpRequest request, HttpResponse response,
//                     @RequestParam(name = "name", required = false) String name,
//                     @RequestParam(name = "idcard", required = false) String idcard,
//                     @RequestParam(name = "flag") String flag) {
//        Session session = request.getValidSession();
//        session.setAttribute(LoginInterceptor.LOGIN_ATTRIBUTE, true);
//        if (response.getStatus() == 200) {
//            List<FaceListEntity> faceList = GreendaoUtil.getInstance().queryFaceList(name, idcard, flag);
//            List<AndFaceList> result = new ArrayList<>();
//            AndFaceList face = null;
//            String type = "";
//            for (FaceListEntity entity : faceList) {
//                face = new AndFaceList();
//                face.setId(entity.getID());
//                face.setName(entity.getName());
//                face.setIdcard(entity.getIDCard());
//                if (entity.getFlag().equals("1")){
//                    type="白名单";
//                } else if (entity.getFlag().equals("2")) {
//                    type = "黑名单";
//                } else if (entity.getFlag().equals("3")) {
//                    type = "陌生人";
//                }
//                face.setFlag(type);
//                result.add(face);
//
//            }
//            return new Gson().toJson(result);
//        }
//
//        return "人脸查询失败";
//    }
//
//    @PostMapping(path = "/plateQuery")
//    String plateQuery(HttpRequest request, HttpResponse response,
//                      @RequestParam(name = "carnum", required = false) String carnum,
//                      @RequestParam(name = "pinpai", required = false) String pinpai,
//                      @RequestParam(name = "flag") String flag) {
//        if (response.getStatus() == 200) {
//            List<PlateListEntity> plateList = GreendaoUtil.getInstance().queryPlateList(carnum, pinpai, flag);
//            return new Gson().toJson(plateList);
//        }
//
//        return "车牌查询失败";
//    }
//}