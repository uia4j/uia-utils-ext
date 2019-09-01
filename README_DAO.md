DAO Simple Solution
================
The API provides two simple solutions:
* Statement Builder
* DAO Pattern Builder

Datebases the API supports are:
* PostgreSQL
* Oracle
* HANA

## Statement Builder
### AND
Example: *c1=? __and__ (c2 between ? and ?) __and__ c3 like ? __and__ c4<>?*
```java
SimpleWhere and = Where.simpleAnd()
    .eq("c1", "abc")
    .between("c2", "123", "456")
    .likeBegin("c3", "abc")
    .notEq("c4", "def");
```

### OR
Example: *c1=? __or__ (c2 between ? and ?) __or__ c3 like ? __or__ c4<>?*
```java
SimpleWhere or = Where.simpleOr()
    .eqOrNull("c1", "abc")
    .between("c2", "123", "456")
    .likeBeginOrNull("c3", "abc")
    .notEq("c4", "def");
```

### AND + OR
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

## Select Statement
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

## DAO Pattern Builder
You can also do same things below using `DatabaseTool` class.

### Generate from Table
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

### Generate from View
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
