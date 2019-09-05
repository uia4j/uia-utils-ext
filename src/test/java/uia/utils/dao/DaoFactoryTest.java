package uia.utils.dao;

import org.junit.Assert;
import org.junit.Test;

import uia.utils.dao.DaoFactory;
import uia.utils.dao.DaoTable;
import uia.utils.dao.DaoView;
import uia.utils.dao.sample.One;
import uia.utils.dao.sample.Two;
import uia.utils.dao.sample.ViewOne;

public class DaoFactoryTest {

    @Test
    public void testOne() throws Exception {
        DaoFactory factory = new DaoFactory();
        factory.load("uia.utils.dao.sample");

        DaoTable<One> dao = factory.forTable(One.class);
        Assert.assertEquals("INSERT INTO one(id,name,birthday,state_name) VALUES (?,?,?,?)", dao.forInsert().getSql());
        Assert.assertEquals("UPDATE one SET name=?,birthday=?,state_name=? WHERE id=?", dao.forUpdate().getSql());
        Assert.assertEquals("DELETE FROM one WHERE id=?", dao.forDelete().getSql());
        Assert.assertEquals("SELECT id,name,birthday,state_name FROM one ", dao.forSelect().getSql());
    }

    @Test
    public void testTwo() throws Exception {
        DaoFactory factory = new DaoFactory();
        factory.load("uia.utils.dao.sample");

        DaoTable<Two> dao = factory.forTable(Two.class);
        Assert.assertEquals("INSERT INTO org_supplier(org_id,supplier_id,state_name) VALUES (?,?,?)", dao.forInsert().getSql());
        Assert.assertEquals("UPDATE org_supplier SET state_name=? WHERE org_id=? AND supplier_id=?", dao.forUpdate().getSql());
        Assert.assertEquals("DELETE FROM org_supplier WHERE org_id=? AND supplier_id=?", dao.forDelete().getSql());
        Assert.assertEquals("SELECT org_id,supplier_id,state_name FROM org_supplier ", dao.forSelect().getSql());
    }

    @Test
    public void testViewOne() throws Exception {
        DaoFactory factory = new DaoFactory();
        factory.load("uia.utils.dao.sample");

        DaoView<ViewOne> dao = factory.forView(ViewOne.class);
        Assert.assertEquals("SELECT description,id,name,birthday FROM view_one ", dao.forSelect().getSql());
    }
}
