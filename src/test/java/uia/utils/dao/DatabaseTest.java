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
                for (String tableName : hana.selectTableNames("ACTIVITY")) {
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
    public void testCreateTable_Ora2Hana() throws Exception {
        try (Database hana = hanaDev()) {
            try (Database ora = ora()) {
                for (String tableName : hana.selectTableNames("ZR_CARRIER_CLEAN")) {
                    TableType table = hana.selectTable(tableName, true);
                    ora.createTable(table);
                }
            }
        }
    }

    @Test
    public void testCreateTable_Pg2Hana() throws Exception {
        try (Database hana = hanaDev()) {
            try (Database ora = ora()) {
                for (String tableName : hana.selectTableNames("ZR_")) {
                    TableType table = hana.selectTable(tableName, true);
                    ora.createTable(table);
                }
            }
        }
    }

    @Test
    public void testCreateTable_Hana2Hana() throws Exception {
        try (Database hana1 = hanaDev()) {
            try (Database hana2 = hanaProd()) {
                hana2.createTable(hana1.selectTable("Z_PMS_SPAREPARTS_PARAMETER", true));
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
    public void testCreateView_Hana2Hana() throws Exception {
        try (Database hana1 = hanaDev()) {
            try (Database hana2 = hanaTest()) {
                String viewName1 = "VIEW_PMS_CHECKLIST_PARAMETER";
                String script1 = hana1.selectViewScript(viewName1);
                hana2.createView(viewName1, script1);

                String viewName2 = "VIEW_PMS_PLAN";
                String script2 = hana1.selectViewScript(viewName2);
                hana2.createView(viewName2, script2);

                String viewName3 = "VIEW_PMS_PLAN_CHANGE";
                String script3 = hana1.selectViewScript(viewName3);
                hana2.createView(viewName3, script3);
            }
        }
    }

    @Test
    public void testSameAsTable_PgOra() throws Exception {
        TableType table1 = pg().selectTable("bom_component", true);
        TableType table2 = ora().selectTable("BOM_COMPONENT", true);
        table1.sameAs(table2).print(false);
    }

    @Test
    public void testSameAsTables_Hana() throws Exception {
        try (Database src = hanaDev()) {
            try (Database tgt = pg()) {
                for (String tableName : src.selectTableNames("BOM")) {
                    TableType table1 = src.selectTable(tableName, true);
                    TableType table2 = tgt.selectTable(tableName, true);
                    table1.sameAs(table2, new ComparePlan(false, false, false, true, true)).print(true);
                }
            }
        }
    }

    @Test
    public void testSameAsViews_Hana() throws Exception {
        try (Database src = hanaDev()) {
            try (Database tgt = pg()) {
                for (String viewName : src.selectViewNames("VIEW_")) {
                    TableType table1 = src.selectTable(viewName, false);
                    TableType table2 = tgt.selectTable(viewName, false);
                    table1.sameAs(table2, ComparePlan.view()).print(false);
                }
            }
        }
    }

    private Database ora() throws Exception {
        return new Oracle("10.160.1.48", "1521", "MESDEV", "WIP", "wip");
    }

    private Database pg() throws Exception {
        return new PostgreSQL("localhost", "5432", "wip", "postgres", "pgAdmin");
    }

    private Database hanaDev() throws Exception {
        return new Hana("10.160.1.52", "39015", null, "WIP", "Hdb12345");
    }

    private Database hanaTest() throws Exception {
        return new Hana("10.160.2.23", "31015", null, "WIP", "Sap12345");
    }

    private Database hanaProd() throws Exception {
        return new Hana("10.160.2.20", "30015", null, "WIP", "Sap12345");
    }
}
