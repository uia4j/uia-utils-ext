package uia.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
*
* @author Kyle K. Lin
*
*/
public interface DaoColumnWriter {

    public void write(PreparedStatement ps, int index, Object value) throws SQLException;

    public static void empty2Null(PreparedStatement ps, int index, Object value) throws SQLException {
        if (value != null && value.toString().isEmpty()) {
            ps.setString(index, null);
        }
        else {
            ps.setString(index, (String) value);
        }
    }

}
