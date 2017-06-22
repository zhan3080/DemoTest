package com.example.videotest;

import https.MAPServerHTTPSUtils;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author Hzz
 *
 */
public class ClientService extends Service {

	private MAPServerHTTPSUtils mMAPServerHTTPSUtils;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		new Thread(new Runnable() 
		{	
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				mMAPServerHTTPSUtils = new MAPServerHTTPSUtils();
				if(null!=mMAPServerHTTPSUtils.initSSLAllWithHttpClient())
		        {
		          System.out.println("MAPServerHTTPSUtils,get json successfully!!");
		        }
				System.out.println("MAPServerHTTPSUtils,get json faile!!");
			}
		}).start();
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
