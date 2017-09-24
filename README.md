#YouTube Downloader.

You can use this tool to download videos from YouTube.

## Installation Instructions.

You will need JDK to build and install the tool locally. If you don't have JDK, you can use the binary, but you will still need JRE to run it.

To install it, simply checkout the project from `trunk` and then run `mvn clean compile assembly:single` to build an executable JAR.

## Usage instructions.

To download a single video:

`java -jar ytdl.jar -u <url>`
or
`java -jar ytdl.jar --url <url>`

TO download multiple videos, put their URLs in a file in separate lines and execute:

`java -jar -f "path/to/file"`
or
`java -jar --file "path/to/file"`

## Network options

Sometimes the YouTube API restricts the download. You can use proxies in such cases, but this is heavily unreliable.

-i <ip:port>		- Use the supplied server as proxy.
-p					- Use the internal  proxy server list (from file proxylist).
-P					- Use Proxynova.

## Other options.

-X 					- Enables verbose output.

## Examples

Download a video using internal proxy with verbose output enabled:

`java -jar ytdl.jar -u <url> -pX`

Download multiple videos by reading *videos.txt* with verbose output enabled:

`java -jar ytdl.jar -f videos.txt -X`