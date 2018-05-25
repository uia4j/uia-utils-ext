package uia.utils.dao;

import org.junit.Assert;
import org.junit.Test;

import uia.utils.dao.where.SimpleWhere;
import uia.utils.dao.where.Where;
import uia.utils.dao.where.WhereAnd;
import uia.utils.dao.where.WhereOr;

public class WhereTest {

    @Test
    public void testOr() {
        Where and1 = SimpleWhere.createAnd().eq("A", "A").eq("B", "B");
        Where and2 = SimpleWhere.createAnd().eq("C", "C").eq("D", "D");

        Where where = new WhereOr().add(and1).add(and2);
        Assert.assertEquals("(A=? and B=?) or (C=? and D=?)", where.generate());
    }

    @Test
    public void testAnd() {
        Where or1 = SimpleWhere.createOr().eq("A", "A").eq("B", "B");
        Where or2 = SimpleWhere.createOr().eq("C", "C").eq("D", "D");
        Where and3 = SimpleWhere.createAnd().eq("E", "E").eq("F", "F");

        Where where1 = new WhereAnd().add(or1).add(or2);
        Assert.assertEquals("(A=? or B=?) and (C=? or D=?)", where1.generate());

        Where where2 = new WhereOr().add(where1).add(and3);
        Assert.assertEquals("((A=? or B=?) and (C=? or D=?)) or (E=? and F=?)", where2.generate());
    }
}
