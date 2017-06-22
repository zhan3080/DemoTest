package com.example.icomettest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class DemoMainActivity extends Activity {

	final static String TAG= "DemoMainActivity";
    private Button button1;
    private Button button2;
    private Button button3;
    private static TextView mTextview;
    private ImageView qrImageView;
    private static VideoView mVideoView;
    private IcometConnectService mIcometConnectService;
    private ConnectionService connectionService;  
    private ICometConnectCallback callback;
    private boolean mIsBound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_main);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        mTextview = (TextView)findViewById(R.id.textView1);
        qrImageView = (ImageView)findViewById(R.id.imageView1);
//        mVideoView = (VideoView)findViewById(R.id.videoView);
        button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mTextview.setText("jobServerConnect");
				mIcometConnectService.jobServerConnect();
			}
		});
        button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mTextview.setText("connectServerConn");
				mIcometConnectService.connectServerConn();
			}
		});
        button3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				mTextview.setText("setqrImageView");
//				Bitmap qrBitmap = mIcometConnectService.setqrImageView();
//				qrImageView.setImageBitmap(qrBitmap);
				doUnbindService();
			}
		});
        Log.i(TAG, "******oncreat********");
        connectionService = new ConnectionService();
        callback = new ICometConnectCallback();
        doBindService();
        
    }
    
    
    private void startTestService(){
    	
    }
    
    private static void playUrl(String url){
    	 
        Uri uri = Uri.parse( url );
     
//        mVideoView = (VideoView)this.findViewById(R.id.videoView );
     
        //设置视频控制器
//        mVideoView.setMediaController(new MediaController(this));
     
        //播放完成回调
//        mVideoView.setOnCompletionListener( new MyPlayerOnCompletionListener());
     
        //设置视频路径
        mVideoView.setVideoURI(uri);
     
        //开始播放视频
        mVideoView.start();
    	return;
    }
    class MyPlayerOnCompletionListener implements OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			Toast.makeText( DemoMainActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
		}
      }
    /**  
     * 绑定服务  
     */  
    void doBindService() {  
    	Log.i(TAG, "******doBindService********");
        bindService(new Intent(DemoMainActivity.this,  
        		IcometConnectService.class), connectionService, 1);  
//    	startService(new Intent(DemoMainActivity.this,IcometConnectService.class));
        mIsBound = true;  
    }  
  
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		doUnbindService();
	}
	/**  
     * 解除绑定  
     */  
    void doUnbindService() {  
    	Log.i(TAG, "******doUnbindService********");
        if (mIsBound) {  
            unbindService(connectionService);  
            mIsBound = false;  
        }  
    }  
    class ConnectionService  implements ServiceConnection{

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			// TODO Auto-generated method stub
			Log.i(TAG, "******onServiceConnected********");
			mIcometConnectService = ((IcometConnectService.LocalBinder) arg1).getService();
			Log.i(TAG, "******mIcometConnectService=********"+mIcometConnectService);
			 if(mIcometConnectService!=null)
		     {
		        	mIcometConnectService.setDemoCallback(callback);
		     }
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, "******onServiceDisconnected********");
			mIcometConnectService = null;  
		}
    	
    }
    
    public static class ICometConnectCallback implements ICometConnectServiceCallback {


		@Override
		public void onMsgArrived(String msg) {
			// TODO Auto-generated method stub
			Log.i(TAG, "******msg=********"+msg);
//			mSetText(msg);
			Message message;
	    	message = mHandler.obtainMessage();//性能优化后
	        message.what=1;  
	        message.obj = msg;
	        mHandler.sendMessage(message);
		}
    }
    public static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
        	 switch (msg.what) {
	            case 0:
	            {
//	            	String s = mIcometConnect.getStrings();
//	            	mTextview.setText(s);
	            	mHandler.sendEmptyMessageDelayed(0, 2000);
	            	break;
	            }
	            case 1:
	            {
	            	String s = (String)msg.obj;
	            	mTextview.setText(s);
	            	if(s.startsWith("playUrl:")){
	            		String url = s.substring(s.indexOf(":")+1);
	            		Log.i(TAG, "******url=********"+url);
//	            		playUrl(url);
	            	}
	            	break;
	            }
        	 }
        }
    };


}
