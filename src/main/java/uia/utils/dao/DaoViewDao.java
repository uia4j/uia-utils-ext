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
public class DaoViewDao<T> {

    protected final Connection conn;

    protected final DaoView<T> daoView;

    /**
     * Constructor.
     *
     * @param conn A JDBC connection.
     * @param dao A DAO for a specific table.
     */
    public DaoViewDao(Connection conn, DaoView<T> dao) {
        this.conn = conn;
        this.daoView = dao;
    }

    public List<T> selectAll() throws SQLException, DaoException {
        DaoMethod<T> method = this.daoView.forSelect();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql())) {
            try (ResultSet rs = ps.executeQuery()) {
                return method.toList(rs);
            }
        }
    }

    public List<T> select(Where where, String... orders) throws SQLException, DaoException {
        DaoMethod<T> method = this.daoView.forSelect();
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
