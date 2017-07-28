package com.weaverplatform.absexcelconverter.util.workbook.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A model containing all the data provided in an ABS Excel sheet. Containing
 * methods and enumarations to iterate over the provided data.
 * 
 * An ABSExcel sheet fundamentally is a two dimensional table with a header row.
 * In this table data is stored to generate Weaver WriteOperation. This object
 * should be used like prepared statements are used. For example if you want to
 * retrieve row 2 from the sheet, you call getRow(1), ignoring the zeroth row,
 * which is the header row. To make this example more concrete, consider the
 * attachment in this issue: http://jira.sysunite.com/browse/RC-77, to get row 2
 * counting from Import-ID's, you call getRow(1);.
 * 
 * @author alex
 *
 */
public class ABSExcel {

  public enum ABSColumn {
    IMPORTID, OBJECTNAAM, OTLTYPEID, IMPORTIDPARENTABS, OCMSIDPARENTABS, SBSID, OBJECTSTATUS, STATUSX
  }

  private String[][] rows;

  public ABSExcel(String[][] rows) {
    this.rows = rows;
  }

  /**
   * Selects and returns the requested row.
   * 
   * @param row
   *          which row to return.
   * @return an ABSRow containing all the data from the selected row.
   */
  public ABSRow getRow(int row) {
    return new ABSRow(rows[row + 1]);
  }

  /**
   * Counts and returns the number of rows of data. Note: this is excluding the
   * header row with data.
   * 
   * @return the total number of rows of data.
   */
  public int getRowCount() {
    return rows.length - 1;
  }

  /**
   * Retrieves a single value from an intersection in this Excel sheet.
   * 
   * @param type
   *          the y-value of the intersection.
   * @param row
   *          the x-value of the intersection.
   * @return data that is stored in that particular cell in this ABSExcel sheet.
   */
  public String getValue(ABSColumn type, int row) {
    return rows[row + 1][type.ordinal()];
  }

  /**
   * Retrieves non-empty values from columns as selected in the where parameter of
   * an ABSExcel sheet.
   * 
   * @param excel
   *          the ABSExcel sheet to read through.
   * @param where
   *          the condition to meet, read as; which non-empty values should be
   *          returned from what column.
   * @param filter
   *          indication to filter non-empty values. True to filter, false to
   *          incluse empty values.
   * @return
   */
  public String[] getValues(ABSColumn where, boolean filter) {
    List<String> data = new ArrayList<String>();
    // We do add an empty value tho for this header field,
    // but only if filter is not set.
    // This way we stay true to it's design principle.
    if (!filter)
      data.add("");
    for (int i = 0; i < getRowCount(); i++) {
      String value = getValue(where, i);
      if (filter && value.isEmpty()) {
        continue;
      }
      data.add(value);
    }
    return data.toArray(new String[] {});
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String[] row : rows) {
      for (String str : row) {
        sb.append(str + "--");
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * Small inner class to replace String[] type for rows in an ABSExcel.
   * 
   * @author alex
   *
   */
  public class ABSRow {
    private String[] row;

    public ABSRow(String[] row) {
      this.row = row;
    }

    /**
     * Retrieve a value from a column in this row.
     * 
     * @param column
     * @return
     */
    public String getValue(ABSColumn column) {
      return row[column.ordinal()];
    }

    /**
     * Helper method to quickly identify this rows import id.
     * 
     * @return
     */
    public int getImportId() {
      return Integer.parseInt(getValue(ABSColumn.IMPORTID));
    }
  }

}
