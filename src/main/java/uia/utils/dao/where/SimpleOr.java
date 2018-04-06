package uia.utils.dao.where;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import uia.utils.dao.where.rules.RuleBetweenType;
import uia.utils.dao.where.rules.RuleEqType;
import uia.utils.dao.where.rules.RuleLikeType;
import uia.utils.dao.where.rules.RuleType;

public class SimpleOr extends Where {

    private ArrayList<RuleType> rules;

    public SimpleOr() {
        this.rules = new ArrayList<RuleType>();
    }

    public String generate() {
        if (this.rules.size() == 0) {
            return "";
        }
        List<String> data = this.rules.stream().map(c -> c.getStatement()).collect(Collectors.toList());

        return String.join(" OR ", data) + orderBy();
    }

    public SimpleOr eq(String key, Object value) {
        if (isEmpty(key) || isEmpty(value)) {
            return this;
        }
        this.rules.add(new RuleEqType(key, value));
        return this;
    }

    public SimpleOr eqOrNull(String key, Object value) {
        if (isEmpty(key)) {
            return this;
        }
        this.rules.add(new RuleEqType(key, value));
        return this;
    }

    public SimpleOr likeBegin(String key, Object value) {
        if (isEmpty(key) || isEmpty(value)) {
            return this;
        }
        this.rules.add(new RuleLikeType(key, value + "%"));
        return this;
    }

    public SimpleOr likeBeginOrNull(String key, Object value) {
        if (isEmpty(key)) {
            return this;
        }
        this.rules.add(new RuleLikeType(key, value != null ? value + "%" : null));
        return this;
    }

    public SimpleOr between(String key, Object value1, Object value2) {
        if (isEmpty(key) || isEmpty(value1) || isEmpty(value2)) {
            return this;
        }
        this.rules.add(new RuleBetweenType(key, value1, value2));
        return this;
    }

    public SimpleOr or(RuleType where) {
        this.rules.add(where);
        return this;
    }

    public PreparedStatement prepareStatement(Connection conn, String selectSql) throws SQLException {
        String where = generate();
        PreparedStatement ps;
        if (where == null || where.length() == 0) {
            ps = conn.prepareStatement(selectSql);
        }
        else {
            ps = conn.prepareStatement(selectSql + " WHERE " + where);
        }
        int i = 1;
        for (RuleType rule : this.rules) {
            i = rule.accpet(ps, i);
        }
        return ps;
    }
}
