package com.mfino.report.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class PDFReportEvents extends PdfPageEventHelper {

	int pagenumber = 0;
	Phrase phrase;
	PdfContentByte pdfcontentByte;

	@Override
	public void onOpenDocument(PdfWriter writer, Document document) {
		this.pdfcontentByte = writer.getDirectContent();
	}

	@Override
	public void onStartPage(PdfWriter writer, Document document) {
		this.pagenumber++;
		Font font = new Font(Font.TIMES_ROMAN, 12);
		font.setStyle("italic");
		this.phrase = new Phrase("Page " + this.pagenumber, font);
		Rectangle rect = writer.getBoxSize("art");

		float x = rect.getRight() - 100;
		float y = rect.getTop() - 50;
		ColumnText.showTextAligned(this.pdfcontentByte, Element.ALIGN_RIGHT,
				this.phrase, x, y, 0);
	}

	@Override
	public void onCloseDocument(PdfWriter writer, Document document) {
		Font font = new Font(Font.TIMES_ROMAN, 12);
		font.setStyle("italic");
		this.phrase = new Phrase(" of " + this.pagenumber, font);
		Rectangle rect = writer.getBoxSize("art");
		float x = rect.getRight() - 80;
		float y = rect.getTop() - 50;
		ColumnText.showTextAligned(this.pdfcontentByte, Element.ALIGN_RIGHT,
				this.phrase, x, y, 0);

	}

}
