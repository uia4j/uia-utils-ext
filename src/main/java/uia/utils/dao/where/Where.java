package uia.utils.dao.where;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Where {
	
	private List<String> orders;
	
	public Where() {
		this.orders = new ArrayList<String>();
	}
	
	public Where addOrder(String columnName) {
		this.orders.add(columnName);
		return this;
	}

    public abstract PreparedStatement prepareStatement(Connection conn, String selectSql) throws SQLException;
    
    protected String orderBy() {
        return this.orders.size() == 0 ? "" : " ORDER BY " + String.join(",", orders);
    }

    protected boolean isEmpty(Object value) {
        return value == null || value.toString().trim().length() == 0;
    }

}
