package uia.utils.dao.where;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Where {

    public abstract String generate();

    public abstract int accept(PreparedStatement ps, int index) throws SQLException;

    protected boolean isEmpty(Object value) {
        return value == null || value.toString().trim().length() == 0;
    }

}
