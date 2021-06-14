package com.nlu.nlulib.rule;

import com.nlu.nlulib.parser.NluContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


public class NluRules {	
	
	public final static String WHOLE_WORD = "*";
	
	/**
	 * name for this rule set, this can be empty
	 * 
	 * a rule's name is used for a reference
	 */
	private String name = StringUtils.EMPTY;
	
	/**
	 * 目前全部只支持单个类别返回，多个以后再改吧
	 * 
	 * 正则表达式里面用于返回的组
	 */
	private String returnGroup;
	
	 
	private String[] returnGroups;
	
	
	private List<Map<String, Integer>>  groupName2IndexMapList;
	
	/**
	 * 正则表达式里面用于返回的组的值做为一个value存放到一个map里面，而这个value的key，
	 * 则由returnKey来指定
	 */
	private String returnKey;
	
	/**
	 * 是否是确认类型Yes/No的的规则
	 */
	private boolean confirm;
	
	/**
	 * regular expressions for this rule set
	 */
	private List<NluPatternConfig> patterns;
	
	
	/**
	 * NluFilters for this rule
	 */
	private List<NluFilter> filters;
	
	
	/**
	 * result handler
	 */
	private List<NluResultHandlerConfig>  resultHandlers;
	
	/**
	 * 是否全字匹配
	 */
	private boolean matchWholeWord;
	
	/**
	 * 是否是一个替换规则 replacement attribute有配置
	 */
	private String replacement;
	
	
	/**
	 * 正则表达式里面的组如果需要validate，则可以指定相应的validator
	 * 
	 * key是待验正的group, value是NluRuleValidateConfig
	 */
	private Map<String, List<NluRuleValidateConfig>> validators; 
	
	public NluRules(String name, String returnGroup, String returnKey, boolean matchWholeWord, boolean confirm, String replacement) {
		super();
		this.name = name;
		this.returnGroup = returnGroup;
		this.returnKey = returnKey;
		this.matchWholeWord = matchWholeWord;
		this.confirm = confirm;
		this.replacement = replacement;
		
		this.patterns = new ArrayList<>();
		this.filters = new ArrayList<>();
		this.resultHandlers = new ArrayList<>();
		
		if (StringUtils.isEmpty(this.returnGroup)) {
			//this.returnGroups = {};
		} else {
			this.returnGroups = this.returnGroup.split(",");
			for(int i = 0; i < this.returnGroups.length; i++) {
				this.returnGroups[i] = this.returnGroups[i].trim();
			}
		}
		this.groupName2IndexMapList = new ArrayList<>();
		
	}
	
	public NluRules() {
		this(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, false, false, null);
	}
	
	public NluRules(String returnGroup, String returnKey) {
		this(StringUtils.EMPTY, returnGroup, returnKey, false, false, null); 
	} 
	
	public boolean empty() {
		return patterns.size() == 0;
	}

	public void addPattern(String regex) {
		
		Pattern pattern = Pattern.compile(regex);
		NluPatternConfig patternConfig = new NluPatternConfig(pattern, returnGroups);
		
		this.patterns.add(patternConfig);
	}
	
	public List<NluPatternConfig> getPatternConfig() {
		return this.patterns;
	}
	
	public void addPattern(NluRules nluRules) {
		
		List<NluPatternConfig> patternConfigList = nluRules.getPatternConfig();
		for(NluPatternConfig patternConfig : patternConfigList) {
			this.patterns.add(patternConfig);
		}
	}
	
	public void addFilter(NluFilter filter) {
		this.filters.add(filter);
	}
	
	public boolean isReplace() {
		return this.replacement != null;
	}
	
	
	public void addResultHandler(String group, NluResultHandler resultHandler, boolean chain) {
		this.resultHandlers.add(new NluResultHandlerConfig(resultHandler, group, chain));
	}

	public void addValidator(String group, NluValidator validator) {
		
		NluRuleValidateConfig config = new NluRuleValidateConfig(group, validator, true);
		this.addValidator(config);
		
	}

	
	public void addValidator(String group, NluValidator validator, boolean passValue) {
		
		NluRuleValidateConfig config = new NluRuleValidateConfig(group, validator, passValue);
		this.addValidator(config);
		
	}
	
