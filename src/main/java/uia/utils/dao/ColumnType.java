package uia.utils.dao;

public class ColumnType {

    private boolean pk;

    private String columnName;

    private String propertyName;

    private int dataType;

    private String dataTypeName;

    private int columnSize;

    private boolean nullable;

    private int decimalDigits;

    public boolean isPk() {
        return this.pk;
    }

    public boolean sameAs(ColumnType ct, CompareResult cr) {
        if (ct == null) {
            cr.setPassed(false);
            cr.addMessage(this.columnName + " not found");
            return false;
        }
        if (!this.columnName.equals(ct.columnName)) {
            cr.setPassed(false);
            cr.addMessage(this.columnName + " columnName not the same");
            return false;
        }
        if (this.nullable != ct.nullable) {
            cr.setPassed(false);
            cr.addMessage(this.columnName + " nullable not the same");
            return false;
        }
        if (this.pk != ct.pk) {
            cr.setPassed(false);
            cr.addMessage(this.columnName + " pk not the same");
            return false;
        }

        // String
        if (this.dataTypeName.contains("VARCHAR") && ct.dataTypeName.contains("VARCHAR")) {
            return true;
        }
        // Timestamp
        if (this.dataTypeName.contains("TIMESTAMP") && ct.dataTypeName.contains("TIMESTAMP")) {
            return true;
        }

        if (this.decimalDigits != ct.decimalDigits) {
            cr.setPassed(false);
            cr.addMessage(String.format("%s  decimalDigits not the same, %s != %s",
                    this.columnName,
                    this.decimalDigits,
                    ct.decimalDigits));
            return false;
        }

        if (this.dataType != ct.dataType) {
            cr.setPassed(false);
            cr.addMessage(this.columnName + " dataType not the same");
            return false;
        }
        if (this.columnSize != ct.columnSize) {
            cr.setPassed(false);
            cr.addMessage(this.columnName + " columnSize not the same");
            return false;
        }

        return true;

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

    public int getDataType() {
        return this.dataType;
    }

    /**
     * 3. NUMBER
     * 12. VARCHAR
     * 93. TIMESTAMP
     * -101. TIMESTAMP WITH TIME ZONE
     * @param dataType
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
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

    public int getColumnSize() {
        return this.columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public int getDecimalDigits() {
        return this.decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public String getPsSet(int index) {
        if (this.dataType == 3) {
            if (this.columnSize >= 18) {
                this.dataType = 10003;
            }
            else if (this.columnSize >= 10) {
                this.dataType = 1003;
            }
        }
        switch (this.dataType) {
            case -101:
            case 93:
                return String.format("ps.setTimestamp(%s, new Timestamp(data.get%s().getTime()));",
                        index,
                        this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1));
            case 3:
                return String.format("ps.setInt(%s, data.get%s());",
                        index,
                        this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1));
            case 1003:
                return String.format("ps.setLong(%s, data.get%s());",
                        index,
                        this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1));
            case 10003:
                return String.format("ps.setBigDecimal(%s, data.get%s());",
                        index,
                        this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1));
            default:
                return String.format("ps.setString(%s, data.get%s());",
                        index,
                        this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1));
        }
    }

    public String getPsSetEx(int index) {
        if (this.dataType == 3) {
            if (this.columnSize >= 18) {
                this.dataType = 10003;
            }
            else if (this.columnSize >= 10) {
                this.dataType = 1003;
            }
        }
        switch (this.dataType) {
            case -101:
            case 93:
                return String.format("ps.setTimestamp(%s, %s);",
                        index,
                        this.propertyName);
            case 3:
                return String.format("ps.setInt(%s, %s);",
                        index,
                        this.propertyName);
            case 1003:
                return String.format("ps.setLong(%s, %s);",
                        index,
                        this.propertyName);
            case 10003:
                return String.format("ps.setBigDecimal(%s, %s);",
                        index,
                        this.propertyName);
            default:
                return String.format("ps.setString(%s, %s);",
                        index,
                        this.propertyName);
        }
    }

    public String getRsGet(String index) {
        String type = "String";
        if (this.dataType == 3) {
            if (this.columnSize >= 18) {
                this.dataType = 10003;
            }
            else if (this.columnSize >= 10) {
                this.dataType = 1003;
            }
        }
        switch (this.dataType) {
            case 3:
                type = "Int";
                break;
            case 1003:
                type = "Long";
                break;
            case 10003:
                type = "BigDecimal";
                break;
            case -101:
            case 93:
                type = "Timestamp";
                break;
        }

        String rsGet = String.format("data.set%s(rs.get%s(%s));",
                this.propertyName.substring(0, 1).toUpperCase() + this.propertyName.substring(1),
                type,
                index);
        return rsGet;
    }

    public String getMemberDef() {
        String type = "String";
        if (this.dataType == 3) {
            if (this.columnSize >= 18) {
                this.dataType = 10003;
            }
            else if (this.columnSize >= 10) {
                this.dataType = 1003;
            }
        }
        switch (this.dataType) {
            case 3:
                type = this.nullable ? "Integer" : "int";
                break;
            case 1003:
                type = this.nullable ? "Long" : "long";
                break;
            case 10003:
                type = "BigDecimal";
                break;
            case -101:
            case 93:
                type = "Date";
                break;
        }

        return String.format("%s %s", type, this.propertyName);
    }

    @Override
    public String toString() {
        return String.format("%-30s, pk:%-5s, %2s, %-15s [%s]", this.columnName, this.pk, this.dataType, this.dataTypeName, this.columnSize);
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
