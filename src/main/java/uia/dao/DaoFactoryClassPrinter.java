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
package uia.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The DTO and DAO class printer.
 *
 * @author Kyle K. Lin
 *
 */
public class DaoFactoryClassPrinter {

    private static final String CLASS_ANNOTATION = "{CLASS_ANNOTATION}";

    private static final String DAO_PACKAGE = "{DAO_PACKAGE}";

    private static final String DTO_PACKAGE = "{DTO_PACKAGE}";

    private static final String DTO = "{DTO}";

    private static final String VIEW_NAME = "{VIEW_NAME}";

    private static final String TABLE_NAME = "{TABLE_NAME}";

    private static final String CODE_INITIAL = "{CODE_INITIAL}";

    private static final String CODE_GETSET = "{CODE_GETSET}";

    private static final String MEMBER = "{MEMBER}";

    private static final String TOSTRING = "{TOSTRING}";

    private final TableType table;

    private final String templateDTO;

    private final String templateDAOTable1;

    private final String templateDAOTable2;

    private final String templateDAOView;

    /**
     * Constructor.
     *
     * @param db The database.
     * @param tableOrView The table or view name.
     * @throws IOException Failed to load template files.
     * @throws SQLException Failed to query definition of the table or view.
     */
    public DaoFactoryClassPrinter(Database db, String tableOrView) throws IOException, SQLException {
        this(db.selectTable(tableOrView, true));
    }

    /**
     * Constructor.
     *
     * @param table The definition of a table or a view.
     * @throws IOException Failed to load template files.
     */
    public DaoFactoryClassPrinter(TableType table) throws IOException {
        this.table = table;
        this.templateDAOTable1 = readContent("uia_dao_table_template1.txt");
        this.templateDAOTable2 = readContent("uia_dao_table_template2.txt");
        this.templateDAOView = readContent("uia_dao_view_template.txt");
        this.templateDTO = readContent("uia_dto_template.txt");
    }

    /**
     * Generates the DAO and DTO class.
     *
     * @param daoPackageName The package name for DAO class.
     * @param dtoPackageName The package name for DTO class.
     * @param dtoName The DTO class name.
     * @return The result.
     */
    public Result generate(String daoPackageName, String dtoPackageName, String dtoName) {
        if (this.table.isTable()) {
            return new Result(
                    generateDTO(dtoPackageName, dtoName),
                    generateDAO4Table(daoPackageName, dtoPackageName, dtoName));
        }
        else {
            return new Result(
                    generateDTO(dtoPackageName, dtoName),
                    generateDAO4View(daoPackageName, dtoPackageName, dtoName));
        }
    }

    /**
     * Generates the DTO class.
     *
     * @param dtoPackageName The package name for DTO class.
     * @param dtoName The DTO class name.
     * @return The result.
     */
    public String generateDTO(String dtoPackageName, String dtoName) {
        String annotation = this.table.isTable()
                ? String.format("@TableInfo(name = \"%s\")", this.table.getTableName())
                : String.format("@ViewInfo(name = \"%s\")", this.table.getTableName());

        ArrayList<String> toString = new ArrayList<>();
        StringBuilder codeMember = new StringBuilder();
        StringBuilder codeConstr = new StringBuilder();
        StringBuilder codeGetSet = new StringBuilder();
        List<ColumnType> columnTypes = this.table.getColumns();
        for (int i = 0; i < columnTypes.size(); i++) {
            ColumnType ct = columnTypes.get(i);
            String propNameLower = CamelNaming.lower(ct.columnName);
            String propNameUpper = CamelNaming.upper(ct.columnName);
            String javaType = ct.getJavaTypeName();

            if (ct.isPk()) {
                toString.add("this." + propNameLower);
            }

            codeMember.append("\n");
            if (ct.isPk()) {
                codeMember.append(String.format("    @ColumnInfo(name = \"%s\", primaryKey = true)%n",
                        ct.getColumnName()));
            }
            else {
                codeMember.append(String.format("    @ColumnInfo(name = \"%s\")%n",
                        ct.getColumnName()));
            }

            codeMember.append(String.format("    private %s %s;%n", javaType, propNameLower));
            codeConstr.append("        this.").append(propNameLower).append(" = data.").append(propNameLower).append(";\n");

            codeGetSet.append("\n");
            if ("Date".equals(javaType)) {
                if (ct.getRemark() != null) {
                    codeGetSet.append(String.format("    /**%n     * Returns %s.%n     *%n     * @return %s.%n     */%n", ct.getRemark(), ct.getRemark()));
                }
                codeGetSet.append(String.format("    public %s get%s() {%n", javaType, propNameUpper));
                codeGetSet.append(String.format("        return this.%s == null ? null : new Date(this.%s.getTime());%n    }%n%n", propNameLower, propNameLower));
                if (ct.getRemark() != null) {
                    codeGetSet.append(String.format("    /**%n     * Sets %s.%n     *%n     * @param %s %s.%n     */%n", ct.getRemark(), propNameLower, ct.getRemark()));
                }
                codeGetSet.append(String.format("    public void set%s(%s %s) {%n", propNameUpper, javaType, propNameLower));
                codeGetSet.append(String.format("        this.%s = %s == null ? null : new Date(%s.getTime());%n    }%n%n", propNameLower, propNameLower, propNameLower));
            }
            else {
                if (ct.getRemark() != null) {
                    codeGetSet.append(String.format("    /**%n     * Returns %s.%n     *%n     * @return %s.%n     */%n", ct.getRemark(), ct.getRemark()));
                }
                codeGetSet.append(String.format("    public %s get%s() {%n", javaType, propNameUpper));
                codeGetSet.append(String.format("        return this.%s;%n    }%n%n", propNameLower));
                if (ct.getRemark() != null) {
                    codeGetSet.append(String.format("    /**%n     * Sets %s.%n     *%n     * @param %s %s.%n     */%n", ct.getRemark(), propNameLower, ct.getRemark()));
                }
                codeGetSet.append(String.format("    public void set%s(%s %s) {%n", propNameUpper, javaType, propNameLower));
                codeGetSet.append(String.format("        this.%s = %s;%n    }%n%n", propNameLower, propNameLower));
            }
        }

        return this.templateDTO
                .replace(CLASS_ANNOTATION, annotation)
                .replace(DTO_PACKAGE, dtoPackageName)
                .replace(DTO, dtoName)
                .replace(TABLE_NAME, this.table.getTableName())
                .replace(CODE_INITIAL, codeConstr.toString())
                .replace(CODE_GETSET, codeGetSet.toString())
                .replace(MEMBER, codeMember.toString())
                .replace(TOSTRING, String.join(" + \", \" + ", toString));
    }

