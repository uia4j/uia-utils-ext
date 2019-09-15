package uia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uia.dao.where.Where;

/**
 *
 * @author Kyle K. Lin
 *
 * @param <T> DTO class type.
 */
public class ViewDao<T> {

    protected final Connection conn;

    protected final ViewDaoHelper<T> viewHelper;

    /**
     * Constructor.
     *
     * @param conn A JDBC connection.
     * @param viewHelper A DAO helper for a specific view.
     */
    public ViewDao(Connection conn, ViewDaoHelper<T> viewHelper) {
        this.conn = conn;
        this.viewHelper = viewHelper;
    }

    public List<T> selectAll() throws SQLException, DaoException {
        DaoMethod<T> method = this.viewHelper.forSelect();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql())) {
            try (ResultSet rs = ps.executeQuery()) {
                return method.toList(rs);
            }
        }
    }

    public List<T> select(Where where, String... orders) throws SQLException, DaoException {
        DaoMethod<T> method = this.viewHelper.forSelect();
        SelectStatement sql = new SelectStatement(method.getSql())
                .where(where);
        sql.orderBy(orders);
        try (PreparedStatement ps = sql.prepare(this.conn)) {
            try (ResultSet rs = ps.executeQuery()) {
                return method.toList(rs);
            }
        }
    }
}
