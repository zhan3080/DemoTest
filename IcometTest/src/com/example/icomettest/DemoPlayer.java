package com.example.icomettest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.VideoView;

public class DemoPlayer extends Activity{
	private String TAG = "DemoPlayer";
	private VideoView mVideoView;
	private FrameLayout container;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE); 
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        container = new FrameLayout(this);
		FrameLayout.LayoutParams lp1 = 		new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		lp1.gravity = Gravity.LEFT|Gravity.TOP;
		container.setLayoutParams(lp1);
		container.setBackgroundColor(Color.BLACK);
		setContentView(container);	
		mVideoView = new VideoView(this);		
		FrameLayout.LayoutParams lp = 		new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,Gravity.CENTER);
		mVideoView.setLayoutParams(lp);
		container.addView(mVideoView);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
        	String mUrl = bundle.getString("URL","");
        	playUrl(mUrl);
        }
        
	
	}
	private  void playUrl(String url){
		Log.i(TAG, "playUrl "+url);
        Uri uri = Uri.parse( url );
     
//        mVideoView = (VideoView)this.findViewById(R.id.videoView );
     
        //������Ƶ������
//        mVideoView.setMediaController(new MediaController(this));
     
        //������ɻص�
//        mVideoView.setOnCompletionListener( new MyPlayerOnCompletionListener());
     
        //������Ƶ·��
        mVideoView.setVideoURI(uri);
     
        //��ʼ������Ƶ
        mVideoView.start();
    	return;
    }

}
