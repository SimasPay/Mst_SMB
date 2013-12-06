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
public class PermissionItemsDBCreatorTool {

    private static final String PERMISSION_FILE_PATH = "../../../../perm-items.xls";
    private static final String outputFile =  "D:\\perm-items.sql" ;
    private static final int SHEETNUM_SERVER = 0;
    private static final int SHEETNUM_UI = 1;
    private static final int STARTROW_SERVER = 2;
    private static final int ENDROW_SERVER = 100;
    private static final int STARTROW_UI = 1;
    private static final int ENDROW_UI = 250;
    private static final int ENDCOL = 5;
    private static final int STARTCOL = 1;

    public static void main(String[] args) throws IOException {

        POIFSFileSystem fs = null;

        try {
            fs = new POIFSFileSystem(PermissionItemsDBCreatorTool.class.getResourceAsStream(PERMISSION_FILE_PATH));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        if (fs == null) {
            return;
        }

        HSSFWorkbook wb = new HSSFWorkbook(fs);

        // Iterate through the WorkBook.
        HSSFSheet serverSheet = (HSSFSheet) wb.getSheetAt(SHEETNUM_SERVER);
        HSSFSheet uiSheet = (HSSFSheet) wb.getSheetAt(SHEETNUM_UI);
        
        Writer outputFileWriter = null;
    	
        if ( outputFile != null ) {
			System.out.println("writing generated schema to file: " + outputFile );
			outputFileWriter = new FileWriter( outputFile );
		}
       String init= "delete from mfino.permission_item;\n ALTER TABLE mfino.permission_item AUTO_INCREMENT = 1;\n";
       outputFileWriter.write(init);
        generateSQL(serverSheet, STARTROW_SERVER, ENDROW_SERVER, outputFileWriter);
        generateSQL(uiSheet, STARTROW_UI, ENDROW_UI, outputFileWriter);

        try {
			if( outputFileWriter != null ) {
				outputFileWriter.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
    }

    private static void generateSQL(HSSFSheet uiSheet, int startRow, int endRow, Writer outputWriter) {
		        
        for (Row row : uiSheet) {
            if (row.getRowNum() > endRow || row.getRowNum() < startRow) {
                continue;
            }

            Integer permission = null;
            Integer itemType = null;
            String itemID = null;
            String fieldID = null;
            String action = null;

            for (Cell cell : row) {
                if (cell.getColumnIndex() > ENDCOL || cell.getColumnIndex() < STARTCOL) {
                    continue;
                }

                switch (cell.getColumnIndex()) {
                    case 1:
                        permission = (int) cell.getNumericCellValue();
                        break;
                    case 2:
                        itemType = (int) cell.getNumericCellValue();
                        break;
                    case 3:
                        itemID = cell.getStringCellValue();
                        break;
                    case 4:
                        fieldID = cell.getStringCellValue();
                        break;
                    case 5:
                        action = cell.getStringCellValue();
                        break;            
                }
            }

            if(fieldID == null)
                fieldID = "default";
            if(action == null)
                action = "default";

            if(permission != null) {
                String insertStmt = "INSERT INTO permission_item " +
                    "(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) " +
                    "VALUES('1', NOW(), 'system', NOW(), 'system', " + permission + "," + itemType + ",'" + itemID + "','" + fieldID + "','" + action + "');";
                if(outputWriter!=null){
                	try {
						outputWriter.write( insertStmt + "\n" );
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
           
            
            }

        }
    }
}


