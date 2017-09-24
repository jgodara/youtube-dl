package jgodara.ytdl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import jgodara.ytdl.configuration.Settings;

public class ConnectionUtils {
	
	private static final List<Proxy> proxyPool = new ArrayList<Proxy>();
	private static final List<Proxy> proxiesInUse = new ArrayList<Proxy>();
	
	public static String getResponse(String url) throws IOException {
		return getResponse(url, false);
	}
	
	public static String getResponse(String url, boolean usingProxy) throws IOException {
		URL urlObj = new URL(url);
		HttpURLConnection connection = null;
		if (usingProxy) {
			connection = (HttpURLConnection) urlObj.openConnection(createProxy());
		} else {
			 connection = (HttpURLConnection) urlObj.openConnection();
		}
		connection.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String inoutLine;
		StringBuffer response = new StringBuffer();
		while ((inoutLine = in.readLine()) != null) {
			response.append(inoutLine);
		}

		in.close();
		
		return response.toString();
	}
	
	public static Proxy createProxy() throws IOException {
		
		Proxy proxy = null;
		boolean autoCfg = Settings.getConfiguration().isFlagEnabled('p') || Settings.getConfiguration().isFlagEnabled('P');
		if (!autoCfg) {
			
			String ip = Settings.getConfiguration().getOption("-i", "--use-proxy");
			String[] parts = ip.trim().split(":");
			InetSocketAddress address = new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
			proxy = new Proxy(Proxy.Type.HTTP, address);
			
		} else if (Settings.getConfiguration().isFlagEnabled('p')) {
			
			if (Settings.getConfiguration().isVerbose())
				System.out.println("Using internal proxy pool.");
			
			if (proxyPool.isEmpty())
				initializeProxyPool();
			
			for (Proxy proxy2 : proxyPool) {
				if (!proxiesInUse.contains(proxy2)) {
					proxiesInUse.add(proxy2);
					proxy = proxy2;
					break;
				}
			}
			
			if (proxy == null) {
				
				if (Settings.getConfiguration().isVerbose())
					System.out.println("Pooled proxy servers are not free.");
				
				proxy = createProxyUsingProxynova();
				
			}
			
		} else {
			
			proxy = createProxyUsingProxynova();
			
		}
		
		if (Settings.getConfiguration().isVerbose())
			System.out.println("Proxy created:\t\t" + proxy);
		
		return proxy;
	}
	
	private static Proxy createProxyUsingProxynova() throws IOException {
		if (Settings.getConfiguration().isVerbose())
			System.out.println("Using proxynova.");
	
		String pac = getResponse("https://www.proxynova.com/proxy.pac").replaceAll("/\\/\\*(.*)\\*\\/", "");
		String pacs = "";
		
		String proxyIp = "";
		String proxyPort = "";
		
		Pattern pacsPattern = Pattern.compile("var .*=\\[\"(.*)\"\\];");
		Matcher pacsMatcher = pacsPattern.matcher(pac);
		if (pacsMatcher.find())
			pacs = pacsMatcher.group(1);
		else
			throw new IOException("Cannot initialize proxy.");
		
		String[] pacsData = pacs.split(",");
		for (int i = 0 ; i <= 1 ; i++) {
			pacsPattern = Pattern.compile("\\\\x(..)");
			pacsMatcher = pacsPattern.matcher(pacsData[i]);
			while(pacsMatcher.find()) {
				int hexVal = Integer.parseInt(pacsMatcher.group(1), 16);
				if (i == 0)
					proxyIp += (char) hexVal;
				else
					proxyPort += (char) hexVal;
			}
		}
		
		proxyPort = proxyPort.substring(1);
		
		return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIp, Integer.parseInt(proxyPort)));
	}
	
	private static void initializeProxyPool() throws IOException {
		if (!proxyPool.isEmpty())
			throw new IllegalStateException("The proxy pool has already been initialized");
		
		@SuppressWarnings("deprecation")
		List<String> ips = FileUtils.readLines(new File("proxylist"));
		for (String ip : ips) {
			String[] parts = ip.trim().split(":");
			InetSocketAddress address = new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
			Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
			proxyPool.add(proxy);
			
			if (Settings.getConfiguration().isVerbose())
				System.out.println("Pooled proxy connection: " + proxy);
		}
	}

}
