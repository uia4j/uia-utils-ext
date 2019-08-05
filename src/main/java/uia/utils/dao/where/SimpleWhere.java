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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import uia.utils.dao.where.conditions.BetweenType;
import uia.utils.dao.where.conditions.ConditionType;
import uia.utils.dao.where.conditions.EqType;
import uia.utils.dao.where.conditions.LessThanType;
import uia.utils.dao.where.conditions.LikeType;
import uia.utils.dao.where.conditions.MoreThanType;
import uia.utils.dao.where.conditions.NotEqType;

public class SimpleWhere extends Where {

    private final ArrayList<ConditionType> conds;

    private final String op;

    SimpleWhere(String op) {
        this.conds = new ArrayList<ConditionType>();
        this.op = op;
    }
    
    public boolean hasConditions() {
    	return this.conds.size() > 0;
    }

    public SimpleWhere eq(String key, Object value) {
        if (isEmpty(key) || isEmpty(value)) {
            return this;
        }
        this.conds.add(new EqType(key, value));
        return this;
    }

    public SimpleWhere eqOrNull(String key, Object value) {
        if (isEmpty(key)) {
            return this;
        }
        this.conds.add(new EqType(key, value));
        return this;
    }

    public SimpleWhere like(String key, Object value) {
        if (isEmpty(key) || isEmpty(value)) {
            return this;
        }
        this.conds.add(new LikeType(key, "%" + value + "%"));
        return this;
    }

    public SimpleWhere likeOrNull(String key, Object value) {
        if (isEmpty(key)) {
            return this;
        }
        this.conds.add(new LikeType(key, value != null ? "%" + value + "%" : null));
        return this;
    }

    public SimpleWhere likeBegin(String key, Object value) {
        if (isEmpty(key) || isEmpty(value)) {
            return this;
        }
        this.conds.add(new LikeType(key, value + "%"));
        return this;
    }

    public SimpleWhere likeBeginOrNull(String key, Object value) {
        if (isEmpty(key)) {
            return this;
        }
        this.conds.add(new LikeType(key, value != null ? value + "%" : null));
        return this;
    }

    public SimpleWhere between(String key, Object value1, Object value2) {
        if (isEmpty(key) || isEmpty(value1) || isEmpty(value2)) {
            return this;
        }
        this.conds.add(new BetweenType(key, value1, value2));
        return this;
    }

    public SimpleWhere notEq(String key, Object value) {
        if (isEmpty(key) || isEmpty(value)) {
            return this;
        }
        this.conds.add(new NotEqType(key, value));
        return this;
    }

    public SimpleWhere notEqOrNull(String key, Object value) {
        if (isEmpty(key)) {
            return this;
        }
        this.conds.add(new NotEqType(key, value));
        return this;
    }

    public SimpleWhere add(ConditionType cond) {
        this.conds.add(cond);
        return this;
    }
    
    public SimpleWhere moreThan(String key, Object value, boolean eq) {
        if (isEmpty(key)) {
            return this;
        }
    	this.conds.add(new MoreThanType(key, value, eq));
        return this;
    }

    
    public SimpleWhere lessThan(String key, Object value, boolean eq) {
        if (isEmpty(key)) {
            return this;
        }
    	this.conds.add(new LessThanType(key, value, eq));
        return this;
    }

    @Override
    public String generate() {
        if (this.conds.size() == 0) {
            return "";
        }
        List<String> data = this.conds.stream()
        		.map(c -> c.getStatement())
        		.collect(Collectors.toList());
        return String.join(this.op, data);
    }

    @Override
    public int accept(PreparedStatement ps, int index) throws SQLException {
        int i = index;
        for (ConditionType cond : this.conds) {
            i = cond.accpet(ps, i);
        }
        return i;
    }

    @Override
    public String toString() {
        if (this.conds.size() == 0) {
            return "";
        }
        List<String> data = this.conds.stream().map(c -> c.toString()).collect(Collectors.toList());
        return String.join(this.op, data);
    }
}
