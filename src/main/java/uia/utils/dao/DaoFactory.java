package uia.utils.dao;

import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import uia.utils.Reflections;
import uia.utils.dao.annotation.TableInfo;
import uia.utils.dao.annotation.ViewInfo;

/**
*
* @author Kyle K. Lin
*
*/
public final class DaoFactory {

    private final TreeMap<String, DaoColumnReader> readers;

    private final TreeMap<String, DaoColumnWriter> writers;

    private final DaoColumnReader objectReader;

    private final DaoColumnWriter objectWriter;

    private final TreeMap<String, DaoTable<?>> daoTables;

    private final TreeMap<String, DaoView<?>> daoViews;

    public DaoFactory() {
        this.readers = new TreeMap<>();
        this.readers.put("short", this::readShort);
        this.readers.put("int", this::readInt);
        this.readers.put("long", this::readLong);
        this.readers.put("bigdecimal", this::readBigDecimal);
        this.readers.put("string", this::readString);
        this.readers.put("date", this::readDate);
        this.readers.put("clob", this::readString);
        this.objectReader = this::readObject;

        this.writers = new TreeMap<>();
        this.writers.put("short", this::writeShort);
        this.writers.put("int", this::writeInt);
        this.writers.put("long", this::writeLong);
        this.writers.put("bigdecimal", this::writeBigDecimal);
        this.writers.put("string", this::writeString);
        this.writers.put("date", this::writeDate);
        this.writers.put("clob", this::writeClob);
        this.objectWriter = this::writeObject;

        this.daoTables = new TreeMap<>();
        this.daoViews = new TreeMap<>();
    }

    public void load(String packageName) throws DaoException {
        load(packageName, null);
    }

    public void load(String packageName, ClassLoader loader) throws DaoException {
        try {
            List<Class<?>> tables = Reflections.findClasses(packageName, TableInfo.class, loader);
            for (Class<?> t : tables) {
                this.daoTables.put(t.getName(), new DaoTable<>(this, t));
            }

            List<Class<?>> views = Reflections.findClasses(packageName, ViewInfo.class, loader);
            for (Class<?> v : views) {
                this.daoViews.put(v.getName(), new DaoView<>(this, v));
            }
        }
        catch (Exception ex) {
            throw new DaoException(ex);
        }
    }

    public Set<String> getTables() {
        return this.daoTables.keySet();
    }

    public Set<String> getViews() {
        return this.daoViews.keySet();
    }

    @SuppressWarnings("unchecked")
    public <T> DaoTable<T> forTable(Class<T> clz) {
        return (DaoTable<T>) this.daoTables.get(clz.getName());
    }

    @SuppressWarnings("unchecked")
    public <T> DaoView<T> forView(Class<T> clz) {
        return (DaoView<T>) this.daoViews.get(clz.getName());
    }

    public DaoColumnReader getColumnReader(String typeName) {
        DaoColumnReader reader = this.readers.get(typeName.toLowerCase());
        return reader == null ? this.objectReader : reader;
    }

    public DaoColumnWriter getColumnWriter(String typeName) {
        DaoColumnWriter writer = this.writers.get(typeName.toLowerCase());
        return writer == null ? this.objectWriter : writer;
    }

    public void addColumnReader(String typeName, DaoColumnReader reader) {
        this.readers.put(typeName.toLowerCase(), reader);
    }

    public void addColumnWriter(String typeName, DaoColumnWriter writer) {
        this.writers.put(typeName.toLowerCase(), writer);
    }

    private Object readShort(ResultSet rs, int index) throws SQLException {
        return rs.getShort(index);
    }

    private Object readInt(ResultSet rs, int index) throws SQLException {
        return rs.getInt(index);
    }

    private Object readLong(ResultSet rs, int index) throws SQLException {
        return rs.getLong(index);
    }

    private Object readBigDecimal(ResultSet rs, int index) throws SQLException {
        return rs.getBigDecimal(index);
    }

    private Object readString(ResultSet rs, int index) throws SQLException {
        return rs.getString(index);
    }

    private Date readDate(ResultSet rs, int index) throws SQLException {
        return DateUtils.getDate(rs, index);
    }

    private Object readObject(ResultSet rs, int index) throws SQLException {
        return rs.getObject(index);
    }

    private void writeShort(PreparedStatement ps, int index, Object value) throws SQLException {
        ps.setShort(index, (short) value);
    }

    private void writeInt(PreparedStatement ps, int index, Object value) throws SQLException {
        ps.setInt(index, (int) value);
    }

    private void writeLong(PreparedStatement ps, int index, Object value) throws SQLException {
        ps.setLong(index, (long) value);
    }

    private void writeBigDecimal(PreparedStatement ps, int index, Object value) throws SQLException {
        ps.setBigDecimal(index, (BigDecimal) value);
    }

    private void writeString(PreparedStatement ps, int index, Object value) throws SQLException {
        ps.setString(index, value == null ? null : value.toString());
    }

    private void writeDate(PreparedStatement ps, int index, Object value) throws SQLException {
        DateUtils.setDate(ps, index, (Date) value);
    }

    private void writeObject(PreparedStatement ps, int index, Object value) throws SQLException {
        ps.setObject(index, value);
    }

    private void writeClob(PreparedStatement ps, int index, Object value) throws SQLException {
        String content = (String) value;
        try (StringReader stringReader = new StringReader(content)) {
            ps.setCharacterStream(index, stringReader, content.length());
        }
    }
}
