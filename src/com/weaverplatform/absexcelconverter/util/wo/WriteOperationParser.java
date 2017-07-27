package com.weaverplatform.absexcelconverter.util.wo;

import java.util.ArrayList;
import java.util.List;

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
      List<WriteOperation> operations = new ArrayList<WriteOperation>();
      for(String objectNaam : excel.getValues(ABSColumn.OBJECTNAAM, true)) {
        operations.add(createNodeOperation(objectNaam));
      }
      
      return operations.toArray(new WriteOperation[] {});
    } catch (WeaverError we) {
      // Rethrow the error
      throw we;
    }
  }
  
  private static WriteOperation createNodeOperation(String value) {
    // TODO: for tomorrow...
    return null;
  }
}
