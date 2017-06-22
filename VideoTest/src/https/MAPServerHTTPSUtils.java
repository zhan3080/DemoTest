package https;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

//import com.hpcastlink.controler.mirror.IHappyCastEventListener;
//import com.hpcastlink.module.mirror.HappyCastEventUtils.HappyCastMAPServerEventCode;

import android.content.Context;

@SuppressWarnings("deprecation")
public class MAPServerHTTPSUtils 
{
//	private IHappyCastEventListener mHappyCastEventListener = null;

//	public MAPServerHTTPSUtils(IHappyCastEventListener mHappyCastEventListener) 
//	{
//		this.mHappyCastEventListener = mHappyCastEventListener;
//		// TODO Auto-generated constructor stub
//	}
	public MAPServerHTTPSUtils() 
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * HttpUrlConnection 方式，支持指定load-der.crt证书验证，此种方式Android官方建议
	 * 
	 * @throws CertificateException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public JSONObject initSSL(Context ct)
	{
		try 
		{
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream in = ct.getAssets().open("load-der.crt");
			Certificate ca = cf.generateCertificate(in);

			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(null, null);
			keystore.setCertificateEntry("ca", ca);

			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance(tmfAlgorithm);
			tmf.init(keystore);

			// Create an SSLContext that uses our TrustManager
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, tmf.getTrustManagers(), null);
			URL url = new URL("https://certs.cac.washington.edu/CAtest/");
			HttpsURLConnection urlConnection = (HttpsURLConnection) url
					.openConnection();
			urlConnection.setSSLSocketFactory(context.getSocketFactory());
			InputStream input = urlConnection.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) 
			{
				result.append(line);
			}

			System.out.println("initSSL,result:" + result.toString());
			try 
			{
				JSONObject mDataJson = new JSONObject(result.toString());
				return mDataJson;
			}
			catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * HttpUrlConnection支持所有Https免验证，不建议使用
	 * 
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public JSONObject initSSLALL() throws KeyManagementException,NoSuchAlgorithmException, IOException 
	{
		URL url = new URL("https://certs.cac.washington.edu/CAtest/");
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, new TrustManager[] { new TrustAllManager() }, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() 
		{
			@Override
			public boolean verify(String hostname, SSLSession session)
			{
				// TODO Auto-generated method stub
				return true;
			}
		});

		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(false);
		connection.setRequestMethod("GET");
		connection.connect();
		InputStream in = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = "";
		StringBuffer result = new StringBuffer();
		while ((line = reader.readLine()) != null) 
		{
			result.append(line);
		}

