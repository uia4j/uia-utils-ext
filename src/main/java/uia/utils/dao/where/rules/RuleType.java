package uia.utils.dao.where.rules;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface RuleType {

    public String getStatement();

    public default int accpet(PreparedStatement ps, int index) throws SQLException {
    	return index;
    }
}
