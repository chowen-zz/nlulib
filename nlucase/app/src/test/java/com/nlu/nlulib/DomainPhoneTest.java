package com.nlu.nlulib;

import com.nlu.nlulib.parser.NluContactContext;
import com.nlu.nlulib.parser.NluContext;
import com.nlu.nlulib.parser.NluContextKey;
import com.nlu.nlulib.parser.NluParser;
import com.nlu.nlulib.parser.NluParserFactory;
import com.nlu.nlulib.tool.JsonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


public class DomainPhoneTest {
	
	@Test
	public void test() {
		char c = '一';
		
		System.out.println(c);
	}
	
	@Test
	public void getGroupIndex() {
		String regex = "(((?<who>.*)打)(?<whom>.*))";
		String returnGroups[] = {"who", "whom"};
		String input = "我打你";
		Map<String, Integer>  groupName2IndexMap = new HashMap<>();
		for(int i = 0; i < returnGroups.length; i++) {
			int end = regex.indexOf(returnGroups[i]);
			
			if (end == -1)
				continue;
			int groupIndex = 0;
			for(int n = 0; n < end; n++) {
				if (regex.charAt(n) == '(') {
					groupIndex++;
				}
			}
			if (groupIndex != 0) {
				groupName2IndexMap.put(returnGroups[i], groupIndex);
			}
		}
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		
		if (matcher.find()) {
			
			int count = matcher.groupCount();
			
			for(int i = 0; i <= count; i++) {
				System.out.println("["+i+"]"+matcher.group(i));
			}
			
			int index = groupName2IndexMap.get("who");
			System.out.println(matcher.group(index));
			
			index = groupName2IndexMap.get("whom");
			System.out.println(matcher.group(index));
		} else {
			System.err.println("fail");
		}
		
	}
	
	
	@Test
	/**
	 * 单轮对话，调用系统命令或是垂类打开之前的指令识别
	 */
    public void testPhoneSingle() {
    	NluParser parser = NluParserFactory.create();
    	
    	List<String> contactList = new ArrayList<>();
    	contactList.add("四十六");
    	NluContactContext context = new NluContactContext(contactList, null);
    	
    	parser.setContext(context);
    	NluCommand command;
    	List<String> inputs = Arrays.asList(
    			"我想与家人通话"    			
				);

    	for (String input : inputs) {    		
    		// 单轮调用，直接传入NluContext.SINGLE_ROUND_DIALOG或是NluContext.None都可以
    		command = parser.parse(input, NluContext.SINGLE_ROUND_DIALOG);
    		
        	System.out.println(input);
        	System.out.println(JsonUtil.toJson(command));
    	}
    }
	
	/**
	 * 由指令识别进入多轮会话
	 */
	@Test	
    public void testPhoneMultiRound() {
		List<String> inputs = Arrays.asList("打电话");
    	
    	NluParser parser = NluParserFactory.create();
    	
    	List<String> contactList = new ArrayList<>();
    	contactList.add("小明");   
    	NluContactContext context = new NluContactContext(contactList, null);
    	
    	parser.setContext(context);
    	
    	// 没有进入到多轮之前，parse的调用直接传入传入NluContext.SINGLE_ROUND_DIALOG或是NluContext.None都可以
    	NluCommand command = parser.parse(inputs.get(0), NluContext.SINGLE_ROUND_DIALOG); 	
    	System.out.println(inputs.get(0));
    	System.out.println(JsonUtil.toJson(command));
    	
    	System.out.println("=======================================================================================");
    	
    	inputs = Arrays.asList(
    			"关机",
    			""
				);

    	
    	for (String input : inputs) {
    		
    		if (StringUtils.isEmpty(input))
    			continue;
    		
    		// 2. 利用上一轮的对话id，创建新的NluContext
    		NluContext ctx = new NluContext(command.getDialogId());
    		ctx.set(NluContextKey.DOMAIN_CONTEXT, "phone.calling");
    		command = parser.parse(input, ctx);
    		
        	System.out.println(input);
        	System.out.println(JsonUtil.toJson(command));
    	}
    }
	
