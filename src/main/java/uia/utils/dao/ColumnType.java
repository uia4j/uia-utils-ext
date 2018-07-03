package uia.utils.dao;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.SQLException;

public abstract class ColumnType {

    public enum DataType {

        VARCHAR,

        NVARCHAR,

        VARCHAR2,

        NVARCHAR2,

        INTEGER,

        LONG,

        NUMERIC,

        FLOAT,

        DOUBLE,

        TIMESTAMP,

        DATE,

        TIME,

        BLOB,

        CLOB,

        NCLOB,

        OTHERS;

        DataType() {
        }
    }

    protected boolean pk;

    protected String columnName;

    protected String propertyName;

    protected DataType dataType;

    protected int dataTypeCode;

    protected String dataTypeName;

    protected long columnSize;

    protected boolean nullable;

    protected int decimalDigits;

    public boolean isPk() {
        return this.pk;
    }

    public void setPk(boolean pk) {
        this.pk = pk;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
        this.propertyName = convert(this.columnName);
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public DataType getDataType() {
        return this.dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public int getDataTypeCode() {
        return this.dataTypeCode;
    }

    public void setDataTypeCode(int dataTypeCode) {
        this.dataTypeCode = dataTypeCode;
    }

    public String getDataTypeName() {
        return this.dataTypeName;
    }

    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public long getColumnSize() {
        return this.columnSize;
    }

    public void setColumnSize(long columnSize) {
        this.columnSize = columnSize;
    }

    public int getDecimalDigits() {
        return this.decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    /**
     * Check if this column is string including NVARCHAR, NVARCHAR2, VARCHAR, VARCHAR2.
     * @return Result.
     */
    public boolean isStringType() {
        // THINK: include BLOB?
        switch (this.dataType) {
            case NVARCHAR:
            case NVARCHAR2:
            case VARCHAR:
            case VARCHAR2:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if this column is date-time including Date, TIME, TIMESTAMP.
     * @return Result.
     */
    public boolean isDateTimeType() {
        switch (this.dataType) {
            case DATE:
            case TIME:
            case TIMESTAMP:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if this column is numeric including INTEGER, LONG, NUMERIC, FLOAT, DOUBLE.
     * @return Result.
     */
    public boolean isNumericType() {
        switch (this.dataType) {
            case INTEGER:
            case LONG:
            case NUMERIC:
            case FLOAT:
            case DOUBLE:
                return true;
            default:
                return false;
        }
    }

    public String getJavaTypeName() {
        String type = "String";
        switch (this.dataType) {
            case INTEGER:
                type = this.nullable ? "Integer" : "int";
                break;
            case LONG:
                type = this.nullable ? "Long" : "long";
                break;
            case NUMERIC:
            case FLOAT:
            case DOUBLE:
                type = "BigDecimal";
                break;
            case DATE:
            case TIME:
            case TIMESTAMP:
                type = "Date";
                break;
            case CLOB:
                type = "Clob";
                break;
            case NCLOB:
                type = "NClob";
                break;
            case BLOB:
                type = "byte[]";
                break;
            default:
                type = "String";
        }

        return String.format("%s %s", type, this.propertyName);
    }

    public Object read(Connection conn, Object orig) throws SQLException {
        switch (this.dataType) {
            case CLOB:
                Clob clob = conn.createClob();
                clob.setString(1, orig.toString());
                return clob;
            case NCLOB:
                NClob nclob = conn.createNClob();
                nclob.setString(1, orig.toString());
                return nclob;
            default:
                return orig;
        }
    }

    public boolean sameAs(ColumnType targetColumn, ComparePlan plan, CompareResult cr) {
        if (targetColumn == null) {
            cr.setPassed(false);
            cr.addMessage(this.columnName + " not found");
            return false;
        }

        // column name
        if (!this.columnName.equalsIgnoreCase(targetColumn.getColumnName())) {
            cr.setPassed(false);
            cr.addMessage(this.columnName + " columnName not the same");
            return false;
        }

        // null
        if (plan.checkNullable) {
            if (this.nullable != targetColumn.isNullable()) {
                cr.setPassed(false);
                cr.addMessage(String.format("%s nullable not the same: (%s,%s)",
                        this.columnName,
                        this.nullable,
                        targetColumn.isNullable()));
                return false;
            }
        }

        // primary key
        if (this.pk != targetColumn.isPk()) {
            cr.setPassed(false);
            cr.addMessage(String.format("%s pk not the same: (%s,%s)",
                    this.columnName,
                    this.pk,
                    targetColumn.isPk()));
            return false;
        }

        // string
        if (!plan.strictVarchar && isStringType() && targetColumn.isStringType()) {
            return true;
        }

        // numeric
        if (!plan.strictNumeric && isNumericType() && targetColumn.isNumericType()) {
            return true;
        }

        // data, time, timestamp
        if (!plan.strictDateTime && isDateTimeType() && targetColumn.isDateTimeType()) {
            return true;
        }

        // data type
        if (this.dataType != targetColumn.getDataType()) {
            cr.setPassed(false);
            cr.addMessage(String.format("%s dataType not the same: (%s,%s)",
                    this.columnName,
                    this.dataType,
                    targetColumn.getDataType()));
            return false;
        }

        // date/time
        if (isDateTimeType() && targetColumn.isDateTimeType()) {
            return true;
        }

        // BLOB, CLOB, NCLOB
        if (this.dataType == DataType.BLOB || this.dataType == DataType.CLOB || this.dataType == DataType.NCLOB) {
            return true;
        }

        // decimal digit
        if (this.decimalDigits != targetColumn.getDecimalDigits()) {
            cr.setPassed(false);
            cr.addMessage(String.format("%s decimalDigits not the same, (%s:%s,%s:%s)",
                    this.columnName,
                    this.dataTypeName,
                    this.decimalDigits,
                    targetColumn.getDataTypeName(),
                    targetColumn.getDecimalDigits()));
            return false;
        }

        // column size
        if (plan.checkDataSize) {
            if (this.columnSize != targetColumn.getColumnSize()) {
                cr.setPassed(false);
                cr.addMessage(String.format("%s columnSize not the same: (%s,%s)",
                        this.columnName,
                        this.columnSize,
                        targetColumn.getColumnSize()));
                return false;
            }
        }

        return true;
    }

    String genPsSet(int index) {
        switch (this.dataType) {
            case DATE:
            case TIME:
            case TIMESTAMP:
                return String.format("ps.setTimestamp(%s, new Timestamp(data.get%s().getTime()));",
                        index,
                        this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1));
            case INTEGER:
                return String.format("ps.setInt(%s, data.get%s());",
                        index,
                        this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1));
            case LONG:
                return String.format("ps.setLong(%s, data.get%s());",
                        index,
                        this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1));
            case NUMERIC:
            case FLOAT:
            case DOUBLE:
                return String.format("ps.setBigDecimal(%s, data.get%s());",
                        index,
                        this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1));
            case CLOB:
                return String.format("ps.setClob(%s, data.get%s());",
                        index,
                        this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1));
            case NCLOB:
                return String.format("ps.setNClob(%s, data.get%s());",
                        index,
                        this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1));
            case BLOB:
                return String.format("ps.setBlob(%s, data.get%s());",
                        index,
                        this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1));

            default:
                return String.format("ps.setString(%s, data.get%s());",
                        index,
                        this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1));
        }
    }

    String genPsSetEx(int index) {
        switch (this.dataType) {
            case DATE:
            case TIME:
            case TIMESTAMP:
                return String.format("ps.setTimestamp(%s, new Timestamp(%s.getTime()));",
                        index,
                        this.propertyName);
            case INTEGER:
                return String.format("ps.setInt(%s, %s);",
                        index,
                        this.propertyName);
            case LONG:
                return String.format("ps.setLong(%s, %s);",
                        index,
                        this.propertyName);
            case NUMERIC:
            case FLOAT:
            case DOUBLE:
                return String.format("ps.setBigDecimal(%s, %s);",
                        index,
                        this.propertyName);
            case CLOB:
                return String.format("ps.setClob(%s, %s);",
                        index,
                        this.propertyName);
            case NCLOB:
                return String.format("ps.setNClob(%s, %s);",
                        index,
                        this.propertyName);
            case BLOB:
                return String.format("ps.setBlob(%s, %s);",
                        index,
                        this.propertyName);
            default:
                return String.format("ps.setString(%s, %s);",
                        index,
                        this.propertyName);
        }
    }

    String genRsGet(String index) {
        String type = "String";
        switch (this.dataType) {
            case INTEGER:
                type = "Int";
                break;
            case LONG:
                type = "Long";
                break;
            case NUMERIC:
            case FLOAT:
            case DOUBLE:
                type = "BigDecimal";
                break;
            case DATE:
            case TIME:
            case TIMESTAMP:
                type = "Timestamp";
                break;
            case CLOB:
                type = "Clob";
                break;
            case NCLOB:
                type = "NClob";
                break;
            case BLOB:
                type = "Blob";
                break;
            default:
                type = "String";
        }

        String rsGet = String.format("data.set%s(rs.get%s(%s));",
                this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1),
                type,
                index);
        return rsGet;
    }

    @Override
    public String toString() {
        return String.format("%-30s, pk:%-5s, %-9s, %4s:%-13s [%s]",
                this.columnName,
                this.pk,
                this.dataType,
                this.dataTypeCode,
                this.dataTypeName,
                this.columnSize);
    }

    private static String convert(String columnName) {
        String result = "";
        int x = columnName.indexOf("_");
        if (x < 0) {
            return columnName.toLowerCase();
        }
        else {
            result = columnName.substring(0, x).toLowerCase();
            columnName = columnName.substring(x + 1);
        }

        x = columnName.indexOf("_");
        while (x > 0) {
            result += columnName.substring(0, 1).toUpperCase() + columnName.substring(1, x).toLowerCase();
            columnName = columnName.substring(x + 1);
            x = columnName.indexOf("_");
        }
        result += columnName.substring(0, 1).toUpperCase() + columnName.substring(1).toLowerCase();
        return result;
    }
}
