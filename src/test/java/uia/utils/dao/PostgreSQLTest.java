package uia.utils.dao;

import org.junit.Test;

import uia.utils.dao.hana.Hana;
import uia.utils.dao.ora.Oracle;
import uia.utils.dao.pg.PostgreSQL;

public class PostgreSQLTest {

    @Test
    public void testSelectTableNames() throws Exception {
        Database db = new PostgreSQL("localhost", "5432", "mvsdb", "huede", "huede");
        db.selectTableNames("ivp").forEach(t -> System.out.println(t));
        db.close();
    }

    @Test
    public void testSelectViewNames() throws Exception {
        Database db = new PostgreSQL("localhost", "5432", "mvsdb", "huede", "huede");
        db.selectViewNames("ivp_").forEach(t -> System.out.println(t));
        db.close();
    }

    @Test
    public void testSelectTable() throws Exception {
        Database db = new PostgreSQL("localhost", "5432", "tmd", "postgres", "pgAdmin");

        TableType table = db.selectTable("test", true);
        System.out.println(table.getTableName());
        table.getColumns().forEach(System.out::println);
        System.out.println(table.generateInsertSQL());
        System.out.println(table.generateUpdateSQL());
        System.out.println(table.generateSelectSQL());

        db.close();
    }

    @Test
    public void testSelectView() throws Exception {
        Database db = new PostgreSQL("localhost", "5432", "mvsdb", "huede", "huede");

        TableType table = db.selectTable("ivp_raw_event_view", false);
        System.out.println(table.getTableName());
        table.getColumns().forEach(System.out::println);
        System.out.println(table.generateInsertSQL());
        System.out.println(table.generateUpdateSQL());
        System.out.println(table.generateSelectSQL());

        db.close();
    }

    @Test
    public void testSelectViewScript() throws Exception {
        Database db = new PostgreSQL("localhost", "5432", "mvsdb", "huede", "huede");
        System.out.println(db.selectViewScript("ivp_raw_event_view"));
        db.close();
    }

    @Test
    public void testGenerateCreateTableSQL() throws Exception {
        Database db = new PostgreSQL("localhost", "5432", "mvsdb", "huede", "huede");
        TableType table = db.selectTable("ivp", false);

        System.out.println("=== PostgreSQL ===");
        System.out.println(db.generateCreateTableSQL(table));

        System.out.println("=== Oracle ===");
        try (Database ora = new Oracle("WIP", null, null, null, null)) {
            System.out.println(ora.generateCreateTableSQL(table));
        }

        System.out.println("=== Hana ===");
        try (Database hana = new Hana("WIP", null, null, null, null)) {
            System.out.println(hana.generateCreateTableSQL(table));
        }

        db.close();
    }
}
