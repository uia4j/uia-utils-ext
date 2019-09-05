package uia.utils.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uia.utils.dao.where.Where;

/**
 *
 * @author Kyle K. Lin
 *
 * @param <T> DTO class type.
 */
public class DaoTableDao<T> {

    protected final Connection conn;

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

    public int insert(T data) throws SQLException, DaoException {
        DaoMethod<T> method = this.daoTable.forInsert();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql())) {
            method.assign(ps, data);
            return ps.executeUpdate();
        }
    }

    public int insert(List<T> data) throws SQLException, DaoException {
        DaoMethod<T> method = this.daoTable.forInsert();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql())) {
            for (T t : data) {
                method.assign(ps, t);
                ps.addBatch();
            }
            return ps.executeUpdate();
        }
    }

    public int update(T data) throws SQLException, DaoException {
        DaoMethod<T> method = this.daoTable.forUpdate();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql())) {
            method.assign(ps, data);
            return ps.executeUpdate();
        }
    }

    public int update(List<T> data) throws SQLException, DaoException {
        DaoMethod<T> method = this.daoTable.forUpdate();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql())) {
            for (T t : data) {
                method.assign(ps, t);
                ps.addBatch();
            }
            return ps.executeUpdate();
        }
    }

    public int deleteByPK(String... pks) throws SQLException {
        if (pks.length == 0) {
            return 0;
        }

        DaoMethod<T> method = this.daoTable.forDelete();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql())) {
            for (int i = 0; i < pks.length; i++) {
                ps.setObject(i, pks[i]);
            }
            return ps.executeUpdate();
        }
    }

    public List<T> selectAll() throws SQLException, DaoException {
        DaoMethod<T> method = this.daoTable.forSelect();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql())) {
            try (ResultSet rs = ps.executeQuery()) {
                return method.toList(rs);
            }
        }
    }

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
