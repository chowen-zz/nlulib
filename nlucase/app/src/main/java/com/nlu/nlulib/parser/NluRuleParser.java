package com.nlu.nlulib.parser;

import com.nlu.nlulib.NluCommand;
import com.nlu.nlulib.rule.NluDomainActionRules;
import com.nlu.nlulib.rule.NluDomainMatchResult;
import com.nlu.nlulib.rule.NluFilter;
import com.nlu.nlulib.rule.NluRuleConfiguration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;


/**
 * 基于配置规则的NLU解析器
 *
 */
public class NluRuleParser implements NluParser {

	/**
	 * 当前的上下文信息
	 */
	private NluContext context = new NluContext(null);

	/**
	 * 规则配置对像
	 */
	private NluRuleConfiguration ruleConfig;

	/**
	 * 上一次parser返回的NluCommand
	 */
	private NluCommand lastCommand = new NluCommand("", "");
	
	private String lastDialogId = StringUtils.EMPTY;
	
	/**
	 * 对话id对应的domain
	 */
	private Map<String, String> dialogId2DomainMap = new HashMap<>();
	

	/**
	 * constructor
	 * 
	 * @param ruleConfiguration
	 */
	public NluRuleParser(NluRuleConfiguration ruleConfiguration) {
		this.ruleConfig = ruleConfiguration;
	}
	
	/**
	 * 根据dialogId来获取domain名称
	 * 
	 * @param dialogId
	 * @return
	 */
	private String getDomainName(String dialogId) {
		
		String domainName = dialogId2DomainMap.get(dialogId);
		
		if (domainName == null) {
			return "";
		}
		
		return domainName;
	}
	
	
	private void setDialogDomain(String dialogId, String domainName) {

		dialogId2DomainMap.clear();
		dialogId2DomainMap.put(dialogId, domainName);
	}
	

	public NluCommand processExternalMatchedResult(NluDomainMatchResult result) {
		return processExternalMatchedResult(result, null);
	}

	public NluCommand processExternalMatchedResult(NluDomainMatchResult result, String dialogId) {
		NluCommand command;
		if (StringUtils.isEmpty(dialogId)) {
			command = result.toNluCommand();
		} else {
			command = result.toNluCommand(dialogId);
		}

		if (result.isMatch()) {
			lastCommand = command;
			lastDialogId = command.getDialogId();
			setDialogDomain(command.getDialogId(), command.getDomain());
		}

		return command;
	}

	@Override
	public NluCommand parse(String input, NluContext nluContext) {

		// 如果context是一个新的引用，说明当前需要在新的context环境下跑一下
		// 1. 如果context只是一个纯粹的NluContext，则继续使用保留this.context只是把
		input = input.trim();
		NluContext context = nluContext;
		
		this.context.set(NluContextKey.DOMAIN_CONTEXT, "");
		
		if (context != this.context) {
			String className = context.getClass().getSimpleName();
			if (NluContext.CLASS_NAME.equals(className)) {
				
				Object domainContext = context.get(NluContextKey.DOMAIN_CONTEXT);
				
				if (domainContext != null ) {
					this.context.set(NluContextKey.DOMAIN_CONTEXT, domainContext.toString());
				} 
				this.context.setDialogId(context.getDialogId());
				
				context = this.context;
				
			} else if (context instanceof NluNewDialogContext) {
				
				NluNewDialogContext newDialogContext = (NluNewDialogContext)context;
				String domainActionName = newDialogContext.getDomainActionName();
				String[] domainAndAction = domainActionName.split("\\.");
				
				if (domainAndAction == null || domainAndAction.length == 0) {
					return NluDomainMatchResult.emptyResult().toNluCommand(newDialogContext.getDialogId());
				}
				
				if (domainAndAction.length > 1 ) {
					this.context.set(NluContextKey.DOMAIN_CONTEXT, domainActionName);
				} 
				
				setDialogDomain(newDialogContext.getDialogId(), domainAndAction[0]);
				this.context.setDialogId(context.getDialogId());
				this.lastDialogId = context.getDialogId(); 
				
				context = this.context;
			}
		}
		
		// filter preprocess
		Map<String, NluFilter> filterMap = ruleConfig.getGlobalFilterMap();
		Iterator<Entry<String, NluFilter>> filterEntryIter = filterMap.entrySet().iterator();
		while (filterEntryIter.hasNext()) {
			NluFilter filter = filterEntryIter.next().getValue();
			input = filter.filter(input, context);
		}
		
		String currentDialogId = context.getDialogId();
		//String lastDialogId = this.lastDialogId;
		String lastDomainName = getDomainName(currentDialogId);
		
		boolean isSameDialog = !StringUtils.isEmpty(currentDialogId) 
				&& !StringUtils.isEmpty(lastDialogId) 
				&& !StringUtils.isEmpty(lastDomainName)
				&& StringUtils.equals(currentDialogId, lastDialogId);
		
		NluDomainMatchResult result = NluDomainMatchResult.emptyResult();
		
		if ( isSameDialog ) {
			List<NluDomainActionRules> systemRulesList = ruleConfig.getExternalDomainRulesList();
			List<NluDomainActionRules> domainRulesList = ruleConfig.getInternalDomainRulesList(lastDomainName);
			
			String domainContext = (String)this.context.get(NluContextKey.DOMAIN_CONTEXT);
			
			int i = 0, j = 0;
			while (i < systemRulesList.size() && j < domainRulesList.size() && !result.isMatch()) {
				NluDomainActionRules systemRules = systemRulesList.get(i);
				NluDomainActionRules domainRules = domainRulesList.get(j);
				
				if (systemRules.comparePriority(domainRules) <= 0) {
					
					if (! domainContext.startsWith(systemRules.getDomainName()) )
						result = systemRules.match(input, context);
					
					i += 1;
				} else {
					result = domainRules.match(input, context);
					j += 1;
				}
			}
			
			if (result.isMatch()) {
				return result.toNluCommand(lastDialogId);
			}
			
			if (j >= domainRulesList.size()) {
				for (;i < systemRulesList.size() && !result.isMatch(); i++) {
					NluDomainActionRules nluDomainActionRules = systemRulesList.get(i);
					
					if (! domainContext.startsWith(nluDomainActionRules.getDomainName()) )
						result = nluDomainActionRules.match(input, context);
				}
			
				if (result.isMatch()) {
					return result.toNluCommand(lastDialogId);
				}
			}
		
			if (i >= systemRulesList.size()) {
				for (;j < domainRulesList.size() && !result.isMatch(); j++) {
		
					NluDomainActionRules domainActionRules = domainRulesList.get(j);
					result = domainActionRules.match(input, context);
				}
				
				if (result.isMatch()) {
					return result.toNluCommand(lastDialogId);
				}
			}
			return result.toNluCommand(lastDialogId);
		} else { 
			// 单轮对话			
			List<NluDomainActionRules> systemRulesList = ruleConfig.getExternalDomainRulesList();

			for (int i = 0; i < systemRulesList.size() && !result.isMatch(); i++) {
				NluDomainActionRules nluDomainActionRules = systemRulesList.get(i);
				result = nluDomainActionRules.match(input, context);
			}
			
			if (result.isMatch()) {
				return processExternalMatchedResult(result);
			}
		}
		return NluCommand.NONE;
	}

	@Override
	public void setContext(NluContext context) {
		this.context = context;		
	}


	@Override
	public NluCommand parse(String input, String dialogId) {
		this.context.setDialogId(dialogId);
		return parse(input, this.context);
	}
}
