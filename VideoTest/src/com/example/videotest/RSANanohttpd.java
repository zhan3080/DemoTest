package com.example.videotest;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RSANanohttpd extends Service {
	private String TAG = "RSANanohttpd";
	private RsaHTTPD mMyHttpd;
	private Context mContext;
	private PrivateKey privateKey=null;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.mContext = getApplicationContext();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try
                {        	
        	        mMyHttpd = new RsaHTTPD( mContext,52277);
        	        mMyHttpd.start();
                }
                catch(Exception e)
                {
                	e.printStackTrace();
                	mMyHttpd = null;
                }
			}
		}).start();
	}
	public class RsaHTTPD extends NanoRsaHTTPD
    {

		public RsaHTTPD(Context c, int port) {
			super(c, port);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Response serve(String uri, Method method,
				Map<String, String> header, Map<String, String> parms,
				Map<String, String> files) {
			// TODO Auto-generated method stub
			Log.i(TAG, "uri="+uri);
			Log.i(TAG, "method:"+method.name().toString());
    		Log.i(TAG, "header="+header.toString());
    		Log.i(TAG, "parms="+parms.get("NanoHttpd.QUERY_STRING"));
			try {
				privateKey = RSAUtils.loadPrivateKey(RSAHandleActivity.PRIVATE_KEY);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             // 从文件中得到私钥
//             InputStream inPrivate = getResources().getAssets().open(pkcs8_rsa_private_key.pem);
//             PrivateKey privateKey = RSAUtils.loadPrivateKey(inPrivate);
             // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
             byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode((parms.get("NanoHttpd.QUERY_STRING")).toString()), privateKey);
             String decryptStr = new String(decryptByte);
             Log.i(TAG, "decryptStr="+decryptStr);
             JSONObject data1 = null;
			try {
				data1 = new JSONObject(decryptStr);
				Log.i(TAG, "data="+data1.getString("data"));
				Log.i(TAG, "url="+data1.getString("url"));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
//    		String msg = "<html><body><h1>Hello server</h1>\n";
//            Map<String, String> parms = session.getParms();
//            if (parms.get("username") == null) {
//                msg += "<form action='?' method='get'>\n" + "  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
//            } else {
//                msg += "<p>Hello, " + parms.get("username") + "!</p>";
//            }
//
//            msg += "</body></html>\n";
//             String msg = "<html><body><h1>Hello server</h1>\n";
////             Map<String, String> parms = new HashMap<String, String>();
//             Map<String, String> parm = new HashMap<String, String>();
//             if (parms.get("username") == null) {
//                 msg += "<form action='?' method='get'>\n" + "  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
//             } else {
//                 msg += "<p>Hello, " + parms.get("username") + "!</p>";
//             }
//
//             msg += "</body></html>\n";
//             
//             NanoRsaHTTPD.Response r = new NanoRsaHTTPD.Response(msg);
             
             JSONObject data = new JSONObject();
             try {
				data.put("status",200);
				data.put("msg","ok");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             String retData = DataEncryptData(data.toString());
             NanoRsaHTTPD.Response r = new NanoRsaHTTPD.Response(retData);
             return r;
		}
		
    }
	
	private String DataEncryptData(String data){
        PublicKey publicKey = null;
        try {
            publicKey = RSAUtils.loadPublicKey(RSAHandleActivity.PUCLIC_KEY);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] encryptByte = RSAUtils.encryptData(data.toString().getBytes(),publicKey);
        // 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换
        String retData = Base64Utils.encode(encryptByte);
        return retData;
//        return encryptByte.toString();
    }
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
