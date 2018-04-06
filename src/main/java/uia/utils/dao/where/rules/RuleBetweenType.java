package uia.utils.dao.where.rules;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class RuleBetweenType implements RuleType {

    private final String key;

    private final Object value1;

    private final Object value2;

    public RuleBetweenType(String key, Object value1, Object value2) {
        this.key = key;
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public String getStatement() {
        return "(" + this.key + " between ? and ?)";
    }

    @Override
    public int accpet(PreparedStatement ps, int index) throws SQLException {
    	if(this.value1 instanceof Date) {
            ps.setTimestamp(index++, new Timestamp(((Date)this.value1).getTime()));
    	}
    	else {
            ps.setObject(index++, this.value1);
    	}
    	if(this.value2 instanceof Date) {
            ps.setTimestamp(index++, new Timestamp(((Date)this.value2).getTime()));
    	}
    	else {
            ps.setObject(index++, this.value2);
    	}
        return index;
    }

}
