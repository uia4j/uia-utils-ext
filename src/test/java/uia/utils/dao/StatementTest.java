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

import org.junit.Test;

import uia.utils.dao.where.SimpleWhere;
import uia.utils.dao.where.Where;
import uia.utils.dao.where.SimpleStatement;

public class StatementTest {

    @Test
    public void test1() throws SQLException {
        SimpleWhere where = Where.simpleAnd()
                .eq("f1", "value1")
                .eq("f2", "value2");

        try (Connection conn = createDB()) {
            PreparedStatement ps = new SimpleStatement("SELECT f1,f2,f3,f4 FROM table_name")
            	.where(where)
            	.groupBy("A")
            	.prepare(conn);
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
