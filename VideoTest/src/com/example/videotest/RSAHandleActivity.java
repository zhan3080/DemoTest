package com.example.videotest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RSAHandleActivity extends Activity implements OnClickListener{
	 private String TAG = "RSAHandleActivity";
	 private Button btn1, btn2;// 加密，解密
	 private EditText et1, et2, et3;// 需加密的内容，加密后的内容，解密后的内容
	 
	    /* 密钥内容 base64 code */
	 public static String PUCLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfRTdcPIH10gT9f31rQuIInLwe"
	    		+"7fl2dtEJ93gTmjE9c2H+kLVENWgECiJVQ5sonQNfwToMKdO0b3Olf4pgBKeLThra"
	    		+"z/L3nYJYlbqjHC3jTjUnZc0luumpXGsox62+PuSGBlfb8zJO6hix4GV/vhyQVCpG"
	    		+"9aYqgE7zyTRZYX9byQIDAQAB";
	 public static String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ9FN1w8gfXSBP1/"
			 	+ "fWtC4gicvB7t+XZ20Qn3eBOaMT1zYf6QtUQ1aAQKIlVDmyidA1/BOgwp07Rvc6V/"
	            + "imAEp4tOGtrP8vedgliVuqMcLeNONSdlzSW66alcayjHrb4+5IYGV9vzMk7qGLHg"  
	            + "ZX++HJBUKkb1piqATvPJNFlhf1vJAgMBAAECgYA736xhG0oL3EkN9yhx8zG/5RP/"  
	            + "WJzoQOByq7pTPCr4m/Ch30qVerJAmoKvpPumN+h1zdEBk5PHiAJkm96sG/PTndEf"  
	            + "kZrAJ2hwSBqptcABYk6ED70gRTQ1S53tyQXIOSjRBcugY/21qeswS3nMyq3xDEPK" 
	            + "XpdyKPeaTyuK86AEkQJBAM1M7p1lfzEKjNw17SDMLnca/8pBcA0EEcyvtaQpRvaL" 
	            + "n61eQQnnPdpvHamkRBcOvgCAkfwa1uboru0QdXii/gUCQQDGmkP+KJPX9JVCrbRt" 
	            + "7wKyIemyNM+J6y1ZBZ2bVCf9jacCQaSkIWnIR1S9UM+1CFE30So2CA0CfCDmQy+y" 
	            + "7A31AkB8cGFB7j+GTkrLP7SX6KtRboAU7E0q1oijdO24r3xf/Imw4Cy0AAIx4KAu" 
	            + "L29GOp1YWJYkJXCVTfyZnRxXHxSxAkEAvO0zkSv4uI8rDmtAIPQllF8+eRBT/deD" 
	            + "JBR7ga/k+wctwK/Bd4Fxp9xzeETP0l8/I+IOTagK+Dos8d8oGQUFoQJBAI4Nwpfo" 
	            + "MFaLJXGY9ok45wXrcqkJgM+SN6i8hQeujXESVHYatAIL/1DgLi+u46EFD69fw0w+c7o0HLlMsYPAzJw=";
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rsa);
		initView();
	}
	
	private void initView()
    {
		btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
 
        et1 = (EditText) findViewById(R.id.textId);
        et2 = (EditText) findViewById(R.id.TextView02);
        et3 = (EditText) findViewById(R.id.TextView03);
    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		switch (arg0.getId())
        {
			//加密
			case R.id.button1:
				String source = et1.getText().toString().trim();
//	            try
//	            {
//	                // 从字符串中得到公钥
//	                 PublicKey publicKey = RSAUtils.loadPublicKey(PUCLIC_KEY);
//	                // 从文件中得到公钥
//	                //InputStream inPublic = getResources().getAssets().open(rsa_public_key.pem);
////	                PublicKey publicKey = RSAUtils.loadPublicKey(inPublic);
//	                // 加密
//	                byte[] encryptByte = RSAUtils.encryptData(source.getBytes(), publicKey);
//	                // 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换
//	                String afterencrypt = Base64Utils.encode(encryptByte);
//	                et2.setText(afterencrypt);
//	            } catch (Exception e)
//	            {
//	                e.printStackTrace();
//	            }
//				httpGetURL();
				httpPostURL(source,6);
				break;
			//解密
			case R.id.button2:
//				String encryptContent = ed;
//	            try
//	            {
//	            	String stest = "cG+kO7rqFJ4vpvR7CtsV88d3m8Nv5lkSSPvzbDKh/AdoFhh0lO7SbFvSWgp6vcuy0PFNAomOxJ3Dw7yH6BwI5MTW2pQcblrYiGEkxy6zIwVzUSUxvFDm2mGAmBx1ZAlUp9KKxSbpSljOjKAt8QN6XREK5p0Gcs+83t9TrEuOSKQ=";
//	            	//String stest = "cihcMv/bUJ4AMGckrm0Y6QkKYpWc46CtQ3Po1Ll65z8 KY6JCduhLKgmvryHG4 1PSw9cW/hEw1KyQpHAkkVAtLNN7o2CDwP8pAD47 w5SNElFTInY6F1tiFNQU18U5PW4AR7DPVMoazgMj rcN4WPScNr/zyzBV7XriHIziSwM=, NanoHttpd.QUERY_STRING=cihcMv/bUJ4AMGckrm0Y6QkKYpWc46CtQ3Po1Ll65z8+KY6JCduhLKgmvryHG4+1PSw9cW/hEw1KyQpHAkkVAtLNN7o2CDwP8pAD47+w5SNElFTInY6F1tiFNQU18U5PW4AR7DPVMoazgMj+rcN4WPScNr/zyzBV7XriHIziSwM=";
//	            //String stest = "SgQBkTnSR5PRIt3iukdGcpKj3UhNbVMVbHcryfThiMCdsJmGFbaMVmZvjkfoK9mE+asHawcskZ2MQKMjmxDGJe4sLKFS5QwcZCxihQ+OpRgfrpYrkW+hh7aBWXcIrgDfW8x/3wIxrB2PTJLcItccRx+2augxgXbNP4gOrqTeS4k=";
//	                // 从字符串中得到私钥
//	                 PrivateKey privateKey = RSAUtils.loadPrivateKey(PRIVATE_KEY);
//	                // 从文件中得到私钥
////	                InputStream inPrivate = getResources().getAssets().open(pkcs8_rsa_private_key.pem);
////	                PrivateKey privateKey = RSAUtils.loadPrivateKey(inPrivate);
//	                // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
//	                byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(stest), privateKey);
//	                String decryptStr = new String(decryptByte);
//	                et3.setText("hzz =============="+decryptStr);
//	            } catch (Exception e)
//	            {
//	                e.printStackTrace();
//	            }
				httpPostURL("",5);
				break;
			//
			 default:
		            break;
        }
	}
	private String result=null;
	private String ed = null;
