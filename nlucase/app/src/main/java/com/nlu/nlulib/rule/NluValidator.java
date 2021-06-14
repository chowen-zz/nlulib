package com.nlu.nlulib.rule;


import com.nlu.nlulib.parser.NluContext;

public interface NluValidator {
    /**
     *
     * @param value 校验值
     * @param context 上下文
     * @return
     */
    boolean validate(String value, NluContext context);

    /**
     * 如果<validator>指定了rule的标签，setRules接口就会被
     * 调用，否则忽略
     */
    void setRules(NluRules rules);

}
