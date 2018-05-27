package uia.utils.dao.pg;

import uia.utils.dao.ColumnType;
import uia.utils.dao.ComparePlan;
import uia.utils.dao.CompareResult;

public class PostgreSQLColumnType extends ColumnType {

    @Override
    public boolean sameAs(ColumnType targetColumn, ComparePlan plan, CompareResult cr) {
        return super.sameAs(targetColumn, plan, cr);
    }
}
