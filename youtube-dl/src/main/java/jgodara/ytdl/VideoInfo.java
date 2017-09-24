package jgodara.ytdl;

import java.io.Serializable;

public class VideoInfo implements Serializable {

	private static final long serialVersionUID = -2838893611421897253L;
	
	private String videoId;
	private String quality;
	private String url;
	private String title;

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
