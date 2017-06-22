package com.https.server;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.util.Log;
//这套方案没走通
public class HttpsService extends Service {

	@Override
	public void onCreate() {
		new Thread(new Runnable() {
			public void run() {
				try {
		            AssetManager am = getAssets();
		            InputStream ins2 = am.open("server.cer");
//		            InputStream ins2 = am.open("android.kbs");
		            MyHttpd myHttpd = new MyHttpd(HttpsService.this, 5678);

		            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		            keyStore.load(ins2, null);

		            //读取证书,注意这里的密码必须设置
		            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		            keyManagerFactory.init(keyStore, "android".toCharArray());

		            myHttpd.makeSecure(NanoHTTPD.makeSSLSocketFactory(keyStore, keyManagerFactory), null);
		            myHttpd.start();


		        } catch (IOException e) {
		            Log.e("IOException", "Couldn't start server:\n" + e.getMessage());
		        } catch (NumberFormatException e) {
		            Log.e("NumberFormatException", e.getMessage());
		        } catch (KeyStoreException e) {
		            Log.e("HTTPSException", "HTTPS certificate error:\n " + e.getMessage());
		        } catch (NoSuchAlgorithmException e) {
		            Log.e("HTTPSException", "HTTPS certificate error:\n " + e.getMessage());
		        }catch (UnrecoverableKeyException e) {
		            Log.e("UnrecoverableKeyException", "UnrecoverableKeyException" + e.getMessage());
		        }   catch (CertificateException e) {
		            e.printStackTrace();
		        }
			}
		}).start();
	}
	
	
	
	public class MyHttpd extends NanoHTTPD
    {
    	public MyHttpd(Context c, int remoteport) throws IOException
    	{
    		super(c, remoteport);
    	}

		@Override
		public Response serve(String uri, Method method,
				Map<String, String> header, Map<String, String> parms,
				Map<String, String> files) {
			// TODO Auto-generated method stub
			return null;
		}
    }
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
