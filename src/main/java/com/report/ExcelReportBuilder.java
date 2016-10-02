package com.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.sla.SLAData;
import com.sla.SLAUtils;

public class ExcelReportBuilder {
	
	private Workbook wb;
	private CreationHelper helper;
	private Sheet sheet;
	private CellStyle border;
	
	public ExcelReportBuilder() {
		wb = new HSSFWorkbook();
		helper = wb.getCreationHelper();
		sheet = wb.createSheet("SLA Evaluation");
	}
	
	private void createBorder() {
		border = wb.createCellStyle();
		border.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		border.setBorderTop(HSSFCellStyle.BORDER_THIN);
		border.setBorderRight(HSSFCellStyle.BORDER_THIN);
		border.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	}

	public void createExcelReport(List<SLAData> slaDataList) throws IOException {
		
		createHeaderForExcel();
		int rowCount =1;
		for(SLAData data:slaDataList) {
			createRowForSingleObject(data, rowCount);
			rowCount++;
		}
		
		FileOutputStream fileOut = new FileOutputStream("log/SlaReport.xls");
		if(fileOut != null) {
			wb.write(fileOut);
			fileOut.close();
		}
	}

	private void createRowForSingleObject(SLAData data, int rowCount) {
		Row row = sheet.createRow(rowCount);
		int cellcount = 0;
		
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.project));
		cellcount++;
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.issueId));
		cellcount++;
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.priority));
		cellcount++;
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.IssueType));
		cellcount++;
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.detectionPhase));
		cellcount++;
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.issueDescription));
		cellcount++;
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.assignee));
		cellcount++;
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.status));
		cellcount++;
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.remainingTimeFirstResponse));
		cellcount++;
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.remainingTimeFixResponse));
		cellcount++;
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.timeSpentFixResponse));
		cellcount++;
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.HoldTime));
		cellcount++;
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.holidays+""));
		cellcount++;
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.firstResponseOk));
		cellcount++;
		row.createCell(cellcount).setCellValue(helper.createRichTextString(data.fixResponseOk));
		cellcount++;
		
		/*setting font*/
		createBorder();
		if(data.firstResponseOk.equalsIgnoreCase("NOK") || data.fixResponseOk.equalsIgnoreCase("NOK")) {
			border.setFillForegroundColor(IndexedColors.RED.getIndex());
			border.setFillPattern(CellStyle.SOLID_FOREGROUND);
		}
		
		for(int indx=0;indx<cellcount;indx++){
			row.getCell(indx).setCellStyle(border);
		}
		
	}

	private void createHeaderForExcel() {
		Row row = sheet.createRow(0);
		int count = 0;
		for(String heading:SLAUtils.ReportHeading){
			row.createCell(count).setCellValue(helper.createRichTextString(heading));
			count++;
		}
		
		createBorder();
		Font font = wb.createFont();
		font.setBold(true);
		border.setFont(font); 
		
		for(int indx=0;indx<count;indx++){
			row.getCell(indx).setCellStyle(border);
		}
		
	}
	
}
