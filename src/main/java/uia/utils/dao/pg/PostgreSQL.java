package uia.utils.dao.pg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import uia.utils.dao.AbstractDatabase;
import uia.utils.dao.ColumnType;
import uia.utils.dao.ColumnType.DataType;
import uia.utils.dao.TableType;

public class PostgreSQL extends AbstractDatabase {

    public PostgreSQL(String schema, Connection conn) {
        super(schema, conn);
    }

    public PostgreSQL(String host, String port, String service, String user, String pwd) throws SQLException {
        super("public", DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + service, user, pwd));
    }

    @Override
    public String selectViewScript(String viewName) throws SQLException {
        String script = null;

        Statement stat = this.conn.createStatement();
        ResultSet rs = stat.executeQuery("select pg_get_viewdef('" + viewName + "', true)");
        if (rs.next()) {
            script = rs.getString(1);
            script = script.replace("::text", "").trim();
            script = script.substring(0, script.length() - 1);
        }
        return script;
    }

    @Override
    protected List<ColumnType> queryColumnDefs(String tableName, boolean firstAsPK) throws SQLException {
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
                    ColumnType ct = new PostgreSQLColumnType();
                    ct.setPk(pks.contains(columnName));
                    ct.setColumnName(columnName);
                    ct.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
                    ct.setDataTypeCode(rs.getInt("DATA_TYPE"));
                    ct.setDataTypeName(rs.getString("TYPE_NAME"));
                    ct.setNullable("1".equals(rs.getString("NULLABLE")));
                    ct.setColumnSize(rs.getInt("COLUMN_SIZE"));

                    switch (rs.getInt("DATA_TYPE")) {
                        case -5:    // int8
                            ct.setDataType(DataType.LONG);
                            break;
                        case 2:     // numeric
                            ct.setDataType(DataType.NUMERIC);
                            break;
                        case 4:     // int4
                        case 5:     // int2
                            ct.setDataType(DataType.INTEGER);
                            break;
                        case 7:     // float4
                            ct.setDataType(DataType.FLOAT);
                            break;
                        case 8:     // float8
                            ct.setDataType(DataType.DOUBLE);
                            break;
                        case 12:    // varchar,text
                            if (ct.getColumnSize() > Integer.MAX_VALUE / 2) {
                                ct.setDataType(DataType.BLOB);
                            }
                            else {
                                ct.setDataType(DataType.VARCHAR);
                            }
                            break;
                        case 91:    // date
                            ct.setDataType(DataType.DATE);
                            break;
                        case 92:    // time
                            ct.setDataType(DataType.TIME);
                            break;
                        case 93:    // timestamp
                            ct.setDataType(DataType.TIMESTAMP);
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
    public String generateCreateTableSQL(TableType table) {
        if (table == null) {
            return null;
        }

        ArrayList<String> pks = new ArrayList<String>();

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE \"" + table.getTableName().toLowerCase() + "\"\n(\n");
        for (ColumnType ct : table.getColumns()) {
            if (ct.isPk()) {
                pks.add(ct.getColumnName().toLowerCase());
            }
            sb.append(prepareColumnDef(ct));
        }

        // TODO: without PK
        String pkSQL = String.format(" CONSTRAINT %s_pkey PRIMARY KEY (%s)\n",
                table.getTableName().toLowerCase(),
                String.join(",", pks));
        sb.append(pkSQL).append(");");

        return sb.toString();
    }

    private String prepareColumnDef(ColumnType ct) {
        String type = "";
        switch (ct.getDataType()) {
            case LONG:
                type = "bigint";
            case NUMERIC:
                type = "numeric";
            case FLOAT:
                type = "real";
            case DOUBLE:
                type = "double precision";
                break;
            case INTEGER:
                type = "integer";
                break;
            case DATE:
                type = "date";
                break;
            case TIME:
                type = "time without time zone";
            case TIMESTAMP:
                type = "timestamp without time zone";
                break;
            case NVARCHAR:
            case NVARCHAR2:
            case VARCHAR:
            case VARCHAR2:
                type = "character varying(" + (ct.getColumnSize() == 0 ? 32 : ct.getColumnSize()) + ")";
                break;
            case BLOB:
                type = "text";
                break;
            default:
                throw new NullPointerException(ct.getColumnName() + " type not found");

        }

        String nullable = "";
        if (ct.isPk() || !ct.isNullable()) {
            nullable = " NOT NULL";
        }

        return " \"" + ct.getColumnName().toLowerCase() + "\" " + type + nullable + ",\n";
    }
}
