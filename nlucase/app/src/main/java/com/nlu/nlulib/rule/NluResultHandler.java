package com.nlu.nlulib.rule;


import com.nlu.nlulib.parser.NluContext;

public abstract class NluResultHandler {

    public abstract NluRuleMatchResult handle(NluRuleMatchResult result, String group, NluContext context, boolean chain);

    /**
     * result handler的chain属性，用于一个rules中配置的最后一个handler的配置，用于声明handler处理失败时的逻辑
     * <p>
     * chain的取值的意义：
     * true:	result handler处理失败的时候，会设置match为false（因为进到resultHandler的时候，match必定为true）交给下一个rule处理
     * false:	直接返回, match为true，返回内容根据具体的result handler处理结果而定
     *
     * @param result
     * @param group
     * @return
     */
    public NluRuleMatchResult chain(NluRuleMatchResult result, String group) {
        result.setMatch(false);

        result.remove(group);

        return result;
    }

}
