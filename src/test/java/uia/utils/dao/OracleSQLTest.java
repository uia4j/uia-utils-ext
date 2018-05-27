package uia.utils.dao;

import org.junit.Test;

import uia.utils.dao.hana.Hana;
import uia.utils.dao.ora.Oracle;
import uia.utils.dao.pg.PostgreSQL;

public class OracleSQLTest {

    @Test
    public void testSelectTableNames() throws Exception {
        Database db = new Oracle("10.160.1.48", "1521", "MESDEV", "WIP", "wip");
        db.selectTableNames("ZD_").forEach(t -> System.out.println(t));
        db.close();
    }

    @Test
    public void testSelectViewNames() throws Exception {
        Database db = new Oracle("10.160.1.48", "1521", "MESDEV", "WIP", "wip");
        db.selectViewNames("VIEW_").forEach(t -> System.out.println(t));
        db.close();
    }

    @Test
    public void testSelectTable() throws Exception {
        Database db = new Oracle("10.160.1.48", "1521", "MESDEV", "WIP", "wip");

        TableType table = db.selectTable("ZR_TEST", true);
        System.out.println(table.getTableName());
        table.getColumns().forEach(System.out::println);
        System.out.println(table.generateInsertSQL());
        System.out.println(table.generateUpdateSQL());
        System.out.println(table.generateSelectSQL());

        db.close();
    }

    @Test
    public void testSelectView() throws Exception {
        Database db = new Oracle("10.160.1.48", "1521", "MESDEV", "WIP", "wip");

        TableType table = db.selectTable("VIEW_DISPATCH_SFC", false);
        System.out.println(table.getTableName());
        table.getColumns().forEach(System.out::println);
        System.out.println(table.generateSelectSQL());

        db.close();
    }

    @Test
    public void testSelectViewScript() throws Exception {
        Database db = new Oracle("10.160.1.48", "1521", "MESDEV", "WIP", "wip");
        System.out.println(db.selectViewScript("VIEW_DISPATCH_SFC"));
        db.close();
    }

    @Test
    public void testGenerateCreateTableSQL() throws Exception {
        Database db = new Oracle("10.160.1.48", "1521", "MESDEV", "WIP", "wip");
        TableType table = db.selectTable("ZR_TEST", true);

        System.out.println("=== Oracle ===");
        System.out.println(db.generateCreateTableSQL(table));

        System.out.println("=== Hana ===");
        try (Database hana = new Hana("WIP", null)) {
            System.out.println(hana.generateCreateTableSQL(table));
        }

        System.out.println("=== PogtgreSQL ===");
        try (Database pg = new PostgreSQL("public", null)) {
            System.out.println(pg.generateCreateTableSQL(table));
        }

        db.close();
    }
}
