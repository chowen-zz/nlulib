package com.nlu.nlulib.rule.filter;

import com.nlu.nlulib.parser.NluContext;
import com.nlu.nlulib.rule.NluFilter;
import com.nlu.nlulib.rule.NluRules;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


public class DateTime2NumberFilter implements NluFilter {
	
	private static final Map<String, Integer> DATE_TO_NUMBER_MAP = new HashMap<>();
	
	private static final Map<String, Integer> CHAR_TO_NUMBER_MAP = new HashMap<>();

    static {
    	CHAR_TO_NUMBER_MAP.put("一", 1);
    	CHAR_TO_NUMBER_MAP.put("二", 2);
    	CHAR_TO_NUMBER_MAP.put("三", 3);
    	CHAR_TO_NUMBER_MAP.put("四", 4);
    	CHAR_TO_NUMBER_MAP.put("五", 5);
    	CHAR_TO_NUMBER_MAP.put("六", 6);
    	CHAR_TO_NUMBER_MAP.put("七", 7);
    	CHAR_TO_NUMBER_MAP.put("八", 8);
    	CHAR_TO_NUMBER_MAP.put("九", 9);
    	CHAR_TO_NUMBER_MAP.put("十", 1);
        
        
        
        // TODO: hard coded for now, should be loaded from an external file
    	DATE_TO_NUMBER_MAP.put("昨天", 1);
    	DATE_TO_NUMBER_MAP.put("今天", 0);
    	DATE_TO_NUMBER_MAP.put("前天", 2);
    	DATE_TO_NUMBER_MAP.put("昨日", 1);
    	DATE_TO_NUMBER_MAP.put("今日", 0);
    	DATE_TO_NUMBER_MAP.put("前日", 2);
    	
        DATE_TO_NUMBER_MAP.put("一天", 1);
        DATE_TO_NUMBER_MAP.put("二天", 2);
        DATE_TO_NUMBER_MAP.put("三天", 3);
        DATE_TO_NUMBER_MAP.put("四天", 4);
        DATE_TO_NUMBER_MAP.put("五天", 5);
        DATE_TO_NUMBER_MAP.put("六天", 6);
        DATE_TO_NUMBER_MAP.put("七天", 7);
        DATE_TO_NUMBER_MAP.put("八天", 8);
        DATE_TO_NUMBER_MAP.put("九天", 9);
        DATE_TO_NUMBER_MAP.put("十天", 10);
        
        DATE_TO_NUMBER_MAP.put("一号", 1);
        DATE_TO_NUMBER_MAP.put("二号", 2);
        DATE_TO_NUMBER_MAP.put("三号", 3);
        DATE_TO_NUMBER_MAP.put("四号", 4);
        DATE_TO_NUMBER_MAP.put("五号", 5);
        DATE_TO_NUMBER_MAP.put("六号", 6);
        DATE_TO_NUMBER_MAP.put("七号", 7);
        DATE_TO_NUMBER_MAP.put("八号", 8);
        DATE_TO_NUMBER_MAP.put("九号", 9);
        DATE_TO_NUMBER_MAP.put("十号", 10);
        
        DATE_TO_NUMBER_MAP.put("一日", 1);
        DATE_TO_NUMBER_MAP.put("二日", 2);
        DATE_TO_NUMBER_MAP.put("三日", 3);
        DATE_TO_NUMBER_MAP.put("四日", 4);
        DATE_TO_NUMBER_MAP.put("五日", 5);
        DATE_TO_NUMBER_MAP.put("六日", 6);
        DATE_TO_NUMBER_MAP.put("七日", 7);
        DATE_TO_NUMBER_MAP.put("八日", 8);
        DATE_TO_NUMBER_MAP.put("九日", 9);
        DATE_TO_NUMBER_MAP.put("十日", 10);
        
        
        DATE_TO_NUMBER_MAP.put("1天", 1);
        DATE_TO_NUMBER_MAP.put("2天", 2);
        DATE_TO_NUMBER_MAP.put("3天", 3);
        DATE_TO_NUMBER_MAP.put("4天", 4);
        DATE_TO_NUMBER_MAP.put("5天", 5);
        DATE_TO_NUMBER_MAP.put("6天", 6);
        DATE_TO_NUMBER_MAP.put("7天", 7);
        DATE_TO_NUMBER_MAP.put("8天", 8);
        DATE_TO_NUMBER_MAP.put("9天", 9);
        DATE_TO_NUMBER_MAP.put("10天", 10);
        
        DATE_TO_NUMBER_MAP.put("1号", 1);
        DATE_TO_NUMBER_MAP.put("2号", 2);
        DATE_TO_NUMBER_MAP.put("3号", 3);
        DATE_TO_NUMBER_MAP.put("4号", 4);
        DATE_TO_NUMBER_MAP.put("5号", 5);
        DATE_TO_NUMBER_MAP.put("6号", 6);
        DATE_TO_NUMBER_MAP.put("7号", 7);
        DATE_TO_NUMBER_MAP.put("8号", 8);
        DATE_TO_NUMBER_MAP.put("9号", 9);
        DATE_TO_NUMBER_MAP.put("10号", 10);
        
        DATE_TO_NUMBER_MAP.put("1日", 1);
        DATE_TO_NUMBER_MAP.put("2日", 2);
        DATE_TO_NUMBER_MAP.put("3日", 3);
        DATE_TO_NUMBER_MAP.put("4日", 4);
        DATE_TO_NUMBER_MAP.put("5日", 5);
        DATE_TO_NUMBER_MAP.put("6日", 6);
        DATE_TO_NUMBER_MAP.put("7日", 7);
        DATE_TO_NUMBER_MAP.put("8日", 8);
        DATE_TO_NUMBER_MAP.put("9日", 9);
        DATE_TO_NUMBER_MAP.put("10日", 10);
    }

