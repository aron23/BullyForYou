package beacon.bully.sentiment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.os.AsyncTask;

public class WebUtil {
	public static InputStream getInputStreamFromUrl(String url) {
		InputStream content = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			HttpClient httpclient = secureClient();
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);
			content = response.getEntity().getContent();
                } catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return content;
	}

	public static void addLabel(String content_id, String user, String sentiment, String severity, String text) {
		try {
			HttpGet httpGet = new HttpGet("https://beaconinitiative.com/api/put/harmony_label/"+content_id+"/"+user+"/"+sentiment+"/"+severity+"/"+URLEncoder.encode(text.replaceAll("\\/", "-").replaceAll("http(?:s)?:\\/\\/[^\\s]*", " "), "UTF-8"));
			HttpClient httpclient = secureClient();
			httpclient.execute(httpGet);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void vote(String cid, String api_key, String vote) {
		try {
			HttpGet httpGet = new HttpGet("https://beaconinitiative.com/api/put/bully_vote/"+api_key+"/"+vote+"/"+cid+"/");
			HttpClient httpclient = secureClient();
			httpclient.execute(httpGet);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static HttpClient secureClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new EasySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(
                   SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
	}

	public static boolean isKeyValid(String myapi) {
		InputStream content = null;		
		StringBuilder total = new StringBuilder();
		try {
			HttpGet httpGet = new HttpGet("https://beaconinitiative.com/api/isvalid/"+myapi+"/");
			HttpClient httpclient = secureClient();
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);
			content = response.getEntity().getContent();
			BufferedReader r = new BufferedReader(new InputStreamReader(content));
			String line;
			while ((line = r.readLine()) != null) {
			    total.append(line);
			}
	    } catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		
		if (total.toString().equals("1"))
			return true;
		else
			return false;
	}

	
	
}


