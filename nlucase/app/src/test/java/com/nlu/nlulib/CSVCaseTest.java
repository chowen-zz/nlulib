package com.nlu.nlulib;

import com.nlu.nlulib.NluCommand;
import com.nlu.nlulib.parser.NluContactContext;
import com.nlu.nlulib.parser.NluContext;
import com.nlu.nlulib.parser.NluParser;
import com.nlu.nlulib.parser.NluParserFactory;
import com.nlu.nlulib.tool.JsonUtil;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Test;


public class CSVCaseTest{

	private static final Logger LOGGER = Logger.getLogger("CSVCaseTest");
	private final static int T_USE_CASE 			= 0;
	private final static int T_IS_VALID				= 1;
	private final static int T_IS_MULTI				= 2;
	
	private final static int T_CONTEXT				= 3;
	
	private final static int T_CONTACT 				= 4;		// #号分开
	private final static int T_DOMAIN 				= 5;
	private final static int T_ACTION 				= 6;
	private final static int T_PAYLOAD_NUM			= 7;
	// 以上8列必填
	
	private final static int T_KV_START_IDX 		= 8;		// payload列数据开始索引

//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();
//		LOGGER.info("setUp>>>>>");
//		NluRuleConfiguration configuration		= new NluRuleConfiguration();
//		configuration.configure();
//	}

