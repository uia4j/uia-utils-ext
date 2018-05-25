package uia.utils.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public static void setDate(PreparedStatement ps, int index, Date value) throws SQLException {
        if (value == null) {
            ps.setNull(index, java.sql.Types.TIMESTAMP);
        }
        else {
            ps.setTimestamp(index, new java.sql.Timestamp(value.getTime()));
        }
    }

    public static void setDateTz(PreparedStatement ps, int index, Date value) throws SQLException {
        if (value == null) {
            ps.setNull(index, java.sql.Types.TIMESTAMP);
        }
        else {
            ps.setTimestamp(index, new java.sql.Timestamp(value.getTime() - TimeZone.getDefault().getRawOffset()));
        }
    }

    public static Date getDate(ResultSet rs, int index) throws SQLException {
        if (rs.getObject(index) == null) {
            return null;
        }
        return new Date(rs.getTimestamp(index).getTime());
    }

    public static Date getDateTz(ResultSet rs, int index) throws SQLException {
        if (rs.getObject(index) == null) {
            return null;
        }
        return new Date(rs.getTimestamp(index).getTime() + TimeZone.getDefault().getRawOffset());
    }
}
