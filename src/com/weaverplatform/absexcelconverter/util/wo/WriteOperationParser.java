package com.weaverplatform.absexcelconverter.util.wo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weaverplatform.absexcelconverter.util.Props;
import com.weaverplatform.absexcelconverter.util.workbook.Workbook;
import com.weaverplatform.absexcelconverter.util.workbook.model.ABSExcel;
import com.weaverplatform.absexcelconverter.util.workbook.model.ABSExcel.ABSColumn;
import com.weaverplatform.absexcelconverter.util.workbook.model.ABSExcel.ABSRow;
import com.weaverplatform.protocol.WeaverError;
import com.weaverplatform.protocol.model.AttributeDataType;
import com.weaverplatform.protocol.model.CreateAttributeOperation;
import com.weaverplatform.protocol.model.CreateNodeOperation;
import com.weaverplatform.protocol.model.CreateRelationOperation;
import com.weaverplatform.protocol.model.WriteOperation;

/**
 * Object used to parse Weaver WriteOperations from Excel Workbook objects.
 * 
 * @author alex
 *
 */
public class WriteOperationParser {

  private static JsonParser jsonParser = new JsonParser();
  public static String DEFAULT_USERNAME = Props.get("parser.default_name");

  /**
   * Parses WriteOperation's based on a provided Weaver Workbook object. It tries
   * to extract an ABSExcel object from that Workbook which forms a base for the
   * WriteOperation objects. When no valid ABSExcel can be extracted, it will
   * throw a WeaverError.
   * 
   * @param workbook
   *          an Weaver Workbook.
   * @return an array containing Weaver WriteOperation.
   */
  public synchronized static WriteOperation[] parseOperations(Workbook workbook) {
    try {
      // First things first extract the ABSExcel from the provided Workbook
      ABSExcel excel = workbook.read();
      // If that succeeded allocate and setup an operations ArrayList which
      // later on will be converted to a WriteOperation array.
      List<WriteOperation> operations = new ArrayList<WriteOperation>();

      // Loop through all the rows in the ABSExcel document.
      for (int i = 0; i < excel.getRowCount(); i++) {
        // And create an PreparedWriteOperation for it, this way we will generate
        // UUIDs for it and we can use these later for dependent rows.
        PreparedWriteOperation pwo = new PreparedWriteOperation(excel.getRow(i));
        // When the pwo isn't dependant we generate the WO directly and insert them
        // into the operations list.
        if (!pwo.isDependant()) {
          for (WriteOperation wo : pwo.toWriteOperations()) {
            operations.add(wo);
          }
          // If the pwo is dependant we first have to get the UUID of the parent
          // row and use that one to generate the rest of the WO, which we later insert
          // into the operations list.
        } else {
          // Easy check, if it's an OCMS parent ID, we just set that as the source.
          // Otherwise we'll retrieve the sourceId from the other pwo.
        }
      }

      // for (String objectName : excel.getValues(ABSColumn.OBJECTNAAM, true)) {
      // String sourceId = uuid();
      // operations.add(createNodeOperation(sourceId));
      // operations.add(createAttributeOperation(sourceId, "hasName", objectName));
      // operations.add(createAttributeOperation(sourceId, "objectStatus", ));
      //
      // // THESE ARE THE KEY NAMES FOR THE MISSING COLUMNS
      // // rdf:type (otl rel)
      //
      // // otl:hasPart (import ID xor OCMS id)
      //
      // // hasSBS
      // // objectStatus
      // // statusX
      // }

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
   * @param workbook
   *          an Weaver Workbook.
   * @return a String containing all the WriteOperations in Json format.
   */
  public synchronized static String parseArray(Workbook workbook) {
    JsonArray array = new JsonArray();
    WriteOperation[] operations = parseOperations(workbook);

    for (WriteOperation wo : operations) {
      JsonObject json = jsonParser.parse(wo.toJson()).getAsJsonObject();
      array.add(json);
    }

    return array.toString();
  }

  private static WriteOperation createNodeOperation(String value) {
    return new CreateNodeOperation(DEFAULT_USERNAME, value);
  }

  private static WriteOperation createAttributeOperation(String uuid, String sourceId, String key, String value) {
    return new CreateAttributeOperation(DEFAULT_USERNAME, uuid, sourceId, key, value, AttributeDataType.STRING);
  }

  private static WriteOperation createRelationOperation(String uuid, String sourceId, String key, String targetId) {
    return new CreateRelationOperation(DEFAULT_USERNAME, uuid, sourceId, key, targetId);
  }

  /**
   * Inner class which 'prepares' an ABSRow to be converted into a WriteOperation.
   * Generally it only generates an UUID beforehand.
   * 
   * @author alex
   *
   */
  public static class PreparedWriteOperation implements Dependency {

    private String sourceId = UUID.randomUUID().toString();
    private ABSRow row;

    public PreparedWriteOperation(ABSRow row) {
      this.row = row;
    }

    /**
     * Delegate method.
     * 
     * @return the Import-ID belonging to this row.
     */
    public int getImportId() {
      return getRow().getImportId();
    }

    public ABSRow getRow() {
      return row;
    }

    public void setSourceId(String sourceId) {
      this.sourceId = sourceId;
    }

    public String getSourceId() {
      return sourceId;
    }

    public WriteOperation[] toWriteOperations() {
      return null;
    }

    @Override
    public boolean isDependant() {
      return !(row.getValue(ABSColumn.IMPORTIDPARENTABS).isEmpty()
          && row.getValue(ABSColumn.OCMSIDPARENTABS).isEmpty());
    }
  }
}
