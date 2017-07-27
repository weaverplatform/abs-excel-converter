package com.weaverplatform.absexcelconverter.util.workbook.model;

/**
 * A model containing all the data provided in an ABS Excel sheet.
 * Containing methods and enumarations to iterate over the provided
 * data.
 * 
 * @author alex
 *
 */
public class ABSExcel {

  public enum ABSRow {
    IMPORTID, OBJECTNAAM, OTLTYPEID, IMPORTIDPARENTABS, OCMSIDPARENTABS, SBSID, OBJECTSTATUS, STATUSX
  }

  private String[][] rows;

  public ABSExcel(String[][] rows) {
    this.rows = rows;
  }

  public String[] getRow(int row) {
    return rows[row];
  }

  public String getColumn(ABSRow type, int row) {
    return rows[row][type.ordinal()];
  }

}
