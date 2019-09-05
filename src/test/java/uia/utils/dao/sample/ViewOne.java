package uia.utils.dao.sample;

import uia.utils.dao.annotation.ColumnInfo;
import uia.utils.dao.annotation.ViewInfo;

@ViewInfo(name = "view_one", inherit = 1)
public class ViewOne extends One {

    @ColumnInfo(name = "description")
    private String description;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
