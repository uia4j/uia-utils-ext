package uia.utils.dao.hana;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uia.utils.dao.AbstractDatabase;
import uia.utils.dao.ColumnType;
import uia.utils.dao.ColumnType.DataType;
import uia.utils.dao.TableType;

public class Hana extends AbstractDatabase {

    static {
        try {
            Class.forName("com.sap.db.jdbc.Driver");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Hana(String schema, Connection conn) throws SQLException {
        super(schema, conn);
    }

    public Hana(String host, String port, String service, String user, String pwd) throws SQLException {
        super(user, DriverManager.getConnection("jdbc:sap://" + host + ":" + port, user, pwd));
    }

    @Override
    public String selectViewScript(String viewName) throws SQLException {
        String script = null;
        PreparedStatement ps = this.conn.prepareStatement("SELECT definition FROM VIEWS WHERE schema_name=? AND view_name=?");
        ps.setString(1, this.schema);
        ps.setString(2, viewName);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            script = rs.getString(1);
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
                    ColumnType ct = new HanaColumnType();
                    ct.setPk(pks.contains(columnName));
                    ct.setColumnName(columnName);
                    ct.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
                    ct.setDataTypeCode(rs.getInt("DATA_TYPE"));
                    ct.setDataTypeName(rs.getString("TYPE_NAME"));
                    ct.setNullable("1".equals(rs.getString("NULLABLE")));
                    ct.setColumnSize(rs.getInt("COLUMN_SIZE"));

                    switch (rs.getInt("DATA_TYPE")) {
                        case -9:
                            ct.setDataType(DataType.NVARCHAR);
                            break;
                        case 3:
                            ct.setDataType(DataType.NUMERIC);
                            break;
                        case 12:
                            ct.setDataType(DataType.VARCHAR);
                            break;
                        case 93:
                            ct.setDataType(DataType.TIMESTAMP);
                            break;
                        case 2005:
                            ct.setDataType(DataType.BLOB);
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
        sb.append("CREATE COLUMN TABLE \"" + table.getTableName().toUpperCase() + "\"\n(\n");
        for (ColumnType ct : table.getColumns()) {
            if (ct.isPk()) {
                pks.add(ct.getColumnName());
            }
            sb.append(prepareHanaColumnDef(ct));
        }
        // TODO: without PK
        sb.append(" PRIMARY KEY (\"" + String.join("\",\"", pks) + "\")\n);");
        return sb.toString();
    }

    private String prepareHanaColumnDef(ColumnType ct) {
        String type = "";
        switch (ct.getDataType()) {
            case INTEGER:
            case LONG:
            case NUMERIC:
            case FLOAT:
            case DOUBLE:
                long cs = ct.getColumnSize();
                type = "DECIMAL("
                        + (cs >= 38 || cs <= 0 ? 38 : ct.getColumnSize())
                        + ","
                        + ct.getDecimalDigits()
                        + ")";
                break;
            case DATE:
                type = "SECONDDATE";
                break;
            case TIME:
            case TIMESTAMP:
                type = "TIMESTAMP";
                break;
            case NVARCHAR:
            case NVARCHAR2:
            case VARCHAR:
            case VARCHAR2:
                type = "NVARCHAR(" + (ct.getColumnSize() == 0 ? 32 : ct.getColumnSize()) + ")";
                break;
            case BLOB:
                type = "CLOB";
                break;
            default:
                throw new NullPointerException(ct.getColumnName() + " type not found");

        }

        String nullable = "";
        if (ct.isPk() || !ct.isNullable()) {
            nullable = " NOT NULL";
        }

        return " \"" + ct.getColumnName().toUpperCase() + "\" " + type + nullable + ",\n";
    }
}
