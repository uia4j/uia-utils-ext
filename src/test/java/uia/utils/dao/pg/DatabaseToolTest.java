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
package uia.utils.dao.pg;

import java.sql.SQLException;

import org.junit.Test;

import uia.utils.dao.Database;
import uia.utils.dao.DatabaseTool;

/**
 *
 * @author Kyle K. Lin
 *
 */
public class DatabaseToolTest {

    @Test
    public void testToTableScript() throws Exception {
        Database db = pg();
        DatabaseTool tool = new DatabaseTool(db);
        tool.toTableScript("d:/temp/authdb.sql");
    }

    @Test
    public void testToJava() throws Exception {
        Database db = pg();
        DatabaseTool tool = new DatabaseTool(db);
        tool.table2Java("d:/temp/test", "a", false);
    }

    @Test
    public void testToDAO() throws Exception {
        Database db = pg();
        DatabaseTool tool = new DatabaseTool(db);
        tool.table2DAO("d:/temp/test", "a.dao", "a");
    }

    private Database pg() throws SQLException {
        return new PostgreSQL("localhost", "5432", "scmdb", "scm", "scmAdmin");
    }

}
