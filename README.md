# abs-excel-converter
This microservice is meant to convert excel sheets in a particular format (see this [issue](http://jira.sysunite.com/browse/RC-77)) to Weaver WriteOperations. These WriteOperations can be used to inject the excel structure into a weaver-database.

This is a Maven Java project which exposes a REST API (on a configurable port) to communicate with it. The general concept is to upload an excel file and you get a WriteOperation (batch) returned.

***Version: 0.0.1-SNAPSHOT***

### Configuration
See the **main.properties** file in the **resources** folder and adjust accordingly.

### Usage
To use this product you'll have these routes at your disposal:

##### `[GET] / `
***Returns***

Basic product information.

***Example***
```
{
    "name": "${project.artifactId}",
    "version": "${project.version}",
    "source": "https://github.com/weaverplatform/abs-excel-converter"
}
```