	@Override
	public String filter(String input, NluContext context) {

		if (StringUtils.isEmpty(input)) {
			return input;
		}
		
		
		for (Map.Entry<String, Integer> entry : CHAR_TO_NUMBER_MAP.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();
			
			input = input.replaceAll(key, value+"");
		}
		
		if ( !input.contains("分") && !input.contains("分钟")) {
			input = input.replaceAll("半个?小时", "30分");
		}
		
		if ( !input.contains("秒")) {
			input = input.replaceAll("半分钟?", "30秒");
		}
		
		if (! input.contains("年") &&  !input.contains("月")) {
			for (Map.Entry<String, Integer> entry : DATE_TO_NUMBER_MAP.entrySet()) {
				String key = entry.getKey();
				Integer value = entry.getValue();
				
				if (input.contains(key)) {
					Date d = new Date();   
					SimpleDateFormat df = null;
	
					if (input.indexOf("天前") != -1 || input.indexOf("日前") != -1 || input.indexOf("号前") != -1) {				
						df = new SimpleDateFormat("yyyy年MM月dd日前");
					} else if (input.indexOf("天后") != -1 || input.indexOf("日后") != -1 || input.indexOf("号后") != -1 || input.indexOf("最近") != -1) {
						df = new SimpleDateFormat("最近yyyy年MM月dd日");
					} else if (input.contains("今天") ||input.contains("昨天") ||input.contains("前天") ||
							input.contains("今日") ||input.contains("昨日") ||input.contains("前日") ){
						df = new SimpleDateFormat("yyyy年MM月dd日");
					} else {
						df = new SimpleDateFormat("最近yyyy年MM月dd日");
					}
					
					String replace = df.format(new Date(d.getTime() - value * 24 * 60 * 60 * 1000));
					return input.replace(key, replace);
				}
			}
		}
		
		
		return input;
	}

	@Override
	public void setRules(NluRules rules) {
		// TODO Auto-generated method stub
		
	}

}
