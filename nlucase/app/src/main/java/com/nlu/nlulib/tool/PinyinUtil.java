package com.nlu.nlulib.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtil {
	
	private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();;

	public PinyinUtil() {
	}

	public static String getPinYin(String source, boolean polyphone) throws RuntimeException {
		return getPinYin(source, "", polyphone);
	}

	public static String getPinYin(String source, String seperator, boolean polyphone) {
		if (StringUtils.isBlank(source))
			return "";
		
		if (polyphone && StringUtils.equals(seperator, "|")) {
			throw new RuntimeException("seperator can not be \"|\" when using polyphone mode.");
		}

		StringBuffer sbTarget = new StringBuffer();
		String[] pinyin = null;

		char lastCharacter = 0;
		for (int i = 0; i < source.length(); i++) {
			char character = source.charAt(i);
			
			if ((int) character <= 128)
				sbTarget.append(character);
			else {
				try {
					pinyin = PinyinHelper.toHanyuPinyinStringArray(character, format);

				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
				if (pinyin == null || pinyin.length == 0)
					sbTarget.append(character);
				else {
					if (lastCharacter != 0 && (int) lastCharacter <= 128 && (int)character > 128) {
						sbTarget.append(seperator);
					}
					if ( polyphone ) {
						for (int n = 0; n < pinyin.length; n++) {
							sbTarget.append(pinyin[n]);
							if (polyphone && n != pinyin.length - 1) {
								sbTarget.append("|");
							}
						}
					} else {
						sbTarget.append(pinyin[0]);
					}
					sbTarget.append(seperator);
				}
			}
			lastCharacter = character;
		}
		return sbTarget.toString().trim();
	}
	
	
	public static String getPinYinRegexPattern(String source, boolean polyphone) {
		if (StringUtils.isBlank(source))
			return "";
		
		StringBuffer sbTarget = new StringBuffer();
		String[] pinyin = null;

		char lastCharacter = 0;
		for (int i = 0; i < source.length(); i++) {
			char character = source.charAt(i);
			
			if ((int) character <= 128) {
				sbTarget.append("(");
				sbTarget.append(character);
			} else {
				try {
					pinyin = PinyinHelper.toHanyuPinyinStringArray(character, format);

				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
				if (pinyin == null || pinyin.length == 0)
					sbTarget.append(character);
				else {
					if (lastCharacter != 0 && (int) lastCharacter <= 128 && (int)character > 128) {
						sbTarget.append(")?");
					}
					
					sbTarget.append("(");
					if ( polyphone ) {						
						for (int n = 0; n < pinyin.length; n++) {
							sbTarget.append(pinyin[n]);
							if (polyphone && n != pinyin.length - 1) {
								sbTarget.append("|");
							}
						}
					} else {
						sbTarget.append(pinyin[0]);
					}
					sbTarget.append(")?");
				}
			}
			lastCharacter = character;
		}
		return sbTarget.toString().trim();
	}
	
	
	public static List<String> getPinYinList(String source, boolean polyphone) {
		if (StringUtils.isBlank(source))
			return Collections.emptyList();

		List<String> pinyinList = new ArrayList<>();
		String[] pinyin = null;

		char lastCharacter = 0;
		for (int i = 0; i < source.length(); i++) {
			char character = source.charAt(i);
			
			if ((int) character <= 128)
				pinyinList.add(String.valueOf(character));
			else {
				try {
					pinyin = PinyinHelper.toHanyuPinyinStringArray(character, format);

				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
				if (pinyin == null || pinyin.length == 0)
					pinyinList.add(String.valueOf(character));
				else {
					if (lastCharacter != 0 && (int) lastCharacter <= 128 && (int)character > 128) {
						;
					}
					if ( polyphone ) {
						StringBuffer sbTarget = new StringBuffer();
						for (int n = 0; n < pinyin.length; n++) {
							sbTarget.append(pinyin[n]);
							if (polyphone && n != pinyin.length - 1) {
								sbTarget.append("|");
							}
						}
						pinyinList.add(sbTarget.toString());
					} else {
						pinyinList.add(pinyin[0]);
					}
				}
			}
			lastCharacter = character;
		}
		return pinyinList;
	}
}