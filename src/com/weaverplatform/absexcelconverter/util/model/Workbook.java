package com.weaverplatform.absexcelconverter.util.model;

import java.io.IOException;

import javax.servlet.http.Part;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Workbook extends XSSFWorkbook {
  
  public Workbook(Part file) throws IOException {
    super(file.getInputStream());
  }
  
}
