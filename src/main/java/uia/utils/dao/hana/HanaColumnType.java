package uia.utils.dao.hana;

import uia.utils.dao.ColumnType;
import uia.utils.dao.ComparePlan;
import uia.utils.dao.CompareResult;

public class HanaColumnType extends ColumnType {

    @Override
    public boolean sameAs(ColumnType targetColumn, ComparePlan plan, CompareResult cr) {
        return super.sameAs(targetColumn, plan, cr);
    }
}
