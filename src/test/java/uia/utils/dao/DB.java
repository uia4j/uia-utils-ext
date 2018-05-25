package uia.utils.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

	public static Connection create() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/mvsdb", "huede", "huede");
	}
}
