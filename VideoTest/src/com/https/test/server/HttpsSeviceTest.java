package com.https.test.server;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

import com.example.videotest.R;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.util.Log;

public class HttpsSeviceTest extends Service {
	private static final String TAG = HttpsSeviceTest.class.getSimpleName();  
    private static final int  SERVER_PORT = 4567;  
    SoudboxServer soudboxServer;  
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		 soudboxServer = new SoudboxServer(SERVER_PORT);  
	    Log.i(TAG,"create server");  
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {  
                    Log.i(TAG,"server thread start ");  
                    createMySSLFactory();  
                    soudboxServer.start();  
                    Log.i(TAG, "getListeningPort="+soudboxServer.getListeningPort());
                   
                    Log.i(TAG,"server start");  
                } catch (IOException e) {  
                    e.printStackTrace();  
                } catch (CertificateException e) {  
                    e.printStackTrace();  
                } catch (NoSuchAlgorithmException e) {  
                    e.printStackTrace();  
                } catch (UnrecoverableKeyException e) {  
                    e.printStackTrace();  
                } catch (KeyStoreException e) {  
                    e.printStackTrace();  
                } catch (KeyManagementException e) {  
                    e.printStackTrace();  
                }  
            } 
			}).start();
		
	}

	private static final String KEYSTORE_PWD = "ssltest";  
	private void createMySSLFactory() throws NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException, KeyManagementException, CertificateException {  
	    InputStream inputStream = null;  
	    //选择安全协议的版本  
	    SSLContext ctx = SSLContext.getInstance("TLS");  
	    KeyManagerFactory keyManagers = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());  
//	    inputStream = getResources().openRawResource(R.raw.test);  
	    AssetManager am = getAssets();
	    inputStream = am.open("test.keystore");
	    //选择keystore的储存类型，andorid只支持BKS  
	    KeyStore ks = KeyStore.getInstance("BKS");  
	
	    ks.load(inputStream, KEYSTORE_PWD.toCharArray());  
	
	    keyManagers.init(ks, KEYSTORE_PWD.toCharArray());  
	
	    ctx.init(keyManagers.getKeyManagers(), null, null);  
	
	    SSLServerSocketFactory serverSocketFactory = ctx.getServerSocketFactory();  
	    soudboxServer.makeSecure(serverSocketFactory,null);  
	} 
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
