DAO Simple Solution
================
The API provides three simple solutions:
* DAO Factory API
* Statement Builder
* Simple DAO Builder

Datebases the API supports are:
* PostgreSQL
* Oracle
* HANA

## DAO Factory API
Generate CRUD SQL statements depending on Annotations in the DTO class. 

* Focus on the design of DTO classes only. No XML, no configuration.
* No need to modify the SQL statement.
* No need to implement standard CRUD methods.
* Least implementation of DAO classes.

### Table
#### Key Points
* TableInfo - annotate a class for a table.
* ColumnInfo - annotate attributes for the columns.
* DaoTableDao<T> - generic DAO for a table.

#### Example
1. Define a DTO for a table.

    ```java
    package a.b.c;

    @TableName(name = "job_detail")
    public class JobDetail {

        @ColumnName(name = "id", primaryKey = true)
        private Stirng id;

        @ColumnName(name = "job_id")
        private String jobId;

        @ColumnName(name = "job_detail_name")
        private String jobDetailName;
    }
    ```
2. Load DTO classes into the factory.

    ```java
    DaoFactory factory = new DaoFactory();
    factory.load("a.b.c");
    ```

3. CRUD

    ```java
    DaoTable<JobDetail> daoTable = factory.forTable(JobDetail.class);
    DaoTableDao<JobDetail> dao = new DaoTableDao(conn, daoTable);
    dao.insert(...);
    dao.udpate(...);
    dao.deleteByPK(...);
    List<JobDetail> result = dao.selectAll();
    JobDetail one = dao.selectByPK(...);
    ```

### Custom DAO
1. Inherit from DaoTableDao<T>

    ```java
    public class JobDetailDao exttends DaoTableDao<JobDetail> {

        public JobDetailDetail(Connection conn) {
            super(conn, factory.forTable(JobDetail.class));
        }
    }
    ```

2. Implement a custom `SELECT` statement

    ```java
    public List<JobDtail> selectByName(String name) {
        DaoMethod<JobDtail> method = this.daoTable.forSelect();
        try(PreparedStatement ps = this.conn.prepareStatement(method.getSql() + "WHERE job_detail_name=? ORDER BY id")) {
            ps.setString(1, name);
            try(ResultSet rs = ps.executeQuery()) {
                return method.toList(rs);
            }
        }
    }
    ```


### View
#### Annotations
* ViewInfo - annotate a class for a view.
* ColumnInfo - annotate attributes for the columns.
* DaoViewDao<T> - generic DAO for a view.

#### Example
1. Define a DTO for a view.

    ```java
    package a.b.c;

    @ViewName(name = "view_job_detail")
    public class ViewJobDetail extends JobDetail {

        @ColumnName(name = "job_name")
        private String jobName;
    }
    ```
2. Load DTO classes into the factory.

    ```java
    DaoFactory factory = new DaoFactory();
    factory.load("a.b.c");
    ```

3. Query

    ```java
    DaoView<ViewJobDetail> daoView = factory.forView(ViewJobDetail.class);
    DaoViewDao<ViewJobDetail> dao = new DaoViewDao(conn, daoView);
    List<ViewJobDetail> result = dao.selectAll();
    ```


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

## Simple DAO Builder
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
SimpleDaoClassPrinter printer = new SimpleDaoClassPrinter(db, tableName);
String dto = printer.generateDTO(dtoPackage, dtoName);
String dao = printer.generateDAO4Table(daoPackage, dtoPackage, dtoName);

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
SimpleDaoClassPrinter printer = new SimpleDaoClassPrinter(db, viewName);
String dto = printer.generateDTO(dtoPackage, dtoName);
String dao = printer.generateDAO4View(daoPackage, dtoPackage, dtoName);

// save
Files.write(Paths.get(dtoPath + dtoName + ".java"), dto.getBytes());
Files.write(Paths.get(daoPath + dtoName + "Dao.java"), dao.getBytes());
```
