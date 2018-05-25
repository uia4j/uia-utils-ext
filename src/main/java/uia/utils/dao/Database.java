package uia.utils.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private Connection conn;

    public Database(Connection conn) {
        this.conn = conn;
    }

    public String selectViewScript(String viewName) throws SQLException {
        Statement stat = this.conn.createStatement();
        ResultSet rs = stat.executeQuery("select DBMS_METADATA.GET_DDL('VIEW','" + viewName + "') from DUAL");
        if (rs.next()) {
            return rs.getString(1);
        }
        return null;
    }

    public TableType selectTable(String tableOrView) throws SQLException {
        List<ColumnType> columns = queryColumnDefs(tableOrView);
        return columns.size() == 0 ? null : new TableType(tableOrView, columns);
    }

    public List<String> selectTableNames() throws SQLException {
        ArrayList<String> tables = new ArrayList<String>();
        try (ResultSet rs = this.conn.getMetaData().getTables(null, null, null, new String[] { "TABLE" })) {
            while (rs.next()) {
                tables.add(rs.getString(3));
            }
            return tables;
        }
    }

    public List<String> selectTableNames(String prefix) throws SQLException {
        ArrayList<String> tables = new ArrayList<String>();
        try (ResultSet rs = this.conn.getMetaData().getTables(null, null, prefix + "%", new String[] { "TABLE" })) {
            while (rs.next()) {
                String tn = rs.getString(3);
                if (tn.startsWith(prefix)) {
                    tables.add(tn);
                }
            }
            return tables;
        }
    }

    public List<String> selectViewNames() throws SQLException {
        ArrayList<String> tables = new ArrayList<String>();
        try (ResultSet rs = this.conn.getMetaData().getTables(null, null, null, new String[] { "VIEW" })) {
            while (rs.next()) {
                tables.add(rs.getString(3));
            }
            return tables;
        }
    }

    public List<String> selectViewNames(String prefix) throws SQLException {
        ArrayList<String> views = new ArrayList<String>();
        try (ResultSet rs = this.conn.getMetaData().getTables(null, null, prefix + "%", new String[] { "VIEW" })) {
            while (rs.next()) {
                String vn = rs.getString(3);
                if (vn.startsWith(prefix)) {
                    views.add(vn);
                }
            }
            return views;
        }
    }

    private List<ColumnType> queryColumnDefs(String tableName) throws SQLException {
        ArrayList<String> pks = new ArrayList<String>();
        try (ResultSet rs = this.conn.getMetaData().getPrimaryKeys(null, null, tableName)) {
            while (rs.next()) {
                pks.add(rs.getString("COLUMN_NAME"));
            }
        }

        /**
         * TABLE_CAT
         * TABLE_SCHEM
         * TABLE_NAME
         * COLUMN_NAME
         * DATA_TYPE
         * TYPE_NAME
         * COLUMN_SIZE
         * BUFFER_LENGTH
         * DECIMAL_DIGITS
         * NUM_PREC_RADIX
         * NULLABLE
         * REMARKS
         * COLUMN_DEF
         * SQL_DATA_TYPE
         * SQL_DATETIME_SUB
         * CHAR_OCTET_LENGTH
         * ORDINAL_POSITION
         * IS_NULLABLE
         * SCOPE_CATALOG
         * SCOPE_SCHEMA
         * SCOPE_TABLE
         * SOURCE_DATA_TYPE
         * IS_AUTOINCREMENT         *
         */
        List<ColumnType> cts = new ArrayList<ColumnType>();
        try (ResultSet rs = this.conn.getMetaData().getColumns(null, null, tableName, null)) {
            while (rs.next()) {
                if (tableName.equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
                    String columnName = rs.getString("COLUMN_NAME");
                    ColumnType ct = new ColumnType();
                    ct.setPk(pks.contains(columnName));
                    ct.setColumnName(columnName);
                    ct.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
                    ct.setDataType(rs.getInt("DATA_TYPE"));
                    ct.setDataTypeName(rs.getString("TYPE_NAME"));
                    ct.setNullable("1".equals(rs.getString("NULLABLE")));
                    ct.setColumnSize(rs.getInt("COLUMN_SIZE"));
                    cts.add(ct);
                }
            }
        }
        if (pks.size() == 0 && cts.size() > 0) {
            cts.get(0).setPk(true);
            pks.add(cts.get(0).getColumnName());
        }

        return cts;
    }
}
