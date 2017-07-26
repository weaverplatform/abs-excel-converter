package com.weaverplatform.absexcelconverter.controllers;

import com.google.gson.Gson;
import com.weaverplatform.absexcelconverter.util.Props;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 *
 * @author alex
 *
 */
public class ApplicationController {

	/**
	 * Displays some basic data about the server.
	 */
	public static Route about = (Request req, Response res) -> new About().toJson();

  static class About {

    @SuppressWarnings("unused")
    private String name, version, source;

    public About() {
      this.name    = Props.get("application.name");
      this.version = Props.get("application.version");
      this.source  = Props.get("application.source");
    }

    public String toJson(){
      return new Gson().toJson(this);
    }
  }
}
