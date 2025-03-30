package uk.co.filesecur;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

public class TheAppleWikiHTTPClient {

	private String buildid, model;

	public TheAppleWikiHTTPClient(String buildid, String model) {
		this.setBuildId(buildid);
		this.setModel(model);
	}

	public String getBuildId() {
		return buildid;
	}

	public void setBuildId(String buildid) {
		this.buildid = buildid;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	// https://theapplewiki.com/index.php?search=16H81+iPhone6%2C1&title=Special%3ASearch&profile=advanced&fulltext=1&ns2304=1
	private static void addRequestHeaders(HttpRequestBase request) {
		request.addHeader("Connection", "keep-alive");
		request.addHeader(
				"sec-ch-ua",
				"\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"100\", \"Microsoft Edge\";v=\"100\"");
		request.addHeader("sec-ch-ua-mobile", "?0");
		request.addHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4863.0 Safari/537.36 Edg/100.0.1163.1");
		request.addHeader("sec-ch-ua-platform", "\"Windows\"");
		request.addHeader("Sec-Fetch-Site", "same-origin");
		request.addHeader("Sec-Fetch-Mode", "cors");
		request.addHeader("Sec-Fetch-Dest", "empty");
		request.addHeader("sec-gpc", "1");
	}

	private StringBuilder consume(CloseableHttpResponse response)
			throws UnsupportedOperationException, IOException {
		return consume(response, true);
	}

	private StringBuilder consume(CloseableHttpResponse response,
			boolean headers) throws UnsupportedOperationException, IOException {
		StringBuilder sb = new StringBuilder();
		if (headers) {
			sb.append(response.getStatusLine() + "" + '\n');
			for (Header header : response.getAllHeaders()) {
				sb.append(header + "\n");
			}
			sb.append("\n");
		}
		InputStream is = response.getEntity().getContent();
		int r = 0;
		byte[] b = new byte[1024];
		while ((r = is.read(b, 0, b.length)) != -1) {
			byte[] b2 = Arrays.copyOfRange(b, 0, r);
			sb.append(new String(b2));
		}
		return sb;
	}

	public Optional<String> getBuildIdForVersion(String ver) {
		// https://api.ipsw.me/v4/device/iPhone6,1
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet("https://api.ipsw.me/v4/device/"
					+ model);
			this.addRequestHeaders(request);
			String response = consume(httpClient.execute(request), false)
					.toString();
			String token = response;
			int i = 0;
			for (String split : token.split("\"version\":\"")) {
				if (i == 0) {
					i++;
					continue;
				}
				String version = split.substring(0, split.indexOf("\""));
				String buildid = split.substring(split
						.indexOf("\"buildid\":\"") + "\"buildid\":\"".length());
				buildid = buildid.substring(0, buildid.indexOf("\""));
				if (version.equals(ver)) {
					return Optional.of(buildid);
				}
				i++;
			}
			return Optional.empty();
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	public Optional<String> getFirmwareKeyWebpage() {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(
					"https://theiphonewiki.com/w/index.php?search="
							+ buildid
							+ "+"
							+ URLEncoder.encode(model,
									StandardCharsets.UTF_8.toString())
							+ "&title=Special%3ASearch&profile=advanced&fulltext=1&ns2304=1");
			this.addRequestHeaders(request);
			String response = consume(httpClient.execute(request), false)
					.toString();
			String token = response;
			token = token.substring(token.indexOf("<a href=\"/wiki/")
					+ "<a href=\"/wiki/".length());
			token = token.substring(0, token.indexOf("\""));
			token = "https://www.theiphonewiki.com/wiki/" + token;
			return Optional.of(token);
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	public FirmwareKeys getAllFirmwareKeys() {
		// keypage-filename" id="keypage-
		FirmwareKeys keys = new FirmwareKeys();
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			String url = this.getFirmwareKeyWebpage().get();
			HttpGet request = new HttpGet(url);
			this.addRequestHeaders(request);
			String response = consume(httpClient.execute(request), false)
					.toString();
			String token = response;
			int i = 0;
			for (String split : token.split("keypage-filename\" id=\"keypage-")) {
				if (i == 0) {
					i++;
					continue;
				}
				StringBuilder ivkey = new StringBuilder();
				String fn = split.substring(split.indexOf("\">")
						+ "\">".length());
				if (fn.contains("sep-firmware")) {
					continue;
				}
				fn = fn.substring(0, fn.indexOf("</"));
				String iv = split.substring(split
						.indexOf("<code id=\"keypage-")
						+ "<code id=\"keypage-".length());
				iv = iv.substring(iv.indexOf("\">") + "\">".length());
				iv = iv.substring(0, iv.indexOf("</"));
				ivkey.append(iv);
				int begindex = split.indexOf("<code id=\"keypage-")
						+ "<code id=\"keypage-".length();
				String t = split.substring(begindex);
				int nextfnindex = t.indexOf("<code id=\"keypage-");
				if (nextfnindex != -1) {
					// iv+key
					String key = t.substring(t.indexOf("<code id=\"keypage-")
							+ "<code id=\"keypage-".length());
					key = key.substring(key.indexOf("\">") + "\">".length());
					key = key.substring(0, key.indexOf("</"));
					ivkey.append(key);
				}
				keys.getKeys().put(fn, ivkey.toString());
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return keys;
	}

	public Optional<String> getFirmwareKey(String fn2) {
		// keypage-filename" id="keypage-
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			String url = this.getFirmwareKeyWebpage().get();
			HttpGet request = new HttpGet(url);
			this.addRequestHeaders(request);
			String response = consume(httpClient.execute(request), false)
					.toString();
			String token = response;
			int i = 0;
			StringBuilder ivkey = new StringBuilder();
			for (String split : token.split("keypage-filename\" id=\"keypage-")) {
				if (i == 0) {
					i++;
					continue;
				}
				String fn = split.substring(split.indexOf("\">")
						+ "\">".length());
				if (fn.contains("sep-firmware")) {
					continue;
				}
				fn = fn.substring(0, fn.indexOf("</"));
				if (!fn.equals(fn2)) {
					continue;
				}
				String iv = split.substring(split
						.indexOf("<code id=\"keypage-")
						+ "<code id=\"keypage-".length());
				iv = iv.substring(iv.indexOf("\">") + "\">".length());
				iv = iv.substring(0, iv.indexOf("</"));
				ivkey.append(iv);
				int begindex = split.indexOf("<code id=\"keypage-")
						+ "<code id=\"keypage-".length();
				String t = split.substring(begindex);
				int nextfnindex = t.indexOf("<code id=\"keypage-");
				if (nextfnindex != -1) {
					// iv+key
					String key = t.substring(t.indexOf("<code id=\"keypage-")
							+ "<code id=\"keypage-".length());
					key = key.substring(key.indexOf("\">") + "\">".length());
					key = key.substring(0, key.indexOf("</"));
					ivkey.append(key);
				}
				i++;
			}
			return Optional.of(ivkey.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

}
