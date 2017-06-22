package https;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import android.content.Context;

@SuppressWarnings("deprecation")
public class MAPServerTrustCertainHostNameFactory extends SSLSocketFactory
{
	private static MAPServerTrustCertainHostNameFactory mMAPServerTrustCertainHostNameFactoryInstance=null;

	@SuppressWarnings("deprecation")
	public MAPServerTrustCertainHostNameFactory(KeyStore truststore)throws NoSuchAlgorithmException, KeyManagementException,KeyStoreException, UnrecoverableKeyException 
	{
		super(truststore);
	}

	public static MAPServerTrustCertainHostNameFactory getDefault(Context context) 
	{
		KeyStore keystore = null;
		try 
		{
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream in;
			System.out.println("getAssets!!");
			in = context.getAssets().open("client.crt");
			if(null==in)
			{
				System.out.println("getAssets check failed!!");
				return null;
			}
			
			System.out.println("begin to generateCertificate!!");
			Certificate ca = cf.generateCertificate(in);
			System.out.println("KeyStore.getDefaultType!!");
			keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			System.out.println("load!!");
			keystore.load(null, null);
			keystore.setCertificateEntry("ca", ca);
			System.out.println("setCertificateEntry!!");

			if (null == mMAPServerTrustCertainHostNameFactoryInstance) 
			{
				System.out.println("setCertificateEntry111!!");
				mMAPServerTrustCertainHostNameFactoryInstance = new MAPServerTrustCertainHostNameFactory(keystore);
				System.out.println("setCertificateEntry222!!");
			}
		} 
		catch (Exception e) 
		{
			
		}
		return mMAPServerTrustCertainHostNameFactoryInstance;
	}

	@Override
	public Socket createSocket() throws IOException
	{
		return super.createSocket();
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port,boolean autoClose) throws IOException, UnknownHostException 
	{
		return super.createSocket(socket, host, port, autoClose);
	}

}
