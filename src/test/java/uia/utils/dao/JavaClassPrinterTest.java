package uia.utils.dao;

import java.sql.SQLException;

import org.junit.Test;

import uia.utils.dao.pg.PostgreSQL;

public class JavaClassPrinterTest {

    @Test
    public void testGenerateTable() throws Exception {
        JavaClassPrinter.Result result = new JavaClassPrinter(createDB(), "chamber_param")
                .generate("uia.pie.db.dao", "uia.pie.db", "ChamberParam");
        System.out.println("=========================");
        System.out.println(result.dto);
        System.out.println("=========================");
        System.out.println(result.dao);
    }

    @Test
    public void testGenerateView() throws Exception {
        JavaClassPrinter.Result result = new JavaClassPrinter(createDB(), "view_app_node_equip")
                .generate4View("uia.fdc.db.dao", "uia.fdc.db", "ViewAppNodeEquip");
        System.out.println("=========================");
        System.out.println(result.dto);
        System.out.println("=========================");
        System.out.println(result.dao);
    }

    @Test
    public void testGenerateDTO() throws Exception {
        System.out.println(new JavaClassPrinter(createDB(), "ivp_raw_event_view")
                .generateDTO("uia.utils.dao", "DispatchSfc"));
    }

    @Test
    public void testGenerateDAO() throws Exception {
        System.out.println(new JavaClassPrinter(createDB(), "ivp_raw_event_view")
                .generateDAO("uia.utils.dao", "uia.utils.dao", "DispatchSfc"));
    }

    @Test
    public void testGenerateDAO4View() throws Exception {
        System.out.println(new JavaClassPrinter(createDB(), "ivp_raw_event_view")
                .generateDAO4View("uia.utils.dao", "uia.utils.dao", "ivp_raw_event_view"));
    }

    private Database createDB() throws SQLException {
        //return new PostgreSQL("localhost", "5432", "tmd", "postgres", "pgAdmin");
        return new PostgreSQL("localhost", "5432", "pie", "pie", "pie");
    }

}
