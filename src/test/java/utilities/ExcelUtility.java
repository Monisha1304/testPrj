package utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ExcelUtility {
    public FileInputStream fileInputStream;
    public FileOutputStream fileOutputStream;
    public File file;
    public XSSFWorkbook workbook;
    public XSSFSheet sheet;
    public XSSFRow row;
    public XSSFCell cell;
    public CellStyle cellStyle;
    public String filePath;

    public ExcelUtility(String filePath){
        this.filePath=filePath;
    }

    public XSSFWorkbook loadWorkbook(String filePath) throws IOException {
        fileInputStream=new FileInputStream(filePath);
        workbook=new XSSFWorkbook(fileInputStream);
        return workbook;
    }

    public String formatCellValue(XSSFCell cell){
        DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(cell);
    }
    public int getRowCount(String sheetName) throws IOException {
        workbook=loadWorkbook(filePath);
        sheet=workbook.getSheet(sheetName);
        int rowCount=sheet.getLastRowNum();
        workbook.close();
        fileInputStream.close();
        return rowCount;
    }

    public int getCellCount(String sheetName,int rowNum) throws IOException {
        workbook=loadWorkbook(filePath);
        sheet=workbook.getSheet(sheetName);
        row=sheet.getRow(rowNum);
        int cellCount=row.getLastCellNum();
        workbook.close();
        fileInputStream.close();
        return cellCount;
    }

    public String getCellData(String sheetName,int rowNum,int cellNum) throws IOException {
        workbook=loadWorkbook(filePath);
        sheet=workbook.getSheet(sheetName);
        row=sheet.getRow(rowNum);
        cell=row.getCell(cellNum);
        String data= formatCellValue(cell);
        workbook.close();
        fileInputStream.close();
        return data;
    }

    public void setCellData(String sheetName,int rowNum,int cellNum,String data) throws IOException {
        file=new File(filePath);
        if(!file.exists()){ //file not exists create a file
            workbook=new XSSFWorkbook();
            fileOutputStream=new FileOutputStream(file);
            workbook.write(fileOutputStream);
        }
        fileInputStream=new FileInputStream(file);
        workbook=new XSSFWorkbook(fileInputStream);

        if(workbook.getSheetIndex(sheetName)==-1){
            sheet=workbook.createSheet(sheetName);
        };
        sheet=workbook.getSheet(sheetName);
        if(sheet.getRow(rowNum)==null){
            row=sheet.createRow(rowNum);
        };
        row=sheet.getRow(rowNum);
        cell=row.createCell(cellNum);
        cell.setCellValue(data);

        fileOutputStream=new FileOutputStream(file);
        workbook.write(fileOutputStream);
        workbook.close();
        fileInputStream.close();
        fileOutputStream.close();

    }

    public void fillGreenColor(String sheetName,int rowNum,int cellNum) throws IOException {
        workbook=loadWorkbook(filePath);
        sheet=workbook.getSheet(sheetName);
        row=sheet.getRow(rowNum);
        cell=row.getCell(cellNum);

        cellStyle=workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cell.setCellStyle(cellStyle);

        fileOutputStream=new FileOutputStream(file);
        workbook.write(fileOutputStream);
        workbook.close();
        fileInputStream.close();
        fileOutputStream.close();
    }

    public void fillRedColor(String sheetName,int rowNum,int cellNum) throws IOException {
        workbook=loadWorkbook(filePath);
        sheet=workbook.getSheet(sheetName);
        row=sheet.getRow(rowNum);
        cell=row.getCell(cellNum);

        cellStyle=workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cell.setCellStyle(cellStyle);

        fileOutputStream=new FileOutputStream(file);
        workbook.write(fileOutputStream);
        workbook.close();
        fileInputStream.close();
        fileOutputStream.close();
    }


}
