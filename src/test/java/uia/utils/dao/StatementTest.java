package uia.utils.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.junit.Test;

import uia.utils.dao.where.SimpleWhere;
import uia.utils.dao.where.SimpleStatement;
import uia.utils.dao.where.WhereOr;

public class StatementTest {

    @Test
    public void test1() throws SQLException {
        SimpleWhere where = SimpleWhere.createAnd()
                .eq("A", "A1")
                .eq("B", "B1")
                .between("TIME", new Date(), new Date());

        SimpleStatement stat = new SimpleStatement();
        stat.where(where).orderBy("A");
        stat.where(where).groupBy("A");

        try (Connection conn = createDB()) {
            PreparedStatement ps = stat.prepare(conn, "select * from IVP");
            System.out.println(ps);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void test2() throws SQLException {
        SimpleWhere and1 = SimpleWhere.createAnd()
                .eq("A", "A1")
                .eq("B", "B1");

        SimpleWhere and2 = SimpleWhere.createAnd()
                .eq("C", "C1")
                .between("TIME", new Date(), new Date());

        WhereOr where = new WhereOr().add(and1).add(and2);

        SimpleStatement stat = new SimpleStatement();
        stat.where(where).orderBy("A").orderBy("B");

        try (Connection conn = createDB()) {
            PreparedStatement ps = stat.prepare(conn, "select * from ivp");
            System.out.println(ps);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Connection createDB() {
        return null;
    }
}
