package uia.utils.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableType {

    private final String tableName;

    private final List<ColumnType> columns;

    TableType(String tableName, List<ColumnType> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public String getTableName() {
        return this.tableName;
    }

    public List<ColumnType> getColumns() {
        return this.columns;
    }

    public CompareResult sameAs(TableType table) {
        CompareResult cr = new CompareResult(this.tableName);
        if (table == null || !this.tableName.equals(table.getTableName())) {
            cr.setPassed(false);
            cr.addMessage("tableName mismatch");
            return cr;
        }

        if (this.columns.size() != table.columns.size()) {
            cr.setPassed(false);
            cr.addMessage("columnCount mismatch");
            return cr;
        }

        try {
            Map<String, ColumnType> source = this.columns.stream().collect(Collectors.toMap(c -> c.getColumnName(), c -> c));
            Map<String, ColumnType> target = table.columns.stream().collect(Collectors.toMap(c -> c.getColumnName(), c -> c));
            for (String key : source.keySet()) {
                ColumnType ct1 = source.get(key);
                ColumnType ct2 = target.containsKey(key) ? target.get(key) : null;
                ct1.sameAs(ct2, cr);
            }
        }
        catch (Exception ex) {
            cr.setPassed(false);
            cr.addMessage(ex.getMessage());
        }

        return cr;
    }

    public String generateSelectSQL() {
        ArrayList<String> cs = new ArrayList<String>();
        for (ColumnType column : this.columns) {
            cs.add(column.getColumnName().toLowerCase());
        }

        return String.format("SELECT %s FROM %s", String.join(",", cs), this.tableName.toLowerCase());
    }

    public String generateInsertSQL() {
        ArrayList<String> cs = new ArrayList<String>();
        ArrayList<String> ps = new ArrayList<String>();
        for (ColumnType column : this.columns) {
            cs.add(column.getColumnName().toLowerCase());
            ps.add("?");
        }

        return String.format("INSERT INTO %s(%s) VALUES (%s)",
                this.tableName.toLowerCase(),
                String.join(",", cs),
                String.join(",", ps));
    }

    public String generateUpdateSQL() {
        ArrayList<String> pks = new ArrayList<String>();
        for (ColumnType column : this.columns) {
            if (column.isPk()) {
                pks.add(column.getColumnName().toLowerCase());
            }
        }

        ArrayList<String> cs = new ArrayList<String>();
        ArrayList<String> ws = new ArrayList<String>();
        for (ColumnType column : this.columns) {
            String columnName = column.getColumnName().toLowerCase();
            if (pks.size() == 0 || pks.contains(columnName)) {
                pks.add(columnName);
                ws.add(columnName + "=?");
            }
            else {
                cs.add(columnName + "=?");
            }
        }

        return String.format("UPDATE %s SET %s WHERE %s",
                this.tableName.toLowerCase(),
                String.join(",", cs),
                String.join(" AND ", ws));
    }
}
