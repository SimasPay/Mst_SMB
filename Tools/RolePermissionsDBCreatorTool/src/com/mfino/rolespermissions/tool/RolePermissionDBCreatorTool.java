/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.rolespermissions.tool;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;


/**
 *
 * @author sandeepjs
 */
public class RolePermissionDBCreatorTool {

    private static final int ROW_NUMBER_FIRST_PERM = 4;
    private static final int ROW_NUMBER_LAST_PERM = 300;
    private static final int COL_NUMBER_FIRST_PERM = 4;
    private static final int COL_NUMBER_LAST_PERM = 28;
    private static String PERMISSION_FILE_PATH = "../../../../";
    private static  String outputFile =  "D:\\" ;
    private static final int ROLE_VALUE_ROW_NUMBER = 1;

    public static void main(String[] args) throws IOException {

        POIFSFileSystem fs = null;
        if(args.length<1){
        	System.out.println("provide profile name");
        	return; 
        }
        PERMISSION_FILE_PATH = PERMISSION_FILE_PATH+args[0]+"_role_perm.xls";
        outputFile = outputFile+args[0]+"_role_perm.sql";
        try {
            fs = new POIFSFileSystem(RolePermissionDBCreatorTool.class.getResourceAsStream(PERMISSION_FILE_PATH));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        if (fs == null) {
            return;
        }
        Writer outputFileWriter = null;
        if ( outputFile != null ) {
        	System.out.println("writing generated schema to file: " + outputFile );
			outputFileWriter = new FileWriter( outputFile );
		}
        String init= "delete from role_permission;\n ALTER TABLE role_permission AUTO_INCREMENT = 1;\n";
        outputFileWriter.write(init);
        HSSFWorkbook wb = new HSSFWorkbook(fs);

        // Iterate through the WorkBook.
        HSSFSheet sheet = (HSSFSheet) wb.getSheetAt(1);
        Row roleValueRow = sheet.getRow(ROLE_VALUE_ROW_NUMBER);

        for (Row row : sheet) {

            if (row.getRowNum() < ROW_NUMBER_FIRST_PERM || row.getRowNum() > ROW_NUMBER_LAST_PERM) {
                continue;
            }

            Cell permissionStringCell = row.getCell(0);
            String permissionStr = "";
            if(permissionStringCell != null)
                 permissionStr = processPermString(permissionStringCell.getStringCellValue());

            for (Cell cell : row) {
                if(cell.getColumnIndex() < COL_NUMBER_FIRST_PERM || cell.getColumnIndex() > COL_NUMBER_LAST_PERM)
                    continue;

                if (cell.getStringCellValue().trim().equals("Y") && !permissionStr.trim().equalsIgnoreCase("")) {
                    int role = (int) roleValueRow.getCell(cell.getColumnIndex()).getNumericCellValue();
                    String insertStmt = "INSERT INTO role_permission " +
                            "(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) " +
                            "VALUES('1', NOW(), 'system', NOW(), 'system', '" + role + "','" + permissionStr + "');";
                    if(outputFileWriter!=null){
                    	try {
    						outputFileWriter.write( insertStmt + "\n" );
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
                    }
                }
            }
        }
        try {
			if( outputFileWriter != null ) {
				outputFileWriter.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
    }

    private static String processPermString(String stringCellValue) {
        String[] tokens = stringCellValue.split(",");
        String retValue = "";
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.length() < 2 && i != 0) {
                token = "0" + token;
            }

            retValue = retValue + token;
        }

        return retValue;
    }
}
