package uia.utils.dao.ora;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import uia.utils.dao.AbstractDatabase;
import uia.utils.dao.ColumnType;
import uia.utils.dao.ColumnType.DataType;
import uia.utils.dao.TableType;

public class Oracle extends AbstractDatabase {

    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Oracle(String host, String port, String service, String user, String pwd) throws SQLException {
        super("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@" + host + ":" + port + ":" + service, user, pwd, user);
    }

    @Override
    public String selectViewScript(String viewName) throws SQLException {
        String script = null;
        Statement stat = this.conn.createStatement();
        ResultSet rs = stat.executeQuery("select DBMS_METADATA.GET_DDL('VIEW','" + viewName.toUpperCase() + "') from DUAL");
        if (rs.next()) {
            script = rs.getString(1);
            int i = script.indexOf(") AS");
            script = script.substring(i + 5, script.length()).trim();
        }
        return script;
    }

    @Override
    public String generateCreateTableSQL(TableType table) {
        if (table == null) {
            return null;
        }

        ArrayList<String> pks = new ArrayList<String>();
        ArrayList<String> cols = new ArrayList<String>();
        for (ColumnType ct : table.getColumns()) {
            if (ct.isPk()) {
                pks.add(ct.getColumnName().toUpperCase());
            }
            cols.add(prepareColumnDef(ct));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + this.schema + "." + table.getTableName().toUpperCase() + "\n(\n");
        sb.append(String.join(",\n", cols));
        if (pks.isEmpty()) {
            sb.append("\n)");
        }
        else {
            String pkSQL = String.format(",\n CONSTRAINT %s_pkey PRIMARY KEY (\"%s\")\n",
                    table.getTableName().toUpperCase(),
                    String.join("\",\"", pks));
            sb.append(pkSQL).append(")");
        }

        return sb.toString();
    }

    @Override
    public String generateAlterTableSQL(String tableName, List<ColumnType> columns) {
        ArrayList<String> cols = new ArrayList<String>();
        for (ColumnType column : columns) {
            cols.add(prepareColumnDef(column));
        }
        return "ALTER TABLE " + tableName + " ADD (\n" + String.join(",\n", cols) + "\n)";
    }

    @Override
    public List<ColumnType> selectColumns(String tableName, boolean firstAsPK) throws SQLException {
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
                    ColumnType ct = new OracleColumnType();
                    ct.setPk(pks.contains(columnName));
                    ct.setColumnName(columnName);
                    ct.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
                    ct.setDataTypeCode(rs.getInt("DATA_TYPE"));
                    ct.setDataTypeName(rs.getString("TYPE_NAME"));
                    ct.setNullable("1".equals(rs.getString("NULLABLE")));
                    ct.setColumnSize(rs.getInt("COLUMN_SIZE"));

                    switch (rs.getInt("DATA_TYPE")) {
                        case -9:        // NVARCHAR2
                            ct.setDataType(DataType.NVARCHAR2);
                            break;
                        case -1:        // LONG
                            ct.setDataType(DataType.LONG);
                            break;
                        case 1:        // CHAR
                            ct.setDataType(DataType.NVARCHAR2);
                            break;
                        case 3:         // NUMBER
                            ct.setDataType(DataType.NUMERIC);
                            break;
                        case 6:         // FLOAT
                            ct.setDataType(DataType.FLOAT);
                            break;
                        case 12:        // VARCHAR, VARCHAR2
                            ct.setDataType(DataType.VARCHAR2);
                            break;
                        case 93:        // DATE,TIMESTAMP
                            if (ct.getDataTypeName().startsWith("TIMESTAMP")) {
                                ct.setDataType(DataType.TIMESTAMP);
                            }
                            else {
                                ct.setDataType(DataType.DATE);
                            }
                            break;
                        case 2004:      // BLOB
                            ct.setDataType(DataType.BLOB);
                            break;
                        case 2005:      // CLOB
                            ct.setDataType(DataType.CLOB);
                            break;
                        case 2011:      // NCLOB
                            ct.setDataType(DataType.NCLOB);
                            break;
                        default:
                            ct.setDataType(DataType.OTHERS);

                    }
                    cts.add(ct);
                }
            }
        }
        if (pks.size() == 0 && firstAsPK && cts.size() > 0) {
            cts.get(0).setPk(true);
            pks.add(cts.get(0).getColumnName());
        }

        return cts;
    }

    @Override
    protected String upperOrLower(String value) {
        return value.toUpperCase();
    }

    private String prepareColumnDef(ColumnType ct) {
        String type = "";
        switch (ct.getDataType()) {
            case LONG:
                type = "LONG";
                break;
            case DOUBLE:
            case NUMERIC:
                long cs = ct.getColumnSize();
                type = "NUMBER("
                        + (cs >= 38 || cs <= 0 ? 38 : ct.getColumnSize())
                        + ","
                        + ct.getDecimalDigits()
                        + ")";
                break;
            case FLOAT:
                type = "FLOAT(126)";
                break;
            case INTEGER:
                type = "INTEGER";
                break;
            case DATE:
                type = "DATE";
                break;
            case TIME:
            case TIMESTAMP:
                type = "TIMESTAMP(6)";
                break;
            case NVARCHAR:
            case NVARCHAR2:
                type = "NVARCHAR2(" + (ct.getColumnSize() == 0 ? 32 : ct.getColumnSize()) + ")";
                break;
            case VARCHAR:
            case VARCHAR2:
                type = "VARCHAR2(" + (ct.getColumnSize() == 0 ? 32 : ct.getColumnSize()) + " BYTE)";
                break;
            case BLOB:
                type = "BLOB";
                break;
            case CLOB:
                type = "CLOB";
                break;
            case NCLOB:
                type = "NCLOB";
                break;
            default:
                throw new NullPointerException(ct.getColumnName() + " type not found");

        }

        String nullable = "";
        if (ct.isPk() || !ct.isNullable()) {
            nullable = " NOT NULL";
        }

        return " \"" + ct.getColumnName().toUpperCase() + "\" " + type + nullable;
    }
}