package com.weaverplatform.absexcelconverter.util.wo;

import com.weaverplatform.absexcelconverter.util.workbook.Workbook;
import com.weaverplatform.absexcelconverter.util.workbook.model.ABSExcel;
import com.weaverplatform.absexcelconverter.util.workbook.model.ABSExcel.RowType;
import com.weaverplatform.protocol.WeaverError;
import com.weaverplatform.protocol.model.WriteOperation;

public class WriteOperationParser {

  public synchronized static WriteOperation parse(Workbook workbook) {
    try {
      ABSExcel excel = workbook.read();
      System.out.println(excel.getColumn(RowType.OBJECTNAAM, 2));
    } catch(NullPointerException npe) {
      throw new WeaverError(WeaverError.DATATYPE_UNSUPPORTED, "Could not read the ABS Excek workbook.");
    }
    return null;
  }
  //determine is the supplied excel sheet is
  //todo parse writeoperations in this class based on a valid
}
