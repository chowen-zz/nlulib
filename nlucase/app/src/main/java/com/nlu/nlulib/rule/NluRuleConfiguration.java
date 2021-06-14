package com.nlu.nlulib.rule;

import com.nlu.nlulib.NluDomainName;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class NluRuleConfiguration {

    private final static String configFilePath = "nlu.xml";

    //==========================rules-规则=============================
    private Map<String, NluRules> globalRuleMap = new HashMap<>();

    //=========================validator-校验================================
    private Map<String, NluValidator> validatorMap = new HashMap<>();

    /**
     * 保存 vaidator的name和rule的一个映射，用于在解析完validator和rules之后，设置validator依赖的规则
     */
    private Map<String, String> validatorNameRuleMap = new HashMap<>();


    //==========================filter ==================================
    private Map<String, NluFilter> filterMap = new HashMap<>();

    private Map<String, NluFilter> globalFilterMap = new HashMap<>();

    /**
     * 保存filter标签定义的rules属性名字
     */
    private Map<String, String> filterNameRuleMap = new HashMap<>();

    //==========================resultHandler=============================

    private Map<String, NluResultHandler> resultHandlerMap = new HashMap<>();

    //==========================domain================================
    /**
     * key = actionName
     */
    private Map<String, List<NluDomainActionRules>> actionRulesMap = new HashMap<>();


    /**
     * 单轮
     */
    private List<NluDomainActionRules> externalDomainRulesList = new ArrayList<>();
    /**
     * key = domainName 外部的domain Rules，能直接被唤起
     */
    private Map<String, List<NluDomainActionRules>> externalDomainRulesMap = new HashMap<>();

    /**
     * key = domainName 内部的domain Rules，即不可能直接被唤起，需要已经进入当前domain的时候，才能执行
     */
    private Map<String, List<NluDomainActionRules>> internalDomainRulesMap = new HashMap<>();


    /**
     * key = domainName，externalDomainRulesMap和internalDomainRulesMap和合集
     */
    private Map<String, List<NluDomainActionRules>> allDomainRulesMap = new HashMap<>();

    private boolean alreadyConfigured = false;

    public NluRuleConfiguration() {
    }

    public void configure() throws RuntimeException {
        this.configure(false);
    }

    public void configure(boolean force) throws RuntimeException {
        Logger.getLogger("chowen").info("force>>" + force + ">" + alreadyConfigured);
        if (alreadyConfigured && !force) {
            return;
        }

        final List<String> configPathList = new ArrayList<>();

        File fileDir = new File("/Users/zhouwen/Desktop/NativeNLP/nlulib/app/src/main/java/com/nlu/nlulib/config");

        List<String> filelists = new ArrayList<>();
        if (fileDir.exists()) {
            File[] files = fileDir.listFiles();
            for (File file : files) {
                Logger.getLogger("chowen").info("file_path>>" + file.getName());
                filelists.add(file.getName());
            }

            for (String file : filelists) {
                if (file.startsWith("rules_")) {
                    configPathList.add("/Users/zhouwen/Desktop/NativeNLP/nlulib/app/src/main/java/com/nlu/nlulib/config/" + file);
                }
            }
            for (String file : filelists) {
                if (file.startsWith("domain_")) {
                    configPathList.add("/Users/zhouwen/Desktop/NativeNLP/nlulib/app/src/main/java/com/nlu/nlulib/config/" + file);
                }
            }

        }


        try {

            for (String configPath : configPathList) {
                SAXReader xmlReader = new SAXReader();
                Document document = xmlReader.read(configPath);
                Element root = document.getRootElement();
                //===================================================
                // don't change this parsing sequence.
                //===================================================

                // validate parsing
                parseValidatorsElement(root);

                // result handler parse
                parseResultHandlersElement(root);


                parseFiltersElement(root);


                // global rules parsing
                parseRulesElement(root);


                // 设置validator依赖的rule
                setValidatorRules();

                // 设置fitler依赖的rule
                setFilterRules();

                // domain parsing
                parseDomainElement(root);
            }

            sortRules();

            alreadyConfigured = true;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private void sortRules() {

        Comparator<NluDomainActionRules> comparator = new Comparator<NluDomainActionRules>() {

            @Override
            public int compare(NluDomainActionRules o1, NluDomainActionRules o2) {
                // TODO Auto-generated method stub
                int priority = o1.getPriority() - o2.getPriority();
                if (priority == 0) {
                    return o1.getDomain().getPriority() - o2.getDomain().getPriority();
                }
                return priority;
            }
        };

        // 对internal的domain action进行排序
        for (Entry<String, List<NluDomainActionRules>> internalDomainRulesEntry : internalDomainRulesMap.entrySet()) {

            if (!internalDomainRulesEntry.getKey().equals(NluDomainName.SYSTEM)) {
                List<NluDomainActionRules> systemInnerRulesList = getInternalDomainRulesList(NluDomainName.SYSTEM);
                internalDomainRulesEntry.getValue().addAll(systemInnerRulesList);
            }

            Collections.sort(internalDomainRulesEntry.getValue(), comparator);
        }

        // 对external的domain action进行排序
        for (Entry<String, List<NluDomainActionRules>> externalDomainRulesEntry : externalDomainRulesMap.entrySet()) {
            this.externalDomainRulesList.addAll(externalDomainRulesEntry.getValue());
        }
        Collections.sort(this.externalDomainRulesList, comparator);

        // 对单个domain内的所有action进行排序（包括internal和external）
        for (Entry<String, List<NluDomainActionRules>> allDomainRulesEntry : allDomainRulesMap.entrySet()) {
            Collections.sort(allDomainRulesEntry.getValue(), comparator);
        }
    }

    /**
     * 获取一个action的所有rules
     *
     * @param action
     * @return
     */
    public List<NluDomainActionRules> getActionRules(String action) {
        List<NluDomainActionRules> actionRules = this.actionRulesMap.get(action);

        if (actionRules == null)
            return Collections.emptyList();

        return actionRules;
    }

    /**
     * 返回全局的filter Map
     *
     * @return
     */
    public Map<String, NluFilter> getGlobalFilterMap() {
        return this.globalFilterMap;
    }


    public List<NluDomainActionRules> getInternalDomainRulesList(String domain) {
        List<NluDomainActionRules> nluDomainRulesList = this.internalDomainRulesMap.get(domain);

        return nluDomainRulesList == null ? Collections.EMPTY_LIST : nluDomainRulesList;
    }


    public Map<String, List<NluDomainActionRules>> getExternalDomainRulesMap() {
        return this.externalDomainRulesMap;
    }


    public List<NluDomainActionRules> getExternalDomainRulesList() {
        return this.externalDomainRulesList;
    }

    public List<NluDomainActionRules> getExternalDomainRulesList(String domain) {
        List<NluDomainActionRules> nluDomainRulesList = this.externalDomainRulesMap.get(domain);

        return nluDomainRulesList == null ? Collections.EMPTY_LIST : nluDomainRulesList;
    }

    public List<NluDomainActionRules> getDomainRulesList(String domain) {
        List<NluDomainActionRules> nluDomainRulesList = this.allDomainRulesMap.get(domain);

        return nluDomainRulesList == null ? Collections.EMPTY_LIST : nluDomainRulesList;
    }

    /**
     * 解析rules标签
     *
     * @param root
     * @throws RuntimeException
     */
    private void parseRulesElement(Element root) throws RuntimeException {
        List rules = root.elements("rules");
        Iterator<?> rulesIterator = rules.iterator();
        while (rulesIterator.hasNext()) {
            Element rulesElem = (Element) rulesIterator.next();

            String rulesName = rulesElem.attributeValue("name");

            if (StringUtils.isEmpty(rulesName)) {
                throw new RuntimeException("global rules element must have a name");
            }

            NluRules nluRules = parseRulesPropertyAndAttr(rulesElem, rulesName, null);

            parseRulesFunction(rulesElem, nluRules, null);

            // 添加一个global的规则
            globalRuleMap.put(rulesName, nluRules);
        }
    }


    private void parseValidatorsElement(Element root) throws RuntimeException {
        Element validators = root.element("validators");

        if (validators != null) {
            List validatorElems = validators.elements("validator");
            Iterator<?> validatorIterator = validatorElems.iterator();
            while (validatorIterator.hasNext()) {
                Element validatorElem = (Element) validatorIterator.next();

                String name = validatorElem.attributeValue("name");
                if (StringUtils.isEmpty(name)) {
                    throw new RuntimeException("validator name is empty.");
                }

                if (validatorMap.containsKey(name)) {
                    throw new RuntimeException("validator name already exists.");
                }

                String classPath = validatorElem.attributeValue("class");
                if (StringUtils.isEmpty(classPath)) {
                    throw new RuntimeException("validator <" + name + "> attribute \"class\" is empty");
                }

                try {
                    Object obj = Class.forName(classPath).newInstance();

                    if (obj instanceof NluValidator) {
                        // 添加一个validator
                        this.validatorMap.put(name, (NluValidator) obj);

                        String rules = validatorElem.attributeValue("rule");
                        if (!StringUtils.isEmpty(rules)) {
                            this.validatorNameRuleMap.put(name, rules);
                        }

                    } else {
                        throw new RuntimeException("class " + classPath + " is not a NluValidator object.");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }


    private void parseResultHandlersElement(Element root) throws RuntimeException {
        Element validators = root.element("result_handlers");

        if (validators != null) {
            List resultHandlersElem = validators.elements("result_handler");
            Iterator<?> validatorIterator = resultHandlersElem.iterator();
            while (validatorIterator.hasNext()) {
                Element resultHandlerElem = (Element) validatorIterator.next();

                String name = resultHandlerElem.attributeValue("name");
                if (StringUtils.isEmpty(name)) {
                    throw new RuntimeException("result handler name is empty.");
                }

                if (resultHandlerMap.containsKey(name)) {
                    throw new RuntimeException("result handler name already exists.");
                }

                String classPath = resultHandlerElem.attributeValue("class");
                if (StringUtils.isEmpty(classPath)) {
                    throw new RuntimeException("result handler <" + name + "> attribute \"class\" is empty");
                }

                try {
                    Object obj = Class.forName(classPath).newInstance();

                    if (obj instanceof NluResultHandler) {
                        // 添加一个validator
                        this.resultHandlerMap.put(name, (NluResultHandler) obj);

                    } else {
                        throw new RuntimeException("class " + classPath + " is not a NluResultHandler object.");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }


    private void setValidatorRules() {
        Set<String> keys = this.validatorNameRuleMap.keySet();

        for (String validatorName : keys) {
            String ruleName = this.validatorNameRuleMap.get(validatorName);

            NluRules rules = this.globalRuleMap.get(ruleName);

            if (rules == null) {
                throw new RuntimeException("the rule <" + ruleName + "> for validator " + validatorName + " does not exist.");
            }

            this.validatorMap.get(validatorName).setRules(rules);
        }
    }


    private void setFilterRules() {
        Set<String> keys = this.filterNameRuleMap.keySet();

        for (String filterName : keys) {
            String ruleName = this.filterNameRuleMap.get(filterName);

            NluRules rules = this.globalRuleMap.get(ruleName);

            if (rules == null) {
                throw new RuntimeException("the rule <" + ruleName + "> for validator " + filterName + " does not exist.");
            }

            this.filterMap.get(filterName).setRules(rules);
        }
    }


    private void parseFiltersElement(Element root) throws RuntimeException {
        Element validators = root.element("filters");

        if (validators != null) {
            List validatorList = validators.elements("filter");
            Iterator<?> validatorIterator = validatorList.iterator();
            while (validatorIterator.hasNext()) {
                Element FilterElem = (Element) validatorIterator.next();

                String name = FilterElem.attributeValue("name");
                if (StringUtils.isEmpty(name)) {
                    throw new RuntimeException("result handler name is empty.");
                }

                if (resultHandlerMap.containsKey(name)) {
                    throw new RuntimeException("result handler name already exists.");
                }

                String classPath = FilterElem.attributeValue("class");
                if (StringUtils.isEmpty(classPath)) {
                    throw new RuntimeException("result handler <" + name + "> attribute \"class\" is empty");
                }

                try {
                    Object obj = Class.forName(classPath).newInstance();

                    if (obj instanceof NluFilter) {
                        // 添加一个validator
                        this.filterMap.put(name, (NluFilter) obj);

                        String rules = FilterElem.attributeValue("rule");
                        if (!StringUtils.isEmpty(rules)) {
                            this.filterNameRuleMap.put(name, rules);
                        }

                        String global = FilterElem.attributeValue("global");
                        if (StringUtils.equalsIgnoreCase(global, "true")) {
                            this.globalFilterMap.put(name, (NluFilter) obj);
                        }

                    } else {
                        throw new RuntimeException("class " + classPath + " is not a NluResultHandler object.");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }

    private void parseDomainElement(Element root) throws RuntimeException {
        List domains = root.elements("domain");

        Iterator<?> domainsIteartor = domains.iterator();
        while (domainsIteartor.hasNext()) {
            Element domainElem = (Element) domainsIteartor.next();

            String domainName = domainElem.attributeValue("name");

            NluDomain.DomainType domainType = NluDomain.DomainType.BOTH;
            String type = domainElem.attributeValue("type");
            if (StringUtils.equalsIgnoreCase(type, "internal")) {
                domainType = NluDomain.DomainType.INTERNAL;
            } else if (StringUtils.equalsIgnoreCase(type, "external")) {
                domainType = NluDomain.DomainType.EXTERNAL;
            }
            boolean external = StringUtils.equalsIgnoreCase(domainElem.attributeValue("internal"), "false");
            String priorityAttr = domainElem.attributeValue("priority");

            int priority = NluDomain.PRIORITY_DEFAULT;

            if (StringUtils.equalsIgnoreCase(domainName, "system")) {
                priority = NluDomain.PRIORITY_SYSTEM;
            } else if (domainType == NluDomain.DomainType.INTERNAL) {
                priority = NluDomain.PRIORITY_INTERNAL;
            }

            if (!StringUtils.isEmpty(priorityAttr)) {
                try {
                    priority = Integer.parseInt(priorityAttr);
                } catch (Exception e) {
                }
            }

            NluDomain nluDomain = new NluDomain(domainName, domainType, priority);

            if (StringUtils.isEmpty(domainName))
                throw new RuntimeException("domain name is empty");

            List rulesElems = domainElem.elements("rules");
            Iterator<?> rulesElemsIterator = rulesElems.iterator();
            while (rulesElemsIterator.hasNext()) {
                Element rulesElem = (Element) rulesElemsIterator.next();

                String ruleAction = rulesElem.attributeValue("action");
                priorityAttr = rulesElem.attributeValue("priority");

                String refered = rulesElem.attributeValue("refered");

                priority = NluDomainActionRules.DEFAULT_PRIORITY;
                if (!StringUtils.isEmpty(priorityAttr)) {
                    try {
                        priority = Integer.parseInt(priorityAttr);
                    } catch (Exception e) {
                    }
                }


                if (StringUtils.isEmpty(ruleAction))
                    throw new RuntimeException("rules action is empty in domain <" + domainName + ">");

                NluDomainActionRules nluDomainRules = new NluDomainActionRules(nluDomain, ruleAction, priority, !StringUtils.equalsIgnoreCase(refered, "false"));

                NluRules nluRules = parseRulesPropertyAndAttr(rulesElem, domainName + "." + ruleAction, nluDomainRules);

                parseRulesFunction(rulesElem, nluRules, nluDomainRules);

                if (!nluRules.empty()) {
                    nluDomainRules.addRules(nluRules);
                }

                //1. 增加到ActionRuleMap, key = actionName
                if (!this.actionRulesMap.containsKey(ruleAction))
                    this.actionRulesMap.put(ruleAction, new ArrayList<NluDomainActionRules>());

                this.actionRulesMap.get(ruleAction).add(nluDomainRules);

                //2.  添加到DomainActionRulesMap, key = domainName.actionName
                if (NluDomain.DomainType.INTERNAL == domainType) {
                    List<NluDomainActionRules> domainRulesList = this.internalDomainRulesMap.get(domainName);

                    if (domainRulesList == null) {
                        domainRulesList = new ArrayList();
                        this.internalDomainRulesMap.put(domainName, domainRulesList);
                    }

                    domainRulesList.add(nluDomainRules);
                } else if (NluDomain.DomainType.EXTERNAL == domainType) {
                    List<NluDomainActionRules> domainRulesList = this.externalDomainRulesMap.get(domainName);

                    if (domainRulesList == null) {
                        domainRulesList = new ArrayList();
                        this.externalDomainRulesMap.put(domainName, domainRulesList);
                    }

                    domainRulesList.add(nluDomainRules);
                } else {
                    List<NluDomainActionRules> domainRulesList = this.internalDomainRulesMap.get(domainName);

                    if (domainRulesList == null) {
                        domainRulesList = new ArrayList();
                        this.internalDomainRulesMap.put(domainName, domainRulesList);
                    }

                    domainRulesList.add(nluDomainRules);

                    domainRulesList = this.externalDomainRulesMap.get(domainName);

                    if (domainRulesList == null) {
                        domainRulesList = new ArrayList();
                        this.externalDomainRulesMap.put(domainName, domainRulesList);
                    }

                    domainRulesList.add(nluDomainRules);
                }

                //3. 全部加到全局的DomainRulesMaps
                List<NluDomainActionRules> domainRulesList = this.allDomainRulesMap.get(domainName);

                if (domainRulesList == null) {
                    domainRulesList = new ArrayList();
                    this.allDomainRulesMap.put(domainName, domainRulesList);
                }

                domainRulesList.add(nluDomainRules);
            }
        }
    }


    private NluRules parseRulesPropertyAndAttr(Element rulesElem, String rulesName, NluDomainActionRules nluDomainRules) {
        // if isMatch is true, we will use perfect match for this rule,
        // instead of a blur search
        String matchWholeWord = rulesElem.attributeValue("match");
        String returnGroup = rulesElem.attributeValue("return_group");
        String returnKey = rulesElem.attributeValue("return_key");
        String confirm = rulesElem.attributeValue("confirm");
        String replacement = rulesElem.attributeValue("replacement");

        NluRules nluRules = new NluRules(rulesName, returnGroup, returnKey,
                StringUtils.equalsIgnoreCase("true", matchWholeWord),
                StringUtils.equalsIgnoreCase("true", confirm), replacement);


        if (nluDomainRules != null) {
            String dependActionValue = rulesElem.attributeValue("depend");
            if (!StringUtils.isEmpty(dependActionValue)) {
                String[] dependActionArray = StringUtils.split(dependActionValue, ",");
                for (int i = 0; i < dependActionArray.length; i++) {
                    nluDomainRules.addDependAction(dependActionArray[i].trim());
                }
            }
        }

        List ruleElems = rulesElem.elements("rule");
        Iterator<?> ruleIterator = ruleElems.iterator();

        while (ruleIterator.hasNext()) {
            Element ruleElem = (Element) ruleIterator.next();

            String refRuleName = ruleElem.attributeValue("ref");

            if (!StringUtils.isEmpty(refRuleName)) {
                if (this.globalRuleMap.containsKey(refRuleName)) {
                    // add a reference rule here
                    if (nluDomainRules != null) {
                        nluDomainRules.getRules().add(this.globalRuleMap.get(refRuleName));
                    } else {
                        NluRules refNluRules = this.globalRuleMap.get(refRuleName);
                        nluRules.addPattern(refNluRules);
                    }
                } else {
                    throw new RuntimeException("rule ref name <" + refRuleName + "> does not exist");
                }
            } else {
                String ruleRegex = StringUtils.trim(ruleElem.getText());

                if (StringUtils.isEmpty(ruleRegex)) {
                    throw new RuntimeException(nluRules.getName() + " contains empty rule element");
                } else {
                    nluRules.addPattern(ruleRegex);
                }
            }
        }

        return nluRules;
    }


    private void parseRulesFunction(Element rulesElem, NluRules nluRules, NluDomainActionRules nluDomainRules) {

        List<Element> validatorElems = rulesElem.elements("validator");
        for (Element validatorElem : validatorElems) {

            String validateGroup = validatorElem.attributeValue("group");
            String validatorName = validatorElem.attributeValue("name");
            String returnValue = validatorElem.attributeValue("return");
            String cascade = validatorElem.attributeValue("cascade");

            if (StringUtils.isEmpty(validateGroup)) {
                throw new RuntimeException("validator group is empty");
            }
            if (StringUtils.isEmpty(validatorName)) {
                throw new RuntimeException("validator name is empty");
            }

            if (!this.validatorMap.containsKey(validatorName)) {
                throw new RuntimeException("the validator <" + validatorName + "> for group <" + validateGroup + "> does not exist");
            }

            nluRules.addValidator(validateGroup, this.validatorMap.get(validatorName), StringUtils.equalsIgnoreCase(returnValue, "true"));

            if (nluDomainRules != null && StringUtils.equalsIgnoreCase(cascade, "true")) {
                for (NluRules rules2 : nluDomainRules.getRules()) {
                    rules2.addValidator(validateGroup,
                            this.validatorMap.get(validatorName),
                            StringUtils.equalsIgnoreCase(returnValue, "true"));
                }
            }
        }


        List<Element> filterElems = rulesElem.elements("filter");
        for (Element filterElem : filterElems) {
            String filterName = filterElem.attributeValue("name");

            if (StringUtils.isEmpty(filterName)) {
                throw new RuntimeException("filter name for rule name <" + nluRules.getName() + "> is empty");
            }

            if (this.globalFilterMap.containsKey(filterName)) {
                throw new RuntimeException("filter name for rule name <" + nluRules.getName() + "> is a global filter");
            }

            NluFilter filter = this.filterMap.get(filterName);
            if (filter == null) {
                throw new RuntimeException("filter name for rule name <" + nluRules.getName() + "> is not exist");
            }
            nluRules.addFilter(filter);

            String cascade = filterElem.attributeValue("cascade");

            if (nluDomainRules != null && StringUtils.equalsIgnoreCase(cascade, "true")) {
                for (NluRules rules2 : nluDomainRules.getRules()) {
                    rules2.addFilter(filter);
                }
            }
        }

        List<Element> resultHandlerElems = rulesElem.elements("result_handler");
        for (Element resultHandlerElem : resultHandlerElems) {
            String handlerName = resultHandlerElem.attributeValue("name");

            String chain = resultHandlerElem.attributeValue("chain");

            if (StringUtils.isEmpty(handlerName)) {
                throw new RuntimeException("result handler's name for rule name <" + nluRules.getName() + "> is empty");
            }

            NluResultHandler resultHandler = this.resultHandlerMap.get(handlerName);
            if (resultHandler == null) {
                throw new RuntimeException("result handler <" + handlerName + "> for rule name <" + nluRules.getName() + "> is not exist");
            }

            String group = resultHandlerElem.attributeValue("group");
            if (StringUtils.isEmpty(group)) {
                throw new RuntimeException("gropu value <" + group + "> for result handler <" + handlerName + "> of rule name<" + nluRules.getName() + "> is empty");
            }

            nluRules.addResultHandler(group, resultHandler, StringUtils.equalsIgnoreCase("true", chain));

            String cascade = resultHandlerElem.attributeValue("cascade");

            if (nluDomainRules != null && StringUtils.equalsIgnoreCase(cascade, "true")) {
                for (NluRules rules2 : nluDomainRules.getRules()) {
                    rules2.addResultHandler(group, resultHandler, StringUtils.equalsIgnoreCase("true", chain));
                }
            }
        }
    }
}
