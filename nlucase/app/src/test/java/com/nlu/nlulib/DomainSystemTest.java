package com.nlu.nlulib;

import com.nlu.nlulib.parser.NluContext;
import com.nlu.nlulib.parser.NluParser;
import com.nlu.nlulib.parser.NluParserFactory;
import com.nlu.nlulib.rule.util.ChineseNumeral;
import com.nlu.nlulib.tool.JsonUtil;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;


public class DomainSystemTest {
	
	@Test
	public void scoreTest() {
		String regex1 = "今天天气.{0,2}好";
		String regex2 = "今天天气.{0,2}差";
		
		
	}
	
	@Test
	public void test() {
		System.out.println(ChineseNumeral.toChinese("10000"));
		System.out.println(ChineseNumeral.toChinese("100000100"));
		System.out.println(ChineseNumeral.toChinese("1100000100"));
		System.out.println(ChineseNumeral.toChinese("3010100"));
		System.out.println(ChineseNumeral.toChinese("10010"));
		System.out.println(ChineseNumeral.toChinese("10001"));
		System.out.println(ChineseNumeral.toChinese("12345"));
		System.out.println(ChineseNumeral.toChinese("5201314"));
		System.out.println(ChineseNumeral.toChinese("46"));
	}
	
	@Test
    public void testSystem() {
		NluParser parser = NluParserFactory.create();
		
    	List<String> inputs = Arrays.asList(
    			"查看今天的相册视频",
    			"我想看昨天的照片",
    			"我想看今天的抓拍"
    			
//    			"更换蓝牙设备",
//    			"更换蓝牙连接",
//    			"更换蓝牙",
//    			"切换蓝牙",
//    			"切换蓝牙设备",
//    			"切换蓝牙连接",
//    			"蓝牙",
//
//    			"播放视频"
//    			"打开正在播放",
//    			"显示正在播放"
//    			"打开勿扰模式",
//    			"我要休息了",
//    			"我要睡觉",
//    			"勿扰模式",
//    			"进入勿扰模式",
//    			"把勿扰模式打开",
//    			"退出勿扰模式",
//    			"关闭勿扰模式",
//    			"取消勿扰模式",
//    			"把勿扰模式关掉"
/**    			
//    			"快进到1小时30分20秒",
//    			"快进至最后1小时30分20秒",
//    			"快进半小时",
//    			"快进到半小时",
//    			"快进到最后半小时",
//    			"后退1小时30分20秒",
//    			"后退到1小时30分20秒",
//    			"后退至最后1小时30分20秒",
//    			"后退半小时",
//    			"后退到半小时",
//    			"后退到最后半分钟",
//    			"投到电视上",
//    			"音量调到百分之二百",
//    			"音量调到百分之二百零一",
//    			"音量调到百分之二百一十",
//    			"音量调到百分之二百一十一",
//    			"音量增加二百",
//    			"音量增加二百零一",
//    			"音量增加二百一十",
//    			"音量增加二百一十一",
//    			
//    			"音量减小二百",
//    			"音量减小二百零一",
//    			"音量减小二百一十",
//    			"音量减小二百一十一",
//    			"亮度调到百分之二百",
//    			"亮度调到百分之二百零一",
//    			"亮度调到百分之二百一十",
//    			"亮度调到百分之二百一十一",
//    			
//    			"亮度增加二百",
//    			"亮度增加二百零一",
//    			"亮度增加二百一十",
//    			"亮度增加二百一十一",
//    			
//    			"亮度减小二百",
//    			"亮度减小二百零一",
//    			"亮度减小二百一十",
//    			"亮度减小二百一十一",
//    			"亮一点",
//    			"暗一点",
//    			"给我调到最小亮度",
//    			"最大亮度",
//    			"投屏",
//    			"我要投屏",
//    			"我想投屏",
//    			"开始投屏",
//    			"打开投屏",
//    			"我想要投屏",
//    			"帮我打开投屏",
//    			"开启投屏",
//    			"帮我投屏",
//    			"投屏到电视机上",
//    			"帮我投屏到电视机上",
//    			"帮我把这个投屏到电视机上",
//    			"帮我把这个电视剧投屏到电视机上",
//    			"帮我把这个电影投屏到电视机上",
//    			"帮我把这个动画片剧投屏到电视机上",
//    			"帮我把这个MV投屏到电视机上",
//    			"我想要投屏到电视机上",
//    			"我想要把这个投屏到电视机上",
//    			"我想要把这个电视剧投屏到电视机上",
//    			"我想要把这个电影投屏到电视机上",
//    			"我想要把这个动画片剧投屏到电视机上",
//    			"我想要把这个MV投屏到电视机上",
//    			"停止投屏",
//    			"退出投屏",
//    			"关闭投屏",
//    			"不想投屏了",
//    			"我不想投屏了",
//    			"取消投屏",
//    			"帮我取消投屏",
//    			"帮我关闭投屏",
//    			"帮我退出投屏",
//    			"我想要退出投屏",
//    			"我想要取消投屏",
//    			"我想要关闭投屏",
//    			"不要投屏了",
//    			"帮我把投屏关了",
//    			"关掉投屏",
//    			"给我把投屏关了",
//    			"把投屏关了吧",
//    			"查看第一个的留言",
//    			"紧急呼叫",
//    			"开始紧急呼叫",
//    			"我要紧急呼叫",
//    			"给我紧急呼叫",
//    			"马上紧急呼叫",
//    			"马上给我紧急呼叫",
//    			"给我马上紧急呼叫",
//    			"语音报错",
//    			
//    			"查看系统更新",
//    			"查看系统升级",
//    			"检查系统更新",
//    			"检查系统升级",
//    			"检查升级",
//    			"查看升级",
//    			"系统有更新吗",
//    			"系统有更新没",
//    			"系统有更新了吗",
//    			" 有没有升级",
//    			"有没有升级",
//    			"有升级吗",
//    			"有没有更新",
//    			"有更新吗",
//    			
//    			"给我调到最小亮度",
    			
    			
//    			"关闭屏幕",
//    			"关闭屏幕",
//    			"关屏",
//    			"把屏幕关上",
//    			"屏幕关上",
//    			"屏幕关上吧",
//    			"请把屏幕关了",
    			
    			
//    			"你都会些什么呀",
//    			"你会做什么",
//    			"你会干什么",
//    			"你都会做什么",
//    			"你都会干什么",
//    			"你有什么本领",
//    			"你知道啥",
//    			"你的技能是什么",
//    			"你有什么技能",
//    			"你有什么本领",
//    			"你会干些啥",
//    			"你懂啥",
//    			"你懂什么",
//    			"你有什么",
//    			"你有啥",
//    			"你都会些啥",
//    			"你的本领是啥",

//    			"我要开机",
//    			"开机",
//    			"我要关机",
//    			"关机",
    			"音量30%"
*/    			
    			);
    	
    	
		for (String input : inputs) {
			NluCommand command = parser.parse(input, NluContext.None);
			System.out.println(input);
			System.out.println(JsonUtil.toJson(command));
		}
    }

