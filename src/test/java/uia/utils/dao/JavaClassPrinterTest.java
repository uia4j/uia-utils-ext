package uia.utils.dao;

import java.sql.SQLException;

import org.junit.Test;

import uia.utils.dao.pg.PostgreSQL;

public class JavaClassPrinterTest {

    @Test
    public void testGenerateTable() throws Exception {
        JavaClassPrinter.Result result = new JavaClassPrinter(createDB(), "test_only")
                .generate("huede.mvs.db.dao", "huede.mvs.db", "Ivp");
        System.out.println("=========================");
        System.out.println(result.dto);
        System.out.println("=========================");
        System.out.println(result.dao);
    }

    @Test
    public void testGenerateView() throws Exception {
        JavaClassPrinter.Result result = new JavaClassPrinter(createDB(), "ivp_raw_event_view")
                .generate4View("huede.mvs.db.dao", "ame.psb.db", "ViewRunTgRun");
        System.out.println("=========================");
        System.out.println(result.dto);
        System.out.println("=========================");
        System.out.println(result.dao);
    }

    @Test
    public void testGenerateDTO() throws Exception {
        System.out.println(new JavaClassPrinter(createDB(), "ivp_raw_event_view")
                .generateDTO("huede.mvs.db", "DispatchSfc"));
    }

    @Test
    public void testGenerateDAO() throws Exception {
        System.out.println(new JavaClassPrinter(createDB(), "ivp_raw_event_view")
                .generateDAO("huede.mvs.db.dao", "huede.mvs.db", "DispatchSfc"));
    }

    @Test
    public void testGenerateDAO4View() throws Exception {
        System.out.println(new JavaClassPrinter(createDB(), "ivp_raw_event_view")
                .generateDAO4View("huede.mvs.db.dao", "huede.mvs.db", "ivp_raw_event_view"));
    }

    private Database createDB() throws SQLException {
        return new PostgreSQL("localhost", "5432", "mvsdb", "huede", "huede");
    }

}
