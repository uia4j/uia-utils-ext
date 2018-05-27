package uia.utils.dao;

public class ComparePlan {

    public boolean checkNullable;

    public boolean checkDataSize;

    public boolean strictNumeric;

    public boolean strictVarchar;

    public static ComparePlan table() {
        return new ComparePlan(true, true, true, true);
    }

    public static ComparePlan view() {
        return new ComparePlan(false, false, false, false);
    }

    public ComparePlan(boolean strictVarchar, boolean strictNumeric, boolean checkNullable, boolean checkDataSize) {
        this.strictVarchar = strictVarchar;
        this.strictNumeric = strictNumeric;
        this.checkNullable = checkNullable;
        this.checkDataSize = checkDataSize;
    }

}
