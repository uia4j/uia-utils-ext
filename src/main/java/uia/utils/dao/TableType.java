/*******************************************************************************
 * Copyright 2018 UIA
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package uia.utils.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Table structure.
 *
 * @author Kyle K. Lin
 *
 */
public class TableType {

    private final String tableName;

    private final List<ColumnType> columns;

    /**
     * Constructor.
     * @param tableName Table name.
     * @param columns Definition of columns.
     */
    public TableType(String tableName, List<ColumnType> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    /**
     * Get table name.
     * @return Table name.
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * select columns of primary key.
     * @return Columns.
     */
    public List<ColumnType> selectPk() {
        return this.columns.stream()
                .filter(c -> c.isPk())
                .collect(Collectors.toList());
    }

    /**
     * select columns of primary key.
     * @return Names of columns.
     */
    public List<String> selectPkNames() {
        return this.columns.stream()
                .filter(c -> c.isPk())
                .map(c -> c.columnName)
                .collect(Collectors.toList());
    }

    /**
     * Get column Definition.
     * @return
     */
    public List<ColumnType> getColumns() {
        return this.columns;
    }

    /**
     * Compare two tables.
     * @param table Table to be compared.
     * @return Result.
     */
    public CompareResult sameAs(TableType table) {
        return sameAs(table, ComparePlan.table());
    }

    /**
     * Compare two tables.
     * @param table Table to be compared.
     * @param plan Plan.
     * @return Result.
     */
    public CompareResult sameAs(TableType table, ComparePlan plan) {
        CompareResult cr = new CompareResult(this.tableName);
        if (table == null || !this.tableName.equalsIgnoreCase(table.getTableName())) {
            cr.setMissing(true);
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
            Map<String, ColumnType> source = this.columns.stream().collect(Collectors.toMap(c -> c.getColumnName().toUpperCase(), c -> c));
            Map<String, ColumnType> target = table.columns.stream().collect(Collectors.toMap(c -> c.getColumnName().toUpperCase(), c -> c));
            for (String key : source.keySet()) {
                ColumnType ct1 = source.get(key);
                ColumnType ct2 = target.containsKey(key) ? target.get(key) : null;
                ct1.sameAs(ct2, plan, cr);
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

        if (cs.isEmpty()) {
            return null;
        }

        return String.format("UPDATE %s SET %s WHERE %s",
                this.tableName.toLowerCase(),
                String.join(",", cs),
                String.join(" AND ", ws));
    }

    public String generateDeleteSQL() {
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

        return String.format("DELETE FROM %s WHERE %s",
                this.tableName.toLowerCase(),
                String.join(" AND ", ws));
    }
}
