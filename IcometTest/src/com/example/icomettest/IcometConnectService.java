package com.example.icomettest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.kyleduo.icomet.Channel;
import com.kyleduo.icomet.ChannelAllocator;
import com.kyleduo.icomet.ICometCallback;
import com.kyleduo.icomet.ICometClient;
import com.kyleduo.icomet.ICometConf;
import com.kyleduo.icomet.IConnCallback;
import com.kyleduo.icomet.message.Message;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class IcometConnectService extends Service{
	private final static String TAG= "IcometConnectService";
	private String JobServerUrl = "http://120.77.23.79:8080/HPAPI/device/sweep?cname=";
    private String packageName = "";
    private SharedPreferences prefMgr;
    private String mMac = "00:00:00:00:00:18";
    private String cnameReturn = "";
    private static String mCname = "";
    private static String mToken = "";
    private static int mSeq = 0;
    private String status;
    private String content;
    private String mHost;
    private String mPort;
    private String mUrl;
    private static ICometClient mClient;
    private static Context mContext;
    private final IBinder mBinder = new LocalBinder();  
    private static ICometConnectServiceCallback mCallback;
    private static boolean isConnect = false;
    @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "******onCreate********");
		mContext = this.getApplicationContext();
		prefMgr = PreferenceManager.getDefaultSharedPreferences(mContext);
		init();
    }
    @Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "******onDestroy********");
		if (mClient != null) {
			mClient.stopComet();
			mClient.stopConnect();
			mClient = null;
		}
	}
	private void init(){
        this.mCname = MD5(mMac+mContext.getApplicationContext().getPackageName());
        Log.i(TAG, "******mCname=********" + mCname);
        JobServerThread();
    }
    
    //job服务器认证；
    private void JobServerThread(){
    	new Thread(){
	       	 public void run() {
	       		 		//jdns.setFun27();
	       		Log.i(TAG, "******cnameReturn=********" + cnameReturn);
	       		while(!cnameReturn.contains("200"))
	        	{
	        		cnameReturn = jobServerGet(mCname);
	        		try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		Log.i(TAG, "******after Thread cnameReturn=********" + cnameReturn);
	        	}
	    		JsonPacket(cnameReturn);
	    		ConnectServerThread();
       	 	}
       	 }.start();
    }
    
    //connect server地址订阅
    private void ConnectServerThread(){
    	new Thread(){
	       	 public void run() {
	       			Log.i(TAG,"connectServerConn");	
	            	mClient = ICometClient.getInstance();
	            	if(mClient != null)
	            	{
		            	ICometConf conf = new ICometConf();
		            	if(conf!=null){
			        		conf.host = mHost;
			        		conf.port = mPort;
			        		conf.url = mUrl;
			        		conf.iConnCallback = new MyConnCallback();
			        		conf.iCometCallback = new MyCometCallback();
			        		conf.channelAllocator = new NoneAuthChannelAllocator();
			        		mClient.prepare(conf);
			        		mClient.connect();
		            	}
	            	}
      	 	}
      	 }.start(); 
    }
    
    
    
    private String jobServerGet(String deviceId){
    	String result = "";
        String readLine = null;
        String strUrl = JobServerUrl+deviceId;
        Log.i(TAG, "******strUrl=********" + strUrl);
        URL url = null;
        try {
            url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
            BufferedReader bufferRead = new BufferedReader(in);

            while ((readLine=bufferRead.readLine())!=null){
                result += readLine;
            }
            in.close();
            urlConn.disconnect();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    private void  JsonPacket(String jsonstring) {
		Log.i(TAG, "***JsonPacket");
		JSONObject object;
		try {
			object = new JSONObject(jsonstring);
			status = object.getString("status");
			content = object.getString("data");
			Log.i(TAG,"status="+status);
			Log.i(TAG,"data="+content);
			object = new JSONObject(content);
			mHost = object.getString("host");
			mPort = object.getString("port");
			mUrl = object.getString("url");
			mCname = object.getString("cname");
			mToken = object.getString("token");
			mSeq = object.getInt("seq");
			Log.i(TAG,"mHost="+mHost);
			Log.i(TAG,"mPort="+mPort);
			Log.i(TAG,"mUrl="+mUrl);
			Log.i(TAG,"mCname="+mCname);
			Log.i(TAG,"mToken="+mToken);
			Log.i(TAG,"mSeq="+mSeq);	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public void reconnect(){
    	Log.i(TAG,"reconnect");
    	if (mClient != null) {
			mClient.stopComet();
			mClient.stopConnect();
			mClient = null;
		}
    	if(!cnameReturn.equals("")){
    		cnameReturn = "";
    	}
    	JobServerThread();
    }
    
    public static class MyConnCallback implements IConnCallback {

		@Override
		public void onFail(String msg) {
			System.out.println("connection fail");
			System.err.println(msg);
			isConnect = false;
			callbackPrint(msg);
		}

		@Override
		public void onSuccess() {
			System.out.println("connection ok");
			callbackPrint("connection ok");
			isConnect = true;
			mClient.comet();
		}

		@Override
		public void onDisconnect() {
			System.err.println("connection has been cut off");
			isConnect = false;
		}

		@Override
		public void onStop() {
			System.out.println("client has been stopped");
			isConnect = false;
		}

		@Override
		public boolean onReconnect(int times) {
			System.err.println("This is the " + times + "st times.");
			if (times >= 3) {
				return true;
			}
			return false;
		}

		@Override
		public void onReconnectSuccess(int times) {
			System.out.println("onReconnectSuccess at " + times + "st time");
			mClient.comet();
		}

	}
	public static class MyCometCallback implements ICometCallback {

		@Override
		public void onDataMsgArrived(String content) {
			System.out.println("data msg arrived: " + content);
			if(content.startsWith("playUrl")){
				String url = content.substring(content.indexOf(":")+1);
        		Log.i(TAG, "******url=********"+url);
				Intent intent = new Intent();   
				 Bundle bundle = new Bundle(); 
				 bundle.putString("URL", url);  //url
			     intent.putExtras(bundle);
			     intent.setClass(mContext, DemoPlayer.class);
			     intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
				 mContext.startActivity(intent);
			}
//			mString = content.toString().trim();
			else{
				callbackPrint(content);
			}
		}

		@Override
		public void onMsgArrived(Message msg) {
			System.out.println("msg arrived: " + msg);
			callbackPrint(msg.toString());
		}

		@Override
		public void onErrorMsgArrived(Message msg) {
			System.err.println("error message arrived with type: " + msg.type);
			callbackPrint(msg.toString());
		}

		@Override
		public void onMsgFormatError() {
			System.err.println("message format error");
			callbackPrint("message format error");
		}

	}

	public static class NoneAuthChannelAllocator implements ChannelAllocator {

		@Override
		public Channel allocate() {
			Channel channel = new Channel();
			channel.cname = mCname;
			channel.token = mToken;
			channel.seq = mSeq;
			return channel;
		}
	}
    
    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String MD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        for (int i = 0; i < 16; i++)
            hash[i] ^= (byte) 0x78;
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
    
    
    public class LocalBinder extends Binder {  
        IcometConnectService getService() {  
            return IcometConnectService.this;  
        }  
    }  
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
    
    
    
    
    
    public static String getHostIP() {  
        
        String hostIp = null;  
        try {  
            Enumeration nis = NetworkInterface.getNetworkInterfaces();  
            InetAddress ia = null;  
            while (nis.hasMoreElements()) {  
                NetworkInterface ni = (NetworkInterface) nis.nextElement();  
                Enumeration<InetAddress> ias = ni.getInetAddresses();  
                while (ias.hasMoreElements()) {  
                    ia = ias.nextElement();  
                    if (ia instanceof Inet6Address) {  
                        continue;// skip ipv6  
                    }  
                    String ip = ia.getHostAddress();  
                    if (!"127.0.0.1".equals(ip)) {  
                        hostIp = ia.getHostAddress();  
                        break;  
                    }  
                }  
            }  
        } catch (SocketException e) {  
            Log.i("yao", "SocketException");  
            e.printStackTrace();  
        }  
        return hostIp;  
      
    }  
    
    public static Bitmap createQRCode(String qrUrl,int size,int padding) {
        Bitmap bitmapQR = null;
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix matrix = new MultiFormatWriter().encode(qrUrl,
                    BarcodeFormat.QR_CODE, size, size, hints);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            //
            boolean isFirstBlackPoint = false;
            int startX = 0;
            int startY = 0;
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (matrix.get(x, y)) {
                        if (isFirstBlackPoint == false) {
                            isFirstBlackPoint = true;
                            startX = x;
                            startY = y;
                        }
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);

            // // 鍓垏涓棿鐨勪簩缁寸爜鍖哄煙锛屽噺灏憄adding鍖哄煙
            if (startX <= padding) {
                return bitmap;
            }

            int x1 = startX - padding;
            int y1 = startY - padding;
            if (x1 < 0 || y1 < 0) {
                return bitmap;
            }

            int w1 = width - x1 * 2;
            int h1 = height - y1 * 2;

            bitmapQR = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmapQR;
    }
    
    public Bitmap setqrImageView(){
    	JSONObject jsondata = new JSONObject();
		try {
			jsondata.put("domain", prefMgr.getString(mContext.getPackageName()+"QRinfodomain", ""));
			jsondata.put("ip", getHostIP());
			jsondata.put("remotePort",prefMgr.getString(mContext.getPackageName()+"QRinforemoteport", "52266"));
			jsondata.put("cname", mCname);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e(TAG,jsondata.toString().trim() );
		Bitmap qrBitmap = createQRCode(jsondata.toString().trim(), 200, 0);
		return qrBitmap;
    }
    
	
	public void setDemoCallback(ICometConnectServiceCallback callback){
    	this.mCallback = callback;
    }
	private static void callbackPrint(String s){
		if(mCallback!=null){
			mCallback.onMsgArrived(s);
		}
		return;
	}
	Runnable networkTask = new Runnable() {  
  	  
        @Override  
        public void run() {  
            // TODO  
            // 在这里进行 http request.网络请求相关操作  
        	Log.i(TAG, "******mCname=********" + mCname);
        	cnameReturn = jobServerGet(mCname);
    		Log.i(TAG, "******cnameReturn=********" + cnameReturn);
    		JsonPacket(cnameReturn);
        }  
    };  
    public void jobServerConnect(){
    	packageName = mContext.getApplicationContext().getPackageName();
        Log.i(TAG, "******packageName=********" + packageName);
        mCname = MD5(mMac+packageName);
		new Thread(networkTask).start(); 
    }
    Runnable networkTask1 = new Runnable() {  
  	  
        @Override  
        public void run() {  
            // TODO  
            // 在这里进行 http request.网络请求相关操作  
        	Log.i(TAG,"connectServerConn");	
        	mClient = ICometClient.getInstance();
        	ICometConf conf = new ICometConf();
    		conf.host = mHost;
    		conf.port = mPort;
    		conf.url = mUrl;
    		conf.iConnCallback = new MyConnCallback();
    		conf.iCometCallback = new MyCometCallback();
    		conf.channelAllocator = new NoneAuthChannelAllocator();
    		mClient.prepare(conf);
    		mClient.connect();
        }  
    }; 
    public void connectServerConn(){
    	new Thread(networkTask1).start(); 
    	return;
    }
}
