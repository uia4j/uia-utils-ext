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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

/**
 * Database tool.
 *
 * @author Kyle K. Lin
 *
 */
public class DaoFactoryTool {

    private final Database source;

    /**
     * Constructor.
     *
     * @param source The source database.
     */
    public DaoFactoryTool(Database source) {
        this.source = source;
    }

    /**
     * Save DTO structure of all tables to Java files.
     *
     * @param dir The source folder.
     * @param packageName The package.
     * @throws SQLException Failed to execute SQL statements.
     * @throws IOException Failed to save files.
     */
    public void tables2DTO(String dir, String packageName) throws SQLException, IOException {
        for (String t : this.source.selectTableNames()) {
            toDTO(dir, packageName, t);
        }
    }

    /**
     * Save DTO structure of all view to Java files.
     *
     * @param dir The source folder.
     * @param packageName The package.
     * @throws SQLException Failed to execute SQL statements.
     * @throws IOException Failed to save files.
     */
    public void views2DTO(String dir, String packageName) throws SQLException, IOException {
        for (String v : this.source.selectViewNames()) {
            toDTO(dir, packageName, v);
        }
    }

    /**
     * Save DTO structure of a table to a Java file.
     *
     * @param dir The source folder.
     * @param packageName The package.
     * @param tableOrView The table or view name.
     * @throws SQLException Failed to execute SQL statements.
     * @throws IOException Failed to save files.
     */
    public void toDTO(String dir, String packageName, String tableOrView) throws IOException, SQLException {
        String dtoName = CamelNaming.upper(tableOrView);
        DaoFactoryClassPrinter printer = new DaoFactoryClassPrinter(this.source, tableOrView);
        String cls = printer.generateDTO(packageName, dtoName);
        String file = new StringBuilder(dir)
                .append("/")
                .append(packageName.replace('.', '/'))
                .append("/")
                .append(dtoName)
                .append(".java").toString();
        Files.write(Paths.get(file), cls.getBytes());
    }

    /**
     * Save DTO and DAO structure of a view to the Java files.
     *
     * @param dir The source folder.
     * @param daoPackageName The package for DAO Java files.
     * @param dtoPackageName The package for DTO Java files.
     * @throws SQLException Failed to execute SQL statements.
     * @throws IOException Failed to save files.
     */
    public void tables2DAO(String dir, String daoPackageName, String dtoPackageName) throws IOException, SQLException {
        for (String t : this.source.selectTableNames()) {
            toDAO(dir, daoPackageName, dtoPackageName, t);
        }
    }

    /**
     * Save DTO and DAO structure of all views to the Java files.
     *
     * @param dir The source folder.
     * @param daoPackageName The package for DAO Java files.
     * @param dtoPackageName The package for DTO Java files.
     * @throws SQLException Failed to execute SQL statements.
     * @throws IOException Failed to save files.
     */
    public void views2DAO(String dir, String daoPackageName, String dtoPackageName) throws SQLException, IOException {
        for (String v : this.source.selectViewNames()) {
            toDAO(dir, daoPackageName, dtoPackageName, v);
        }
    }

    /**
     * Save DTO and DAO structure of a view to the Java files.
     *
     * @param dir The source folder.
     * @param daoPackageName The package for DAO Java files.
     * @param dtoPackageName The package for DTO Java files.
     * @param tableOrView The view name.
     * @throws SQLException Failed to execute SQL statements.
     * @throws IOException Failed to save files.
     */
    public void toDAO(String dir, String daoPackageName, String dtoPackageName, String tableOrView) throws IOException, SQLException {
        String dtoName = CamelNaming.upper(tableOrView);
        DaoFactoryClassPrinter printer = new DaoFactoryClassPrinter(this.source, tableOrView);
        DaoFactoryClassPrinter.Result result = printer.generate(daoPackageName, dtoPackageName, dtoName);

        String file1 = String.format("%s/%s/%sDao.java", dir, daoPackageName.replace('.', '/'), dtoName);
        String file2 = String.format("%s/%s/%s.java", dir, dtoPackageName.replace('.', '/'), dtoName);

        Files.write(Paths.get(file1), result.dao.getBytes());
        Files.write(Paths.get(file2), result.dto.getBytes());
    }
}
