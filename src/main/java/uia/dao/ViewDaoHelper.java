package uia.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;

import uia.dao.annotation.ColumnInfo;
import uia.dao.annotation.ViewInfo;

/**
*
* @author Kyle K. Lin
*
*/
public final class ViewDaoHelper<T> {

    private final String viewName;

    private final DaoMethod<T> select;

    ViewDaoHelper(DaoFactory factory, Class<T> clz) {
        ViewInfo ti = clz.getDeclaredAnnotation(ViewInfo.class);

        this.viewName = ti.name();
        this.select = new DaoMethod<>(clz);

        ArrayList<String> selectColNames = new ArrayList<>();
        Class<?> curr = clz;
        for (int i = 0; i <= ti.inherit(); i++) {
            Field[] fs = curr.getDeclaredFields();

            for (Field f : fs) {
                ColumnInfo ci = f.getDeclaredAnnotation(ColumnInfo.class);
                if (ci != null && ci.inView()) {
                    String typeName = ci.typeName();
                    if (typeName.isEmpty()) {
                        typeName = f.getType().getSimpleName();
                    }

                    DaoColumn column = new DaoColumn(
                            f,
                            factory.getColumnReader(typeName),
                            factory.getColumnWriter(typeName));

                    this.select.addColumn(column);
                    selectColNames.add(ci.name());

                }
            }
            curr = curr.getSuperclass();
        }
        this.select.setSql(String.format("SELECT %s FROM %s ",
                String.join(",", selectColNames),
                this.viewName));
    }

    public String getViewName() {
        return this.viewName;
    }

    public DaoMethod<T> forSelect() {
        return this.select;
    }

    @Override
    public String toString() {
        return this.viewName;
    }
}