	/**
	 * 由触屏进入多轮会话，此时的多轮会话不是由nlu开启的，而是由触屏开启的。
	 * 
	 * 假设由触屏进入到发留言界面 
	 */
	@Test	
    public void testMessageMultiRoundByTouch() {
    	
    	NluParser parser = NluParserFactory.create();
    	
    	List<String> contactList = new ArrayList<>();
    	contactList.add("小明的家");
    	contactList.add("小明的手");
    	NluContactContext context = new NluContactContext(contactList, null);
    	
    	parser.setContext(context);
    	
    	// 假设现在客户端已经由进入到发留言的垂类了，
    	
    	List<String> inputs = Arrays.asList(
    			"小明的家",
    			"第一个",
    			"发送"
				);

    	
    	//==================================================================================
    	// 有两种调用方式
    	//==================================================================================
    	
    	// 方法1
    	// 客户端保存的当前垂类的上一个识别返回的NluCommand对像
    	NluCommand command = null;    	
    	for (String input : inputs) {
    		
    		if (command == null) {
    			command = parser.parse(input, NluContext.newDialog(NluDomainName.MESSAGE));
    		} else {
    			command = parser.parse(input, command.getDialogId());
    		}
        	System.out.println(input);
        	System.out.println(JsonUtil.toJson(command));
    	}
    	
    	
    	// 方法2
    	// 直接调用
    	for (String input : inputs) {
    		
    		NluCommand curCommand = parser.parse(input, NluContext.newDialog(NluDomainName.MESSAGE));
        	System.out.println(input);
        	System.out.println(JsonUtil.toJson(curCommand));
    	}
    }
	
	@Test
    public void testPhone() {
    	List<String> inputs = Arrays.asList("呼叫XX",
    										"打电话给186XXXXX",
    										"呼叫张冰的家",
    										"打电话给110",
    										"打电话给13800138000",
    										"打电话给365428",
    										
    										"给XX打电话",
    								           "拨打XX电话",
    								           "与XX视频",
    								           "与XX进行视频",
    								           "我想和XX打电话",
    								           "与XX语音通话",
    								           "打电话给XX",
    								           "我想和XX视频",
    								           "我想和XX通话",
    								           "打电话给186XXXXX",
    								           "呼叫186XXXXX",
    								           "拨打186XXXXX",
    								           "嗯嗯嗯嗯嗯嗯嗯呼叫妈妈",
    								           "你小孩打电话给大v",
    								           "打电话给下建立",
    								           "拨打大众polo",
    								           "跟爸爸打电话",
    								           "我要给妈妈打电话",
    								           "我要给妈妈打视频",
    								           "给希希姐姐打电话的电话费",
    								           "呼叫一下爸爸",
    								           "我要打一个电话给爸爸",
    								           "给广平打一个电话",
    								           "帮帮忙给王斌打一个电话",
    								           "和姥姥视频",
    								           "我想给奶奶打电话",
    								           "拨打曲兹一电话",
    								           "打给陈晓",
    								           "跟婷婷的家视频",
    								           "给嘿嘿打电话",
    								           "给我接通谢军的电话",
    								           "打王冠电话",
    								           "拨通小米的电话",
    								           "呼叫爸爸",
    								           "我要打个电话给爸爸",
    								           "test",
    								           "呼叫xx",
    								           "呼叫一下xx",
    								           "呼叫电话xx",
    								           "拨给xx",
    								           "拨xx电话",
    								           "拨一下xx电话",
    								           "拨一下xx的电话",
    								           "拨电话给xx",
    								           "拨一个电话给xx",
    								           "拨一通电话给xx",
    								           "拨个电话给xx",
    								           "拨xx的电话",
    								           "打给xx",
    								           "打电话给xx",
    								           "打个电话给xx",
    								           "打一个电话给xx",
    								           "打下电话给xx",
    								           "打一下电话给xx",
    								           "打通电话给xx",
    								           "打一通电话给xx",
    								           "打一下xx的电话",
    								           "打一下xx电话",
    								           "打下xx的电话",
    								           "打下xx电话",
    								           "给xx打电话",
    								           "给xx打个电话",
    								           "给xx打一个电话",
    								           "给xx打通电话",
    								           "给xx打一通电话",
    								           "给xx打下电话",
    								           "给xx打一下电话",
    								           "与xx语音",
    								           "与xx视频",
    								           "与xx通话",
    								           "与xx通电话",
    								           "与xx打电话",
    								           "与xx打通电话",
    								           "与xx打个电话",
    								           "与xx打下电话",
    								           "与xx打一通电话",
    								           "与xx打一个电话",
    								           "与xx打一下电话",
    								           "和xx语音",
    								           "和xx视频",
    								           "和xx通话",
    								           "和xx通电话",
    								           "和xx打电话",
    								           "和xx打通电话",
    								           "和xx打个电话",
    								           "和xx打下电话",
    								           "和xx打一通电话",
    								           "和xx打一个电话",
    								           "和xx打一下电话",
    								           "跟xx语音",
    								           "跟xx视频",
    								           "跟xx通话",
    								           "跟xx通电话",
    								           "跟xx打电话",
    								           "跟xx打通电话",
    								           "跟xx打个电话",
    								           "跟xx打下电话",
    								           "跟xx打一通电话",
    								           "跟xx打一个电话",
    								           "跟xx打一下电话",
    								           "去电给xx",
    								           "去电话给xx",
    								           "去个电话给xx",
    								           "去一个电话给xx",
    								           "去通电话给xx",
    								           "去一通电话给xx",
    								           "去下电话给xx",
    								           "去一下电话给xx");
    	
    	NluParser parser = NluParserFactory.create();
    	
    	List<String> contactList = new ArrayList<>();
    	contactList.add("XX");
    	contactList.add("xx");
    	contactList.add("186XXXXX");
    	contactList.add("张斌的家");
    	NluContactContext context = new NluContactContext(contactList, null);
    	
    	parser.setContext(context);
    	
    	
    	for (String input : inputs) {
    		NluCommand command = parser.parse(input, NluContext.None); 	
        	System.out.println(input);
        	System.out.println(JsonUtil.toJson(command));
        	
        	//assert("XX".equals(command.getPayload().get("name")));
    	}

    }
	
