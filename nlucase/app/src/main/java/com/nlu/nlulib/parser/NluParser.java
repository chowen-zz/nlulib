package com.nlu.nlulib.parser;


import com.nlu.nlulib.NluCommand;

public interface NluParser {
	
	/**
	 * 设置context
	 * 
	 * 设置一个context之后，调用parse(String)和parse(String
	 * , NluCommand)都会使用同一个Context
	 * @param context
	 */
	void setContext(NluContext context);
	
	/**
	 * dialogId即为sessionId
	 * 
	 * 通常在垂类外调用此函数时，dialogId置为null
	 * 在垂类内调用此函数时，dialogId为当前session对话id
	 * 
	 * @param input
	 * @return
	 */
	NluCommand parse(String input, String dialogId);
	
	/**
	 * 直接调用，每次都传context
	 * @param input
	 * @param context
	 * @return
	 */
	NluCommand parse(String input, NluContext context);
}
