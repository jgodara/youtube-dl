package jgodara.ytdl.thread;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import jgodara.ytdl.VideoInfo;

public class DownloadThread extends Thread {
	
	private VideoInfo videoInfo;
	
	public DownloadThread(VideoInfo videoInfo) {
		this.videoInfo = videoInfo;
	}
	
	@Override
	public void run() {
		BufferedInputStream in = null;
		FileOutputStream out = null;

		try {
			URL url = new URL(videoInfo.getUrl());
			URLConnection conn = url.openConnection();
			int size = conn.getContentLength();

			if (size <= 0) {
				System.out.println(videoInfo.getTitle() + " [Could not get the file size]");
			} else {
				System.out.println(videoInfo.getTitle() + "[Size: " + (size / 1024.0)/1024.0 + " MBytes]");
			}

			in = new BufferedInputStream(url.openStream());
			out = new FileOutputStream(videoInfo.getTitle() + "_" + videoInfo.getVideoId() + ".mp4");
			byte data[] = new byte[1024];
			int count;
			double sumCount = 0.0;

			long startTime = System.currentTimeMillis();
			while ((count = in.read(data, 0, 1024)) != -1) {
				out.write(data, 0, count);

				sumCount += count;
				long speedInKBps = 0;
				if (size > 0) {
					long timeInSecs = (System.currentTimeMillis() - startTime) / 1000;
					speedInKBps = (long) ((sumCount / timeInSecs) / 1024);
					String prog = "Downloading: " + videoInfo.getTitle() + "\t\t[" + ( (int) (sumCount / size * 100)) + "%]\t(" + speedInKBps + " KBytes/s)\r";
					System.out.write(prog.getBytes());
				}
			}

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e3) {
					e3.printStackTrace();
				}
			if (out != null)
				try {
					out.close();
				} catch (IOException e4) {
					e4.printStackTrace();
				}
		}
	}

}
