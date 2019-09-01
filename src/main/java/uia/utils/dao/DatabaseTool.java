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
public class DatabaseTool {

    private final Database source;

    /**
     * Constructor.
     *
     * @param source The source database.
     */
    public DatabaseTool(Database source) {
        this.source = source;
    }

    /**
     * Save DTO structure of all tables to Java files.
     *
     * @param dir The source folder.
     * @param packageName The package.
     * @param jpa Add JPA annotation or not.
     * @throws SQLException Failed to execute SQL statements.
     * @throws IOException Failed to save files.
     */
    public void table2Java(String dir, String packageName, boolean jpa) throws SQLException, IOException {
        for (String t : this.source.selectTableNames()) {
            table2Java(dir, packageName, t, jpa);
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
    public void view2Java(String dir, String packageName) throws SQLException, IOException {
        for (String v : this.source.selectViewNames()) {
            table2Java(dir, packageName, v, false);
        }
    }

    /**
     * Save DTO structure of a table or view to a Java file.
     *
     * @param dir The source folder.
     * @param packageName The package.
     * @param tableOrView The table name or view name.
     * @param jpa Add JPA annotation or not.
     * @throws SQLException Failed to execute SQL statements.
     * @throws IOException Failed to save files.
     */
    private void table2Java(String dir, String packageName, String tableOrView, boolean jpa) throws IOException, SQLException {
        String dtoName = CamelNaming.upper(tableOrView);
        JavaClassPrinter printer = new JavaClassPrinter(this.source, tableOrView);
        String cls = printer.generateDTO(packageName, dtoName, jpa);
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
    public void table2DAO(String dir, String daoPackageName, String dtoPackageName) throws IOException, SQLException {
        for (String t : this.source.selectTableNames()) {
            table2DAO(dir, daoPackageName, dtoPackageName, t);
        }
    }

    /**
     * Save DTO and DAO structure of a view to the Java files.
     *
     * @param dir The source folder.
     * @param daoPackageName The package for DAO Java files.
     * @param dtoPackageName The package for DTO Java files.
     * @param tableName The table name.
     * @throws SQLException Failed to execute SQL statements.
     * @throws IOException Failed to save files.
     */
    public void table2DAO(String dir, String daoPackageName, String dtoPackageName, String tableName) throws IOException, SQLException {
        String dtoName = CamelNaming.upper(tableName);
        JavaClassPrinter printer = new JavaClassPrinter(this.source, tableName);
        JavaClassPrinter.Result result = printer.generate(daoPackageName, dtoPackageName, dtoName, false);

        String file1 = String.format("%s/%s/%sDao.java", dir, daoPackageName.replace('.', '/'), dtoName);
        String file2 = String.format("%s/%s/%s.java", dir, dtoPackageName.replace('.', '/'), dtoName);

        Files.write(Paths.get(file1), result.dao.getBytes());
        Files.write(Paths.get(file2), result.dto.getBytes());
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
    public void view2DAO(String dir, String daoPackageName, String dtoPackageName) throws SQLException, IOException {
        for (String v : this.source.selectTableNames()) {
            view2DAO(dir, daoPackageName, dtoPackageName, v);
        }
    }

    /**
     * Save DTO and DAO structure of a view to the Java files.
     *
     * @param dir The source folder.
     * @param daoPackageName The package for DAO Java files.
     * @param dtoPackageName The package for DTO Java files.
     * @param viewName The view name.
     * @throws SQLException Failed to execute SQL statements.
     * @throws IOException Failed to save files.
     */
    public void view2DAO(String dir, String daoPackageName, String dtoPackageName, String viewName) throws IOException, SQLException {
        String dtoName = CamelNaming.upper(viewName);
        JavaClassPrinter printer = new JavaClassPrinter(this.source, viewName);
        JavaClassPrinter.Result result = printer.generate4View(daoPackageName, dtoPackageName, dtoName);

        String file1 = String.format("%s/%s/%sDao.java", dir, daoPackageName.replace('.', '/'), dtoName);
        String file2 = String.format("%s/%s/%s.java", dir, dtoPackageName.replace('.', '/'), dtoName);

        Files.write(Paths.get(file1), result.dao.getBytes());
        Files.write(Paths.get(file2), result.dto.getBytes());
    }

    /**
     * Output a script to generate all tables.
     *
     * @param file The file name.
     * @throws SQLException Failed to execute SQL statements.
     * @throws IOException Failed to save files.
     */
    public void toTableScript(String file) throws IOException, SQLException {
        toTableScript(file, this.source);
    }

    /**
     * Output a script to generate all tables.
     *
     * @param file The file name.
     * @param target The database the script is executed on.
     * @throws SQLException Failed to execute SQL statements.
     * @throws IOException Failed to save files.
     */
    public void toTableScript(String file, Database target) throws IOException, SQLException {
        StringBuilder scripts = new StringBuilder();
        for (String t : this.source.selectTableNames()) {
            String sql1 = target.generateCreateTableSQL(this.source.selectTable(t, false));
            scripts.append(sql1).append(";\n\n");
        }
        Files.write(Paths.get(file), scripts.toString().getBytes());
    }

    /**
     * Output a script to generate all view.
     *
     * @param file The file name.
     * @param viewPrefix The prefix of view name.
     * @throws SQLException Failed to execute SQL statements.
     * @throws IOException Failed to save files.
     */
    public void toViewScript(String file, String viewPrefix) throws IOException, SQLException {
        toViewScript(file, viewPrefix, this.source);
    }

    /**
     * Output a script to generate all tables.
     *
     * @param file The file name.
     * @param viewPrefix The prefix of view name.
     * @param target The database the script is executed on.
     * @throws SQLException Failed to execute SQL statements.
     * @throws IOException Failed to save files.
     */
    public void toViewScript(String file, String viewPrefix, Database target) throws IOException, SQLException {
        StringBuilder scripts = new StringBuilder();
        for (String v : this.source.selectViewNames(viewPrefix)) {
            String sql = this.source.selectViewScript(v);
            String script = target.generateCreateViewSQL(v, sql);
            scripts.append(script).append(";\n\n");
        }
        Files.write(Paths.get(file), scripts.toString().getBytes());
    }

}
