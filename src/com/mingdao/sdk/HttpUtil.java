package com.mingdao.sdk;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;



public class HttpUtil {

	private static final String CHARSET = "UTF-8";
	// cmwap 设置代理 不过现在有些地方 不用设置代理也可以，不好判断，所以默认不设置代理
	public static boolean IS_CMWAP = false;
	private static final String CMWAP_HOST = "10.0.0.172";
	private static final int CMWAP_PORT = 80;
	/**
	 * 释放HttpClient连接
	 * 
	 * @param hrb
	 *            请求对象
	 * @param httpclient
	 *            client对象
	 */
	private static void abortConnection(final HttpRequestBase hrb,
			final HttpClient httpclient) {
		if (hrb != null) {
			try {
				hrb.abort();
			} catch (Exception e) {
			}
		}
		if (httpclient != null) {
			try {
				httpclient.getConnectionManager().shutdown();
			} catch (Exception e) {
			}
		}
	}
	/************************************* 已下用于ssl get httpclient方式访问网络 *******************************************/
	/**
	 * Get方式提交
	 * 
	 * @param url
	 *            提交地址
	 * @param paramsCharset
	 *            参数提交编码集 可为null、默认UTF-8
	 * @param resultCharset
	 *            返回结果编码集可为null、默认UTF-8
	 * @return String
	 */
	public static String httpByGet2StringSSL(String url, String paramsCharset,
			String resultCharset) {
		if (url == null || "".equals(url)) {
			return null;
		}
		String responseStr = null;
		HttpClient httpClient = null;
		HttpGet hg = null;
		try {
			httpClient = getNewHttpClient();
			hg = new HttpGet(url);
			// 发送请求，得到响应
			HttpResponse response = httpClient.execute(hg);
			if (resultCharset == null || "".equals(resultCharset)) {
				responseStr = EntityUtils.toString(response.getEntity(),
						CHARSET);
			} else {
				responseStr = EntityUtils.toString(response.getEntity(),
						resultCharset);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			abortConnection(hg, httpClient);
		}
		return responseStr;
	}
	/**
	 * 
	 * javax.net.ssl.SSLPeerUnverifiedException: No peer certificate
	 * 
	 * @author Administrator
	 * 
	 */
	public static class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				//@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}

				//@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);
			DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);
			// 设置cmwap代理
			if (IS_CMWAP) {
				HttpHost proxy = new HttpHost(CMWAP_HOST, CMWAP_PORT);
				httpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}
}