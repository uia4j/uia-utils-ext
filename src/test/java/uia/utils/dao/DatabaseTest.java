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

import org.junit.Assert;
import org.junit.Test;

import uia.utils.dao.sqlite.SQLite;

/**
 *
 * @author Kyle K. Lin
 *
 */
public class DatabaseTest {

    @Test
    public void test1() throws Exception {
        try (Database db = sqlit1()) {
            Assert.assertEquals(3, db.selectTableNames().size());
            Assert.assertEquals(2, db.selectTableNames("equip").size());
            Assert.assertEquals(12, db.selectColumns("equip", false).size());
            Assert.assertNotNull(db.selectTable("equip", false));
            Assert.assertNotNull(db.selectTable("equip_group", false));
            Assert.assertNull(db.selectTable("pms", false));

            Assert.assertEquals(1, db.selectViewNames().size());
            Assert.assertEquals(1, db.selectViewNames("view_").size());
        }
    }

    @Test
    public void test2() throws Exception {
        try (Database db1 = sqlit1()) {
            try (Database db2 = sqlit2()) {
                db2.createTable(db1.selectTable("equip", false));
                Assert.assertEquals(1, db2.selectTableNames().size());
                db2.dropTable("equip");
                Assert.assertEquals(0, db2.selectTableNames().size());
            }
        }
    }

    @Test
    public void test3() throws Exception {
        try (Database db1 = sqlit1()) {
            TableType t1 = db1.selectTable("equip", false);
            try (Database db2 = sqlit2()) {
                db2.createTable(t1);
                TableType t2 = db2.selectTable("equip", false);
                db2.dropTable("equip");

                CompareResult cr = t1.sameAs(t2);
                Assert.assertTrue(cr.isPassed());
            }
        }
    }

    private Database sqlit1() throws Exception {
        return new SQLite("test/sqlite1");
    }

    private Database sqlit2() throws Exception {
        return new SQLite("test/sqlite2");
    }
}
