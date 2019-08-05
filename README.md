UIA Utils extension
================

[![Download](https://api.bintray.com/packages/uia4j/maven/uia-utils-ext/images/download.svg)](https://bintray.com/uia4j/maven/uia-utils-ext/_latestVersion)
[![Build Status](https://travis-ci.org/uiaj4/uia-utils-ext.svg?branch=master)](https://travis-ci.org/uia4j/uia-utils-ext)
[![codecov](https://codecov.io/gh/uia4j/uia-utils-ext/branch/master/graph/badge.svg)](https://codecov.io/gh/uia4j/uia-utils-ext)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4466fb1ecba5481691e8befeacb27d32)](https://www.codacy.com/app/gazer2kanlin/uia-utils-ext?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=uia4j/uia-utils-ext&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/github/license/uia4j/uia-utils-ext.svg)](LICENSE)


Some useful solution for system integration. Need JDK 1.8 or above.

The package incldues 3 sub packages:

* Cube - Multi dimension to query and view data.
* States - __State machine pattern__ implementation.
* DAO - __DAO pattern__ implementation and some helpers to access databases.

## Cube
### key classes
* CubeBuilder
* Cube

```java
CubeBuilder builder = new CubeBuilder();
builder.put("Kyle Lin")
       .addTag("firstName", "Kan")
       .addTag("lasttName", "Lin")
       .addTag("sex", "M")
       .addTag("job", "Engineer");
builder.put("Patrick Lin")
       .addTag("firstName", "Wei")
       .addTag("lasttName", "Lin")
       .addTag("sex", "M")
       .addTag("job", "Manager");
builder.put("Avril Lin")
       .addTag("firstName", "Qin")
       .addTag("lasttName", "Lin")
       .addTag("sex", "F")
       .addTag("job", "Student");
builder.put("Amber Lin")
       .addTag("firstName", "Yue")
       .addTag("lasttName", "Lin")
       .addTag("sex", "F")
       .addTag("job", "Student");
builder.put("Charlotte Chang")
       .addTag("firstName", "Chiayu")
       .addTag("lasttName", "Chang")
       .addTag("sex", "F")
       .addTag("job", "Engineer");
builder.put("Cathy Tsa")
       .addTag("firstName", "Chiahwei")
       .addTag("lasttName", "Tsai")
       .addTag("sex", "F")
       .addTag("job", "Sales");

Cube cube = builder.build();
cube.select("job", "Student"); // Avril, Amber
cube.select("sex", "F").valuesMapping("job"); // Student, Engineer, Sales
```

## States
### key classes
* StateMachine
* AbstractStateWorker

```java
public class MyWokrer extends AbstractStateWorker {

  @Override
  protected void initial() {
    this.stateMachine.register("STATE1")
        .addEvent("E2", (c, a) -> return "STATE2")
        .addEvent("E3", (c, a) -> return "STATE3");
    this.stateMachine.register("STATE2")
        .addEvent("E3", (c, a) -> return "STATE3");
    this.stateMachine.register("STATE3");
  }
}

MyWokrer worker = new MyWokrer();
worker.changeState("STATE1");
worker.run("E2", value);  // STATE2
worker.run("E3", value);  // STATE3
worker.run("E2", value);  // STATE3
```

## DAO
Datebases the API supports are:
* PostgreSQL
* Oracle
* HANA

### Where Statement
#### AND
Example: *c1=? __and__ (c2 between ? and ?) __and__ c3 like ? __and__ c4<>?*
```java
SimpleWhere and = Where.simpleAnd()
    .eq("c1", "abc")
    .between("c2", "123", "456")
    .likeBegin("c3", "abc")
    .notEq("c4", "def");
```

#### OR
Example: *c1=? __or__ (c2 between ? and ?) __or__ c3 like ? __or__ c4<>?*
```java
SimpleWhere or = Where.simpleOr()
    .eqOrNull("c1", "abc")
    .between("c2", "123", "456")
    .likeBeginOrNull("c3", "abc")
    .notEq("c4", "def");
```

#### AND + OR
Example #1: *(A=? __and__ B=?) __or__ (C=? __and__ D=?)*
```java
SimpleWhere and1 = Where.simpleAnd()
        .eq("A", "A1")
        .eq("B", "B1");

SimpleWhere and2 = Where.simpleAnd()
        .eq("C", "C1")
        .eq("D", "D1");

WhereOr where = Where.or(and1, and2);
```

Example #2: *(A=? __or__ B=?) __and__ (C=? __or__ D=?)*
```java
SimpleWhere or1 = Where.simpleOr()
        .eq("A", "A1")
        .eq("B", "B1");

SimpleWhere or2 = Where.simpleOr()
        .eq("C", "C1")
        .eq("D", "D1");

WhereAnd where = Where.and(or1, or2);
```

### Select Statement
Create a __READY TO EXECUTE__ PreparedStatement object.

Example: __*SELECT id,revision,sch_name FROM pms_schedule WHERE state_name=? ORDER BY id*__
```java
// where
SimpleWhere where = Where.simpleAnd()
        .notEq("state_name", "on");

// select
SelectStatement select = new SelectStatement("SELECT id,revision,sch_name FROM pms_schedule")
    	.where(where)
    	.orderBy("id");
try (PreparedStatement ps = select.prepare(conn)) {
    try (ResultSet rs = ps.executeQuery()) {
    	while (rs.next()) {
        	System.out.println(rs.getObject(3));
    	}
    }
}
```

### DAO and DTO generator

#### Generate from Table

```java
Database db = new PostgreSQL(host, port, dbName, user, password);
String dtoPath = "src/java/main/project/db/";     // save path
String daoPath = dtoPath + "dao/";

// table
String tableName = "user_profile";              // table name
String dtoName = CamelNaming.upper(tableName);  // 'UserProfile'
String dtoPackage = "project.db";               // package name
String daoPackage = dtoPackage + ".dao";

// printer
JavaClassPrinter printer = new JavaClassPrinter(db, tableName);
String dto = printer.generateDTO(dtoPackage, dtoName);
String dao = printer.generateDAO(daoPackage, dtoPackage, dtoName);

// save
Files.write(Paths.get(dtoPath + dtoName + ".java"), dto.getBytes());
Files.write(Paths.get(daoPath + dtoName + "Dao.java"), dao.getBytes());
```

#### Generate from View

```java
Database db = new PostgreSQL(host, port, dbName, user, password);
String dtoPath = "src/java/main/project/db/";     // save path
String daoPath = dtoPath + "dao/";

// view
String viewName = "view_user_profile";          // view path
String dtoName = CamelNaming.upper(viewName);   // 'ViewUserProfile'
String dtoPackage = "project.db";               // package name
String daoPackage = dtoPackage + ".dao";

// printer
JavaClassPrinter printer = new JavaClassPrinter(db, viewName);
String dto = printer.generateDTO(dtoPackage, dtoName);
String dao = printer.generateDAO4View(daoPackage, dtoPackage, dtoName);

// save
Files.write(Paths.get(dtoPath + dtoName + ".java"), dto.getBytes());
Files.write(Paths.get(daoPath + dtoName + "Dao.java"), dao.getBytes());
```

## Copyright and License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
