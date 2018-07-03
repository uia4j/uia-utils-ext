package uia.utils.dao;

public class ComparePlan {

    public boolean strictVarchar;

    public boolean strictNumeric;

    public boolean strictDateTime;

    public boolean checkNullable;

    public boolean checkDataSize;

    public static ComparePlan table() {
        return new ComparePlan(true, true, true, true, true);
    }

    public static ComparePlan view() {
        return new ComparePlan(false, false, false, false, false);
    }

    public ComparePlan(boolean strictVarchar, boolean strictNumeric, boolean strictDateTime, boolean checkNullable, boolean checkDataSize) {
        this.strictVarchar = strictVarchar;
        this.strictNumeric = strictNumeric;
        this.strictDateTime = strictDateTime;
        this.checkNullable = checkNullable;
        this.checkDataSize = checkDataSize;
    }

}
