package com.weaverplatform.absexcelconverter.util.workbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.Part;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.weaverplatform.absexcelconverter.util.workbook.model.ABSExcel;

/**
 * Custom Workbook object especially designed to work with ABS
 * Excel sheets. This object has not been optimized for large
 * Excel sheets (the raw() method from the
 * Validator is ran more than once). Please use cautious, or see
 * the author for more information.
 * @author alex
 *
 */
public class Workbook extends XSSFWorkbook implements Validator {
    
  public Workbook(Part file) throws IOException {
    super(file.getInputStream());
  }
  
  @Override
  public boolean check() {
    return true;
  }
  
  @Override
  public ABSExcel read() throws NullPointerException {
    if(check()) {
      return new ABSExcel(raw());
    }
    return null;
  }
  
  @Override
  public String[][] raw() {
    List<String[]> data = new ArrayList<String[]>();
    Sheet datatypeSheet = (Sheet)getSheetAt(0);
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
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for(String[] row : raw()) {
      for(String str : row) {
        sb.append(str + "--");
      }
      sb.append("\n");
    }
    return sb.toString();
  }
}
