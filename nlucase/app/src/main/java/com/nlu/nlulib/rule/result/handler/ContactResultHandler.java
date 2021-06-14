package com.nlu.nlulib.rule.result.handler;

import com.nlu.nlulib.parser.NluContact;
import com.nlu.nlulib.parser.NluContext;
import com.nlu.nlulib.parser.NluContactContext;
import com.nlu.nlulib.rule.NluResultHandler;
import com.nlu.nlulib.rule.NluRuleMatchResult;
import com.nlu.nlulib.rule.util.ChineseNumeral;
import com.nlu.nlulib.rule.util.NluSelectIndexUtil;
import com.nlu.nlulib.tool.PinyinUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.debatty.java.stringsimilarity.Levenshtein;

public class ContactResultHandler extends NluResultHandler {
	
	private Levenshtein levenshtein = new Levenshtein();
	
	/**
	 * 末尾的语气词规则
	 */
	private final static String REPLACE_TAIL_STATEMENT_RULE = "((么)+$)((吗)+$)((吧)+$)|((也)+$)|((来)+$)|((了)+$)|((的)+$)|((呀)+$)|((阿)+$)|((呢)+$)|((则)+$)|((啊)+$)|((啦)+$)|((哩)+$)|((哈)+$)|((咧)+$)|((哇)+$)|((呗)+$)|((喽)+$)|((罗)+$)|((呵)+$)|((耶)+$)|((罢)+$)|((噢)+$)|((咯)+$)|((呕)+$)|((哟)+$)|((嘛)+$)|((呐)+$)|((呦)+$)|((啰)+$)|((儿)+$)";
	/**
	 * 末尾的重复词规则
	 */
	private final static String REPLACE_TAIL_REPEAT_RULE = "(\\D){3,}$";
	
	/**
	 * 末尾的重复词规则对象
	 */
	private Pattern tailRepeatPattern;
	
	/**
	 * 先择模糊匹配
	 */
	private final static String CHOICE_FIND_RULE = "[第d地]([一二三四五六七八九十123456789]|10)(个|条|行)?人?$";
	
	/**
	 * 模糊匹配对象
	 */
	private Pattern choiceFindPattern;
	
	private final static String TYPE_KEY = "type";
	
	private final static String TYPE_NUMBER = "number";
	private final static String TYPE_NAME = "name";
	private final static String TYPE_CHOICE = "choice";
	
	
	private final static String VALUE_KEY = "value";	
	
	
	public ContactResultHandler() {
		
		tailRepeatPattern    = Pattern.compile(REPLACE_TAIL_REPEAT_RULE);
		
		choiceFindPattern 	 = Pattern.compile(CHOICE_FIND_RULE);
	}
	
