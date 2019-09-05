package uia.utils.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
*
* @author Kyle K. Lin
*
*/
public class DaoMethod<T> {

    private Class<T> clz;

    private String sql;

    private ArrayList<DaoColumn> columns;

    public DaoMethod(Class<T> clz) {
        this.clz = clz;
        this.columns = new ArrayList<>();
    }

    public String getSql() {
        return this.sql;
    }

    public void assign(PreparedStatement ps, Object obj) throws SQLException, DaoException {
        int index = 1;
        for (DaoColumn col : this.columns) {
            col.run(obj, ps, index);
            index++;
        }
    }

    public List<T> toList(ResultSet rs) throws SQLException, DaoException {
        ArrayList<T> result = new ArrayList<>();
        while (rs.next()) {
            try {
                T data = this.clz.newInstance();
                int index = 1;
                for (DaoColumn col : this.columns) {
                    col.run(data, rs, index);
                    index++;
                }
                result.add(data);
            }
            catch (InstantiationException | IllegalAccessException e) {
                throw new DaoException(e);
            }

        }
        return result;
    }

    public T toOne(ResultSet rs) throws SQLException, DaoException {
        T data = null;
        if (rs.next()) {
            try {
                data = this.clz.newInstance();
                int index = 1;
                for (DaoColumn col : this.columns) {
                    col.run(data, rs, index);
                    index++;
                }
            }
            catch (InstantiationException | IllegalAccessException e) {
                throw new DaoException(e);
            }

        }
        return data;
    }

    public void println() {
        System.out.println(this.sql);
        int i = 1;
        for (DaoColumn col : this.columns) {
            System.out.println(String.format(" %2s. %s", i++, col));
        }
    }

    @Override
    public String toString() {
        return this.sql;
    }

    void setSql(String sql) {
        this.sql = sql;
    }

    void addColumn(DaoColumn column) {
        this.columns.add(column);
    }
}
