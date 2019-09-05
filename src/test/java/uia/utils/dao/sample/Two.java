package uia.utils.dao.sample;

import uia.utils.dao.annotation.ColumnInfo;
import uia.utils.dao.annotation.TableInfo;

@TableInfo(name = "org_supplier")
public class Two {

    @ColumnInfo(name = "org_id", primaryKey = true)
    private String orgId;

    @ColumnInfo(name = "supplier_id", primaryKey = true)
    private String supplierId;

    @ColumnInfo(name = "state_name")
    private int stateName;

    public String getOrgId() {
        return this.orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getSupplierId() {
        return this.supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public int getStateName() {
        return this.stateName;
    }

    public void setStateName(int stateName) {
        this.stateName = stateName;
    }

}