	public void addValidator(NluRuleValidateConfig config) {
		if (validators == null) {
			validators = new HashMap<String, List<NluRuleValidateConfig>>();
		}
		
		List<NluRuleValidateConfig> groupValidatorList = validators.get(config.getGroup());
		
		if (groupValidatorList == null) {
			groupValidatorList = new ArrayList<NluRuleValidateConfig>();
			validators.put(config.getGroup(), groupValidatorList);
		}
		
		groupValidatorList.add(config);
	}
	
	
	/**
	 * 根据指定的validator进行较验，如果validator的返回结果与return attribute中指定的值一致，则返回true，否则返回false
	 * 
	 * @param matcher
	 * @param context
	 * @return
	 */
	protected boolean validate(String input, Matcher matcher, NluPatternConfig patternConfig, NluContext context) {
		
		if (validators == null)
			return true;
		
		Iterator<Entry<String, List<NluRuleValidateConfig>>> validatorIterator = validators.entrySet().iterator();
		while(validatorIterator.hasNext()) {
			Entry<String, List<NluRuleValidateConfig>> entry = validatorIterator.next();
			List<NluRuleValidateConfig> validateConfigList = entry.getValue();
			
			
			for (NluRuleValidateConfig validateConfig : validateConfigList )  {
				try {
					String groupValue = StringUtils.EMPTY;
					
					
					String groupName = validateConfig.getGroup();
					
					if (StringUtils.equals(groupName, WHOLE_WORD)) {
						//groupValue = matcher.group(0);
						groupValue = input;
					} else {
						try {
							int groupIndex = patternConfig.getGroupIndex(groupName);
							groupValue = matcher.group(groupIndex);
						} catch(Exception e) { //IllegalArgumentException
							continue;  // 组不存在，抛出IllegalArgumentException，当验证通过
						}
					}
					
					if ( validateConfig.getValidator().validate(groupValue, context) != validateConfig.isPass()) {
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	protected NluRuleMatchResult handleResult(NluRuleMatchResult result, NluContext context) {
		
		for (NluResultHandlerConfig resultHandler : this.resultHandlers) {
			result = resultHandler.getResultHandler().handle(result, resultHandler.getGroup(), context, resultHandler.isChain());
		}
		return result;		
	}
	
	public NluRuleMatchResult match(String input, NluContext context) {
		return match(input, context, true);
	}
	
	public NluRuleMatchResult match(String input, NluContext context, boolean ignoreEmptyInput)  {

		for (NluFilter filter : this.filters) {
			input = filter.filter(input, context);
		}
		
		if (StringUtils.isEmpty(input) && ignoreEmptyInput) {
			return NluRuleMatchResult.emptyResult();
		}
		
		for (NluPatternConfig patternConfig : this.patterns) {
			
			Matcher matcher = patternConfig.getPattern().matcher(input);
			
			if (isMatchWholeWord()) {
				if (matcher.matches()) {// 全字匹配
					if (validate(input, matcher, patternConfig, context) == false) {
//						return NluRuleMatchResult.emptyResult();
						continue;
					}
				} else {
					continue;
				}
			} else {
				if (matcher.find()) { // 只匹配一次，不匹配多次，即只匹配第一个
					if (validate(input, matcher, patternConfig, context) == false) {
//						return NluRuleMatchResult.emptyResult();
						continue;
					}
				} else {
					continue;
				}
			}
			
//			System.out.println(patternConfig.getPattern().pattern());
			
			// 成功匹配,只做一次匹配
			if (StringUtils.isEmpty(this.getReturnGroup()) || this.getReturnGroups() == null)  {
				return new NluRuleMatchResult(true);
			} else { // regurn_group不为空
				try  {
					String[] groups = this.getReturnGroups();
					if (groups.length == 1) {
						String value;					
						if (WHOLE_WORD.equals(this.getReturnGroup()))
							value = matcher.group(0);
						else {
							try {
								int groupIndex = patternConfig.getGroupIndex(this.getReturnGroup());
								value = matcher.group(groupIndex);
							} catch (Exception e) {
								value = "";
							}
						}
						
						if (!StringUtils.isEmpty(value)) {
							if (StringUtils.isEmpty(getReturnKey()))
								return handleResult(new NluRuleMatchResult(true, this.getReturnGroup(), value), context);
							else 
								return handleResult(new NluRuleMatchResult(true, this.getReturnKey(), value), context);
						}
					} else if (groups.length > 1) {
						Map<String, Object> resultMap = new HashMap<>();
						for (int i = 0; i < groups.length; i++) {
							try {
								String group = groups[i];
								
								int groupIndex = patternConfig.getGroupIndex(group);
								String value = matcher.group(groupIndex);
								
								if (! StringUtils.isEmpty(value) ) {
									resultMap.put(group, value);
								}
							} catch (Exception e) {
							}
						}
						return handleResult(new NluRuleMatchResult(true, resultMap), context);
					}
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			return new NluRuleMatchResult(true);
		}
		
		return NluRuleMatchResult.emptyResult();
	}

	
	public String replace(String input, NluContext context) {
		
		if (this.replacement == null) {
			return input;
		}
		
		for (NluFilter filter : this.filters) {
			input = filter.filter(input, context);
		}
		
		for (NluPatternConfig pattern : this.patterns) {
			
			String regex = pattern.getPattern().pattern();
			
			input = input.replaceAll(regex, this.replacement);
		}
		return input;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isMatchWholeWord() {
		return matchWholeWord;
	}

	public void setMatchWholeWord(boolean matchWholeWord) {
		this.matchWholeWord = matchWholeWord;
	}

	public String getReturnGroup() {
		return returnGroup;
	}
	
	public String[] getReturnGroups() {
		return returnGroups;
	}

	public void setReturnGroup(String returnGroup) {
		this.returnGroup = returnGroup;
	}

	public String getReturnKey() {
		return returnKey;
	}

	public void setReturnKey(String returnKey) {
		this.returnKey = returnKey;
	}

	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}

}

