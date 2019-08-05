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
package uia.utils.dao.where;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SimpleStatement {
	
	private final String selectSql;

    private Where where;

    private final List<String> groups;

    private final List<String> orders;

    public SimpleStatement(String selectSql) {
    	this.selectSql = selectSql;
        this.groups = new ArrayList<String>();
        this.orders = new ArrayList<String>();
    }
    
    public void reset() {
    	this.groups.clear();
    	this.orders.clear();
    }

    public SimpleStatement where(Where where) {
        this.where = where;
        return this;
    }

    public SimpleStatement groupBy(String columnName) {
        this.groups.add(columnName);
        return this;
    }

    public SimpleStatement orderBy(String columnName) {
        this.orders.add(columnName);
        return this;
    }

    public PreparedStatement prepare(Connection conn) throws SQLException {
        String where = this.where == null ? null : this.where.generate();
        PreparedStatement ps;
        if (where == null || where.length() == 0) {
            ps = conn.prepareStatement(String.format("%s%s%s",
            		this.selectSql,
            		groupBy(),
            		orderBy()));
        }
        else {
            ps = conn.prepareStatement(String.format("%s where %s%s%s",
            		this.selectSql,
            		where,
            		groupBy(),
            		orderBy()));
            this.where.accept(ps, 1);
        }

        return ps;
    }

    public PreparedStatement prepareNoBinding(Connection conn, String selectSql) throws SQLException {
        String where = this.where == null ? null : this.where.generate();
        PreparedStatement ps;
        if (where == null || where.length() == 0) {
            ps = conn.prepareStatement(String.format("%s%s%s",
            		this.selectSql,
            		groupBy(),
            		orderBy()));
        }
        else {
            ps = conn.prepareStatement(String.format("%s where %s%s%s",
            		this.selectSql,
            		where,
            		groupBy(),
            		orderBy()));
        }

        return ps;
    }

    protected String groupBy() {
        return this.groups.size() == 0 ? "" : " group by " + String.join(",", this.groups);
    }

    protected String orderBy() {
        return this.orders.size() == 0 ? "" : " order by " + String.join(",", this.orders);
    }

    protected boolean isEmpty(Object value) {
        return value == null || value.toString().trim().length() == 0;
    }

}
