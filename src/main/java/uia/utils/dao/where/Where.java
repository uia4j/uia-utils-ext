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
package uia.utils.dao.where;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public abstract class Where {

    public abstract String generate();

    public abstract int accept(PreparedStatement ps, int index) throws SQLException;

    protected boolean isEmpty(Object value) {
        return value == null || value.toString().trim().length() == 0;
    }

    public static String toString(String where, List<Object> paramValues) {
        if (where == null) {
            return "";
        }

        String sql = where;
        int index = 0;
        for (int i = 0, c = paramValues.size(); i < c; i++) {
            index = sql.indexOf("?");
            if (index > 0) {
                String v = "" + paramValues.get(i);
                sql = sql.substring(0, index) + "'" + v + "'" + sql.substring(index + 1);
                index = index + v.length() + 2;
            }
        }
        return sql;
    }

}
