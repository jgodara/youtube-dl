package jgodara.ytdl.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {

	private List<Character> flags = new ArrayList<Character>();
	private Map<String, String> options = new HashMap<String, String>();
	
	public Configuration(List<Character> flags, Map<String, String> options) {
		this.flags = flags;
		this.options = options;
	}

	public boolean isVerbose() {
		return isFlagEnabled('X');
	}

	public boolean isFlagEnabled(char flag) {
		return flags.contains(flag);
	}

	public String getOption(String optionName, String altName) {
		String option = options.get(optionName);
		if (option == null || "".equals(option))
			option = options.get(altName);
		
		return option;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Configuration: \n")
		.append("Verbose:\t\t").append(isVerbose())
		.append("Flags:\t\t").append(flags)
		.append("Options:\n");
		
		for (String key : options.keySet()) {
			sb.append("\t").append(key).append(" = ").append(options.get(key))
			.append("\n");
		}
		
		return sb.toString();
	}

}
