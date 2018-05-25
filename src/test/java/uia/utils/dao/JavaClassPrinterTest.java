package uia.utils.dao;

import org.junit.Test;

public class JavaClassPrinterTest {

    @Test
    public void testGenerateTable() throws Exception {
        JavaClassPrinter.Result result = new JavaClassPrinter(
        		DB.create(), 
        		"ivp").generate("huede.mvs.db.dao", "huede.mvs.db", "InventoryEx");
        System.out.println("=========================");
        System.out.println(result.dto);
        System.out.println("=========================");
        System.out.println(result.dao);
    }

    @Test
    public void testGenerateView() throws Exception {
        JavaClassPrinter.Result result = new JavaClassPrinter(
        		DB.create(), 
        		"VIEW_RUN_TG_SFC").generate4View("ame.psb.db.dao", "ame.psb.db", "ViewRunTgRun");
        System.out.println("=========================");
        System.out.println(result.dto);
        System.out.println("=========================");
        System.out.println(result.dao);
    }

    @Test
    public void testGenerateDTO() throws Exception {
        System.out.println(new JavaClassPrinter(DB.create(), "ZR_DISPATCH_SFC").generateDTO("ame.psb.db", "DispatchSfc"));
    }

    @Test
    public void testGenerateDAO() throws Exception {
        System.out.println(new JavaClassPrinter(DB.create(), "ZR_DISPATCH_SFC").generateDAO("uia.utils.dao", "uia.utils.dao", "DispatchSfc"));
    }

    @Test
    public void testGenerateDAO4View() throws Exception {
        System.out.println(new JavaClassPrinter(DB.create(), "ZR_DISPATCH_SFC").generateDAO4View("uia.utils.dao", "uia.utils.dao", "DispatchSfc"));
    }

}
