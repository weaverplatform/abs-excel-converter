package com.weaverplatform.absexcelconverter.util.wo;

import com.weaverplatform.absexcelconverter.util.workbook.Workbook;
import com.weaverplatform.absexcelconverter.util.workbook.model.ABSExcel;
import com.weaverplatform.absexcelconverter.util.workbook.model.ABSExcel.ABSRow;
import com.weaverplatform.protocol.WeaverError;
import com.weaverplatform.protocol.model.WriteOperation;

public class WriteOperationParser {

  public synchronized static WriteOperation parse(Workbook workbook) {
    try {
      ABSExcel excel = workbook.read();
      System.out.println(excel.getColumn(ABSRow.OBJECTNAAM, 2));
    } catch (WeaverError we) {
      // Rethrow the error
      throw we;
    }
    return null;
  }
}
