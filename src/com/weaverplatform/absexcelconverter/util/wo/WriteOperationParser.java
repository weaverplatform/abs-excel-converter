package com.weaverplatform.absexcelconverter.util.wo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weaverplatform.absexcelconverter.util.workbook.Workbook;
import com.weaverplatform.absexcelconverter.util.workbook.model.ABSExcel;
import com.weaverplatform.absexcelconverter.util.workbook.model.ABSExcel.ABSColumn;
import com.weaverplatform.protocol.WeaverError;
import com.weaverplatform.protocol.model.AttributeDataType;
import com.weaverplatform.protocol.model.CreateAttributeOperation;
import com.weaverplatform.protocol.model.CreateNodeOperation;
import com.weaverplatform.protocol.model.WriteOperation;

/**
 * Object used to parse Weaver WriteOperations from Excel Workbook
 * objects.
 * 
 * @author alex
 *
 */
public class WriteOperationParser {
  
  private static JsonParser jsonParser = new JsonParser();
  public static String DEFAULT_USERNAME     = "COMPOSED-BY-EXCEL";

  /**
   * Parses WriteOperation's based on a provided Weaver Workbook object. It tries
   * to extract an ABSExcel object from that Workbook which forms a base for the
   * WriteOperation objects. When no valid ABSExcel can be extracted, it will
   * throw a WeaverError.
   * 
   * @param workbook an Weaver Workbook.
   * @return an array containing Weaver WriteOperation.
   */
  public synchronized static WriteOperation[] parseOperations(Workbook workbook) {
    try {
      ABSExcel excel = workbook.read();
      List<WriteOperation> operations = new ArrayList<WriteOperation>();
      
      for(String objectName : excel.getValues(ABSColumn.OBJECTNAAM, true)) {
        String sourceId = uuid();
        operations.add(createNodeOperation(sourceId));
        operations.add(createAttributeOperation(sourceId, "hasName", objectName));
      }
      
      return operations.toArray(new WriteOperation[] {});
    } catch (WeaverError we) {
      // Rethrow the error
      throw we;
    }
  }
  
  /**
   * Parses WriteOperation's based on a provided Weaver Workbook object. It tries
   * to extract an ABSExcel object from that Workbook which forms a base for the
   * WriteOperation objects. When no valid ABSExcel can be extracted, it will
   * throw a WeaverError.
   * 
   * @param workbook an Weaver Workbook.
   * @return a String containing all the WriteOperations in Json format.
   */
  public synchronized static String parseArray(Workbook workbook) {
    JsonArray array = new JsonArray();
    WriteOperation[] operations = parseOperations(workbook);
    
    for(WriteOperation wo : operations) {
      JsonObject json = jsonParser.parse(wo.toJson()).getAsJsonObject();
      array.add(json);
    }
    
    return array.toString();
  }
  
  private static WriteOperation createNodeOperation(String value) {
    return new CreateNodeOperation(DEFAULT_USERNAME, value);
  }
  
  private static WriteOperation createAttributeOperation(String sourceId, String key, String value) {
    return new CreateAttributeOperation(DEFAULT_USERNAME, uuid(), sourceId, key, value, AttributeDataType.STRING);
  }
  
  private static String uuid() {
    return UUID.randomUUID().toString();
  }
}
