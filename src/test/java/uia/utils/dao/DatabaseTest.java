package uia.utils.dao;

import org.junit.Test;

import uia.utils.dao.hana.Hana;
import uia.utils.dao.ora.Oracle;
import uia.utils.dao.pg.PostgreSQL;

public class DatabaseTest {

    @Test
    public void testCreateTable_Ora2Pg() throws Exception {
        try (Database ora = ora()) {
            try (Database pg = pg()) {
                TableType table = ora.selectTable("ZR_TEST", true);
                System.out.println(pg.createTable(table));
            }
        }
    }

    @Test
    public void testCreateTable_Hana2Pg() throws Exception {
        try (Database hana = hanaDev()) {
            try (Database pg = pg()) {
                for (String tableName : hana.selectTableNames("ZH_")) {
                    TableType table = hana.selectTable(tableName, true);
                    pg.createTable(table);
                }
            }
        }
    }

    @Test
    public void testCreateTable_Pg2Ora() throws Exception {
        try (Database pg = pg()) {
            TableType table = pg.selectTable("only_test", true);
            try (Database ora = ora()) {
                System.out.println(ora.createTable(table));
            }
        }
    }

    @Test
    public void testCreateTable_Hana2Ora() throws Exception {
        try (Database hana = hanaDev()) {
            try (Database ora = ora()) {
                for (String tableName : hana.selectTableNames("Z_")) {
                    TableType table = hana.selectTable(tableName, true);
                    ora.createTable(table);
                }
            }
        }
    }

    @Test
    public void testCreateView_Ora2Pg() throws Exception {
        String script = ora().selectViewScript("VIEW_DISPATCH_SFC");
        pg().createView("VIEW_DISPATCH_SFC", script);
    }

    @Test
    public void testCreateView_Hana2Pg() throws Exception {
        String script = hanaDev().selectViewScript("VIEW_DISPATCH_SFC");
        pg().createView("VIEW_DISPATCH_SFC", script);
    }

    @Test
    public void testSameAsTable_PgOra() throws Exception {
        TableType table1 = pg().selectTable("test_only", true);
        TableType table2 = ora().selectTable("TEST_ONLY", true);
        table1.sameAs(table2).printFailed();
    }

    @Test
    public void testSameAsTables_Hana() throws Exception {
        // Database src = hana();
        Database src = ora();
        Database tgt = hanaTest();
        for (String tableName : src.selectTableNames("ZD_")) {
            TableType table1 = src.selectTable(tableName, true);
            TableType table2 = tgt.selectTable(tableName, true);
            table1.sameAs(table2, new ComparePlan(false, true, true, true)).printFailed();
        }
    }

    @Test
    public void testSameAsViews_Hana() throws Exception {
        Database src = ora();
        // Database src = hanaDev();
        Database tgt = hanaTest();
        for (String viewName : src.selectViewNames("VIEW_")) {
            TableType table1 = src.selectTable(viewName, false);
            TableType table2 = tgt.selectTable(viewName, false);
            table1.sameAs(table2, ComparePlan.view()).printFailed();
        }
    }

    private Database ora() throws Exception {
        return new Oracle("10.160.1.48", "1521", "MESDEV", "WIP", "wip");
    }

    private Database pg() throws Exception {
        return new PostgreSQL("localhost", "5432", "wip", "wip", "wip");
    }

    private Database hanaDev() throws Exception {
        return new Hana("10.160.1.52", "39015", null, "WIP", "Hdb12345");
    }

    private Database hanaTest() throws Exception {
        return new Hana("10.160.2.23", "31015", null, "WIP", "Sap12345");
    }
}
