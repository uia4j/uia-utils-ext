package uia.utils.dao;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Kyle K. Lin
 *
 */
public class DaoColumn {

    private final Field field;

    private DaoColumnWriter writer;

    private DaoColumnReader reader;

    public DaoColumn(Field field, DaoColumnReader reader, DaoColumnWriter writer) {
        this.field = field;
        this.field.setAccessible(true);
        this.reader = reader;
        this.writer = writer;
    }

    public void run(Object obj, PreparedStatement ps, int index) throws SQLException, DaoException {
        try {
            this.writer.write(ps, index, this.field.get(obj));
        }
        catch (IllegalArgumentException | IllegalAccessException e) {
            throw new DaoException(e);
        }
    }

    public void run(Object obj, ResultSet rs, int index) throws SQLException, DaoException {
        try {
            this.field.set(obj, this.reader.read(rs, index));
        }
        catch (IllegalArgumentException | IllegalAccessException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public String toString() {
        return this.field.getName();
    }

}
