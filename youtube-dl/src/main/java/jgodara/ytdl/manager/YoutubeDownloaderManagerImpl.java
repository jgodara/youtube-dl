package jgodara.ytdl.manager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jgodara.ytdl.VideoInfo;
import jgodara.ytdl.YoutubeDownloadManager;
import jgodara.ytdl.configuration.Settings;
import jgodara.ytdl.thread.DownloadThread;
import jgodara.ytdl.utils.ConnectionUtils;

public class YoutubeDownloaderManagerImpl implements YoutubeDownloadManager {

	private static final String YOUTUBE_API_TEMPLATE = "http://www.youtube.com/get_video_info?video_id=";
	
	private static final Map<String, VideoInfo> videoCache = new HashMap<String, VideoInfo>();
	
	public void addToDownloadQueue(String videoUrl) {
		Map<String, List<String>> videoInfo = null;
		URL url = null;
		try {
			url = new URL(videoUrl);
			if (!url.getHost().equalsIgnoreCase("www.youtube.com"))
				throw new IllegalArgumentException(url.getHost() + " is not a valid youtube host");
			
			String videoId = splitQuery(url.getQuery()).get("v").get(0);
			String youtubeApiResponse = ConnectionUtils.getResponse(YOUTUBE_API_TEMPLATE + videoId, usingProxy());
			
			videoInfo = splitQuery(youtubeApiResponse);
			
			addToCache(videoId, videoInfo);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("The URL is malformed: " + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("This encoding type is not supported: " + e.getMessage());
		} catch (IOException e) {
			throw new IllegalStateException("IOException: " + e.getMessage());
		}
	}

	public void startDownloads() {
		for (String videoId : videoCache.keySet()) {
			new DownloadThread(videoCache.get(videoId)).start();
		}
	}
	
	private Map<String, List<String>> splitQuery(String query)
			throws UnsupportedEncodingException {
		final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
		final String[] pairs = query.split("&");
		for (String pair : pairs) {
			final int idx = pair.indexOf("=");
			final String key = idx > 0 ? URLDecoder.decode(
					pair.substring(0, idx), "UTF-8") : pair;
			if (!query_pairs.containsKey(key)) {
				query_pairs.put(key, new LinkedList<String>());
			}
			final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder
					.decode(pair.substring(idx + 1), "UTF-8") : null;
			query_pairs.get(key).add(value);
		}
		return query_pairs;
	}
	
	private void addToCache(String videoId, Map<String, List<String>> videoInfo) throws UnsupportedEncodingException {
		if (videoCache.get(videoId) == null) {
			
			String fmtStreamMap = videoInfo.get("url_encoded_fmt_stream_map").get(0);
			Map<String, List<String>> streamMap = splitQuery(fmtStreamMap);
			
			VideoInfo info = new VideoInfo();
			info.setVideoId(videoId);
			info.setTitle(videoInfo.get("title").get(0));
			info.setUrl(streamMap.get("url").get(0));
			info.setQuality(streamMap.get("quality").get(0));
			
			System.out.println(videoInfo.get("title").get(0) + " added to download queue.");
			
			videoCache.put(videoId, info);
		}
	}
	
	private boolean usingProxy() {
		boolean autoCfg = Settings.getConfiguration().isFlagEnabled('p') || Settings.getConfiguration().isFlagEnabled('P');
		if (autoCfg)
			return true;
		else
			return Settings.getConfiguration().getOption("-i", "--use-proxy") != null;
	}

}
