package uia.utils.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
*
* @author Kyle K. Lin
*
*/
public interface DaoColumnWriter {

    public void write(PreparedStatement ps, int index, Object value) throws SQLException;
}
