package uia.utils.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import uia.utils.dao.where.SimpleAnd;
import uia.utils.dao.where.rules.RuleType;

public class AndTest implements RuleType {

    @Test
    public void test1() {
        SimpleAnd and = new SimpleAnd()
                .eq("c1", "abc")
                .eq("c2", null)
                .between("c3", "123", "456")
                .likeBegin("c4", "abc")
                .likeBegin("c5", null)
                .and(this); 
        and.addOrder("c1").addOrder("c2");
        Assert.assertEquals(
        		"c1=? AND (c3 between ? and ?) AND c4 like ? AND (c6='A') ORDER BY c1,c2",
        		and.generate()); 
    }

    @Test
    public void test2() {
        SimpleAnd and = new SimpleAnd()
                .eqOrNull("c1", "abc")
                .eqOrNull("c2", null)
                .between("c3", "123", "456")
                .likeBeginOrNull("c4", "abc")
                .likeBeginOrNull("c5", null)
                .and(() -> "c6 is not null");
        and.addOrder("c1").addOrder("c2");
        Assert.assertEquals(
        		"c1=? AND c2 is null AND (c3 between ? and ?) AND c4 like ? AND c5 is null AND c6 is not null ORDER BY c1,c2",
        		and.generate());
    }

    @Override
    public String getStatement() {
        return "(c6='A')";
    }

    @Override
    public int accpet(PreparedStatement ps, int index) throws SQLException {
        return index;
    }

}
