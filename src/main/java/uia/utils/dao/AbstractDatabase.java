package uia.utils.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDatabase implements Database {

    protected String schema;

    protected final Connection conn;

    protected AbstractDatabase(String schema, Connection conn) {
        this.schema = schema;
        this.conn = conn;
    }

    @Override
    public void close() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    @Override
    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public Connection getConnection() {
        return this.conn;
    }

    @Override
    public int createTable(TableType table) throws SQLException {
        String script = generateCreateTableSQL(table);
        return this.conn.prepareStatement(script)
                .executeUpdate();
    }

    @Override
    public int createView(String viewName, String sql) throws SQLException {
        String script = String.format("CREATE VIEW \"%s\" (\n%s\n)", viewName, sql);
        System.out.println(script);
        return this.conn.prepareStatement(script)
                .executeUpdate();
    }

    @Override
    public int dropTable(String tableName) throws SQLException {
        return this.conn.prepareStatement("DROP TABLE " + tableName)
                .executeUpdate();
    }

    @Override
    public int dropView(String viewName) throws SQLException {
        return this.conn.prepareStatement("DROP VIEW " + viewName)
                .executeUpdate();
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return this.conn.prepareStatement(sql)
                .executeUpdate();
    }

    @Override
    public TableType selectTable(String tableOrView, boolean firstAsPK) throws SQLException {
        List<ColumnType> columns = queryColumnDefs(tableOrView, firstAsPK);
        return columns.size() == 0 ? null : new TableType(tableOrView, columns);
    }

    @Override
    public List<String> selectTableNames() throws SQLException {
        ArrayList<String> tables = new ArrayList<String>();
        try (ResultSet rs = this.conn.getMetaData().getTables(null, this.schema, null, new String[] { "TABLE" })) {
            while (rs.next()) {
                tables.add(rs.getString(3));
            }
            return tables;
        }
    }

    @Override
    public List<String> selectTableNames(String prefix) throws SQLException {
        ArrayList<String> tables = new ArrayList<String>();
        try (ResultSet rs = this.conn.getMetaData().getTables(null, this.schema, prefix + "%", new String[] { "TABLE" })) {
            while (rs.next()) {
                String tn = rs.getString(3);
                if (tn.startsWith(prefix)) {
                    tables.add(tn);
                }
            }
            return tables;
        }
    }

    @Override
    public List<String> selectViewNames() throws SQLException {
        ArrayList<String> tables = new ArrayList<String>();
        try (ResultSet rs = this.conn.getMetaData().getTables(null, this.schema, null, new String[] { "VIEW" })) {
            while (rs.next()) {
                tables.add(rs.getString(3));
            }
            return tables;
        }
    }

    @Override
    public List<String> selectViewNames(String prefix) throws SQLException {
        ArrayList<String> views = new ArrayList<String>();
        try (ResultSet rs = this.conn.getMetaData().getTables(null, this.schema, prefix + "%", new String[] { "VIEW" })) {
            while (rs.next()) {
                String vn = rs.getString(3);
                if (vn.startsWith(prefix)) {
                    views.add(vn);
                }
            }
            return views;
        }
    }

    protected abstract List<ColumnType> queryColumnDefs(String tableName, boolean firstAsPK) throws SQLException;
}
