package uia.utils.dao;

import org.junit.Test;

import uia.utils.dao.hana.Hana;
import uia.utils.dao.ora.Oracle;
import uia.utils.dao.pg.PostgreSQL;

public class HanaSQLTest {

    @Test
    public void testSelectTableNames() throws Exception {
        Database db = new Hana("10.160.1.52", "39015", null, "WIP", "Hdb12345");
        db.selectTableNames().forEach(t -> System.out.println(t));
        db.close();
    }

    @Test
    public void testSelectViewNames() throws Exception {
        Database db = new Hana("10.160.1.52", "39015", null, "WIP", "Hdb12345");
        db.selectViewNames("VIEW_").forEach(t -> System.out.println(t));
        db.close();
    }

    @Test
    public void testSelectTable() throws Exception {
        Database db = new Hana("10.160.1.52", "39015", null, "WIP", "Hdb12345");

        TableType table = db.selectTable("ZD_TEST", true);
        System.out.println(table.getTableName());
        table.getColumns().forEach(System.out::println);
        System.out.println(table.generateInsertSQL());
        System.out.println(table.generateUpdateSQL());
        System.out.println(table.generateSelectSQL());

        db.close();
    }

    @Test
    public void testSelectView() throws Exception {
        Database db = new Hana("10.160.1.52", "39015", null, "WIP", "Hdb12345");

        TableType table = db.selectTable("VIEW_DISPATCH_SFC", false);
        System.out.println(table.getTableName());
        table.getColumns().forEach(System.out::println);
        System.out.println(table.generateSelectSQL());

        db.close();
    }

    @Test
    public void testSelectViewScript() throws Exception {
        Database db = new Hana("10.160.1.52", "39015", null, "WIP", "Hdb12345");
        System.out.println(db.selectViewScript("VIEW_DISPATCH_SFC"));
        db.close();
    }

    @Test
    public void testGenerateCreateTableSQL() throws Exception {
        Database db = new Hana("10.160.1.52", "39015", null, "WIP", "Hdb12345");
        TableType table = db.selectTable("ZR_CARRIER_CLEAN", false);

        System.out.println("=== Hana ===");
        System.out.println(db.generateCreateTableSQL(table));

        System.out.println("=== Oracle ===");
        try (Database ora = new Oracle("WIP", null, null, null, null)) {
            System.out.println(ora.generateCreateTableSQL(table));
        }

        System.out.println("=== PogtgreSQL ===");
        try (PostgreSQL pg = new PostgreSQL("public", null, null, null, null)) {
            System.out.println(pg.generateCreateTableSQL(table));
        }

        db.close();
    }
}
