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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.junit.Test;

import uia.utils.dao.where.SimpleWhere;
import uia.utils.dao.where.SimpleStatement;
import uia.utils.dao.where.WhereOr;

public class StatementTest {

    @Test
    public void test1() throws SQLException {
        SimpleWhere where = SimpleWhere.createAnd()
                .eq("A", "A1")
                .eq("B", "B1")
                .between("TIME", new Date(), new Date());

        SimpleStatement stat = new SimpleStatement();
        stat.where(where).orderBy("A");
        stat.where(where).groupBy("A");

        try (Connection conn = createDB()) {
            PreparedStatement ps = stat.prepare(conn, "select * from IVP");
            System.out.println(ps);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void test2() throws SQLException {
        SimpleWhere and1 = SimpleWhere.createAnd()
                .eq("A", "A1")
                .eq("B", "B1");

        SimpleWhere and2 = SimpleWhere.createAnd()
                .eq("C", "C1")
                .between("TIME", new Date(), new Date());

        WhereOr where = new WhereOr().add(and1).add(and2);

        SimpleStatement stat = new SimpleStatement();
        stat.where(where).orderBy("A").orderBy("B");

        try (Connection conn = createDB()) {
            PreparedStatement ps = stat.prepare(conn, "select * from ivp");
            System.out.println(ps);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Connection createDB() {
        return null;
    }
}