//	private String serverURL = "http://192.168.8.145:52277/setting";
	private String serverURL = "http://192.168.10.2:52277/setting";
	private String bgUrl = "http://cn.bing.com/az/hprichbg/rb/Dongdaemun_ZH-CN10736487148_1920x1080.jpg";
	private String test = "https:/ /ip:6669/setting";
	private void httpGetURL(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				PublicKey publicKey = null;
				try {
					publicKey = RSAUtils.loadPublicKey(PUCLIC_KEY);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        byte[] encryptByte = RSAUtils.encryptData("setting".getBytes(), publicKey);
//		        // 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换
		        String afterencrypt = Base64Utils.encode(encryptByte);
		        Log.i(TAG,"afterencrypt:"+afterencrypt);
//		        ed = afterencrypt;
//		        try {
//		        HttpPost httpRequest = new HttpPost(serverURL);   //建立HTTP POST联机
//		        List <NameValuePair> params = new ArrayList <NameValuePair>();   //Post运作传送变量必须用NameValuePair[]数组储存 
//		        params.add(new BasicNameValuePair("str", afterencrypt));   
//		        httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));   //发出http请求
//		        HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);   //取得http响应
//		        if(httpResponse.getStatusLine().getStatusCode() == 200)
//						result = EntityUtils.toString(httpResponse.getEntity());
//		        Log.i(TAG,"result:"+result);
//		        String test = httpResponse.getParams().getParameter("username").toString();
//		        Log.i(TAG,"test:"+test);
//	                // 从字符串中得到私钥
//	                 PrivateKey privateKey = null;
//					try {
//						privateKey = RSAUtils.loadPrivateKey(PRIVATE_KEY);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//	                // 从文件中得到私钥
////	                InputStream inPrivate = getResources().getAssets().open(pkcs8_rsa_private_key.pem);
////	                PrivateKey privateKey = RSAUtils.loadPrivateKey(inPrivate);
//	                // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
//	                byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(result), privateKey);
//	                String decryptStr = new String(decryptByte);
//	                Log.i(TAG,"result decryptStr:"+decryptStr);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}   //获取字符串
		        Log.i(TAG,"url:"+serverURL+afterencrypt);
				HttpGet httpRequest = new HttpGet(serverURL+afterencrypt);// 建立http get联机
				HttpResponse httpResponse=null;
				try {
					// 发出http请求
					httpResponse = new DefaultHttpClient().execute(httpRequest);
				} catch (ClientProtocolException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				if (httpResponse.getStatusLine().getStatusCode() == 200){
					try {
						// 获取相应的字符串
						result = EntityUtils.toString(httpResponse.getEntity());
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				Log.i(TAG, "result:"+result);
				// 从字符串中得到私钥
                PrivateKey privateKey = null;
				try {
					privateKey = RSAUtils.loadPrivateKey(PRIVATE_KEY);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
               // 从文件中得到私钥
//               InputStream inPrivate = getResources().getAssets().open(pkcs8_rsa_private_key.pem);
//               PrivateKey privateKey = RSAUtils.loadPrivateKey(inPrivate);
               // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
               byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(result), privateKey);
               String decryptStr = new String(decryptByte);
               Log.i(TAG,"result decryptStr:"+decryptStr);
				
			}
		}).start();
		 // 从字符串中得到公钥
        
//		Log.i(TAG, "result:"+result);
	}
	private void httpPostURL(final String bgURL,final int type){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
		        try {
		        Log.i(TAG,"serverURL:"+serverURL);
//		        Log.i(TAG,"bgUrl:"+bgUrl);
		        HttpPost httpRequest = new HttpPost(serverURL);   //建立HTTP POST联机
//		        List <BasicNameValuePair> params = new LinkedList <BasicNameValuePair>();   //Post运作传送变量必须用NameValuePair[]数组储存 
//		        params.add(new BasicNameValuePair("str", "test")); 
		        JSONObject params = new JSONObject();
		        try {
					params.put("type", type);
//					params.put("data2", bgURL);
					params.put("data6", bgURL);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        PublicKey publicKey = null;
				try {
					publicKey = RSAUtils.loadPublicKey(PUCLIC_KEY);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        byte[] encryptByte = RSAUtils.encryptData(params.toString().getBytes(), publicKey);
//		        // 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换
		        String afterencrypt = Base64Utils.encode(encryptByte);
		        Log.i(TAG,"afterencrypt:"+afterencrypt);
		        StringEntity entity = new StringEntity(afterencrypt, "utf-8");
		        httpRequest.setEntity(entity);
//		        httpRequest.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
		        HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);   //取得http响应
		        if(httpResponse.getStatusLine().getStatusCode() == 200)
						result = EntityUtils.toString(httpResponse.getEntity());
		        Log.i(TAG,"result:"+result);
	                // 从字符串中得到私钥
	                 PrivateKey privateKey = null;
					try {
						privateKey = RSAUtils.loadPrivateKey(PRIVATE_KEY);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
	                byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(result), privateKey);
	                String decryptStr = new String(decryptByte);
	                Log.i(TAG,"result decryptStr:"+decryptStr);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}   //获取字符串
			}
		}).start();
	}
	
}
