package uia.utils.dao.where.conditions;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ConditionType {

    public String getStatement();

    public default int accpet(PreparedStatement ps, int index) throws SQLException {
    	return index;
    }
}