    private String generateDAO4View(String daoPackageName, String dtoPackageName, String dtoName) {
        List<ColumnType> columnTypes = this.table.getColumns();

        StringBuilder codeConvert = new StringBuilder();
        codeConvert.append("        int index = 1;").append("\n");
        for (int i = 0; i < columnTypes.size(); i++) {
            codeConvert.append("        ").append(columnTypes.get(i).genRsGet("index++")).append("\n");
        }

        return this.templateDAOView
                .replace(VIEW_NAME, this.table.getTableName().toLowerCase())
                .replace(DAO_PACKAGE, daoPackageName)
                .replace(DTO_PACKAGE, dtoPackageName)
                .replace(DTO, dtoName);
    }

    private String generateDAO4Table(String daoPackageName, String dtoPackageName, String dtoName) {
        List<ColumnType> columnTypes = this.table.getColumns();

        StringBuilder codeSelPk = new StringBuilder();
        ArrayList<String> codeSelPkArgs = new ArrayList<>();
        ArrayList<String> pkWhere = new ArrayList<>();

        List<ColumnType> pk = columnTypes.stream().filter(ColumnType::isPk).collect(Collectors.toList());
        for (int i = 0; i < pk.size(); i++) {
            codeSelPk.append("            ").append(pk.get(i).genPsSetEx(i + 1)).append("\n");
            codeSelPkArgs.add(pk.get(i).getJavaTypeName() + " " + CamelNaming.lower(pk.get(i).columnName));
            pkWhere.add(pk.get(i).getColumnName().toLowerCase() + "=?");
        }

        if (pk.size() != columnTypes.size()) {
            return this.templateDAOTable1
                    .replace(TABLE_NAME, this.table.getTableName().toLowerCase())
                    .replace(DAO_PACKAGE, daoPackageName)
                    .replace(DTO_PACKAGE, dtoPackageName)
                    .replace(DTO, dtoName)
                    .replace("{CODE_SEL_PK_ARGS}", String.join(", ", codeSelPkArgs))
                    .replace("{WHERE_PK}", String.join(" AND ", pkWhere))
                    .replace("{CODE_SEL_PK}", codeSelPk.toString());
        }
        else {
            return this.templateDAOTable2
                    .replace(TABLE_NAME, this.table.getTableName().toLowerCase())
                    .replace(DAO_PACKAGE, daoPackageName)
                    .replace(DTO_PACKAGE, dtoPackageName)
                    .replace(DTO, dtoName)
                    .replace("{CODE_SEL_PK_ARGS}", String.join(", ", codeSelPkArgs))
                    .replace("{WHERE_PK}", String.join(" AND ", pkWhere))
                    .replace("{CODE_SEL_PK}", codeSelPk.toString());
        }

    }

    private String readContent(String name) throws IOException {
        try (InputStream is = DaoFactoryClassPrinter.class.getResourceAsStream(name)) {
            byte[] bytesArray = new byte[is.available()];
            is.read(bytesArray);
            return new String(bytesArray, "utf-8");
        }
    }

    static class Result {

        public final String dto;

        public final String dao;

        public Result(String dto, String dao) {
            this.dto = dto;
            this.dao = dao;
        }
    }
}
