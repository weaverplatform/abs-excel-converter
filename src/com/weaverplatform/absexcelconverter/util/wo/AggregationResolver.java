package com.weaverplatform.absexcelconverter.util.wo;

import java.io.IOException;
import java.nio.charset.Charset;

import com.google.common.io.Resources;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.weaverplatform.protocol.WeaverError;

/**
 * Aggregation between relations of Weaver node's which coher to the
 * OTL-standard have a special identifier. This object is used to lookup this
 * identifier based on sourceId and targetId from that particular relation.
 * 
 * It is a singleton by implementation this way it is for sure the mini.json
 * does not get loaded unnecessarily much into memory.
 * 
 * This class is based on the relation-aggregations-mini.json file which should
 * be in the resources folder.
 * 
 * @author alex
 *
 */
public class AggregationResolver {

  private static AggregationResolver instance = null;
  private volatile JsonArray mapping;

  private AggregationResolver() {
    try {
      JsonParser parser = new JsonParser();
      String data = Resources.toString(Resources.getResource("relation-aggregations-mini.json"),
          Charset.defaultCharset());
      JsonElement element = parser.parse(data);
      mapping = element.getAsJsonArray();
    } catch (IOException e1) {
      throw new WeaverError(-1, "Failed to load the aggregations relation mapping json.");
    }
  }

  public static AggregationResolver getInstance() {
    if (instance == null) {
      instance = new AggregationResolver();
    }
    return instance;
  }

  /**
   * Finds and returns the aggregation id of the given relation.
   * 
   * @param sourceId
   *          the id of the right side of the relation.
   * @param targetId
   *          the id of the left side of the relation.
   * @return a String containing the aggregation id of the relation.
   */
  public synchronized String lookup(String sourceId, String targetId) {
    return retrieveAggregationId(sourceId, targetId).get(2).toString().replaceAll("\"", "");
  }

  /**
   * Loops through the volatile dataset containing all the aggregation id's to
   * find the id matching the sourceId and targetId.
   * 
   * @param sourceId
   *          the id of the right side of the relation.
   * @param targetId
   *          the id of the left side of the relation.
   * @return a JsonArray containing the following info: [0]: the left side
   *         (source)id, [1]: the right side (target)id, [2]: the aggregation is.
   */
  private JsonArray retrieveAggregationId(String sourceId, String targetId) {
    for(JsonElement element : mapping) {
      JsonArray ele = element.getAsJsonArray();
      if(ele.get(0).getAsString().equals("otl:" + targetId) && ele.get(1).getAsString().equals("otl:" + sourceId))
        return ele;
    }
    throw new WeaverError(-1, "No aggregation id found for this relation (source/target): " + sourceId + " " + targetId);
  }

}
