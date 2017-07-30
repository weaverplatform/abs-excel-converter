package com.weaverplatform.absexcelconverter.util.wo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
      // A map to keep track of all created PreparedWriteOperations
      // Sorted on their ImportID integers.
      Map<Integer, PreparedWriteOperation> pwos = new HashMap<Integer, PreparedWriteOperation>();      
      // Loop through all the rows in the ABSExcel document.
      for (int i = 0; i < excel.getRowCount(); i++) {
        // And create an PreparedWriteOperation for it, this way we will generate
        // UUIDs for it and we can use these later for dependent rows.
        PreparedWriteOperation pwo = new PreparedWriteOperation(excel.getRow(i));
        // Add the created pwo to the map.
        pwos.put(pwo.getImportId(), pwo);
        // If the pwo is dependant we first have to get the UUID of the parent
        // row and use that one to generate the rest of the WO, which we later insert
        // into the operations list.
        if (pwo.isDependant()) {
          // Easy check, if it's an OCMS parent ID, we just set that as the source.
          // Otherwise we'll retrieve the nodeId from the mentioned pwo.
          switch (pwo.getDependantType()) {
          case IMPORTIDPARENTABS:
            //String testdebug = pwo.getDependantId();
            //PreparedWriteOperation pwodebug = pwos.get(Integer.parseInt(pwo.getDependantId()));
            
            // In this particular case we are sure (bcuz of the switch) that the
            // getDependantId() will return an Integer wrapped in a String object
            // So we parse it to an Integer.
            String nodeIdFromParent = pwos.get(Integer.parseInt(pwo.getDependantId())).getNodeId();
            pwo.setTargetId(nodeIdFromParent);
            break;
          case OCMSIDPARENTABS:
            pwo.setTargetId(pwo.getDependantId());
            break;
          }
        }
        // After this we generate the WO from the pro and insert them into the
        // operations list.
        for (WriteOperation wo : pwo.toWriteOperations()) {
          operations.add(wo);
        }
      }
      // When that is all done we convert the ArrayList to a normal array
      // and return it to the user.
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

  /**
   * Inner class which 'prepares' an ABSRow to be converted into a WriteOperation.
   * Generally it only generates an UUID beforehand.
   * 
   * @author alex
   *
   */
  public static class PreparedWriteOperation implements Dependency {

    private String nodeId;
    /**
     * This targetId is a hint for this PreparedWriteOperation to indicate it is a
     * dependancy on another node. So when this targetId is set, we actually create
     * a relation to this other node with a keyname otl:hasPart.
     */
    private String targetId;
    private ABSRow row;

    public PreparedWriteOperation(ABSRow row) {
      this.row = row;
      this.nodeId = generateUUID();
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

    public String getNodeId() {
      return nodeId;
    }

    public void setTargetId(String targetId) {
      this.targetId = targetId;
    }

    /**
     * Generates all the WriteOperation's which should semantically describe this
     * PreparedWriteOperation's ABSRow. This method operates as follows, for the
     * ABSRow it holds it: <br>
     * 1st: Creates an new node. <br>
     * 2nd: It creates three attributes, named: hasName, objectStatus, statusX. <br>
     * 3rd: Create one or two relations, named: rdf:type, hasSBS (optional). <br>
     * 4th: Check if the inner targetId is set. If so: <br>
     * 4a: Create an extra relation, named: otl:hasPart. <br>
     * 4b: Otherwise don't create the extra relation. <br>
     * 5th: Return an array containing all these WriteOperations.
     * 
     * @return a WriteOperation array containing all the WriteOperation's which
     *         should cover this PreparedWriteOperation object.
     */
    public WriteOperation[] toWriteOperations() {
      List<WriteOperation> operations = new ArrayList<WriteOperation>();
      operations.add(createNodeOperation(nodeId));
      operations.add(createAttributeOperation(generateUUID(), nodeId, "hasName", getRow().getValue(ABSColumn.OBJECTNAAM)));
      operations.add(createAttributeOperation(generateUUID(), nodeId, "objectStatus", getRow().getValue(ABSColumn.OBJECTSTATUS)));
      operations.add(createAttributeOperation(generateUUID(), nodeId, "statusX", getRow().getValue(ABSColumn.STATUSX)));
      operations.add(createRelationOperation(generateUUID(), nodeId, "rdf:type", getRow().getValue(ABSColumn.OTLTYPEID)));
      // The only optional field, check if it's set.
      if(!getRow().getValue(ABSColumn.SBSID).isEmpty())
        operations.add(createRelationOperation(generateUUID(), nodeId, "hasSBS", getRow().getValue(ABSColumn.SBSID)));
      // Check if targetId is set.
      if(targetId != null)
        operations.add(createRelationOperation(generateUUID(), nodeId, "otl:hasPart", targetId));
      return operations.toArray(new WriteOperation[] {});
    }

    private WriteOperation createNodeOperation(String nodeId) {
      return new CreateNodeOperation(DEFAULT_USERNAME, nodeId);
    }

    private WriteOperation createAttributeOperation(String nodeId, String sourceId, String key, String value) {
      return new CreateAttributeOperation(DEFAULT_USERNAME, nodeId, sourceId, key, value, AttributeDataType.STRING);
    }

    private WriteOperation createRelationOperation(String uuid, String sourceId, String key, String targetId) {
      return new CreateRelationOperation(DEFAULT_USERNAME, uuid, sourceId, key, targetId);
    }
    
    private String generateUUID() {
      return UUID.randomUUID().toString();
    }

    @Override
    public boolean isDependant() {
      return !(row.getValue(ABSColumn.IMPORTIDPARENTABS).isEmpty()
          && row.getValue(ABSColumn.OCMSIDPARENTABS).isEmpty());
    }

    @Override
    public String getDependantId() {
      String value = null;
      if (isDependant()) {
        switch (getDependantType()) {
        case IMPORTIDPARENTABS:
          value = row.getValue(ABSColumn.IMPORTIDPARENTABS);
          break;
        case OCMSIDPARENTABS:
          value = row.getValue(ABSColumn.OCMSIDPARENTABS);
          break;
        }
      }
      return value;
    }

    @Override
    public WriteOperationType getDependantType() {
      WriteOperationType value = null;
      if (isDependant()) {
        // Either one is filled in, never both. Return the appropriate value.
        if (row.getValue(ABSColumn.IMPORTIDPARENTABS).isEmpty()) {
          value = WriteOperationType.OCMSIDPARENTABS;
        } else {
          value = WriteOperationType.IMPORTIDPARENTABS;
        }
      }
      return value;
    }
  }
}
