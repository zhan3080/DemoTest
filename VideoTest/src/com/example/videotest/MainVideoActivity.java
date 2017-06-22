package com.example.videotest;

import https.MAPServerHTTPSUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.Format.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import javax.net.ssl.KeyManagerFactory;

import org.apache.http.conn.util.InetAddressUtils;

import com.https.server.HttpsService;
import com.https.test.server.HttpsSeviceTest;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.LinkAddress;
import android.net.ProxyInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainVideoActivity extends Activity {
	private final static String TAG = "MainVideoActivity";
	private Button button;
	private Button button2;
	private InetAddress inetAddress;
	private Object ipConfigurationInstance;
	private RelativeLayout mRelativeContain;
	private ImageView imageView;
//	private Myhttpsd myHttpsd;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video);
//        getWindow().getDecorView().setBackground(this.getResources().getDrawable(R.drawable.baidu));
        button = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        mRelativeContain = (RelativeLayout)findViewById(R.id.contentid);
        imageView = (ImageView)findViewById(R.id.main_lunch_view);
//        myHttpsd = new Myhttpsd(52267);
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				PowerManager pManager=(PowerManager) getSystemService(Context.POWER_SERVICE); //������fastbootģʽ
//				pManager.reboot("");
//				try {
//					Log.i(TAG, "myHttpsd.start()"); 
//					myHttpsd.start();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				httpsTest();
//				Intent intent = new Intent();
//				intent.setClass(MainVideoActivity.this, HttpsSeviceTest.class);
//        		
//				MainVideoActivity.this.startService(intent);
//				Intent intent = new Intent();
//				intent.setClass(MainVideoActivity.this, RSANanohttpd.class);
//        		
//				MainVideoActivity.this.startService(intent);
//				setNetWork();
//				shutDownSystem();
//				rebootSystem();
//				shutDownAlarm(5000);
			}
		});
        
		 button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.setClass(MainVideoActivity.this, HttpsService.class);
//        		
//				MainVideoActivity.this.startService(intent);
//				getLocalMacAddress();
				Intent intent = new Intent();
				intent.setClass(MainVideoActivity.this, RSAHandleActivity.class);
				MainVideoActivity.this.startActivity(intent);