	@Test
	public void testPhoneOpen2() {
    	List<String> inputs = Arrays.asList("呼叫XX",
				"打电话给186XXXXX",
				"呼叫张冰的家",
				"打电话给110",
				"打电话给13800138000",
				"打电话给365428",
				
				"给XX打电话",
		           "拨打XX电话",
		           "与XX视频",
		           "与XX进行视频",
		           "我想和XX打电话",
		           "与XX语音通话",
		           "打电话给XX",
		           "我想和XX视频",
		           "我想和XX通话",
		           "打电话给186XXXXX",
		           "呼叫186XXXXX",
		           "拨打186XXXXX",
		           "嗯嗯嗯嗯嗯嗯嗯呼叫妈妈",
		           "你小孩打电话给大v",
		           "打电话给下建立",
		           "拨打大众polo",
		           "跟爸爸打电话",
		           "我要给妈妈打电话",
		           "我要给妈妈打视频",
		           "给希希姐姐打电话的电话费",
		           "呼叫一下爸爸",
		           "我要打一个电话给爸爸",
		           "给广平打一个电话",
		           "帮帮忙给王斌打一个电话",
		           "和姥姥视频",
		           "我想给奶奶打电话",
		           "拨打曲兹一电话",
		           "打给陈晓",
		           "跟婷婷的家视频",
		           "给嘿嘿打电话",
		           "给我接通谢军的电话",
		           "打王冠电话",
		           "拨通小米的电话",
		           "呼叫爸爸",
		           "我要打个电话给爸爸",
		           "test",
		           "呼叫xx",
		           "呼叫一下xx",
		           "呼叫电话xx",
		           "拨给xx",
		           "拨xx电话",
		           "拨一下xx电话",
		           "拨一下xx的电话",
		           "拨电话给xx",
		           "拨一个电话给xx",
		           "拨一通电话给xx",
		           "拨个电话给xx",
		           "拨xx的电话",
		           "打给xx",
		           "打电话给xx",
		           "打个电话给xx",
		           "打一个电话给xx",
		           "打下电话给xx",
		           "打一下电话给xx",
		           "打通电话给xx",
		           "打一通电话给xx",
		           "打一下xx的电话",
		           "打一下xx电话",
		           "打下xx的电话",
		           "打下xx电话",
		           "给xx打电话",
		           "给xx打个电话",
		           "给xx打一个电话",
		           "给xx打通电话",
		           "给xx打一通电话",
		           "给xx打下电话",
		           "给xx打一下电话",
		           "与xx语音",
		           "与xx视频",
		           "与xx通话",
		           "与xx通电话",
		           "与xx打电话",
		           "与xx打通电话",
		           "与xx打个电话",
		           "与xx打下电话",
		           "与xx打一通电话",
		           "与xx打一个电话",
		           "与xx打一下电话",
		           "和xx语音",
		           "和xx视频",
		           "和xx通话",
		           "和xx通电话",
		           "和xx打电话",
		           "和xx打通电话",
		           "和xx打个电话",
		           "和xx打下电话",
		           "和xx打一通电话",
		           "和xx打一个电话",
		           "和xx打一下电话",
		           "跟xx语音",
		           "跟xx视频",
		           "跟xx通话",
		           "跟xx通电话",
		           "跟xx打电话",
		           "跟xx打通电话",
		           "跟xx打个电话",
		           "跟xx打下电话",
		           "跟xx打一通电话",
		           "跟xx打一个电话",
		           "跟xx打一下电话",
		           "去电给xx",
		           "去电话给xx",
		           "去个电话给xx",
		           "去一个电话给xx",
		           "去通电话给xx",
		           "去一通电话给xx",
		           "去下电话给xx",
		           "去一下电话给xx");
    	
    	NluParser parser = NluParserFactory.create();
    	
    	
    	for (String input : inputs) {
    		NluCommand command = parser.parse(input, NluContext.None); 	
        	System.out.println(input);
        	System.out.println(JsonUtil.toJson(command));
        	
        	//assert("XX".equals(command.getPayload().get("name")));
    	}
	}
	@Test
	public void testPhoneNeedInput() {
    	List<String> inputs = Arrays.asList("紧急呼叫",
							    			"我要打电话",
							    			"我要打个电话",
							    			"我不要打电话",
							    			"打个电话",
							    			"打电话",
							    			"拨打电话",
							    			"拨打",
							    			"请呼叫",
							    			"呼叫",	
							    			
							    			"打听一下范冰冰的电话是多少");
    	
    	NluParser parser = NluParserFactory.create();
    	
    	
    	for (String input : inputs) {
    		NluCommand command = parser.parse(input, NluContext.None); 	
        	System.out.println(input);
        	System.out.println(JsonUtil.toJson(command));
        	
        	//assert("XX".equals(command.getPayload().get("name")));
    	}

    }
    
