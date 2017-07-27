package com.weaverplatform.absexcelconverter.util.wo;

import com.weaverplatform.absexcelconverter.util.workbook.Workbook;
import com.weaverplatform.absexcelconverter.util.workbook.model.ABSExcel;
import com.weaverplatform.absexcelconverter.util.workbook.model.ABSExcel.ABSColumn;
import com.weaverplatform.protocol.WeaverError;
import com.weaverplatform.protocol.model.WriteOperation;

/**
 * Object used to parse Weaver WriteOperations.
 * 
 * @author alex
 *
 */
public class WriteOperationParser {

  /**
   * Parses WriteOperation's based on a provided Weaver Workbook object. It tries
   * to extract an ABSExcel object from that Workbook which forms a base for the
   * WriteOperation objects. When no valid ABSExcel can be extracted, it will
   * throw a WeaverError.
   * 
   * @param workbook
   * @return an array containing Weaver WriteOperation.
   */
  public synchronized static WriteOperation[] parse(Workbook workbook) {
    try {
      ABSExcel excel = workbook.read();
      
      String[] val1 = excel.getValues(ABSColumn.OBJECTNAAM, false);
      String[] val2 = excel.getValues(ABSColumn.OBJECTNAAM, true);
      System.out.println(excel.getValue(ABSColumn.OBJECTNAAM, 2));
    } catch (WeaverError we) {
      // Rethrow the error
      throw we;
    }
    return null;
  }
}
