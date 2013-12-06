package org.saiku.service.util.export;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.olap4j.CellSet;
import org.saiku.olap.dto.resultset.AbstractBaseCell;
import org.saiku.olap.dto.resultset.CellDataSet;
import org.saiku.olap.util.OlapResultSetUtil;
import org.saiku.olap.util.formatter.HierarchicalCellSetFormatter;
import org.saiku.olap.util.formatter.ICellSetFormatter;
import org.saiku.service.util.exception.SaikuServiceException;

public class PdfExporter {

	public static byte[] exportPdf(CellSet cellSet) {
		return exportPdf(cellSet, new HierarchicalCellSetFormatter(),
				"No Title Set");
	}

	public static byte[] exportPdf(CellSet cellSet,
			ICellSetFormatter formatter, String reportTitle) {
		CellDataSet table = OlapResultSetUtil
				.cellSet2Matrix(cellSet, formatter);
		return getPdf(table, reportTitle);
	}

	private static byte[] getPdf(CellDataSet table, String reportTitle) {
		if (table != null) {

			AbstractBaseCell[][] rowData = table.getCellSetBody();
			AbstractBaseCell[][] rowHeader = table.getCellSetHeaders();

			String[][] result = new String[rowHeader.length + rowData.length][];
			for (int x = 0; x < rowHeader.length; x++) {
				List<String> cols = new ArrayList<String>();
				for (int y = 0; y < rowHeader[x].length; y++) {
					cols.add(rowHeader[x][y].getFormattedValue());
				}
				result[x] = cols.toArray(new String[cols.size()]);

			}
			for (int x = 0; x < rowData.length; x++) {
				int xTarget = rowHeader.length + x;
				List<String> cols = new ArrayList<String>();
				for (int y = 0; y < rowData[x].length; y++) {
					String value = rowData[x][y].getFormattedValue();
					// String value = rowData[x][y].getRawValue();
					// if (rowData[x][y] instanceof DataCell
					// && ((DataCell) rowData[x][y]).getRawNumber() != null) {
					// value = ((DataCell) rowData[x][y]).getRawNumber()
					// .toString();
					// }
					cols.add(value);
				}
				result[xTarget] = cols.toArray(new String[cols.size()]);

			}
			return export(result, rowHeader.length, rowHeader[0].length,
					reportTitle);
		}
		return new byte[0];
	}

	private static byte[] export(String[][] resultSet, int headerRows,
			int numColumns, String reportTitle) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			PDFReport pdfReport = new PDFReport(bout, reportTitle);
			pdfReport.createTable(headerRows, numColumns);
			pdfReport.addLogoAndTitle();
			if (resultSet.length > 0) {
				for (int i = 0; i < resultSet.length; i++) {
					String[] row = resultSet[i];
					for (int j = 0; j < row.length; j++) {
						if (row[j] == null || row[j] == "null") {
							if (i != 0) {
								row[j] = resultSet[i - 1][j];
							} else {
								row[j] = "";
							}
						}
					}
					pdfReport.addRow(row);
				}
				pdfReport.closePdfReport();
				byte[] output = bout.toByteArray();
				return output;
			}
		} catch (Throwable e) {
			throw new SaikuServiceException(
					"Error creating pdf export for query", e);
		}
		return new byte[0];
	}

}