    @Test
    public void testPhoneOpen() {
    	
    	System.out.println("================================ open ===============================");
    	
    	NluParser parser = NluParserFactory.create();
    	
    	String input;
    	NluCommand command;
    	
    	input = "我要打电话";
    	command = parser.parse(input, NluContext.None); 	
    	System.out.println(input);
    	System.out.println(JsonUtil.toJson(command));
    	
    	input = "我要打个电话";
    	command = parser.parse(input, NluContext.None); 	
    	System.out.println(input);
    	System.out.println(JsonUtil.toJson(command));
    	
    	input = "我不要打电话";
    	command = parser.parse(input, NluContext.None); 	
    	System.out.println(input);
    	System.out.println(JsonUtil.toJson(command));
    }
    
    
    @Test
    public void testPhoneOpenAndCall() {
    	System.out.println("================================ open_and_input ===============================");
    	NluParser parser = NluParserFactory.create();
    	
    	String input;
    	NluCommand command;
    	
    	input = "我要打电话给李老板";
    	command = parser.parse(input, NluContext.None); 	
    	System.out.println(input);
    	System.out.println(JsonUtil.toJson(command));
    	
    	input = "呼叫李老板";
    	command = parser.parse(input, NluContext.None); 	
    	System.out.println(input);
    	System.out.println(JsonUtil.toJson(command));
    	
    	input = "我不想呼叫傻逼";
    	command = parser.parse(input, NluContext.None); 	
    	System.out.println(input);
    	System.out.println(JsonUtil.toJson(command));
    	
    	
    	input = "给李老板打个电话";
    	command = parser.parse(input, NluContext.None); 	
    	System.out.println(input);
    	System.out.println(JsonUtil.toJson(command));
    }
    
