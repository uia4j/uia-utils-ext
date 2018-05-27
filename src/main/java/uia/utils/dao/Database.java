package uia.utils.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface Database extends AutoCloseable {

    public String getSchema();

    public void setSchema(String schema);

    public Connection getConnection();

    public List<String> selectTableNames() throws SQLException;

    public List<String> selectTableNames(String prefix) throws SQLException;

    public List<String> selectViewNames() throws SQLException;

    public List<String> selectViewNames(String prefix) throws SQLException;

    public TableType selectTable(String tableOrView, boolean firstAsPK) throws SQLException;

    public String selectViewScript(String viewName) throws SQLException;

    public String generateCreateTableSQL(TableType table);

    public int dropTable(String tableName) throws SQLException;

    public int dropView(String viewName) throws SQLException;

    public int createTable(TableType table) throws SQLException;

    public int createView(String viewName, String sql) throws SQLException;

    public int executeUpdate(String sql) throws SQLException;

}
