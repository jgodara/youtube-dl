package jgodara.ytdl.startup;

import jgodara.ytdl.YoutubeDownloadManager;
import jgodara.ytdl.YoutubeDownloaderService;
import jgodara.ytdl.configuration.Settings;
import jgodara.ytdl.manager.YoutubeDownloaderManagerImpl;
import jgodara.ytdl.service.YoutubeDownloaderServiceImpl;

public class YouTubeDownloader {

	public static void main(String[] args) throws Exception {
		
		try {
			Settings.configure(args);
			YoutubeDownloaderService downloaderService = createDownloaderService();
			downloaderService.download();
		} catch (Exception ex) {
			if (ex instanceof NullPointerException)
				ex.printStackTrace();
			else
				System.err.println(ex.getMessage());
			
			System.exit(1);
		}
	}
	
	private static YoutubeDownloaderService createDownloaderService() {
		YoutubeDownloadManager downloadManager = new YoutubeDownloaderManagerImpl();
		YoutubeDownloaderServiceImpl downloaderService = new YoutubeDownloaderServiceImpl(downloadManager);
		return downloaderService;
	}

}