//				shutDownSystem();
				
			}
		});
		 DeReceiver mDeReceiver = new DeReceiver();
		 IntentFilter mFilter = new IntentFilter();
	     mFilter.addAction("com.cartion.abc");
		registerReceiver(mDeReceiver, mFilter);
    }
    private String getLocalMacAddress() {
        String macaddress="";
        byte[] m = {0,0,0,0,0,0};
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        m = intf.getHardwareAddress();//inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            //Log.e("WifiPreference IpAddress", ex.toString());
        }
        Log.i(TAG, "m="+m);
        if (m != null)
        {
            macaddress = byteMacToStr(m);
        }
        m = null;
        Log.i(TAG, "macaddress="+macaddress);
        return macaddress;
    }
    private String byteMacToStr(byte[] ma) {
        String r = "";
        for (int i = 0;i < ma.length; i++)
        {
            if (!r.equals(""))
                r =  r + ":";
            Log.i(TAG, "ma[i]="+ma[i]);
            r = r + String.format("%02X", ma[i]);//Integer. .toHexString(ma[i]);
        }
        return r;
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void rebootSystem(){
//    	Intent intent=new Intent(Intent.ACTION_REBOOT);
//        intent.putExtra("nowait", 1);
//        intent.putExtra("interval", 1);
//        intent.putExtra("window", 0);
//        sendBroadcast(intent);
//    	PowerManager pManager=(PowerManager) getSystemService(Context.POWER_SERVICE); //������fastbootģʽ
//    	pManager.reboot("");
    	
    	
    }
    public static final String ACTION_REQUEST_SHUTDOWN="android.intent.action.ACTION_REQUEST_SHUTDOWN";

    public static final String EXTRA_KEY_CONFIRM="android.intent.extra.KEY_CONFIRM";
    private void shutDownSystem(){
    	Intent i=new Intent(ACTION_REQUEST_SHUTDOWN);
        i.putExtra(EXTRA_KEY_CONFIRM, false);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
//    	try {  
//    		  
//    		 //���ServiceManager��  
//    		 Class<?> ServiceManager = Class.forName("android.os.ServiceManager");  
//    		   
//    		 //���ServiceManager��getService����  
//    		 Method getService = ServiceManager.getMethod("getService", java.lang.String.class);  
//    		   
//    		 //����getService��ȡRemoteService  
//    		 Object oRemoteService = getService.invoke(null,Context.POWER_SERVICE);  
//    		   
//    		 //���IPowerManager.Stub��  
//    		 Class<?> cStub = Class.forName("android.os.IPowerManager$Stub");  
//    		 //���asInterface����  
//    		 Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);  
//    		 //����asInterface������ȡIPowerManager����  
//    		 Object oIPowerManager = asInterface.invoke(null, oRemoteService);  
//    		 //���shutdown()����  
//    		 Method shutdown = oIPowerManager.getClass().getMethod("shutdown",boolean.class,boolean.class);  
//    		 //����shutdown()����  
//    		 shutdown.invoke(oIPowerManager,false,true);     
//    	} catch (Exception e) {           
//    		   Log.e(TAG, e.toString(), e);          
//    		  }  
//    	 try{
//             Log.v(TAG, "root Runtime->shutdown");
//             //Process proc =Runtime.getRuntime().exec(new String[]{"su","-c","shutdown"});  //�ػ�
//             Process proc =Runtime.getRuntime().exec(new String[]{"su","-c","reboot -p"});  //�ػ�
//             int ret = proc.waitFor();
//             Log.v(TAG,"ret="+ret);
////             if (ret != 0) {
//                     System.err.println("exit value = " + proc.exitValue());
////                 }
//
//         }catch(Exception e){
//             e.printStackTrace();
//         }
        
    }
    
    
    public void setNetWork(){

    	Log.e(TAG, "setNetWork");  
		try {  
            //��ȡETHERNET_SERVICE����  
            String ETHERNET_SERVICE = (String) Context.class.getField("ETHERNET_SERVICE").get(null);  
              
            Class<?> ethernetManagerClass = Class.forName("android.net.EthernetManager");  
              
            Class<?> ipConfigurationClass = Class.forName("android.net.IpConfiguration");  
              
            //��ȡethernetManager�������  
            Object ethernetManager = getSystemService(ETHERNET_SERVICE);  
              
            Object getConfiguration = ethernetManagerClass.getDeclaredMethod("getConfiguration").invoke(ethernetManager);  
              
            Log.e(TAG, "ETHERNET_SERVICE : "+ ETHERNET_SERVICE);  
              
            //��ȡ��EthernetManager�еĳ�����mService��Ա����  
            java.lang.reflect.Field mService = ethernetManagerClass.getDeclaredField("mService");  
            
            //�޸�privateȨ��  
            mService.setAccessible(true);  

            //��ȡ�������ʵ��������  
            Object mServiceObject = mService.get(ethernetManager);  
              
            Class<?> iEthernetManagerClass = Class.forName("android.net.IEthernetManager");  
              
            Method[] methods = iEthernetManagerClass.getDeclaredMethods();  
              
            for (Method ms : methods) {  
            	Log.e(TAG, "ms.getName() : "+ms.getName());
              
                if (ms.getName().equals("setEthernetEnabled")){  
                  
                    ms.invoke(mServiceObject,true);  
                      
                    Log.e(TAG, "mServiceObject : "+mServiceObject);  
                      
                }  
                  
            }  
            Class<?> staticIpConfig = Class.forName("android.net.StaticIpConfiguration");  
              
            Constructor<?> staticIpConfigConstructor = staticIpConfig.getDeclaredConstructor(staticIpConfig);  
              
            Object staticIpConfigInstance = staticIpConfig.newInstance();  
              
            //��ȡLinkAddress����ֻ��һ��String���͵Ĺ��췽��  
            Constructor<?> linkAddressConstructor = LinkAddress.class.getDeclaredConstructor(String.class);  
              
            //ʵ������String���͵Ĺ��췽��  
            LinkAddress linkAddress = (LinkAddress) linkAddressConstructor.newInstance("192.168.10.77/24");//192.168.1.1/24--�������볤��,24�൱��255.255.255.0  
              
            Class<?> inetAddressClass = Class.forName("java.net.InetAddress");  
              
            //Ĭ�����ز���  
            byte[] bytes = new byte[]{(byte) 192, (byte) 168, (byte)10, (byte)1};  
              
            Constructor<?>[] inetAddressConstructors = inetAddressClass.getDeclaredConstructors();  
            
//            inetAddress = (InetAddress)linkAddressConstructor.newInstance("192.168.10.1");
//            Log.i(TAG,"staticIpConfigInstance inetAddress="+inetAddress);
//            inetAddress = (InetAddress)linkAddressConstructor.newInstance("255.255.255.0");
//            Log.i(TAG,"staticIpConfigInstance inetAddress="+inetAddress);
//            inetAddress = (InetAddress)linkAddressConstructor.newInstance("8.8.8.8");
//            Log.i(TAG,"staticIpConfigInstance inetAddress="+inetAddress);
            Log.i(TAG,"inetAddressConstructor()len="+inetAddressConstructors.length);
            for (Constructor  inetc: inetAddressConstructors) {  
              
                //��ȡ�����ֲ������͵Ĺ��췽��  
                if(inetc.getParameterTypes().length==3) {  
                  
                    //�޸�Ȩ��  
                    inetc.setAccessible(true);  
                      
                    WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);  
                      
                    int ipAddressInt = wm.getConnectionInfo().getIpAddress();  
                      
                    //hostName������  
                    String hostName = String.format(Locale.getDefault(), "%d.%d.%d.%d", (ipAddressInt & 0xff), (ipAddressInt >> 8 & 0xff), (ipAddressInt >> 16 & 0xff), (ipAddressInt >> 24 & 0xff));  

                    inetAddress = (InetAddress) inetc.newInstance(2, bytes, "192.168.10.0");  
                    Log.i(TAG,"staticIpConfigInstance inetAddress="+inetAddress);
                      
                }  
                  
            }  
//            ��ȡstaticIpConfig�����еĳ�Ա����  
            java.lang.reflect.Field[] declaredFields = staticIpConfigInstance.getClass().getDeclaredFields();  
              
            for (java.lang.reflect.Field f :declaredFields ) {  
              
            	Log.i(TAG,"staticIpConfigInstance f.getName()="+f.getName());
                //���ó�Ա������ֵ  
                if (f.getName().equals("ipAddress")){  
                  
                	Log.i(TAG,"staticIpConfigInstance linkAddress="+linkAddress);
                    //����IP��ַ����������  
                    f.set(staticIpConfigInstance,linkAddress);  
                      
                }else if (f.getName().equals("gateway")){  
  
//                	byte[] bytes1 = new byte[]{(byte) 255, (byte) 255, (byte)255, (byte)0};  
//               	 	try {
//						inetAddress = (InetAddress)InetAddress.getByAddress(bytes1);
//					} catch (UnknownHostException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//               	  Log.i(TAG,"staticIpConfigInstance inetAddress="+inetAddress);
//               	    f.set(staticIpConfigInstance,inetAddress); 
                    //����Ĭ������  
//                	inetAddress = (InetAddress)inetAddressConstructors[0].newInstance(new String("255.255.255.0"));
//                    Log.i(TAG, "inetAddress="+inetAddress);
//                    f.set(staticIpConfigInstance, "255.255.255.0");  
  
                }else if (f.getName().equals("domains")){  
//                	byte[] bytes1 = new byte[]{(byte) 192, (byte) 168, (byte)10, (byte)1};  
//               	 	try {
//						inetAddress = (InetAddress)InetAddress.getByAddress(bytes1);
//					} catch (UnknownHostException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//               	 Log.i(TAG,"staticIpConfigInstance inetAddress="+inetAddress);
               	    f.set(staticIpConfigInstance,"192.168.10.1"); 
//                	inetAddress = (InetAddress)inetAddressConstructors[0].newInstance(new String("192.168.10.1"));
//                    Log.i(TAG, "inetAddress="+inetAddress);
//                    f.set(staticIpConfigInstance,"192.168.10.1");  
                      
                }else if (f.getName().equals("dnsServers")){ 
                 
                	byte[] bytes1 = new byte[]{(byte) 8, (byte) 8, (byte)8, (byte)8};  
                	 try {
						inetAddress = (InetAddress)InetAddress.getByAddress(bytes1);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	 Log.i(TAG,"staticIpConfigInstance inetAddress="+inetAddress);
                	 
                	 ArrayList<InetAddress> list = new ArrayList<InetAddress>();
                	 list.add(inetAddress);
////                	 byte[] bytes2 = new byte[]{(byte) 8, (byte) 8, (byte)4, (byte)4};  
//                	 try {
//						inetAddress = (InetAddress)InetAddress.getByName("4.4.4.4");
//					} catch (UnknownHostException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
                	 try {
						inetAddress=InetAddress.getByName("4.4.4.4");
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	 Log.i(TAG,"staticIpConfigInstance inetAddress="+inetAddress);
                	 list.add(inetAddress);
//                	f.getField(staticIpConfig, "dnsServers", ArrayList.class).clear();
//                    for (int i = 0; i < dns.length; i++)
//                        getField(staticIpConfig, "dnsServers", ArrayList.class).add(dns[i]);
//                //����DNS 
//                    f.set(staticIpConfigInstance,list); 
                     
                }  
                  
            }  
            Log.i(TAG, "COMPLETE");
            Object staticInstance =staticIpConfigConstructor.newInstance(staticIpConfigInstance);  
              
            //���ipASSignmentö��������ļ���  
            HashMap ipAssignmentMap=new HashMap();  
              
            //���proxySettingsö��������ļ���  
            HashMap proxySettingsMap=new HashMap();  
              
            Class<?>[] enumClass = ipConfigurationClass.getDeclaredClasses();  
              
            for (Class enumC : enumClass) {  
              
                //��ȡö������  
                Object[] enumConstants = enumC.getEnumConstants();  
                  
                if (enumC.getSimpleName().equals("ProxySettings")){  
                  
                    for (Object enu : enumConstants) {  
                      
                        //���ô������ü��� STATIC DHCP UNASSIGNED PAC  
                        proxySettingsMap.put(enu.toString(),enu);  
                          
                    }  
                      
                }else if (enumC.getSimpleName().equals("IpAssignment")){  
                  
                    for (Object enu : enumConstants) {  
                      
                        //������̫������ģʽ���ü��� STATIC DHCP UNASSIGNED  
                        ipAssignmentMap.put(enu.toString(),enu);  
                          
                    }  
                      
                }  
                  
            }  
            //��ȡipConfiguration��Ĺ��췽��  
            Constructor<?>[] ipConfigConstructors = ipConfigurationClass.getDeclaredConstructors();  
              
            for (Constructor constru : ipConfigConstructors) {  
              
                //��ȡipConfiguration���4�������Ĺ��췽��  
                if (constru.getParameterTypes().length==4){//����������������  
                  
                //��ʼ��ipConfiguration����,���ò���  
                    ipConfigurationInstance = constru.newInstance(ipAssignmentMap.get("STATIC"), proxySettingsMap.get("NONE"), staticInstance, ProxyInfo.buildDirectProxy(null,0));  
                      
                }  
                  
            }  
              
            Log.e(TAG, "ipCon : "+ipConfigurationInstance);  
              
            //��ȡipConfiguration���д���StaticIpConfiguration�������͵�����setStaticIpConfiguration�ķ���  
            Method setStaticIpConfiguration = ipConfigurationClass.getDeclaredMethod("setStaticIpConfiguration", staticIpConfig);  
              
            //�޸�private����Ȩ��  
            setStaticIpConfiguration.setAccessible(true);  
              
            //��ipConfiguration������ʹ��setStaticIpConfiguration����,���������  
            setStaticIpConfiguration.invoke(ipConfigurationInstance,staticInstance);  
              
            Object ethernetManagerInstance = ethernetManagerClass.getDeclaredConstructor(Context.class, iEthernetManagerClass).newInstance(this, mServiceObject);  
              
            ethernetManagerClass.getDeclaredMethod("setConfiguration",ipConfigurationClass).invoke(ethernetManagerInstance,ipConfigurationInstance);  
              
            Log.e("net static", "getConfiguration : "+ getConfiguration.toString());  
        } catch (ClassNotFoundException e) {  
            e.printStackTrace();  
        } catch (InstantiationException e) {  
            e.printStackTrace();  
        } catch (IllegalAccessException e) {  
            e.printStackTrace();  
        } catch (NoSuchMethodException e) {  
            e.printStackTrace();  
        } catch (InvocationTargetException e) {  
            e.printStackTrace();  
        } catch (NoSuchFieldException e) {  
            e.printStackTrace();  
        }
	}
    
    
    AlarmManager am ;  
    PendingIntent pendingIntent ;  
    public void shutDownAlarm(int time){  
    	 Log.i("TAG", "time: " + time ) ;
        am = (AlarmManager) this  
                .getSystemService(Context.ALARM_SERVICE);  
        Intent intent = new Intent("com.cartion.abc") ;  
        pendingIntent = PendingIntent.getBroadcast(this, 0,  
                intent, PendingIntent.FLAG_CANCEL_CURRENT);  
        am = (AlarmManager) this  
                .getSystemService(Context.ALARM_SERVICE);  
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent);  
          
    }  
    public void cancel(PendingIntent pendingIntent){  
        if (am != null) {  
            am.cancel(pendingIntent) ;            
        }  
    }  
    
    
    public class DeReceiver extends BroadcastReceiver{  
    	  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            Log.i("TAG", ": " + intent.getAction() );  
            if (intent.getAction().equals("com.cartion.abc")) {  
//                shutDown(context) ;  
                shutDownSystem();
            }  
        }  
//        private void shutDown(Context context) {  
//            Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);  
//      
//            intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);  
//      
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
//      
//            context.startActivity(intent);  
//        }  
    }  
    
}
