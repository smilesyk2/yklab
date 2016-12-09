/*
 * @(#)DBJob.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.job.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kr.co.wisenut.Exception.StringException;
import kr.co.wisenut.logger.Log2;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * DBJob
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class ExcelJob{

	private Workbook workbook = null;
	private Sheet sheet = null;
	private List<List<Cell>> sheetData = null;


	public ExcelJob(String path, int sheetIdx){

		try {
			this.workbook = WorkbookFactory.create(new FileInputStream(new File(path)));
			this.sheet = workbook.getSheetAt(sheetIdx);
			this.sheetData = new ArrayList<List<Cell>>();

		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addSheetData(int firstRow, int firstColumn){
		Log2.out("[Info] [ExcelJob] [Start Excel sheet Data put...]");
		long line = 0;
		int lastRowNum = sheet.getLastRowNum();
		for(int rn=firstRow; rn<=lastRowNum; rn++){
			Row row = sheet.getRow(rn);
			// 첫번째 값이 비어있는 겨웅 
			if(row.getCell(firstColumn, Row.RETURN_BLANK_AS_NULL)==null){
				continue;
			}
			List<Cell> data = new ArrayList<Cell>();
			int lastColumnNum = row.getLastCellNum();
			for(int cn=firstColumn; cn<lastColumnNum; cn++){
				Cell cell = row.getCell(cn,Row.CREATE_NULL_AS_BLANK);
				data.add(cell);
			}
			sheetData.add(data);
			line++;
		}
		Log2.out("[Info] [ExcelJob] [End Excel sheet Data ["+line+"] Line]");
	}

	public int getSheetSize(){
		return sheetData.size();
	}

	public int getColumnCount(){
		return sheetData.get(0).size();
	}

	public ExcelMapping getCellData(int cnt) throws StringException{

		ArrayList<ExcelInfoSet> ex_catalog = null;
		ExcelMapping ex_mapping = new ExcelMapping();

		List<Cell> list = sheetData.get(cnt);

		if(list != null){
			ex_catalog = new ArrayList<ExcelInfoSet>();
			for(int idx = 0; idx < list.size(); idx++){
				Cell cell = (Cell) list.get(idx);
				ExcelInfoSet ex_infoSet = new ExcelInfoSet();
				if(cell.getCellType() == 0){
					ex_infoSet.setCellData(Double.toString(cell.getNumericCellValue()));
				}else if(cell.getCellType() == 1){
					ex_infoSet.setCellData(cell.getStringCellValue().toString());
				}else if(cell.getCellType() == 3){
					ex_infoSet.setCellData(""); //빈값일때
				}
				ex_catalog.add(ex_infoSet);
			}

			ex_mapping.setExcelCatalog(ex_catalog);

		}else{
			throw new StringException("[Error] [ExcelJob] [Excel sheet data is NULL]");
		}

		return ex_mapping;

	}

}
