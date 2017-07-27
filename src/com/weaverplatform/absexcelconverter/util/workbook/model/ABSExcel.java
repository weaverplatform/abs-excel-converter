package com.weaverplatform.absexcelconverter.util.workbook.model;

/**
 * An object containing all the data provided in an Excel sheet.
 * Containing methods and enumarations to iterate over the provided
 * data.
 * 
 * @author alex
 *
 */
public class ABSExcel {

  public enum RowType {
    IMPORTID, OBJECTNAAM, OTLTYPEID, IMPORTIDPARENTABS, OCMSIDPARENTABS, SBSID, OBJECTSTATUS, STATUSX
  }

  private String[][] rows;

  public ABSExcel(String[][] rows) {
    this.rows = rows;
  }

  public String[] getRow(int row) {
    return rows[row];
  }

  public String getColumn(RowType type, int row) {
    return rows[row][type.ordinal()];
  }

}
