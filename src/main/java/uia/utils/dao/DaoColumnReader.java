package uia.utils.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
*
* @author Kyle K. Lin
*
*/
public interface DaoColumnReader {

    public Object read(ResultSet rs, int index) throws SQLException;
}
