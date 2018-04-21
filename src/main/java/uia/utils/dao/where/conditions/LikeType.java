package uia.utils.dao.where.rules;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RuleLikeType implements RuleType {

    private final String key;

    private final Object value;

    public RuleLikeType(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getStatement() {
        return this.value == null ? this.key + " is null" : this.key + " like ?";
    }

    @Override
    public int accpet(PreparedStatement ps, int index) throws SQLException {
        if (this.value != null) {
            ps.setObject(index++, this.value);
        }
        return index;
    }

}
