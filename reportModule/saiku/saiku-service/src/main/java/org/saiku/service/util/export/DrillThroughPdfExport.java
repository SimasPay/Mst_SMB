package org.saiku.service.util.export;

import java.io.ByteArrayOutputStream;
import java.sql.ResultSet;

import org.saiku.service.util.exception.SaikuServiceException;

public class DrillThroughPdfExport {

	public static byte[] pdfExport(ResultSet rs, String reportTitle) {
		int rowCount = 0;
		int columnCount = 0;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PDFReport pdfReport = null;
		try {
			while (rs.next()) {
				if (rowCount == 0) {
					columnCount = rs.getMetaData().getColumnCount();
					String[] headerRow = new String[columnCount];
					for (int s = 0; s < columnCount; s++) {
						headerRow[s] = rs.getMetaData().getColumnName(s + 1);
					}
					bout = new ByteArrayOutputStream();
					pdfReport = new PDFReport(bout, reportTitle);
					pdfReport.createTable(1, columnCount);
					pdfReport.addLogoAndTitle();
					pdfReport.addRow(headerRow);
					rowCount++;
				}
				String[] rowContent = new String[columnCount];
				for (int i = 0; i < columnCount; i++) {
					String content = rs.getString(i + 1);
					if (content == null) {
						content = "";
					}
					rowContent[i] = content;
				}
				pdfReport.addRow(rowContent);
			}
			if (pdfReport != null) {
				pdfReport.closePdfReport();
			}
			byte[] output = bout.toByteArray();
			return output;
		} catch (Throwable e) {
			throw new SaikuServiceException(
					"Error creating pdf export for query", e);
		}
	}
}
