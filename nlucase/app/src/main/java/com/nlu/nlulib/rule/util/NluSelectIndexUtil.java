package com.nlu.nlulib.rule.util;

import com.nlu.nlulib.parser.NluContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NluSelectIndexUtil {
	private static final Map<String, String> CHOICE_TO_INDEX_MAP = new HashMap<>();

	private static final String CAPITAL_NUMBER_1_to_10_REGEX  = "(?<!([一二三四五六七八九十]|\\d).{0,10}?)[一二三四五六七八九十](?!.*?([一二三四五六七八九十百千万亿]|\\d))"; // here 十 means 10
	private static final String CAPITAL_NUMBER_11_to_19_REGEX = "(?<!([一二三四五六七八九十]|\\d).{0,10}?)十[一二三四五六七八九](?!.*?([一二三四五六七八九十百千万亿]|\\d))"; //here 十 replace 1
	private static final String CAPITAL_NUMBER_X0_REGEX = "(?<!([一二三四五六七八九十]|\\d).{0,10}?)[二三四五六七八九]十(?!.*?([一二三四五六七八九十百千万亿]|\\d))";  // 20,30...90  here十 means 0
	private static final String CAPITAL_NUMBER_XX_REGEX = "(?<!([一二三四五六七八九十]|\\d).{0,10}?)[二三四五六七八九]十[一二三四五六七八九](?!.*?([一二三四五六七八九十百千万亿]|\\d))";  // 21,32...94  here 十  mean ""
	
	private static final Pattern CAPITAL_NUMBER_1_to_10_PATTERN;
	private static final Pattern CAPITAL_NUMBER_11_to_19_PATTERN;
	private static final Pattern CAPITAL_NUMBER_X0_PATTERN;
	private static final Pattern CAPITAL_NUMBER_XX_PATTERN;
	
	
	private static final String CAPITAL_NUMBER_X00_REGEX = "(?<!([一二三四五六七八九十]|\\d).{0,10}?)[一二三四五六七八九]百(?!.*?([一二三四五六七八九十百千万亿]|\\d))";
	private static final String CAPITAL_NUMBER_X01_to_X09_REGEX = "(?<!([一二三四五六七八九十]|\\d).{0,10}?)[一二三四五六七八九]百零[一二三四五六七八九](?!.*?([一二三四五六七八九十百千万亿]|\\d))";
	private static final String CAPITAL_NUMBER_XX0_REGEX = "(?<!([一二三四五六七八九十]|\\d).{0,10}?)[一二三四五六七八九]百[一二三四五六七八九]十(?!.*?([一二三四五六七八九十百千万亿]|\\d))"; // 110, 120, 130...
	private static final String CAPITAL_NUMBER_XXX_REGEX = "(?<!([一二三四五六七八九十]|\\d).{0,10}?)[一二三四五六七八九]百[一二三四五六七八九]十[一二三四五六七八九](?!.*?([一二三四五六七八九十百千万亿]|\\d))"; // XX1,XX2...XX3...XX9...
	
	private static final Pattern CAPITAL_NUMBER_X00_PATTERN;
	private static final Pattern CAPITAL_NUMBER_X01_to_X09_PATTERN;
	private static final Pattern CAPITAL_NUMBER_XX0_PATTERN;
	private static final Pattern CAPITAL_NUMBER_XXX_PATTERN;
	
	private static final String ARABIC_NUMBER_REGEX = "(0?\\.?\\d{1,10})";
	
	private static final Pattern ARABIC_NUMBER_PATTERN;
	
	// Pair here stores values for replacing hundred & ten
	private static final Map<Pattern, Pair<String, String>> capitalPatternMap = new HashMap<>();
	//private static final List<Map.Entry<Pattern, String>> capitalPatternList = new ArrayList<>();
	
    static {
        // TODO: hard coded for now, should be loaded from an external file
        CHOICE_TO_INDEX_MAP.put("一", "1");
        CHOICE_TO_INDEX_MAP.put("二", "2");
        CHOICE_TO_INDEX_MAP.put("三", "3");
        CHOICE_TO_INDEX_MAP.put("四", "4");
        CHOICE_TO_INDEX_MAP.put("五", "5");
        CHOICE_TO_INDEX_MAP.put("六", "6");
        CHOICE_TO_INDEX_MAP.put("七", "7");
        CHOICE_TO_INDEX_MAP.put("八", "8");
        CHOICE_TO_INDEX_MAP.put("九", "9");
        CHOICE_TO_INDEX_MAP.put("零", "0");
        
        CAPITAL_NUMBER_1_to_10_PATTERN = Pattern.compile(CAPITAL_NUMBER_1_to_10_REGEX);
        capitalPatternMap.put(CAPITAL_NUMBER_1_to_10_PATTERN, new Pair<String, String>("", "10"));
        
        CAPITAL_NUMBER_11_to_19_PATTERN = Pattern.compile(CAPITAL_NUMBER_11_to_19_REGEX);
        capitalPatternMap.put(CAPITAL_NUMBER_11_to_19_PATTERN, new Pair<String, String>(",", "1"));
        
        CAPITAL_NUMBER_XX_PATTERN = Pattern.compile(CAPITAL_NUMBER_XX_REGEX);
        capitalPatternMap.put(CAPITAL_NUMBER_XX_PATTERN, new Pair<String, String>("", ""));
        
        CAPITAL_NUMBER_X0_PATTERN = Pattern.compile(CAPITAL_NUMBER_X0_REGEX);
        capitalPatternMap.put(CAPITAL_NUMBER_X0_PATTERN, new Pair<String, String>("", "0"));
        
        ARABIC_NUMBER_PATTERN = Pattern.compile(ARABIC_NUMBER_REGEX);
        
    	CAPITAL_NUMBER_X00_PATTERN = Pattern.compile(CAPITAL_NUMBER_X00_REGEX); 
    	capitalPatternMap.put(CAPITAL_NUMBER_X00_PATTERN, new Pair<String, String>("00", ""));
    	
    	CAPITAL_NUMBER_X01_to_X09_PATTERN = Pattern.compile(CAPITAL_NUMBER_X01_to_X09_REGEX); 
    	capitalPatternMap.put(CAPITAL_NUMBER_X01_to_X09_PATTERN, new Pair<String, String>("", ""));
    	
    	CAPITAL_NUMBER_XX0_PATTERN = Pattern.compile(CAPITAL_NUMBER_XX0_REGEX);
    	capitalPatternMap.put(CAPITAL_NUMBER_XX0_PATTERN, new Pair<String, String>("", "0"));
    	
    	CAPITAL_NUMBER_XXX_PATTERN = Pattern.compile(CAPITAL_NUMBER_XXX_REGEX);
    	capitalPatternMap.put(CAPITAL_NUMBER_XXX_PATTERN, new Pair<String, String>("", ""));
    	
    }
    
    public static int getSelectedIndex(String text, NluContext context)  {
    	return getSelectedIndex(text, context, true);
    }
    
    public static int getSelectedIndex(String text, NluContext context, boolean ignore10e_2)  {
    	if (ignore10e_2) {
	    	if ( text.contains("千") || text.contains("万") || text.contains("亿")) {
	        	return 0;
	        }
    	}
		
		int fromTop = 1;
        if (text.contains("倒数")
                || text.contains("最后") || text.contains("后面")) {
            fromTop = -1;
        }
        
        Matcher matcher = null;
        
        for (Entry<Pattern, Pair<String, String>> patternEntry : capitalPatternMap.entrySet()) {
        	Pattern pattern = patternEntry.getKey();
        	Pair<String, String> pair = patternEntry.getValue();
        	matcher = pattern.matcher(text);
	        if (matcher.find()) {
	        	for (Entry<String, String> item : CHOICE_TO_INDEX_MAP.entrySet()) {
	                String capitalNumber = item.getKey();
	                text = text.replace(capitalNumber, item.getValue());
	            }
	        	
	        	text = text.replaceAll("百(?![分份]之?)", pair.getFirst());
	        	text = text.replace("十", pair.getSecond());
	        	break;
	        }
        }
    	
        int index = 0;
        matcher = ARABIC_NUMBER_PATTERN.matcher(text);
        if (matcher.find()) {
        	String value = matcher.group(1);
        	try {
        		index = Integer.parseInt(value);
        	} catch (Exception e) {
			}
        }
        
        int selectedIndex = index * fromTop;
                
		return selectedIndex;
    }
    
}
