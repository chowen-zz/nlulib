package com.nlu.nlulib.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class NluPatternConfig {

	private Pattern pattern;
	
	private Map<String, Integer> groupName2IndexMap;

	public static final String[] defaultGroups = {"act", "who", "whom", "what", "how"};

	public NluPatternConfig(Pattern pattern, String[] returnGroups) {
		super();
		this.pattern = pattern;

		processGroupIndex(returnGroups);
		processGroupIndex(defaultGroups);
	}
	
	public NluPatternConfig(Pattern pattern) {
		this(pattern, null);
	}


	public Pattern getPattern() {
		return pattern;
	}

	
	private void processGroupIndex(String[] returnGroups) {
		
		if (returnGroups == null)
			return;
		
		for(int i = 0; i < returnGroups.length; i++) {
					
			int end = pattern.pattern().indexOf(returnGroups[i]);
			
			if (end == -1)
				continue;
			int groupIndex = 0;
			for(int n = 0; n < end; n++) {
				String regex = pattern.pattern();
				if (regex.charAt(n) == '(') {
					
					//String regex = "(?<!要)(?<=别)(打)(?=架)(?!人)(?:.)(?>.?)";
					if (n + 3 < end && regex.charAt(n+1) == '?' && regex.charAt(n+2) == '<') {
						if (regex.charAt(n+3) == '!' || regex.charAt(n+3) == '='  ) {
							continue;
						}
					}
					if (n + 2 < end && regex.charAt(n+1) == '?') {
						if (regex.charAt(n+2) == '=' || regex.charAt(n+2) == '!' || regex.charAt(n+2) == ':' || regex.charAt(n+2) == '>' ) {
							continue;
						}
					}
					
					groupIndex++;
				}
			}
			if (groupIndex != 0) {
				if (groupName2IndexMap == null) {
					groupName2IndexMap = new HashMap<>();
				}
				groupName2IndexMap.put(returnGroups[i], groupIndex);
			}
		}
		
	}
	
	public int getGroupIndex(String groupName) {
		
		if (groupName2IndexMap == null) {
			return -1;
		}
		
		Integer groupIndex = groupName2IndexMap.get(groupName);
		
		if (groupIndex != null) {
			return groupIndex.intValue();
		}
		return -1;
	}
	
}
