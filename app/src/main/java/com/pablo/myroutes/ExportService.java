package com.pablo.myroutes;

import android.os.Environment;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

class ExportService {

    private static HSSFWorkbook workbook;
    private static Sheet sheet;
    private static Row row;
    private static Cell cell;

    static void Export(RoutingDay[] routingDayListForExport) throws Exception {

        for (RoutingDay day : routingDayListForExport) {//int i =0;i<routingDayListForExport.size();i++){
            Export(day);
        }
    }
    //TODO: поменять первую страницу
    static void Export(RoutingDay day) throws Exception {
        int rowIndex = 4;
        OpenWorkbook("1");
        sheet = workbook.getSheetAt(0);
        row = sheet.getRow(4);
        cell = row.getCell(29);
        cell.setCellValue(day.date.split(" ")[0]);
        cell = row.getCell(34);
        cell.setCellValue(day.date.split(" ")[1]);

        row = sheet.getRow(18);
        cell = row.getCell(65);
        cell.setCellValue(day.getKilometrageOnBeginningDay());

        row = sheet.getRow(44);
        cell = row.getCell(65);
        cell.setCellValue(day.getKilometrageOnEndingDay());

        SaveWorkbook(day.date + "(1)");

        OpenWorkbook("2");
        Sheet sheet = workbook.getSheetAt(0);
        for (int j = 0; j < day.getListOfRoutes().size(); j++) {
            row = sheet.getRow(rowIndex);
            cell = row.getCell(14);
            cell.setCellValue(day.getListOfRoutes().get(j).getStartPoint());
            cell = row.getCell(32);
            cell.setCellValue(day.getListOfRoutes().get(j).getEndPoint());
            cell = row.getCell(50);
            cell.setCellValue(day.getListOfRoutes().get(j).getStartTime().split(":")[0]);
            cell = row.getCell(54);
            cell.setCellValue(day.getListOfRoutes().get(j).getStartTime().split(":")[1]);
            cell = row.getCell(58);
            cell.setCellValue(day.getListOfRoutes().get(j).getEndTime().split(":")[0]);
            cell = row.getCell(62);
            cell.setCellValue(day.getListOfRoutes().get(j).getEndTime().split(":")[1]);
            cell = row.getCell(66);
            cell.setCellValue(day.getListOfRoutes().get(j).getLength());

            rowIndex++;
        }
        SaveWorkbook(day.date + "(2)");
    }

    private static void OpenWorkbook(String filename) throws Exception {
        File file = new File(Environment.getExternalStorageDirectory(), filename + ".xls");
        FileInputStream fis = new FileInputStream(file);
        workbook = new HSSFWorkbook(fis);
        fis.close();
    }
    //TODO: сохранение в папки по месяцам
    private static void SaveWorkbook(String filename) throws Exception {
        FileOutputStream fs = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), filename + ".xls"));
        workbook.write(fs);
        workbook.close();
        fs.close();
    }
}
