package uia.utils.dao.where;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SimpleStatement {

    private Where where;

    private final List<String> groups;

    private final List<String> orders;

    public SimpleStatement() {
        this.groups = new ArrayList<String>();
        this.orders = new ArrayList<String>();
    }

    public SimpleStatement where(Where where) {
        this.where = where;
        return this;
    }

    public SimpleStatement groupBy(String columnName) {
        this.groups.add(columnName);
        return this;
    }

    public SimpleStatement orderBy(String columnName) {
        this.orders.add(columnName);
        return this;
    }

    public PreparedStatement prepare(Connection conn, String selectSql) throws SQLException {
        String where = this.where == null ? null : this.where.generate();
        PreparedStatement ps;
        if (where == null || where.length() == 0) {
            ps = conn.prepareStatement(selectSql + groupBy() + orderBy());
        }
        else {
            ps = conn.prepareStatement(selectSql + " where " + where + groupBy() + orderBy());
            int index = 1;
            index = this.where.accept(ps, index);
        }

        return ps;
    }

    public PreparedStatement prepareNoBinding(Connection conn, String selectSql) throws SQLException {
        String where = this.where == null ? null : this.where.generate();
        PreparedStatement ps;
        if (where == null || where.length() == 0) {
            ps = conn.prepareStatement(selectSql + groupBy() + orderBy());
        }
        else {
            ps = conn.prepareStatement(selectSql + " where " + where + groupBy() + orderBy());
        }
        return ps;
    }

    protected String groupBy() {
        return this.groups.size() == 0 ? "" : " group by " + String.join(",", this.groups);
    }

    protected String orderBy() {
        return this.orders.size() == 0 ? "" : " order by " + String.join(",", this.orders);
    }

    protected boolean isEmpty(Object value) {
        return value == null || value.toString().trim().length() == 0;
    }

}
