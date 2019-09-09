package uia.utils.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uia.utils.dao.where.Where;

/**
 * The common DAO implementation.
 *
 * @author Kyle K. Lin
 *
 * @param <T> The DTO class type.
 */
public class DaoTableDao<T> {

    /**
     * The JDBC connection.
     */
    protected final Connection conn;

    /**
     * The DAO helper for the table.
     */
    protected final DaoTable<T> daoTable;

    /**
     * Constructor.
     *
     * @param conn A JDBC connection.
     * @param dao A DAO for a specific table.
     */
    public DaoTableDao(Connection conn, DaoTable<T> dao) {
        this.conn = conn;
        this.daoTable = dao;
    }

    /**
     * Inserts a row.
     *
     * @param data The row.
     * @return Result.
     * @throws SQLException Failed to insert.
     * @throws DaoException Failed or ORM.
     */
    public int insert(T data) throws SQLException, DaoException {
        DaoMethod<T> method = this.daoTable.forInsert();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql())) {
            method.assign(ps, data);
            return ps.executeUpdate();
        }
    }

    /**
     * Inserts rows.
     *
     * @param data The rows.
     * @return Result.
     * @throws SQLException Failed to insert.
     * @throws DaoException Failed or ORM.
     */
    public int insert(List<T> data) throws SQLException, DaoException {
        if (data.isEmpty()) {
            return 0;
        }

        DaoMethod<T> method = this.daoTable.forInsert();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql())) {
            for (T t : data) {
                method.assign(ps, t);
                ps.addBatch();
            }
            return ps.executeUpdate();
        }
    }

    /**
     * Updates a row.
     *
     * @param data The rows.
     * @return Result.
     * @throws SQLException Failed to update.
     * @throws DaoException Failed or ORM.
     */
    public int update(T data) throws SQLException, DaoException {
        DaoMethod<T> method = this.daoTable.forUpdate();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql())) {
            method.assign(ps, data);
            return ps.executeUpdate();
        }
    }

    /**
     * Updates rows.
     *
     * @param data The rows.
     * @return Result.
     * @throws SQLException Failed to update.
     * @throws DaoException Failed or ORM.
     */
    public int update(List<T> data) throws SQLException, DaoException {
        if (data.isEmpty()) {
            return 0;
        }

        DaoMethod<T> method = this.daoTable.forUpdate();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql())) {
            for (T t : data) {
                method.assign(ps, t);
                ps.addBatch();
            }
            return ps.executeUpdate();
        }
    }

    /**
     * Deletes a row.
     *
     * @param pks Values of primary keys.
     * @return Result.
     * @throws SQLException Failed to update.
     * @throws DaoException Failed or ORM.
     */
    public int deleteByPK(Object... pks) throws SQLException {
        if (pks.length == 0) {
            return 0;
        }

        DaoMethod<T> method = this.daoTable.forDelete();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql() + "WHERE " + this.daoTable.forWherePK())) {
            for (int i = 0; i < pks.length; i++) {
                ps.setObject(i + 1, pks[i]);
            }
            return ps.executeUpdate();
        }
    }

    /**
     * Select all rows.
     *
     * @return Result.
     * @throws SQLException Failed to update.
     * @throws DaoException Failed or ORM.
     */
    public List<T> selectAll() throws SQLException, DaoException {
        DaoMethod<T> method = this.daoTable.forSelect();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql())) {
            try (ResultSet rs = ps.executeQuery()) {
                return method.toList(rs);
            }
        }
    }

    /**
     * Select a row.
     *
     * @param pks Values of primary keys.
     * @return Result.
     * @throws SQLException Failed to update.
     * @throws DaoException Failed or ORM.
     */
    public T selectByPK(Object... pks) throws SQLException, DaoException {
        if (pks.length == 0) {
            return null;
        }

        DaoMethod<T> method = this.daoTable.forSelect();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql() + "WHERE " + this.daoTable.forWherePK())) {
            for (int i = 0; i < pks.length; i++) {
                ps.setObject(i + 1, pks[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return method.toOne(rs);
            }
        }
    }

    /**
     * Select rows.
     *
     * @param where The WHERE statement.
     * @param orders The ORDER BY statement.
     * @return Result.
     * @throws SQLException Failed to update.
     * @throws DaoException Failed or ORM.
     */
    public List<T> select(Where where, String... orders) throws SQLException, DaoException {
        DaoMethod<T> method = this.daoTable.forSelect();
        SelectStatement sql = new SelectStatement(method.getSql())
                .where(where)
                .orderBy(orders);
        try (PreparedStatement ps = sql.prepare(this.conn)) {
            try (ResultSet rs = ps.executeQuery()) {
                return method.toList(rs);
            }
        }
    }
}
