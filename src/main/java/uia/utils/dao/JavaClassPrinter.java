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
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Kyle K. Lin
 *
 */
public class JavaClassPrinter {

    private final TableType table;

    private final String templateDTO;

    private final String templateDAOTable;

    private final String templateDAOTable2;

    private final String templateDAOView;

    public JavaClassPrinter(Database db, String tableName) throws IOException, SQLException {
        this.templateDTO = readContent("dto.template.txt");
        this.templateDAOTable = readContent("dao_table.template.txt");
        this.templateDAOTable2 = readContent("dao_table.template2.txt");
        this.templateDAOView = readContent("dao_view.template.txt");
        this.table = db.selectTable(tableName, true);
    }

    public JavaClassPrinter(TableType table) throws IOException {
        this.templateDTO = readContent("dto.template.txt");
        this.templateDAOTable = readContent("dao_table.template.txt");
        this.templateDAOTable2 = readContent("dao_table.template2.txt");
        this.templateDAOView = readContent("dao_view.template.txt");
        this.table = table;
    }

    public Result generate(String daoPackageName, String dtoPackageName, String dtoName) {
        return new Result(
                generateDTO(dtoPackageName, dtoName),
                generateDAO(daoPackageName, dtoPackageName, dtoName));
    }

    public Result generate4View(String daoPackageName, String dtoPackageName, String dtoName) {
        return new Result(
                generateDTO(dtoPackageName, dtoName),
                generateDAO4View(daoPackageName, dtoPackageName, dtoName));
    }

    public String generateDTO(String dtoPackageName, String dtoName) {
        ArrayList<String> toString = new ArrayList<String>();
        StringBuilder codeMember = new StringBuilder();
        StringBuilder codeConstr = new StringBuilder();
        StringBuilder codeGetSet = new StringBuilder();
        List<ColumnType> columnTypes = this.table.getColumns();
        for (int i = 0; i < columnTypes.size(); i++) {
            String propNameLower = CamelNaming.lower(columnTypes.get(i).columnName);
            String propNameUpper = CamelNaming.upper(columnTypes.get(i).columnName);
            String javaType = columnTypes.get(i).getJavaTypeName();
            if (columnTypes.get(i).isPk()) {
                toString.add("this." + propNameLower);
            }
            codeMember.append(String.format("%n    private %s %s", javaType, propNameLower)).append(";\n");
            codeConstr.append("        this.").append(propNameLower).append(" = data.").append(propNameLower).append(";\n");

            codeGetSet.append("\n");
            if ("Date".equals(javaType)) {
                codeGetSet.append(String.format("    public %s get%s()", javaType, propNameUpper)).append(" {\n");
                codeGetSet.append(String.format("        return this.%s == null ? null : new Date(this.%s.getTime());", propNameLower, propNameLower)).append("\n    }\n\n");
                codeGetSet.append(String.format("    public void set%s(%s %s)", propNameUpper, javaType, propNameLower)).append(" {\n");
                codeGetSet.append(String.format("        this.%s = %s == null ? null : new Date(%s.getTime());", propNameLower, propNameLower, propNameLower)).append("\n    }\n");
            }
            else {
                codeGetSet.append(String.format("    public %s get%s()", javaType, propNameUpper)).append(" {\n");
                codeGetSet.append(String.format("        return this.%s;", propNameLower)).append("\n    }\n\n");
                codeGetSet.append(String.format("    public void set%s(%s %s)", propNameUpper, javaType, propNameLower)).append(" {\n");
                codeGetSet.append(String.format("        this.%s = %s;", propNameLower, propNameLower)).append("\n    }\n");
            }

        }

        return this.templateDTO
                .replace("{DTO_PACKAGE}", dtoPackageName)
                .replace("{DTO}", dtoName)
                .replace("{CODE_INITIAL}", codeConstr.toString())
                .replace("{CODE_GETSET}", codeGetSet.toString())
                .replace("{MEMBER}", codeMember.toString())
                .replace("{TOSTRING}", String.join(" + \", \" + ", toString));
    }

