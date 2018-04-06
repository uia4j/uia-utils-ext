package uia.utils.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import uia.utils.dao.where.SimpleOr;
import uia.utils.dao.where.rules.RuleType;

public class OrTest implements RuleType {

    @Test
    public void test1() {
        SimpleOr or = new SimpleOr()
                .eq("c1", "abc")
                .eq("c2", null)
                .between("c3", "123", "456")
                .likeBegin("c4", "abc")
                .likeBegin("c5", null)
                .or(this);
        Assert.assertEquals(
        		"c1=? OR (c3 between ? and ?) OR c4 like ? OR (c6='A')",
        		or.generate());
    } 

    @Test
    public void test2() {
        SimpleOr or = new SimpleOr()
                .eqOrNull("c1", "abc")
                .eqOrNull("c2", null)
                .between("c3", "123", "456")
                .likeBeginOrNull("c4", "abc")
                .likeBeginOrNull("c5", null)
                .or(() -> "c6 is not null");
        Assert.assertEquals(
        		"c1=? OR c2 is null OR (c3 between ? and ?) OR c4 like ? OR c5 is null OR c6 is not null",
        		or.generate());
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
