package com.example.oksocketclient.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.example.oksocketclient.LoginStatus;
import com.example.oksocketclient.ServerData;
import com.example.oksocketclient.bean.OkSocketPulseBean;
import com.example.oksocketclient.bean.OkSocketSendData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.socks.library.KLog;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.impl.client.action.ActionDispatcher;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
import com.xuhao.didi.socket.client.sdk.client.connection.NoneReconnect;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClient;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientIOCallback;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientPool;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerManager;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerShutdown;
import com.xuhao.didi.socket.server.action.ServerActionAdapter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OkSocketUtils {
    private static final String TAG = "OkSocketUtils";

    private ConnectionInfo mInfo;
    private IConnectionManager mManager;
    private IServerManager serverManager;
    private Map<String, String> tagMap = null;
    private ServerActionAdapter serverActionAdapter;
    private SocketActionAdapter adapter;
    private IClientIOCallback iClientIOCallback;

    public static OkSocketUtils getInstace() {
        return OkSocketUtilsHolder.instance;
    }

    private static class OkSocketUtilsHolder {
        private static final OkSocketUtils instance = new OkSocketUtils();
    }

    private static LoginStatus mLoginStatus;

    public void setLoginStatusListener(LoginStatus loginStatus){
        mLoginStatus=loginStatus;
    }

    private  void doLoginStatus(boolean isSucessful){
        mLoginStatus.getLoginStatus(isSucessful);
    }

    private static ServerData mServerData;
    public void setServerDataListener(ServerData serverData){
        mServerData=serverData;
    }
    private void doServerData(String str){
        mServerData.getServerData(str);
    }

    private OkSocketUtils() {
        this.tagMap = new HashMap<>();

        this.serverActionAdapter = new ServerActionAdapter() {
            @Override
            public void onServerListening(int serverPort) {
                super.onServerListening(serverPort);
            }

            @Override
            public void onClientConnected(IClient client, int serverPort, IClientPool clientPool) {
                super.onClientConnected(client, serverPort, clientPool);
                //当客户端连接时，需要添加回调.
                //所有IO操作都将通过此回调完成。
                //你最好用一个回调对象来完成这项工作，你将区分每个对象在这个回调中
                client.addIOCallback(iClientIOCallback);
                //客户端连接服务端时会生成一个tag 存起来 服务端往客户端发送消息时会用到
//                tagMap.put(client.getHostIp(),client.getUniqueTag());
                /**
                 * 部署设备的时候需要设置主设备 主设备充当服务器
                 * 不需要本地保存辅设备的ip 但是辅设备连接主设备的时候会生成tag 用于服务器(主设备)与客户端(辅设备)的通信
                 * 所以主设备需要把辅设备的ip和tag本地存起来  用于往辅设备通信时通过ip找到tag
                 * */
//                List<IpSave> list = DaoUtils.getInstance().queryIpSave(client.getHostIp());
//                if (!list.isEmpty()) {
//                    for (IpSave ipSave : list) {
//                        ipSave.setClinettag(client.getUniqueTag());
//                        DaoUtils.getInstance().updataIpSave(ipSave);
//
//                    }
//                } else {
//                    IpSave ipSave = new IpSave();
//                    ipSave.setIp(client.getHostIp());
//                    ipSave.setClinettag(client.getUniqueTag());
//                    DaoUtils.getInstance().addIpSave(ipSave);
//                }
                Log.e(TAG, "onClientConnected: " + client.getUniqueTag() + "-" + client.getHostIp());
            }

            @Override
            public void onClientDisconnected(IClient client, int serverPort, IClientPool clientPool) {
                super.onClientDisconnected(client, serverPort, clientPool);
                //当客户端断开连接时，需要删除回调。
                client.removeIOCallback(iClientIOCallback);
            }

            @Override
            public void onServerWillBeShutdown(int serverPort, IServerShutdown shutdown, IClientPool clientPool, Throwable throwable) {
                //This method will be called back when the server has to be closed due to exception information.
                //You should complete the server save and close work in this method.
                //You should call the server's `shutdown()` method after finishing the finishing work.
                Log.e(TAG, "服务端onServerWillBeShutdown:  " + serverPort);
                shutdown.shutdown();

            }

            @Override
            public void onServerAlreadyShutdown(int serverPort) {
                //This method will be called back when you call the shutdown method.
                Log.e(TAG, "服务端onServerAlreadyShutdown: " + serverPort);
            }
        };
        this.adapter = new SocketActionAdapter() {
            @Override
            public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                Log.e(TAG, "连接成功(客户端Connecting Successful)");

                sendHeartbeat();
            }

            @Override
            public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
                super.onSocketDisconnection(info, action, e);
                doLoginStatus(false);
                Log.e(TAG, "onSocketDisconnection: 连接失败\n"+e);
            }

            //接收数据
            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                Log.e(TAG, "onSocketReadResponse: head "+ Arrays.toString(data.getHeadBytes()) );
                String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
                Log.e(TAG, "客户端onSocketReadResponse: " +"数据量大小："+data.getBodyBytes().length);
//                Log.e(TAG, "客户端onSocketReadResponse: " +"数据："+str);
//                KLog.json(TAG,str);
                JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
                int cmd = jsonObject.get("cmd").getAsInt();
                //喂狗
                if (mManager != null) {
                    if (cmd == ConstantUtils.PILSE) {
                        mManager.getPulseManager().feed();
                        Log.e(TAG, "客户端onSocketReadResponse:收到" + info.getIp() + "应答后喂狗 喂狗成功");
                        doLoginStatus(true);
                    } else {
//                        String strdata = jsonObject.get("data").getAsString();
//                        Log.e(TAG, "客户端onSocketReadResponse: strdata" + strdata);
//                        if (cmd==1001){
//                            Log.e(TAG, "onSocketReadResponse: "+strdata);
//                            doServerData(strdata);
//                        }
                        doServerData(str);

//                        if (cmd == 101 || cmd == 102) {//考勤
//                            AttenceRecord attenceRecord = new Gson().fromJson(strdata, AttenceRecord.class);
//                            if (cmd == 101) {//101为考勤数据新增
//                                DaoUtils.getInstance().addAttenceRecord(attenceRecord);
//                            } else if (cmd == 102) {//102为考勤数据更新
//                                DaoUtils.getInstance().updataAttenceRecord(attenceRecord);
//                            }
//                        } else if (cmd==201){//人臉添加
//                                Face face=new Gson().fromJson(strdata,Face.class);
//                                DaoUtils.getInstance().addFaceData(face);
//                        }

                    }
                }
            }
        };
    }

    //连接
    public void mConnect(String ip, int port) {
        mInfo = new ConnectionInfo(ip, port);
        mManager=OkSocket.open(mInfo);
        final Handler handler = new Handler(Looper.getMainLooper());
        OkSocketOptions mOkOptions = new OkSocketOptions.Builder()
                .setReconnectionManager(OkSocketOptions.getDefault().getReconnectionManager())//设置重连管理器
                .setMaxReadDataMB(100) //最大读取数据的兆数(MB)
                .setReaderProtocol(new ReaderProtocol())//设置包头包体
                .setConnectTimeoutSecond(10) //连接超时时间(秒)
                .setPulseFrequency(1000*10)//设置心跳频率（毫秒）
                .setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
                    @Override
                    public void handleCallbackEvent(ActionDispatcher.ActionRunnable runnable) {
                    handler.post(runnable);
                    }
                })
                .build();
        mManager.option(mOkOptions);
        mManager.registerReceiver(adapter);
        mManager.connect();
        Log.e(TAG, "客户端 mConnect: 正在向服务端连接" );
    }

    public  void disConnect(){
        if (mManager!=null){
            mManager.disconnect();
            mManager.unRegisterReceiver(adapter);
        }
    }

    public static String bytesToHexString(byte... src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    //客户端发送数据
    public void clineSendData(int cmd, Object object) {
        Log.e(TAG, "客户端 clineSendData: 发送数据");
        mManager.send(new OkSocketSendData(cmd, object));
    }

    //发送心跳
    public void sendHeartbeat() {
        Log.e(TAG, "客户端 sendHeartbeat: 发送心跳");
//        OkSocketOptions okSocketOptions=new OkSocketOptions.Builder(mManager.getOption())
//                .setPulseFrequency(1000*60)
//                .build();
//        mManager.option(okSocketOptions);

        mManager
                .getPulseManager()
                .setPulseSendable(new OkSocketPulseBean())
                .pulse();//Start the heartbeat.
    }

    /**
     * 服务端
     */
    //服务端发送数据(全部发送)
    public void serverSendDataToAll(int cmd, Object object) {
        //After listen
        IClientPool pool = serverManager.getClientPool();
        pool.sendToAll(new OkSocketSendData(cmd, object));
    }

    //服务端发送数据
    public void serverSendDataToOne(String tag, ISendable sendable) {

        IClientPool pool = serverManager.getClientPool();
        //The tag you have set.If you never set the tag, the IP address will be the default tag
        IClient client = (IClient) pool.findByUniqueTag(tag);
        if (client != null) {
            client.send(sendable);
            Log.e(TAG, "serverSendDataToOne: ");
        } else {
            Log.e(TAG, "serverSendDataToOne: client=null");
        }
    }

//    服务端方法
    public void registerServerPort() {

        iClientIOCallback = new IClientIOCallback() {
            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool) {
                //参数originaldata是来自服务器的数据。
                //参数client是指哪个客户机接收到这个原始数据
                String str = new String(originalData.getBodyBytes(), Charset.forName("utf-8"));
                Log.e(TAG, "服务端 onClientRead: str " + str);
                JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
                int cmd = jsonObject.get("cmd").getAsInt();
                //收到心跳后应答
                if (cmd == ConstantUtils.PILSE) {
                    if (client != null) {
                        client.send(new OkSocketPulseBean());
                        Log.e(TAG, "服务端 onClientRead: 收到" + client.getHostIp() + "心跳  进行应答");
                    }
                } else {//接到客户端发来的数据如果不是心跳 需将此数据转发给其他客户端
                    String strdata = jsonObject.get("data").getAsString();
                    Log.e(TAG, "服务端 onClientRead: strdata" + strdata);
//                    if (cmd == 101 || cmd == 102) {//考勤
//                        AttenceRecord attenceRecord = new Gson().fromJson(strdata, AttenceRecord.class);
//                        if (cmd == 101) {//101为考勤数据新增
//                            DaoUtils.getInstance().addAttenceRecord(attenceRecord);
//                        } else if (cmd == 102) {//102为考勤数据更新
//                            DaoUtils.getInstance().updataAttenceRecord(attenceRecord);
//                        }
//                    } else if (cmd==201){//人臉添加
//                        Face face=new Gson().fromJson(strdata,Face.class);
//                        DaoUtils.getInstance().addFaceData(face);
//                    }
//                    //设备一发来的数据主设备接收到后需转发给另一台或多台设备
//                   for (IpSave ipSave: DaoUtils.getInstance().queryAllIp()){
//                        if (!ipSave.getIp().equals(client.getHostIp())){
//                            serverSendDataToOne(ipSave.getClinettag(), new OkSocketSendData(cmd, strdata));
//                        }
//                   }


//                    for (String key : tagMap.keySet()) {
//                        Log.e(TAG, "onClientRead: key  " + key + "  value" + tagMap.get(key));
//                        if (!key.equals(client.getHostIp())) {
//                            serverSendDataToOne(tagMap.get(key), new OkSocketSendData(cmd, strdata));
//                        }
//                    }

                }
            }

            @Override
            public void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool) {
                //参数sendable是已发送的数据。
                //参数client 是指发送此数据的客户机
            }
        };
        //注册端口号
        //You should prepare a unused port, and get `IRegister` first
        serverManager = OkSocket.server(ConstantUtils.PORT).registerReceiver(serverActionAdapter);
        Log.e(TAG, "服务端 registerServerPort: 注册端口");
        if (!serverManager.isLive()) {
            //Start listen the specific port
            serverManager.listen();
        }


    }

    class ReaderProtocol implements IReaderProtocol {

        @Override
        public int getHeaderLength() {
            return 4;
        }

        @Override
        public int getBodyLength(byte[] header, ByteOrder byteOrder) {

//            Log.e(TAG, "getBodyLength: "+header[0]+" "+header[1]+" "+header[2]+" "+header[3] );
//            Log.e(TAG, "getBodyLength: "+(((header[0] & 0x00ff) << 24) & 0xff000000)+" "+(((header[1] & 0x00ff) << 16) & 0x00ff0000)+" "+(((header[2] & 0x00ff) << 8) & 0x0000ff00)+" "+((header[3] & 0x00ff)) );
//            int result=(((header[1] & 0x00ff) << 16) & 0x00ff0000)+(((header[2] & 0x00ff) << 8) & 0x0000ff00)+((header[3] & 0x00ff));
//            Log.e(TAG, "getBodyLength: result "+result );
//            return result;
            /**
             * 5
             * */
            int result;
                 result = (((header[0] & 0x00ff) << 24) & 0xff000000)
                        | (((header[1] & 0x00ff) << 16) & 0x00ff0000)
                         | (((header[2] & 0x00ff) << 8) & 0x0000ff00)
                         | ((header[3] & 0x00ff));
            Log.e(TAG, "getBodyLength: result "+result );
                 return result;
            /**
             * 4
             * */
//            int cLen = ((header[2] & 0x00FF) << 8) & 0x0000FF00;
//            Log.e(TAG, "getBodyLength: cLen "+cLen );
//            cLen = (cLen | (header[3] & 0x00FF)) & 0x0000FFFF;
//            Log.e("gggg","-----"+  byteOrder.toString()+"--------cLen"+cLen);
//            return cLen;
            /**
             * 3
             * */
//            Log.e(TAG, "getBodyLength: "+ Arrays.toString(header) );
//            Log.e(TAG, "getBodyLength:23 "+header[2]+" "+header[3] );
//            return header[2]+header[3];
            /**
             * 2
             * */
//            int bodyLength=0;
//            Log.e(TAG, "getBodyLength01: "+header[0]+"  "+header[1] );
//            if (header[0]==0xFa&&header[1]==0x00){
//                byte lenBytes[]=new byte[2];
//                System.arraycopy(header,2,lenBytes,0,lenBytes.length);
//                bodyLength=byteToInt(lenBytes);
//            }
//            return bodyLength;

            /**
             * 1
             * */
//            int bodySize=0;
//            String strHeader=bytesToHexString(header);
//            if (strHeader.toLowerCase().contains("0b24")){
//                String size1=strHeader.substring(4,6);
//                String size2=strHeader.substring(6,8);
//                if (size1.endsWith("ff")){
//                    bodySize=Integer.parseInt(size1,16)+Integer.parseInt(size2,16);
//                }else {
//                    bodySize=Integer.parseInt(size1,16);
//                }
//            }
//            Log.e(TAG, "getBodyLength: "+bytesToHexString(header)+"  "+bodySize+"" );
//            if (bodySize>4){
//                return bodySize-4;
//            }else {
//                return bodySize;
//            }
        }
    }

    private int byteToInt(byte[]lenBytes){
        int len=(lenBytes[1]&0x00FF<<8)&0x0000FF00;
        len=(len|(lenBytes[0]&0xFF00))&0x0000FFFF;
        return len;
    }
}
