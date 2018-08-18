package uia.utils.dao.where;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public abstract class Where {

    public abstract String generate();

    public abstract int accept(PreparedStatement ps, int index) throws SQLException;

    protected boolean isEmpty(Object value) {
        return value == null || value.toString().trim().length() == 0;
    }

    public static String toString(String where, List<Object> paramValues) {
        if (where == null) {
            return "";
        }

        String sql = where;
        int index = 0;
        for (int i = 0, c = paramValues.size(); i < c; i++) {
            index = sql.indexOf("?");
            if (index > 0) {
                String v = "" + paramValues.get(i);
                sql = sql.substring(0, index) + "'" + v + "'" + sql.substring(index + 1);
                index = index + v.length() + 2;
            }
        }
        return sql;
    }

}
