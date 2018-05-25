package uia.utils.dao.where;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import uia.utils.dao.where.conditions.BetweenType;
import uia.utils.dao.where.conditions.ConditionType;
import uia.utils.dao.where.conditions.EqType;
import uia.utils.dao.where.conditions.LikeType;
import uia.utils.dao.where.conditions.NotEqType;

public class SimpleWhere extends Where {

    private final ArrayList<ConditionType> conds;

    private final String op;

    public static SimpleWhere createAnd() {
        return new SimpleWhere(" and ");
    }

    public static SimpleWhere createOr() {
        return new SimpleWhere(" or ");
    }

    private SimpleWhere(String op) {
        this.conds = new ArrayList<ConditionType>();
        this.op = op;
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

    @Override
    public String generate() {
        if (this.conds.size() == 0) {
            return "";
        }
        List<String> data = this.conds.stream().map(c -> c.getStatement()).collect(Collectors.toList());
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
}
