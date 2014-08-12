package ua.infoshoc.megastyle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collections;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import android.content.res.Resources;

public class Request {

	private String url;
	private ArrayList<ParameterValue> params, cookies;

	Request(String url) {
		this.url = url;
		params = new ArrayList<ParameterValue>();
		cookies = new ArrayList<ParameterValue>();
	}

	Request(String url, ParameterValue params[]) {
		this(url);
		if (params != null) {
			Collections.addAll(this.params, params);
		}
	}

	public Request addParam(String key, String value) {
		params.add(new ParameterValue(key, value));
		return this;
	}

	public Request addCookie(String key, String value) {
		cookies.add(new ParameterValue(key, value));
		return this;
	}

	private String formURL() {
		if (url == null) {
			return null;
		}
		int paramsLength = params.size();
		if (paramsLength == 0) {
			return url;
		}
		String result = url + "?" + params.get(0).getParameter() + "="
				+ params.get(0).getValue();
		for (int i = 1; i < paramsLength; ++i) {
			result += "&" + params.get(i).getParameter() + "="
					+ params.get(i).getValue();
		}
		return result;
	}

	private String formCookies() {
		String result = null;
		int cookiesLength = cookies.size();
		if (cookiesLength != 0) {
			result = cookies.get(0).getParameter() + '='
					+ cookies.get(0).getValue();
			for (int i = 1; i < cookiesLength; ++i) {
				result += "; " + cookies.get(i).getParameter() + "="
						+ cookies.get(i).getValue();
			}
		}
		return result;
	}

	private static SSLSocketFactory sslSocketFactory;

	private static void getSSLSocketFactory(Resources resources)
			throws CertificateException, KeyStoreException, IOException,
			NoSuchAlgorithmException, KeyManagementException {
		// Load CAs from an InputStream
		// (could be from a resource or ByteArrayInputStream or ...)
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream caInput = resources.openRawResource(R.raw.rapidssl);
		Certificate ca;
		ca = cf.generateCertificate(caInput);
		caInput.close();

		// Create a KeyStore containing our trusted CAs
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(null, null);
		keyStore.setCertificateEntry("ca", ca);

		// Create a TrustManager that trusts the CAs in our KeyStore
		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
		tmf.init(keyStore);

		// Create an SSLContext that uses our TrustManager
		SSLContext context = SSLContext.getInstance("TLSv1.2");
		context.init(null, tmf.getTrustManagers(), null);
		sslSocketFactory = context.getSocketFactory();
	}

	public TagNode send(Resources resources) throws CertificateException,
			IOException, KeyStoreException, NoSuchAlgorithmException,
			KeyManagementException {
		TagNode rootNode = null;

		if (sslSocketFactory == null) {
			getSSLSocketFactory(resources);
		}

		URL url = new URL(formURL());
		HttpsURLConnection urlConnection = (HttpsURLConnection) url
				.openConnection();
		String cookies = formCookies();
		if (cookies != null) {
			urlConnection.setRequestProperty("Cookie", cookies);
		}
		urlConnection.setSSLSocketFactory(sslSocketFactory);
		urlConnection.connect();
		InputStream in = urlConnection.getInputStream();
		HtmlCleaner cleaner = new HtmlCleaner();
		rootNode = cleaner.clean(in);
		return rootNode;
	}

	public static class ParameterValue {
		private String parameter;
		private String value;

		ParameterValue(String _parameter, String _value) {
			parameter = _parameter;
			value = _value;
		}

		public String getParameter() {
			return parameter;
		}

		public String getValue() {
			return value;
		}
	}
}