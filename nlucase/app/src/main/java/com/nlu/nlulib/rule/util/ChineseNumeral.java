package com.nlu.nlulib.rule.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChineseNumeral {
	
	private static final String DIGITS[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
	private static final String UNITS[] = {"", "", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千", "万"};
	
	private static final String zeroReplaceRegex = "零([十百千万亿])";
	
	private static final Pattern zeroReplacePattern;
	
	static {
		zeroReplacePattern = Pattern.compile(zeroReplaceRegex);
	}
	
	/**
	 * 返回字符
	 * @param str
	 * @return
	 */
	public static String toChinese(String str) {
		
		if (str == null || str.length() > UNITS.length)
			return "";
		
		try {
		
			StringBuffer sBuffer = new StringBuffer();
			int prevDigit = -1;
			for (int i = 0; i < str.length(); i++) {
	
				int unitIndex = str.length() - i;
				String unitString = UNITS[unitIndex];
				
				int digit = str.charAt(i) - '0';
				String digitString = DIGITS[digit];
				
				if (prevDigit != 0 || digit != 0) {
					
					sBuffer.append(digitString);
					if (digit != 0 || unitString.equals("万") || unitString.equals("亿") ) 
						sBuffer.append(unitString);
				}
				prevDigit = digit;
			}
			
			String ret = sBuffer.toString();
			int i = ret.length() - 1;
			for (; i >= 0; i--) {
				if (ret.charAt(i) != '零')
					break;
			}
			
			if (i < ret.length() - 1) {
				ret = ret.substring(0, i + 1);
			}
			
			Matcher matcher = zeroReplacePattern.matcher(ret);
			if (matcher.find()) {
				String unit = matcher.group(1);
				ret = ret.replaceAll(zeroReplaceRegex, unit);
			}
			
			ret = ret.replaceAll("^一十", "十");
			
			return ret;
		} catch (Exception e) {
			
		}
		return "";
	}

}