    public String generateDAO4View(String daoPackageName, String dtoPackageName, String dtoName) {
        List<ColumnType> columnTypes = this.table.getColumns();

        StringBuilder codeConvert = new StringBuilder();
        codeConvert.append("        int index = 1;").append("\n");
        for (int i = 0; i < columnTypes.size(); i++) {
            codeConvert.append("        ").append(columnTypes.get(i).genRsGet("index++")).append("\n");
        }

        return this.templateDAOView
                .replace("{DAO_PACKAGE}", daoPackageName)
                .replace("{DTO_PACKAGE}", dtoPackageName)
                .replace("{DTO}", dtoName)
                .replace("{SQL_SEL}", this.table.generateSelectSQL())
                .replace("{CODE_CONVERT}", codeConvert.toString());
    }

    public String generateDAO(String daoPackageName, String dtoPackageName, String dtoName) {
        List<ColumnType> columnTypes = this.table.getColumns();

        StringBuilder codeConvert = new StringBuilder();
        StringBuilder codeIns = new StringBuilder();
        StringBuilder codeUpd = new StringBuilder();
        StringBuilder codeSelPk = new StringBuilder();
        ArrayList<String> codeSelPkArgs = new ArrayList<String>();
        ArrayList<String> pkWhere = new ArrayList<String>();

        codeConvert.append("        int index = 1;").append("\n");
        for (int i = 0; i < columnTypes.size(); i++) {
            codeIns.append("            ").append(columnTypes.get(i).genPsSet(i + 1)).append("\n");
            codeConvert.append("        ").append(columnTypes.get(i).genRsGet("index++")).append("\n");
        }

        List<ColumnType> pk = columnTypes.stream().filter(c -> c.isPk()).collect(Collectors.toList());
        List<ColumnType> nonPK = columnTypes.stream().filter(c -> !c.isPk()).collect(Collectors.toList());
        for (int i = 0; i < nonPK.size(); i++) {
            codeUpd.append("            ").append(nonPK.get(i).genPsSet(i + 1)).append("\n");
        }
        for (int i = 0; i < pk.size(); i++) {
            codeUpd.append("            ").append(pk.get(i).genPsSet(i + 1 + nonPK.size())).append("\n");
            // TODO: bug
            codeSelPk.append("            ").append(pk.get(i).genPsSetEx(i + 1)).append("\n");
            codeSelPkArgs.add(pk.get(i).getJavaTypeName() + " " + CamelNaming.lower(pk.get(i).columnName));
            pkWhere.add(pk.get(i).getColumnName().toLowerCase() + "=?");
        }

        String updateSQL = this.table.generateUpdateSQL();

        if (updateSQL != null) {
            return this.templateDAOTable
                    .replace("{TABLE_NAME}", this.table.getTableName().toLowerCase())
                    .replace("{DAO_PACKAGE}", daoPackageName)
                    .replace("{DTO_PACKAGE}", dtoPackageName)
                    .replace("{DTO}", dtoName)
                    .replace("{SQL_INS}", this.table.generateInsertSQL())
                    .replace("{SQL_UPD}", updateSQL)
                    .replace("{SQL_SEL}", this.table.generateSelectSQL())
                    .replace("{CODE_INS}", codeIns.toString())
                    .replace("{CODE_UPD}", codeUpd.toString())
                    .replace("{CODE_SEL_PK_ARGS}", String.join(", ", codeSelPkArgs))
                    .replace("{WHERE_PK}", String.join(" AND ", pkWhere))
                    .replace("{CODE_SEL_PK}", codeSelPk.toString())
                    .replace("{CODE_CONVERT}", codeConvert.toString());
        }
        else {
            return this.templateDAOTable2
                    .replace("{TABLE_NAME}", this.table.getTableName().toLowerCase())
                    .replace("{DAO_PACKAGE}", daoPackageName)
                    .replace("{DTO_PACKAGE}", dtoPackageName)
                    .replace("{DTO}", dtoName)
                    .replace("{SQL_INS}", this.table.generateInsertSQL())
                    .replace("{SQL_SEL}", this.table.generateSelectSQL())
                    .replace("{CODE_INS}", codeIns.toString())
                    .replace("{CODE_UPD}", codeUpd.toString())
                    .replace("{CODE_SEL_PK_ARGS}", String.join(", ", codeSelPkArgs))
                    .replace("{WHERE_PK}", String.join(" AND ", pkWhere))
                    .replace("{CODE_SEL_PK}", codeSelPk.toString())
                    .replace("{CODE_CONVERT}", codeConvert.toString());
        }
    }

    private String readContent(String name) throws IOException {
        try (InputStream is = JavaClassPrinter.class.getResourceAsStream(name)) {
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
