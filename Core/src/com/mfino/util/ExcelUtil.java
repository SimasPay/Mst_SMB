package com.mfino.util;


import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.mfino.service.ReportParametersService;

public class ExcelUtil {

	
	public static Font getFont(Workbook hwb,Boolean bold, String reportName){
		Font font = hwb.createFont();
		if(bold){
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		}
		font.setFontName("Times");
		font.setFontHeightInPoints((short)ReportParametersService.getFontSize(reportName));
		return font;
	}
	
    public static Cell createCell(HSSFWorkbook hwb, HSSFRow row, int index, Font font, int border,int thickness){
		CellStyle style= hwb.createCellStyle();
		
		Cell cell = row.createCell(  index);
		int left =1,top =2,right =4,bottom=8;
		if(font!=null){
			style.setFont(font);
		}
		
		if((left&border) == left){
			if((left&thickness) == left){
			style.setBorderLeft(CellStyle.BORDER_MEDIUM);
			}else{
				style.setBorderLeft(CellStyle.BORDER_THIN);
			}
		}
		if((top&border) == top){
			if((top&thickness) == top){
				style.setBorderTop(CellStyle.BORDER_MEDIUM);
				}else{
					style.setBorderTop(CellStyle.BORDER_THIN);
				}
		}
		if((right&border) == right){
			if((right&thickness) == right){
				style.setBorderRight(CellStyle.BORDER_MEDIUM);
				}else{
					style.setBorderRight(CellStyle.BORDER_THIN);
				}
		}
		if((bottom&border) == bottom){
			if((bottom&thickness) == bottom){
				style.setBorderBottom(CellStyle.BORDER_MEDIUM);
				}else{
					style.setBorderBottom(CellStyle.BORDER_THIN);
				}
		}
//		style.setAlignment(CellStyle.ALIGN_FILL);
		cell.setCellStyle(style);
		return cell;
				
	}


	public static Cell createHeaderCell(Workbook hwb, Row row, int index) {
		Font font = hwb.createFont();
		font.setFontName("Calibri");
		font.setFontHeightInPoints((short)15);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle style= hwb.createCellStyle();
		style.setFont(font);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		Cell cell = row.createCell(  index);
		cell.setCellStyle(style);
		return cell;
	}


	/*
	 *  adjusts width of the column to fit cell content
	 */
	public static void autoSizeColumn(HSSFWorkbook hwb, int totalColumns) {
		HSSFSheet sheet = hwb.getSheetAt(0);
		for(int i=0;i<totalColumns;i++)
		sheet.autoSizeColumn(i);
	}
		
}
