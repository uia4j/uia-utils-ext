package uia.utils.dao.sample;

import java.util.Date;

import uia.utils.dao.annotation.ColumnInfo;
import uia.utils.dao.annotation.TableInfo;

@TableInfo(name = "one")
public class One {

    @ColumnInfo(name = "id", primaryKey = true)
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "birthday")
    private Date birthday;

    @ColumnInfo(name = "state_name", inView = false)
    private int stateName;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getStateName() {
        return this.stateName;
    }

    public void setStateName(int stateName) {
        this.stateName = stateName;
    }

}
