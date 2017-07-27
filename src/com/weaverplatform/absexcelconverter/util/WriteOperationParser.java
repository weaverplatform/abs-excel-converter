package com.weaverplatform.absexcelconverter.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.weaverplatform.absexcelconverter.util.model.Workbook;
import com.weaverplatform.protocol.model.WriteOperation;

public class WriteOperationParser {

  private volatile static List<String[]> data;

  public synchronized static WriteOperation parse(Workbook workbook) {
    String[][] data = read(workbook);
    writeOut(data);
    return null;
  }
  //todo move this logic to Workbook
  //todo create an interface to let a class
  //determine is the supplied excel sheet is
  //according to protocol and implement this logic in that class
  //todo parse writeoperations in this class based on a valid
  //excel sheet (the interface should have methods like
  //get importId and getObjectnaam and such, according to columns
  //enzo
  private static String[][] read(Workbook workbook) {
    data = new ArrayList<String[]>();
    Sheet datatypeSheet = (Sheet) workbook.getSheetAt(0);
    // This decides the length of each and every row following it
    int lengthOfRowOne = datatypeSheet.getRow(0).getLastCellNum();
    Iterator<Row> iterator = datatypeSheet.iterator();

    while (iterator.hasNext()) {
      Row currentRow = iterator.next();
      List<String> cells = new ArrayList<String>();

      for (int cn = 0; cn < lengthOfRowOne; cn++) {
        Cell currentCell = currentRow.getCell(cn);
        if (currentCell == null) {
          cells.add("");
        } else {
          if (currentCell.getCellTypeEnum() == CellType.STRING) {
            cells.add(currentCell.getStringCellValue());
          } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
            cells.add("" + currentCell.getNumericCellValue());
          }
        }
      }
      data.add(cells.toArray(new String[] {}));
    }
    return data.toArray(new String[][] {});
  }
  
  //To be removed, debugging purposes only
  private static void writeOut(String[][] data) {
    for(String[] row : data) {
      for(String str : row) {
        System.out.print(str + "--");
      }
      System.out.println();
    }
  }

}
