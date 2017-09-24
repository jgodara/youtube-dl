package jgodara.ytdl.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {

	private static Configuration configuration = null;

	@SuppressWarnings("serial")
	private static final List<String> OPTIONS_ARGS = new ArrayList<String>() {
		{
			add("-u");	add("--url");		// URL of video to download
			add("-f");	add("--file");		// File containing video URLs
			add("-i");	add("--use-proxy");	// Proxy - Use supplied proxy server
		}
	};

	@SuppressWarnings("serial")
	private static final List<Character> FLAGS_ARGS = new ArrayList<Character>() {
		{
			add('X');		// For verbosity
			add('p');		// Proxy - Using proxy list
			add('P');		// Proxy - Using proxynova
		}
	};

	public static Configuration getConfiguration() {
		if (configuration == null)
			throw new IllegalStateException("The program has not been configured");
		return configuration;
	}

	public static void configure(String[] args) {
		if (args.length == 0)
			throw new IllegalArgumentException("Usage: ytdl -u url -o filename");

		if (configuration != null)
			throw new IllegalStateException(
					"The program has already been configured");

		int i = 0, j;
		String arg;
		char flag;
		
		Map<String, String> options = new HashMap<String, String>();
		List<Character> flags = new ArrayList<Character>();

		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];
			
			if (OPTIONS_ARGS.contains(arg)) {
				if (i < args.length)
					options.put(arg, args[i++].trim());
				else
					throw new IllegalArgumentException(arg
							+ " must have some value");
			} else {
				for (j = 1; j < arg.length(); j++) {
					flag = arg.charAt(j);
					if (FLAGS_ARGS.contains(flag)) {
						flags.add(flag);
					} else {
						throw new IllegalArgumentException("Invalid option: "
								+ flag);
					}
				}
			}
		}
		
		configuration = new Configuration(flags, options);
	}

}
