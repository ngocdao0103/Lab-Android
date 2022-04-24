package com.example.admin.augscan;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelExporter {
    public static String currentTime;
    public static <Int> void export(List<Items> model) {
        File sd = Environment.getExternalStorageDirectory();
        File directory = new File(sd.getAbsolutePath());
        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {
            File file = new File("/storage/emulated/0/Download/", currentTime + "-items.xls");
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale(Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry()));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet itemList = workbook.createSheet("Item List", 0);
//            itemList.addCell(new Label(0, 0, "sheet A 1"));
//            itemList.addCell(new Label(1, 0, "sheet A 2"));
//            itemList.addCell(new Label(0, 1, "sheet A 3"));
//            itemList.addCell(new Label(1, 1, "sheet A 4"));
            itemList.addCell(new Label(0, 0, "Barcode"));
            itemList.addCell(new Label(1, 0, "Name"));
            itemList.addCell(new Label(2, 0, "Quantity"));
            itemList.addCell(new Label(3, 0, "Category"));
            itemList.addCell(new Label(4, 0, "Year"));
            itemList.addCell(new Label(5, 0, "Origin"));
            itemList.addCell(new Label(6, 0, "Status"));

            for (int num = 1; num < model.size(); num++) {
                itemList.addCell(new Label(0, num, model.get(num).getItemBarcode()));
                itemList.addCell(new Label(1, num, model.get(num).getItemName()));
                itemList.addCell(new Label(2, num, model.get(num).getItemPrice()));
                itemList.addCell(new Label(3, num, model.get(num).getItemCategory()));
                itemList.addCell(new Label(4, num, model.get(num).getItemYear()));
                itemList.addCell(new Label(5, num, model.get(num).getItemOrigin()));
                itemList.addCell(new Label(6, num, model.get(num).getItemStatus()));
            }
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}