/*******************************************************************************
 * Copyright 2019 UIA
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import uia.utils.dao.sqlite.SQLite;
import uia.utils.dao.where.SimpleWhere;
import uia.utils.dao.where.Where;

/**
 *
 * @author Kyle K. Lin
 *
 */
public class SelectStatementTest {

    @Test
    public void test1() throws Exception {
        SimpleWhere where = Where.simpleAnd()
                .eq("state_name", "on");

        try (Database db = createDB()) {
            SelectStatement select = new SelectStatement("SELECT id,equip_group_id,state_name,sub_state_name FROM equip")
                    .where(where)
                    .orderBy("id");
            try (PreparedStatement ps = select.prepare(db.getConnection())) {
                try (ResultSet rs = ps.executeQuery()) {
                    Assert.assertFalse(rs.next());
                }
            }
        }
    }

    @Test
    public void test2() throws Exception {
        SimpleWhere where = Where.simpleAnd()
                .eq("state_name", "on");

        try (Database db = createDB()) {
            SelectStatement select = new SelectStatement("SELECT equip_group_id FROM equip")
                    .where(where)
                    .groupBy("equip_group_id");
            try (PreparedStatement ps = select.prepare(db.getConnection())) {
                try (ResultSet rs = ps.executeQuery()) {
                    Assert.assertFalse(rs.next());
                }
            }
        }
    }

    private Database createDB() throws SQLException {
        return new SQLite("test/sqlite1");
    }
}
