package com.weaverplatform.absexcelconverter.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import com.weaverplatform.absexcelconverter.util.model.Workbook;
import com.weaverplatform.protocol.model.WriteOperation;

public class WriteOperationParser {
  
  public static WriteOperation parse(Workbook workbook) {
    Sheet datatypeSheet = (Sheet) workbook.getSheetAt(0);
    Iterator<Row> iterator = datatypeSheet.iterator();
  
    while (iterator.hasNext()) {
  
        Row currentRow = iterator.next();
        Iterator<Cell> cellIterator = currentRow.iterator();
  
        while (cellIterator.hasNext()) {
  
            Cell currentCell = cellIterator.next();
            //getCellTypeEnum shown as deprecated for version 3.15
            //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
            if (currentCell.getCellTypeEnum() == CellType.STRING) {
                System.out.print(currentCell.getStringCellValue() + "--");
            } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                System.out.print(currentCell.getNumericCellValue() + "--");
            }
  
        }
        System.out.println();
  
    }
    
    return null;
  }

}
