package com.weaverplatform.absexcelconverter.util.wo;

/**
 * Interface to determine said object is dependant on something else, which
 * should be resolved first.
 * 
 * @author alex
 *
 */
public interface Dependency {
  
  /**
   * Indicates wheter this object is dependant on any other object.
   * @return
   */
  boolean isDependant();

}
