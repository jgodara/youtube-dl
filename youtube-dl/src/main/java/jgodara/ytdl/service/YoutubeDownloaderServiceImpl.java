package jgodara.ytdl.service;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import jgodara.ytdl.YoutubeDownloadManager;
import jgodara.ytdl.YoutubeDownloaderService;
import jgodara.ytdl.configuration.Settings;

public class YoutubeDownloaderServiceImpl implements YoutubeDownloaderService {
	
	private YoutubeDownloadManager downloadManager;
	
	public YoutubeDownloaderServiceImpl(YoutubeDownloadManager downloadManager) {
		this.downloadManager = downloadManager;
	}

	public void download() throws Exception {
		String inputFileName = Settings.getConfiguration().getOption("-f", "--file");
		if (inputFileName != null && !"".equals(inputFileName)) {
			File inputUrls = new File(inputFileName);
			@SuppressWarnings("deprecation")
			List<String> urls = FileUtils.readLines(inputUrls);
			for (String url : urls) {
				downloadManager.addToDownloadQueue(url);
			}
		} else {
			String videoUrl = Settings.getConfiguration().getOption("-u", "--url");
			downloadManager.addToDownloadQueue(videoUrl);
		}
		downloadManager.startDownloads();
	}

}
