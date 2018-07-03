package uia.utils.dao;

import java.sql.SQLException;

import org.junit.Test;

import uia.utils.dao.pg.PostgreSQL;

public class JavaClassPrinterTest {

    @Test
    public void testGenerateTable() throws Exception {
        JavaClassPrinter.Result result = new JavaClassPrinter(createDB(), "tmd_tx_keys")
                .generate("uia.tmd.zztop.db.dao", "uia.tmd.zztop.db", "TmdTxKeys");
        System.out.println("=========================");
        System.out.println(result.dto);
        System.out.println("=========================");
        System.out.println(result.dao);
    }

    @Test
    public void testGenerateView() throws Exception {
        JavaClassPrinter.Result result = new JavaClassPrinter(createDB(), "ivp_raw_event_view")
                .generate4View("uia.utils.dao", "ame.psb.db", "ViewRunTgRun");
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
        return new PostgreSQL("localhost", "5432", "tmd", "postgres", "pgAdmin");
    }

}