	@Test
	public void xlsTest() {

//		String filePathhandle = "D:/case.xls";
//		String[] sheetNames = {"测试case"};
		
		String filePath = "/Users/zhouwen/Desktop/NativeNLU/case_all_mdf_v13.xls";
		String[] sheetNames = {
							"通话泛化",				
							"线上问题",
							"其他泛化",
							"留言泛化",
							"相册泛化",
							"通用泛化",
							"通话",
							"留言",
							"system命令",
							"相册",
							"智能抓拍",
							"在线管家",
							"通讯录",
//							"蓝牙",
							"GYM",
							"录像",
							""  // 空串是为了方便注释以上sheetName而不用修改逗号
							};
		Workbook workbook = null;  
		
	    try {  
	    	InputStream is = new FileInputStream(filePath);  
	        workbook = new HSSFWorkbook(is);  
	        
	        for (String sheetName : sheetNames) {

				LOGGER.info("sheetName>>>"+sheetName);
		        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheet(sheetName);
		        
		        if (StringUtils.isEmpty(sheetName)) {
		        	continue;
		        }
		        
		        int rowSize = sheet.getLastRowNum() + 1;  
		        
				NluParser parser = NluParserFactory.create();
				
				// 设置联系人
				String input;
		    	NluCommand command = NluCommand.NONE;
				for (int counter = 0; counter < rowSize; counter++) {
					
					if (counter == 0) {
						continue;
					}
					
					Row row = sheet.getRow(counter);
					if (row == null) {
						continue;
					}
					
					// 读取用例 
					Cell inputCell = row.getCell(T_USE_CASE);
			    	if (inputCell == null) {
			    		continue;
			    	}
					input = inputCell.getStringCellValue();
					
					if (StringUtils.isEmpty(input)) {
						continue;
					}
					
					// 是否多轮
					boolean multiRound = false;
					Cell multiCell = row.getCell(T_IS_MULTI);
					if (multiCell != null && multiCell.getNumericCellValue() == 1.0) {
						multiRound = true;
					}
					
					// 是否参加测试/是否有效
					Cell validCell = row.getCell(T_IS_VALID);
					if (validCell == null ) {
						continue;
					} else {
						if (validCell.getCellTypeEnum().equals(CellType.NUMERIC) && (int)validCell.getNumericCellValue() != 1) {
							continue;
						} else if (validCell.getCellTypeEnum().equals(CellType.STRING) && !validCell.getStringCellValue().equals("1")) {
							continue;
						}
					}
									
					// 联系人
					Cell configContactCell = row.getCell(T_CONTACT);
					String configContact = configContactCell == null? "" : configContactCell.getStringCellValue();
	
					String[] configContactArr = configContact.split("[;:|]");
					
					
					List<String> contactList = new ArrayList<>();
					for (int i = 0; i <configContactArr.length; i++) {
				    	contactList.add(configContactArr[i].trim());
					}
			    	NluContactContext context = new NluContactContext(contactList, null);
			    	
			    	
			    	parser.setContext(context);
					
			    	Cell domainContextCell = row.getCell(T_CONTEXT);
			    	String domainContext = "";
			    	if (domainContextCell != null) 
			    		domainContext = domainContextCell.getStringCellValue();
					
					System.out.println("ROW["+(counter+1)+"]    "+ input + "      >>> 通讯录：" + configContact);
					String lastDialogId = command.getDialogId();
					
					
					if (counter + 1 == 178) {
						System.out.println(">>>>>> debug >>>>>>");
					}
					
					
					if (multiRound) {
						//===========================================
						// 三种调用方式
						//===========================================
						//1. 直接传dialogId
						//command = parser.parse(input, lastDialogId);
						
						//2. 传入一个新的NluContext
//						command = parser.parse(input, new NluContext(lastDialogId));
						
						//3. 传入一个新的会话Context
						NluContext nluContext = null;
						
						if (!StringUtils.isEmpty(domainContext) ) {
							nluContext = NluContext.newDialog(domainContext); 
						} else {
							nluContext = NluContext.newDialog(row.getCell(T_DOMAIN).getStringCellValue());
						}
						
						command = parser.parse(input, nluContext);
						lastDialogId = nluContext.getDialogId();

						// TODO: 2018/7/30 by zhouwen
//						if ((command.getDomain()=="")|| (command.getAction()== "")){
//							throw new RuntimeException(input + "###Domain is not found or Action is not found by rule!!!");
//						}
//						LOGGER.info("caseTest>>>command="+command.getDomain()+"."+command.getAction()+">>>lastDialogId="+lastDialogId);
					} else {
						command = parser.parse(input, NluContext.SINGLE_ROUND_DIALOG);
					}
			    	int payloadNum = 0;
			    	try {
			    		Cell payloadNumCell = row.getCell(T_PAYLOAD_NUM);
			    		if (payloadNumCell != null)
			    			payloadNum = (int)payloadNumCell.getNumericCellValue();
			    	} catch(Exception e) {
			    		e.printStackTrace();
			    		Assert.assertTrue(false);
			    	}
			    	
			    	
			    	System.out.println(JsonUtil.toJson(command));

					// TODO: 2018/7/30
//			    	Assert.assertTrue(command.getDomain().equals(row.getCell(T_DOMAIN).getStringCellValue()));  // 域相等
//			    	Assert.assertTrue(command.getAction().equals(row.getCell(T_ACTION).getStringCellValue()));
			    	
			    	if (multiRound) {
						Assert.assertTrue(StringUtils.equals(command.getDialogId(), lastDialogId));
					}
			    	
			    	if (payloadNum == 0) {
			    		Assert.assertTrue(command.getPayload().size() == 0);
			    	}
			    	
			    	if (payloadNum > 0) {
			    		
			    		for (int i = 0; i < payloadNum; i++) {
			    			int keyIdx = T_KV_START_IDX + i*2;
			    			int valIdx = T_KV_START_IDX + i*2 + 1;
			    			Cell keyCell = row.getCell(keyIdx);
			    			Assert.assertTrue(keyCell != null);
			    			Object retValue = command.getPayload().get(row.getCell(keyIdx).getStringCellValue());
//			    			Assert.assertTrue(retValue != null);todo
			    			
			    			Cell valueCell = row.getCell(valIdx);
			    			Assert.assertTrue(valueCell != null);
			    			
			    			Assert.assertTrue(valueCell.getCellTypeEnum() != CellType.BLANK);
			    			
			    			if (valueCell.getCellTypeEnum().equals(CellType.STRING));
//			    				Assert.assertTrue(retValue.toString().equals(row.getCell(valIdx).getStringCellValue()));
			    			else if(valueCell.getCellTypeEnum().equals(CellType.NUMERIC)) {
			    				if (retValue instanceof Integer || retValue instanceof Long) {
			    					Assert.assertTrue(Integer.parseInt(retValue.toString() )== (int)valueCell.getNumericCellValue());
			    				} else if (retValue instanceof Double || retValue instanceof Float) {
			    					Assert.assertTrue(Double.parseDouble(retValue.toString() )== valueCell.getNumericCellValue());
								}
			    			}
			    		}
			    	}
			    	System.out.println();
				}
	        } // for sheets
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
