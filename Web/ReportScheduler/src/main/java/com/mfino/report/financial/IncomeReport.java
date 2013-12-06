/**
 * 
 */
package com.mfino.report.financial;

import java.math.BigDecimal;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;

/**
 * @author Chaitanya
 *
 */
public class IncomeReport extends MFSIncomeReport {

	protected int addChargesFromVisafone(BigDecimal[] counts, HSSFWorkbook hwb,
			HSSFSheet sheet, Font font, int index) {
		return index;
	}

	protected int addChargesFromDSTV(BigDecimal[] counts, HSSFWorkbook hwb,
			HSSFSheet sheet, Font font, int index) {
		return index;
	}
	
}
