# excel2wo-ms
Since version 0.1.0 this software got renamed and rewritten. Before version 0.1.0 this software was called abs-excel-converter and served a singular purpose.
It converted an Excel sheet (of static structure) to a set of Weaver write-operations. It was used for a single project, but over time interest grew for this piece of software. More Weaver applications needed some sort of conversion between Excel structures and write-operations. Since version 0.1.0 this software got more generic, using configuration files you can easily define a Excel sheet and how it should map to Weaver write-operations. You could view this readme in it's older state by browsing the 0.0.5 tag.

Relevant JIRA issue: [http://jira.sysunite.com/browse/WS-100](WS-100).

This is a Maven Java project which exposes a REST API (on a configurable port) to communicate with it. The general concept is to upload an excel file and you get a WriteOperation (batch) returned. To see example configuration files to know how to implement different Excel structures please see EXAMPLE.md.

***Version: 0.1.0***

### Configuration
See the **main.properties** file in the **resources** folder and adjust accordingly.

### Usage
To use this product you'll have these routes at your disposal:

##### `[GET] / ` && `[GET] /about `
***Returns***

*Http [200]*

Basic product information.

***Example***
```
{
    "name": "${project.artifactId}",
    "version": "${project.version}",
    "source": "https://github.com/weaverplatform/abs-excel-converter"
}
```
##### `[GET] /connection `
***Returns***

*Http [204]*

Informing the user about the status of the connection.
##### `[POST] /upload `
***Returns***

*Http [200]*

An array containing one or more Weaver WriteOperations in JSON format.

***Example***
```
[{
	<WriteOperation JSON's>
}, ... ]
```

*Http [400]*

When a bad request occured, an invalid ABS Excel sheet was provided. (see the issue
a valid and an invalid example of ABS Excel sheet structures, not the header column).

***Example***
```
{
	"code": 333,
	"message": "Invalid ABS Excel structure."
}
```