	private NluRuleMatchResult processMatchedResult(NluRuleMatchResult result, String typeName, Object matchValue) {
		
		List<Object> valueList = new ArrayList<>();
		valueList.add(matchValue);
		result.getResultMap().put(TYPE_KEY, typeName);
		result.getResultMap().put(VALUE_KEY, valueList);
		return result;
	}
	
	
	@Override
	public NluRuleMatchResult handle(NluRuleMatchResult result, String group, NluContext context, boolean chain) {
		
		if (! result.isMatch() || !result.getResultMap().containsKey(group)) {	
			if (chain)  
				return chain(result, group);
			return result;
		}
		// 把group的值取出来，然后从map中删掉，并且转成小写
		String matchValue = result.getResultMap().get(group).toString().toLowerCase();
		result.getResultMap().remove(group);
		
//		System.out.println(matchValue);
		
		// 如果是字符串
		if (!StringUtils.isNumeric(matchValue) ) {
			if ( ! (context instanceof NluContactContext) || // context不是NluContact
					((NluContactContext)context).getContactList().isEmpty() // 联系人列表为空
			   )  
			{
				if (chain)  
					return chain(result, group);
			
				return result;
			}
		}
		
		NluContactContext nluContactContext = (NluContactContext)context;
		List<NluContact> contactList = nluContactContext.getContactList();
		
		// 不匹配的时候，把match的状态置为false再返回，这一步很重要
		if (StringUtils.isEmpty(matchValue)) {
			if (chain)  
				return chain(result, group);
			return result;
		}
		
		// 去掉结尾的语气词，对重叠词进行替换
		matchValue = filterInput(matchValue);
				
		//A. 如果是一个数字
		if (StringUtils.isNumeric(matchValue)) {
			
			String chineseNumeral = ChineseNumeral.toChinese(matchValue);
			
			// 1. 再判断这个数字是不是一个联系人
			for (NluContact contact : contactList) {
				String originalName = contact.getName();
				
				if (matchValue.equals(originalName.toLowerCase())) {
					// 是一个联系人，返回联系人
					return processMatchedResult(result, TYPE_NAME, originalName);
				}
				
				if (chineseNumeral.equals(originalName.toLowerCase())) {
					// 是一个联系人，返回联系人
					return processMatchedResult(result, TYPE_NAME, originalName);
				}
			}
			
			// 2. 最后判断这是不是一个纯数字电话, 这里直接返回一个数值结果
			return processMatchedResult(result, TYPE_NUMBER, matchValue);
		} else {
		//B. 如果是一个字符串
			List<String> maxProbNameList = new ArrayList<>(); // high confidence
			double maxProb = -1.0;
			
			List<String> blurMatchNameList = new ArrayList<>(); // low  confidence
			
			String pinyinInput = PinyinUtil.getPinYin(matchValue, "", false);
			
			String perfectMatchResultName = null;
			List<String> perfectMatchResultPinYinList = null; 
						
			for (NluContact contact : contactList) {
				String originalName = contact.getName();
				
				// 转成小写
				String filterName = filterInput(contact.getAlias().toLowerCase());
				
				//1. 完全匹配通讯录联系人， 则直接返回
				if (filterName.equals(matchValue)) {
					//return processMatchedResult(result, TYPE_NAME, originalName);
					perfectMatchResultName = originalName;
				}
				
				//2. 判断是不是属于选择第几个
				if (choiceFindPattern.matcher(matchValue).find()) {
					int selectIndex = NluSelectIndexUtil.getSelectedIndex(matchValue, context);
					
					if (selectIndex != 0) {
						return processMatchedResult(result, TYPE_CHOICE, Integer.valueOf(selectIndex));
					}
				}
				
				String pinyinName = PinyinUtil.getPinYin(filterName, "", false);
				
				//3. 判断是不是属于拼音的全字匹配
				if (pinyinInput.equals(pinyinName)) {
					if (perfectMatchResultPinYinList == null)
						perfectMatchResultPinYinList = new ArrayList<>();
					
					perfectMatchResultPinYinList.add(originalName);
				}
					
				//4. 模糊匹配通讯录联系人
				double distance = levenshtein.distance(pinyinInput, pinyinName);
				
				int maxLength = pinyinInput.length() > pinyinName.length() ? pinyinInput.length() : pinyinName.length();
				
				double confidence = (maxLength - distance)/maxLength;
				
				if (confidence > 0.8) {
					if (confidence > maxProb ) {
						maxProbNameList.clear();
						if (!maxProbNameList.contains(originalName))
							maxProbNameList.add(originalName);
						maxProb = confidence;
					} else if (confidence == maxProb) {
						if (!maxProbNameList.contains(originalName))
							maxProbNameList.add(originalName);
					}
				} else if (confidence >= 0.75 && confidence <= 0.8) {
					if (!blurMatchNameList.contains(originalName) && matchValue.length()>= 2 && originalName.length() > 2)
						blurMatchNameList.add(originalName);
				} else { // 用中文匹配
					distance = levenshtein.distance(contact.getAlias(), matchValue);
					
					maxLength = contact.getAlias().length() > matchValue.length() ? contact.getAlias().length() : matchValue.length();
					confidence = (maxLength - distance)/maxLength;
					
					if (confidence >= 0.499 && originalName.length() > 2) {
						if (!blurMatchNameList.contains(originalName))
							blurMatchNameList.add(originalName);
					} else {
						if(StringUtils.contains(contact.getAlias(), matchValue) && matchValue.length()>= 2 && originalName.length() > 2) {
							if (!blurMatchNameList.contains(originalName))
								blurMatchNameList.add(originalName);
						}
					}
				}
			}
			
			if (perfectMatchResultPinYinList != null) {
				result.getResultMap().put(TYPE_KEY, TYPE_NAME);
				result.getResultMap().put(VALUE_KEY, perfectMatchResultPinYinList);
				return result;	
			} else if ( perfectMatchResultName != null ) {
				return processMatchedResult(result, TYPE_NAME, perfectMatchResultName);
			} else if (maxProbNameList.size() > 0) {
				result.getResultMap().put(TYPE_KEY, TYPE_NAME);
				result.getResultMap().put(VALUE_KEY, maxProbNameList);
				return result;	
			} else if (blurMatchNameList.size() > 0) {				
				result.getResultMap().put(TYPE_KEY, TYPE_NAME);
				result.getResultMap().put(VALUE_KEY, blurMatchNameList);
				return result;
			} else if (chain){
				return chain(result, group);
			}
			
		}
		
		if (chain){
			return chain(result, group);
		} 
		return result;
	}
	
	
	private String filterInput(String input) {
		
		if (input == null)
			return StringUtils.EMPTY;
		
		input = input.replaceAll(REPLACE_TAIL_STATEMENT_RULE, "");
    	
    	Matcher matcher = tailRepeatPattern.matcher(input);
    	
    	if (matcher.find()) {
    		return input.replaceAll(REPLACE_TAIL_REPEAT_RULE.replace("\\D", matcher.group(1)), matcher.group(1));
    	} 
    	
    	return input;
	}


}
