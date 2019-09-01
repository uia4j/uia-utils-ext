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

import uia.utils.dao.CamelNaming;
import uia.utils.dao.Database;
import uia.utils.dao.JavaClassPrinter;

/**
 *
 * @author Kyle K. Lin
 *
 */
public class JavaClassPrinterTest {

    @Test
    public void testGenerateTable1() throws Exception {
        Database db = pg();
        String tableName = "factory";
        String dtoPackage = "gs.swim.db";
        String daoPackage = "gs.swim.db.dao";

        JavaClassPrinter.Result result = new JavaClassPrinter(db, tableName).generate(
                daoPackage,
                dtoPackage,
                CamelNaming.upper(tableName),
                false);
        System.out.println(result.dao);
        System.out.println(result.dto);
    }

    @Test
    public void testGenerateTable2() throws Exception {
        Database db = pg();
        String tableName = "part_group_part";
        String dtoPackage = "uia.utils";
        String daoPackage = "uia.utils.dao";

        JavaClassPrinter.Result result = new JavaClassPrinter(db, tableName).generate(
                daoPackage,
                dtoPackage,
                CamelNaming.upper(tableName));
        System.out.println(result.dao);
        System.out.println(result.dto);
    }

    @Test
    public void testGenerateView() throws Exception {
        Database db = pg();
        String viewName = "view_factory";
        String dtoPackage = "gs.swim.db";
        String daoPackage = "gs.swim.db.dao";

        JavaClassPrinter.Result result = new JavaClassPrinter(db, viewName).generate4View(
                daoPackage,
                dtoPackage,
                CamelNaming.upper(viewName));
        System.out.println(result.dao);
        System.out.println(result.dto);
    }

    private Database pg() throws SQLException {
        return new PostgreSQL("localhost", "5432", "scmdb", "scm", "scmAdmin");
    }

}
