package com.nlu.nlulib.parser;


import com.nlu.nlulib.rule.NluRuleConfiguration;

public class NluParserFactory {
	
	private static NluRuleConfiguration configuration;
	
	static {
		configuration = new NluRuleConfiguration();
	}

	public static NluParser create() throws RuntimeException {
		configuration.configure();
		
		return new NluRuleParser(configuration);
	}
}