		System.out.println("initSSLALL,result:" + result.toString());
		try 
		{
			JSONObject mDataJson = new JSONObject(result.toString());
			return mDataJson;
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * HttpClient方式实现，支持所有Https免验证方式链接
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public JSONObject initSSLAllWithHttpClient() 
	{
		HttpParams param = new BasicHttpParams();
		HttpProtocolParams.setVersion(param, HttpVersion.HTTP_1_1);
		// get connection from pool timeout;
		ConnManagerParams.setTimeout(param, 1000);
		// http connection timeout;
		HttpConnectionParams.setConnectionTimeout(param, 2000);
		// send request timeout;
		HttpConnectionParams.setSoTimeout(param, 4000);

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", MAPServerSSLSocketFactory.getInstance(), 4567));
		ClientConnectionManager manager = new ThreadSafeClientConnManager(param, registry);
		DefaultHttpClient client = new DefaultHttpClient(manager, param);

		// HttpGet request = new
		// HttpGet("https://certs.cac.washington.edu/CAtest/");
		//HttpGet request = new HttpGet("https://www.alipay.com/");
		HttpGet request = new HttpGet("https://192.168.10.2/");
		HttpResponse mHttpResponse;
		HttpEntity mHttpEntity = null;
		System.out.println("MAPServerHTTPSUtils");
		try 
		{
			mHttpResponse = client.execute(request);
			mHttpEntity = mHttpResponse.getEntity();
		}
		catch (ClientProtocolException e1) 
		{
			System.out.println("MAPServerHTTPSUtils,fail!!");
			// TODO Auto-generated catch block
			e1.printStackTrace();
//			if (null != mHappyCastEventListener) 
//			{
//				mHappyCastEventListener.onStage1MAPServerEventCallBack(HappyCastMAPServerEventCode.EVENT_ERROR_MAPSERVER_HTTPS_PROTOCOL_EXCEPTION);
//			}
			// System.out.println("initSSLAllWithHttpClient,ClientProtocolException exception!!");
			return null;
		} 
		catch (IOException e1)
		{
			System.out.println("2 fail!!");
			// TODO Auto-generated catch block
			e1.printStackTrace();
//			if (null != mHappyCastEventListener) 
//			{
//				mHappyCastEventListener.onStage1MAPServerEventCallBack(HappyCastMAPServerEventCode.EVENT_ERROR_MAPSERVER_NETWORKCONN_FAILED);
//			}
			return null;
		}
		
		BufferedReader mBufferedReader = null;
		try
		{
			mBufferedReader = new BufferedReader(new InputStreamReader(mHttpEntity.getContent()));
		} 
		catch (IllegalStateException e1) 
		{
			System.out.println("3 fail!!");
			// TODO Auto-generated catch block
			e1.printStackTrace();
//			if (null != mHappyCastEventListener) 
//			{
//				mHappyCastEventListener.onStage1MAPServerEventCallBack(HappyCastMAPServerEventCode.EVENT_ERROR_MAPSERVER_ILLEGAL_STATE_EXCEPTION);
//			}
			return null;
		} 
		catch (IOException e1)
		{
			System.out.println("4 fail!!");
			// TODO Auto-generated catch block
			e1.printStackTrace();
//			if (null != mHappyCastEventListener) 
//			{
//				mHappyCastEventListener.onStage1MAPServerEventCallBack(HappyCastMAPServerEventCode.EVENT_ERROR_MAPSERVER_NETWORKCONN_FAILED);
//			}
			return null;
		}

		StringBuilder result = new StringBuilder();
		String line = null;
		try 
		{
			while ((line = mBufferedReader.readLine()) != null) 
			{
				result.append(line);
			}
		} 
		catch (IOException e1) 
		{
			System.out.println("5 fail!!");
			// TODO Auto-generated catch block
			e1.printStackTrace();
//			if (null != mHappyCastEventListener) 
//			{
//				mHappyCastEventListener.onStage1MAPServerEventCallBack(HappyCastMAPServerEventCode.EVENT_ERROR_MAPSERVER_NETWORKCONN_FAILED);
//			}
			return null;
		}

		System.out.println("initSSLAllWithHttpClient,result:"+ result.toString());
		if ((0 == result.length())) 
		{
//			if (null != mHappyCastEventListener)
//			{
//				mHappyCastEventListener.onStage1MAPServerEventCallBack(HappyCastMAPServerEventCode.EVENT_ERROR_MAPSERVER_HTTPS_PROTOCOL_EXCEPTION);
//			}
			return null;
		}

		try 
		{
			JSONObject mDataJson = new JSONObject(result.toString());
			return mDataJson;
		}
		catch (JSONException e) 
		{
			System.out.println("6,fail!!");
			// TODO Auto-generated catch block
			e.printStackTrace();
//			if (null != mHappyCastEventListener) 
//			{
//				mHappyCastEventListener.onStage1MAPServerEventCallBack(HappyCastMAPServerEventCode.EVENT_ERROR_MAPSERVER_RESPONSE_FORMAT_JSON_EXCEPTION);
//			}
			return null;
		}
	}

	/**
	 * HttpClient方式实现，支持验证指定证书
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */

	// verfiy https using load-der.crt;
	public JSONObject initSSLCertainWithHttpClient(Context mContext) 
	{
		int timeOut = 30 * 1000;
		HttpParams param = new BasicHttpParams();
		HttpProtocolParams.setVersion(param, HttpVersion.HTTP_1_1);
		// get connection from pool timeout;
		ConnManagerParams.setTimeout(param, 1000);
		// http connection timeout;
		HttpConnectionParams.setConnectionTimeout(param, 2000);
		// send request timeout;
		HttpConnectionParams.setSoTimeout(param, 4000);

		SchemeRegistry registry = new SchemeRegistry();
		System.out.println("initSSLCertainWithHttpClient,begin to getSocketFactory!!");
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		//System.out.println("initSSLCertainWithHttpClient,TrustCertainHostNameFactory,begin to get default!!");
		registry.register(new Scheme("https",MAPServerTrustCertainHostNameFactory.getDefault(mContext), 80));
		System.out.println("initSSLCertainWithHttpClient,begin to run ThreadSafeClientConnManager!!");
		ClientConnectionManager manager = new ThreadSafeClientConnManager(param, registry);

		System.out.println("initSSLCertainWithHttpClient,begin to create DefaultHttpClient!!");
		DefaultHttpClient client = new DefaultHttpClient(manager, param);

		// HttpGet request = new
		// HttpGet("https://certs.cac.washington.edu/CAtest/");
//		HttpGet request = new HttpGet("https://www.alipay.com/");
		HttpGet request = new HttpGet("https://192.168.8.54/");
		System.out.println("initSSLCertainWithHttpClient,begin to send request!!");
		HttpResponse response = null;
		try 
		{
			response = client.execute(request);
		}
		catch (ClientProtocolException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
//			if (null != mHappyCastEventListener) 
//			{
//				mHappyCastEventListener.onStage1MAPServerEventCallBack(HappyCastMAPServerEventCode.EVENT_ERROR_MAPSERVER_HTTPS_PROTOCOL_EXCEPTION);
//			}
			return null;
		}
		catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
//			if (null != mHappyCastEventListener) 
//			{
//				mHappyCastEventListener.onStage1MAPServerEventCallBack(HappyCastMAPServerEventCode.EVENT_ERROR_MAPSERVER_NETWORKCONN_FAILED);
//			}
			return null;
		}
		HttpEntity entity = response.getEntity();
		BufferedReader reader = null;
		try 
		{
			reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		}
		catch (IllegalStateException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
//			if (null != mHappyCastEventListener) 
//			{
//				mHappyCastEventListener.onStage1MAPServerEventCallBack(HappyCastMAPServerEventCode.EVENT_ERROR_MAPSERVER_ILLEGAL_STATE_EXCEPTION);
//			}
			return null;
		} 
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
//			if (null != mHappyCastEventListener) 
//			{
//				mHappyCastEventListener.onStage1MAPServerEventCallBack(HappyCastMAPServerEventCode.EVENT_ERROR_MAPSERVER_NETWORKCONN_FAILED);
//			}
			return null;
		}
		StringBuilder result = new StringBuilder();
		String line = "";
		try 
		{
			while ((line = reader.readLine()) != null) 
			{
				result.append(line);
			}
		} 
		catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
//			if (null != mHappyCastEventListener) 
//			{
//				mHappyCastEventListener.onStage1MAPServerEventCallBack(HappyCastMAPServerEventCode.EVENT_ERROR_MAPSERVER_NETWORKCONN_FAILED);
//			}
			return null;
		}
		
		if (0 == result.length()) 
		{
//			if (null != mHappyCastEventListener)
//			{
//				mHappyCastEventListener.onStage1MAPServerEventCallBack(HappyCastMAPServerEventCode.EVENT_ERROR_MAPSERVER_HTTPS_PROTOCOL_EXCEPTION);
//			}
			return null;
		}
		
		try 
		{
			JSONObject mDataJson = new JSONObject(result.toString());
			return mDataJson;
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
//			if (null != mHappyCastEventListener) 
//			{
//				mHappyCastEventListener.onStage1MAPServerEventCallBack(HappyCastMAPServerEventCode.EVENT_ERROR_MAPSERVER_RESPONSE_FORMAT_JSON_EXCEPTION);
//			}
			return null;
		}
	}

	private class TrustAllManager implements X509TrustManager 
	{

		public void checkClientTrusted(X509Certificate[] arg0, String arg1)throws CertificateException 
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)throws CertificateException 
		{
			// TODO Auto-generated method stub
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() 
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

}