    @Test
    public void testInput() {
    	
    	NluParser parser = NluParserFactory.create();
    	
    	String input1 = "我要打电话";
    	NluCommand command;
    	
    	command = parser.parse(input1, NluContext.None); 	
    	System.out.println(input1);
    	System.out.println(JsonUtil.toJson(command));
    	
    	
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	List<String> inputs = Arrays.asList("打给XX",
    			"打给黄大侠",
    			"给黄少侠",
				"李老板",
				"186XXXXX");

		
		NluContext context = new NluContext(command.getDialogId());
		
		for (String input : inputs) {
			//command = parser.parse(input, context);
			command = parser.parse(input, context); 
			System.out.println(input);
			System.out.println(JsonUtil.toJson(command));
		}
    }
    
    /*@Test
    public void testSelect() {
    	List<String> inputs = Arrays.asList("第一个",
    			"二",
    			"第一百个",
				"后面第二个",
				"不第二个",
				"倒数第一个");

		NluParser parser = NluParserFactory.create();
		NluCommand lastCommand = new NluCommand(NluDomainName.PHONE, NluDomainAction.OPEN);
		NluContext context = new NluContext(lastCommand.getDialogId());
		
		for (String input : inputs) {
			NluCommand command = parser.parse(input, context); 	
			System.out.println(input);
			System.out.println(JsonUtil.toJson(command));
		}
    }*/
    
    @Test
    public void testConfirm() {
    	List<String> inputs = Arrays.asList("好的",
    			"不是的",
    			"是",
				"对",
				"没错的",
				"是的呢");
    	
    	NluParser parser = NluParserFactory.create();
		NluCommand lastCommand = new NluCommand(NluDomainName.PHONE, NluDomainAction.OPEN);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NluContext context = new NluContext(lastCommand.getDialogId());
		
		for (String input : inputs) {
			NluCommand command = parser.parse(input, context); 	
			System.out.println("dialogId:"+lastCommand.getDialogId() + ", input:" + input);
			System.out.println(JsonUtil.toJson(command));
		}
    }
    
    @Test
    public void testInvite() {
    	List<String> inputs = Arrays.asList("邀请XX加入通话",
    			"邀请XX一起视频",
    			"邀请XX视频",
    			"把XX加入通话",
    			"把XX加进来",
    			"把xx拉进来一起视频",
    			"请拉小敏进来通话",
    			"请记得喊小敏加入",
    			"拉小敏进来通话",
    			"请小敏一起视频",
    			
    			
    			"请记得喊小敏加入我们明天的Party",
    			"要把小敏拉进来视频吗",
    			"请不要把小敏拉进来视频通话"
    			);
    	
    	NluParser parser = NluParserFactory.create();
		NluCommand lastCommand = new NluCommand(NluDomainName.PHONE, NluDomainAction.OPEN);
		NluContext context = new NluContext(lastCommand.getDialogId());
		
		for (String input : inputs) {
			NluCommand command = parser.parse(input, context); 	
			System.out.println(input);
			System.out.println(JsonUtil.toJson(command));
		}
    }
    
    
    @Test
    public void testAccept() {
    	List<String> inputs = Arrays.asList("接听",
    			"接电话",
    			"使用语音接听",
    			"接通视频",
    			"给我接通谢军的电话");
    	
    	NluParser parser = NluParserFactory.create();
		
		for (String input : inputs) {
			NluCommand command = parser.parse(input, NluContext.None); 	
			System.out.println(input);
			System.out.println(JsonUtil.toJson(command));
		}
    }
    
    
    @Test
    public void testReject() {
    	List<String> inputs = Arrays.asList("挂断",
    			"挂掉电话",
    			"拒掉电话",
    			"取消电话",
    			"拒接",
    			"不接");
    	
    	NluParser parser = NluParserFactory.create();
		NluCommand lastCommand = new NluCommand(NluDomainName.PHONE, NluDomainAction.OPEN);
		NluContext context = new NluContext(lastCommand.getDialogId());
		
		for (String input : inputs) {
			NluCommand command = parser.parse(input, context); 	
			System.out.println(input);
			System.out.println(JsonUtil.toJson(command));
		}
    }
    
    
    
}
