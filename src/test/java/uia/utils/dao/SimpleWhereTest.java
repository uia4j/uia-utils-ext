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
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import uia.utils.dao.where.SimpleWhere;
import uia.utils.dao.where.Where;
import uia.utils.dao.where.conditions.ConditionType;

/**
 *
 * @author Kyle K. Lin
 *
 */
public class SimpleWhereTest implements ConditionType {

    @Test
    public void testAnd1() {
        SimpleWhere and = Where.simpleAnd()
                .eq("c1", "abc")
                .eq("c2", null)
                .between("c3", "123", "456")
                .likeBegin("c4", "abc")
                .likeBegin("c5", null)
                .notEq("c7", "def")
                .notEq("c8", null)
                .add(this);
        Assert.assertEquals(
                "c1=? and (c3 between ? and ?) and c4 like ? and c7<>? and (cx='A')",
                and.generate());
    }

    @Test
    public void testAnd2() {
        SimpleWhere and = Where.simpleAnd()
                .eqOrNull("c1", "abc")
                .eqOrNull("c2", null)
                .between("c3", "123", "456")
                .likeBeginOrNull("c4", "abc")
                .likeBeginOrNull("c5", null)
                .notEqOrNull("c7", "def")
                .notEqOrNull("c8", null)
                .add(() -> "cx is not null");
        Assert.assertEquals(
                "c1=? and c2 is null and (c3 between ? and ?) and c4 like ? and c5 is null and c7<>? and c8 is not null and cx is not null",
                and.generate());
    }

    @Test
    public void testOr1() {
        SimpleWhere or = Where.simpleOr()
                .eq("c1", "abc")
                .eq("c2", null)
                .between("c3", "123", "456")
                .likeBegin("c4", "abc")
                .likeBegin("c5", null)
                .add(this);
        Assert.assertEquals(
                "c1=? or (c3 between ? and ?) or c4 like ? or (cx='A')",
                or.generate());
    }

    @Test
    public void testOr2() {
        SimpleWhere or = Where.simpleOr()
                .eqOrNull("c1", "abc")
                .eqOrNull("c2", null)
                .between("c3", "123", "456")
                .likeBeginOrNull("c4", "abc")
                .likeBeginOrNull("c5", null)
                .add(() -> "cx is not null");
        Assert.assertEquals(
                "c1=? or c2 is null or (c3 between ? and ?) or c4 like ? or c5 is null or cx is not null",
                or.generate());
    }

    @Override
    public String getStatement() {
        return "(cx='A')";
    }

    @Override
    public int accpet(PreparedStatement ps, int index) throws SQLException {
        return index;
    }

}
