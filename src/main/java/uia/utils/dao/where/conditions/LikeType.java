package uia.utils.dao.where.conditions;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LikeType implements ConditionType {

    private final String key;

    private final Object value;

    public LikeType(String key, Object value) {
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

    @Override
    public String toString() {
        return this.value == null ? this.key + " is null" : this.key + " like '" + this.value + "'";
    }
}
