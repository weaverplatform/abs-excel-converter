package com.weaverplatform.absexcelconverter.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

import com.google.common.io.Resources;

public class FileTest extends BaseTest {

  @Test
  public void uploadTest() throws URISyntaxException {

    given()
      .multiPart(new File(Resources.getResource("abs_import_sheet(valid).xlsx").toURI()))
      .expect()
      .statusCode(200)
      .body(equalTo("OK"))
      .when()
      .post("http://localhost:4567/upload");

  }

}