	@Test
    public void testVoice() {
    	List<String> inputs = Arrays.asList(
    			"声音小一点",
    			"小点儿声",
    			"在小点儿声",
    			"加一点儿点儿声",
    			"把声音调到五",
    			"大点声音",
    			"音量调大一个",
    			"声音大一点",
    			"声音再大一点",
    			"小点儿声行吗",
    			"大点儿声",
    			"请再大点儿声",
    			"声音大点儿",
    			"声音小点儿",
    			"声音再小点儿",
    			"声音调小一点",
    			"小点儿声音",
    			"把你的音量调小一点",
    			"声音稍微大一点",
    			"大声一点点",
    			"声音跳到最大声",
    			"大一点声音",
    			"增加音量",
    			"声音大一点儿",
    			"再大声一点",
    			"声音弄大一点",
    			"帮我把音量降到最大",
    			"小一点声音",
    			"声音小一下",
    			"没关系声音小一点儿",
    			" 声音大一点",
    			"大声点",
    			"小声点",
    			"声音再低点儿",
    			"小声一点",
    			"大点儿声音",
    			"最大声音",
    			"声音再大一些",
    			"小点儿声音哭了",
    			"加大声音",
    			"音量放大一点儿",
    			"声音放大一点",
    			" 把音量调大",
    			"声音调的最大",
    			"把声音成关掉",
    			"把声音全部关掉",
    			"声音再大点",
    			"把声音调到最小",
    			"大声点儿",
    			"播放声音大一点",
    			" 大声点",
    			"声音小一些小一些的就行",
    			"声音调到最大",
    			"小声点小声点",
    			"升音量调大",
    			"这儿大声点",
    			"再大声点",
    			"声音调大点",
    			" 小声点",
    			"调大声音调大",
    			"声音调到百分之二三十",
    			"声音调大九",
    			"最小音量",
    			"你可以小点儿声嘛",
    			"声音大一点听不到",
    			"把声音关小",
    			"声音大一也",
    			"请小声一点",
    			"小点儿声儿",
    			"声音大些",
    			"声音大点",
    			"音量开到最大",
    			"放最大声",
    			"声音再小一点",
    			"关闭静音",
    			"开启静音",
    			"音量调最大",
    			"不要退出",
    			"退出看不到",
    			"不能取消",
    			"声音太大了",
    			"声音太小了，听不到",
    			"声音不要太大了",
    			"音量调到十",
    			"音量调到百分之90",
    			"关闭蓝牙",
    			"关闭音乐",
    			"关闭声音",
    			"回到首页",
    			"把摄像头打开",
    			"把摄像头关闭",
    			"声音关掉",
    			"退出音乐",
    			"退出有声",
    			"退出电台",
    			"退出天气",
    			"退出XX",
    			"关掉静音",
    			"摄像头关闭",
    			"静音",
    			"蓝牙关闭",
    			"显示播放界面",
    			"蓝牙",
    			"连蓝牙",
    			"连接蓝牙",
    			"打开蓝牙",
    			"这是蓝牙",
    			"这是静音",
    			"下一",
    			"下一页", 
    			"下一个",
    			"下一项",
    			"下一条",
    			"设置",
    			"打开设置",
    			"退出设置",
    			"检查更新",
    			"休息模式",
    			"开启休息模式",
    			"调亮屏幕",
    			"调亮点",
    			"亮度调到24",
    			"亮度调到二十",
    			"亮度调到负百分之二十",
    			"退出休息模式",
    			"退出",
    			"休息模式退出",
    			"休息模式开启",
    			"回首页",
    			"返回首页",
    			"回主页",
    			"回到首页",
    			"返回主页",
    			"声音关掉",
    			"静音关掉",
    			"静音",
    			"休息模式关掉",
    			"休息模式",
    			"关掉蓝牙",
    			"蓝牙关掉"
    			);
    	
    	NluParser parser = NluParserFactory.create();
		for (String input : inputs) {
			NluCommand command = parser.parse(input, NluContext.None); 	
			System.out.println(input);
			System.out.println(JsonUtil.toJson(command));
		}
    }
	
	
	@Test
    public void testExit() {
    	List<String> inputs = Arrays.asList("退出",
    			"不要退出"
    			);
    	
    	NluParser parser = NluParserFactory.create();
		for (String input : inputs) {
			NluCommand command = parser.parse(input, NluContext.None); 	
			System.out.println(input);
			System.out.println(JsonUtil.toJson(command));
		}
    }
	
	@Test
	public void testChoice() {
		List<String> inputs = Arrays.asList("倒数第一个","一",
    			"第一个",
				"第一",    			
    			"不是第一个"
    			);
    	
    	NluParser parser = NluParserFactory.create();
		for (String input : inputs) {
			NluCommand command = parser.parse(input, NluContext.None); 	
			System.out.println(input);
			System.out.println(JsonUtil.toJson(command));
		}
	}
	
	@Test
	public void testArray() {
		String[] data = {"倒数第一个","一",
		"第一个",
		"第一",    			
		"不是第一个"};
		String value = data.toString();
		System.out.println(value);
	}
}
