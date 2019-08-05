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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

public abstract class AbstractDatabase implements Database {

    protected final Connection conn;

    protected String schema;

    private DataSource dataSource;

    protected AbstractDatabase(String driverName, String url, String user, String pwd, String schema) throws SQLException {
        if (user != null) {
            this.conn = DriverManager.getConnection(url, user, pwd);
            this.dataSource = createDataSource(driverName, url, user, pwd);
            this.schema = schema;
        }
        else {
            this.conn = null;
            this.dataSource = null;
            this.schema = schema;
        }
    }

    @Override
    public void close() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    @Override
    public Connection getConnection() {
        return this.conn;
    }

    @Override
    public Connection getConnectionFromPool() throws SQLException {
        return this.dataSource.getConnection();
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
        if (prefix == null || prefix.trim().length() == 0) {
            return selectTableNames();
        }

        ArrayList<String> tables = new ArrayList<String>();
        try (ResultSet rs = this.conn.getMetaData().getTables(null, this.schema, upperOrLower(prefix) + "%", new String[] { "TABLE" })) {
            while (rs.next()) {
                String tn = rs.getString(3);
                if (tn.startsWith(prefix)) {
                    tables.add(tn);
                }
            }
        }
        return tables;
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
        if (prefix == null || prefix.trim().length() == 0) {
            return selectViewNames();
        }

        ArrayList<String> views = new ArrayList<String>();
        try (ResultSet rs = this.conn.getMetaData().getTables(null, this.schema, upperOrLower(prefix) + "%", new String[] { "VIEW" })) {
            while (rs.next()) {
                String vn = rs.getString(3);
                if (vn.startsWith(prefix)) {
                    views.add(vn);
                }
            }
        }
        return views;
    }

    @Override
    public boolean exists(String tableOrView) throws SQLException {
        try (ResultSet rs = this.conn.getMetaData().getTables(null, this.schema, upperOrLower(tableOrView), new String[] { "TABLE", "VIEW" })) {
            return rs.next();
        }
    }

    @Override
    public TableType selectTable(String tableOrView, boolean firstAsPK) throws SQLException {
    	String comment = null;
        try (ResultSet rs = this.conn.getMetaData().getTables(null, this.schema, upperOrLower(tableOrView), new String[] { "TABLE", "VIEW" })) {
        	if(!rs.next()) {
        		return null;
        	}
            comment = rs.getString("REMARKS");
        }
    	
        List<ColumnType> columns = selectColumns(upperOrLower(tableOrView), firstAsPK);
        return columns.size() == 0 ? null : new TableType(upperOrLower(tableOrView), comment, columns);
    }

    @Override
    public int createTable(TableType table) throws SQLException {
        String script = generateCreateTableSQL(table);
        try(PreparedStatement ps = this.conn.prepareStatement(script)) {
            return ps.executeUpdate();
        }
    }

    @Override
    public int alterTableColumns(String tableName, List<ColumnType> columns) throws SQLException {
        String script = generateAlterTableSQL(tableName, columns);
        try(PreparedStatement ps = this.conn.prepareStatement(script)) {
        	return ps.executeUpdate();
        }
    }

    @Override
    public int dropTable(String tableName) throws SQLException {
    	try(PreparedStatement ps = this.conn.prepareStatement("DROP TABLE " + upperOrLower(tableName))) {
    		return ps.executeUpdate();
    	}
    }

    @Override
    public int createView(String viewName, String sql) throws SQLException {
        String script = String.format("CREATE VIEW \"%s\" (\n%s\n)", 
        		upperOrLower(viewName), 
        		sql);
        try(PreparedStatement ps = this.conn.prepareStatement(script)) {
        	return ps.executeUpdate();
        }
    }

    @Override
    public int dropView(String viewName) throws SQLException {
    	try(PreparedStatement ps = this.conn.prepareStatement("DROP VIEW " + upperOrLower(viewName))) {
    		return ps.executeUpdate();
    	}
    }

    @Override
    public int[] executeBatch(List<String> sqls) throws SQLException {
        try(java.sql.Statement state = this.conn.createStatement()) {
	        for (String sql : sqls) {
	            state.addBatch(sql);
	        }
	        return state.executeBatch();
        }
    }

    @Override
    public int[] executeBatch(String sql, List<List<Object>> rows) throws SQLException {
        try(PreparedStatement ps = this.conn.prepareStatement(sql)) {
	        for (List<Object> row : rows) {
	            int i = 1;
	            for (Object col : row) {
	                ps.setObject(i++, col);
	            }
	            ps.addBatch();
	        }
	        return ps.executeBatch();
        }
    }

    protected abstract String upperOrLower(String value);

    private DataSource createDataSource(String driverName, String connectURI, String user, String pwd) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(connectURI);
        dataSource.setUsername(user);
        dataSource.setPassword(pwd);
        dataSource.setInitialSize(10);
        dataSource.setDriverClassName(driverName);
        dataSource.setMaxTotal(50);
        return dataSource;
    }
}
