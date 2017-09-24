package jgodara.ytdl;

public interface YoutubeDownloadManager {
	
	public void addToDownloadQueue(String videoUrl);
	
	public void startDownloads();

}
