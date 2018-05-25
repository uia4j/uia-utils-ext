package uia.utils.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import uia.utils.dao.where.SimpleWhere;
import uia.utils.dao.where.conditions.ConditionType;

public class SimpleWhereTest implements ConditionType {

    @Test
    public void testAnd1() {
        SimpleWhere and = SimpleWhere.createAnd()
                .eq("c1", "abc")
                .eq("c2", null)
                .between("c3", "123", "456")
                .likeBegin("c4", "abc")
                .likeBegin("c5", null)
                .notEq("c7", "def")
                .notEq("c8", null)
                .add(this);
        Assert.assertEquals(
                "c1=? and (c3 between ? and ?) and c4 like ? and c7<>? and (c6='A')",
                and.generate());
    }

    @Test
    public void testAnd2() {
        SimpleWhere and = SimpleWhere.createAnd()
                .eqOrNull("c1", "abc")
                .eqOrNull("c2", null)
                .between("c3", "123", "456")
                .likeBeginOrNull("c4", "abc")
                .likeBeginOrNull("c5", null)
                .notEqOrNull("c7", "def")
                .notEqOrNull("c8", null)
                .add(() -> "c6 is not null");
        Assert.assertEquals(
                "c1=? and c2 is null and (c3 between ? and ?) and c4 like ? and c5 is null and c7<>? and c8 is not null and c6 is not null",
                and.generate());
    }

    @Test
    public void testOr1() {
        SimpleWhere or = SimpleWhere.createOr()
                .eq("c1", "abc")
                .eq("c2", null)
                .between("c3", "123", "456")
                .likeBegin("c4", "abc")
                .likeBegin("c5", null)
                .add(this);
        Assert.assertEquals(
                "c1=? or (c3 between ? and ?) or c4 like ? or (c6='A')",
                or.generate());
    }

    @Test
    public void testOr2() {
        SimpleWhere or = SimpleWhere.createOr()
                .eqOrNull("c1", "abc")
                .eqOrNull("c2", null)
                .between("c3", "123", "456")
                .likeBeginOrNull("c4", "abc")
                .likeBeginOrNull("c5", null)
                .add(() -> "c6 is not null");
        Assert.assertEquals(
                "c1=? or c2 is null or (c3 between ? and ?) or c4 like ? or c5 is null or c6 is not null",
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
