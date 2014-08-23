package ua.infoshoc.megastyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import android.content.Context;

public class MultipartFormData {

	private static final String LINE_FEED = "\r\n";

	private String boundary;
	private HttpsURLConnection httpsURLConnection;
	private PrintWriter printWriter;
	private String charset;
	private OutputStream outputStream;

	public MultipartFormData(Context context, String requestURL,
			String cookies, String charset) throws IOException,
			KeyStoreException, NoSuchAlgorithmException, CertificateException,
			KeyManagementException {
		boundary = "===" + System.currentTimeMillis() + "===";

		// Load CAs from an InputStream
		// (could be from a resource or ByteArrayInputStream or ...)
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		// From https://www.washington.edu/itconnect/security/ca/load-der.crt
		InputStream caInput = context.getResources().openRawResource(
				R.raw.rapidssl);
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
		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(null, tmf.getTrustManagers(), null);

		URL url = new URL(requestURL);
		httpsURLConnection = (HttpsURLConnection) url.openConnection();
		httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
		// httpsURLConnection.setReadTimeout(100000);
		// httpsURLConnection.setConnectTimeout(150000);
		httpsURLConnection.setRequestMethod("POST");
		// httpsURLConnection.setUseCaches(false);
		// httpsURLConnection.setDoInput(true);
		httpsURLConnection.setDoOutput(true);
		httpsURLConnection.setChunkedStreamingMode(0);
		httpsURLConnection.setRequestProperty("Cookie", cookies);
		httpsURLConnection.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + boundary);

		outputStream = httpsURLConnection.getOutputStream();
		printWriter = new PrintWriter(new OutputStreamWriter(outputStream,
				charset), true);
	}

	public MultipartFormData add(String name, File file) throws IOException {
		printWriter.append("--" + boundary).append(LINE_FEED).flush();
		if (file != null && file.exists()) {
			String fileName = file.getName();

			printWriter
					.append("Content-Disposition: form-data; name=\"" + name
							+ "\"; filename=\"" + fileName + "\"")
					.append(LINE_FEED)
					.append("Content-Type: "
							+ URLConnection.guessContentTypeFromName(fileName))
					.append(LINE_FEED)
					.append("Content-Transfer-Encoding: binary")
					.append(LINE_FEED).append(LINE_FEED).flush();

			final int blockSize = 4096;
			byte[] block = new byte[blockSize];
			InputStream inputStream = new FileInputStream(file);
			for (int bytesRead = inputStream.read(block); bytesRead != -1; bytesRead = inputStream
					.read(block)) {
				outputStream.write(block, 0, bytesRead);
			}
			outputStream.flush();
			inputStream.close();
		} else {
			printWriter
					.append("Content-Disposition: form-data; name=\"" + name
							+ "\"; filename=\"\"")
					.append("Content-Type: application/octet-stream")
					.append(LINE_FEED).append(LINE_FEED).flush();

		}

		return this;
	}

	public MultipartFormData add(String name, String value) {
		printWriter
				.append("--" + boundary)
				.append(LINE_FEED)
				.append("Content-Disposition: form-data; name=\"" + name + "\"")
				.append(LINE_FEED)
				.append("Content-Type: text/plain; charset=" + charset)
				.append(LINE_FEED).append(LINE_FEED).append(value)
				.append(LINE_FEED).flush();

		return this;
	}

	/*
	 * public MultipartFormData addCookie(String name, String value){
	 * printWriter.append("Cookie: " + name + "=" + value).append(LINE_FEED)
	 * .flush(); return this; }
	 */

	public TagNode send() throws IOException {
		printWriter.append(LINE_FEED).append("--" + boundary + "--")
				.append(LINE_FEED).close();
		InputStream inputStream = httpsURLConnection.getInputStream();
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode rootNode = cleaner.clean(inputStream);
		return rootNode;
	}

}
