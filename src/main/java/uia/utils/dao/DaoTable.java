package uia.utils.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;

import uia.utils.dao.annotation.ColumnInfo;
import uia.utils.dao.annotation.TableInfo;

/**
*
* @author Kyle K. Lin
*
*/
public final class DaoTable<T> {

    private final String tableName;

    private final DaoMethod<T> insert;

    private final DaoMethod<T> update;

    private final DaoMethod<T> delete;

    private final DaoMethod<T> select;

    DaoTable(DaoFactory factory, Class<T> clz) {
        TableInfo ti = clz.getDeclaredAnnotation(TableInfo.class);

        this.tableName = ti.name();
        this.insert = new DaoMethod<>(clz);
        this.update = new DaoMethod<>(clz);
        this.delete = new DaoMethod<>(clz);
        this.select = new DaoMethod<>(clz);

        ArrayList<String> prikeyColNames = new ArrayList<>();
        ArrayList<String> insertColNames = new ArrayList<>();
        ArrayList<String> updateColNames = new ArrayList<>();
        ArrayList<String> selectColNames = new ArrayList<>();
        Field[] fs = clz.getDeclaredFields();

        ArrayList<DaoColumn> pks = new ArrayList<>();
        for (Field f : fs) {
            ColumnInfo ci = f.getDeclaredAnnotation(ColumnInfo.class);
            if (ci != null) {
                String typeName = ci.typeName();
                if (typeName.isEmpty()) {
                    typeName = f.getType().getSimpleName();
                }

                DaoColumn column = new DaoColumn(
                        f,
                        factory.getColumnReader(typeName),
                        factory.getColumnWriter(typeName));

                if (ci.primaryKey()) {
                    this.delete.addColumn(column);
                    prikeyColNames.add(ci.name() + "=?");
                    pks.add(column);
                }
                else {
                    this.update.addColumn(column);
                    updateColNames.add(ci.name() + "=?");
                }
                this.insert.addColumn(column);
                this.select.addColumn(column);

                insertColNames.add("?");
                selectColNames.add(ci.name());

            }
        }
        for (DaoColumn col : pks) {
            this.update.addColumn(col);
        }

        this.insert.setSql(String.format("INSERT INTO %s(%s) VALUES (%s)",
                this.tableName,
                String.join(",", selectColNames),
                String.join(",", insertColNames)));
        if (!updateColNames.isEmpty()) {
            this.update.setSql(String.format("UPDATE %s SET %s WHERE %s",
                    this.tableName,
                    String.join(",", updateColNames),
                    String.join(" AND ", prikeyColNames)));
        }
        this.delete.setSql(String.format("DELETE FROM %s WHERE %s",
                this.tableName,
                String.join(" AND ", prikeyColNames)));
        this.select.setSql(String.format("SELECT %s FROM %s ",
                String.join(",", selectColNames),
                this.tableName));
    }

    public String getTableName() {
        return this.tableName;
    }

    public DaoMethod<T> forInsert() {
        return this.insert;
    }

    public DaoMethod<T> forUpdate() {
        return this.update;
    }

    public DaoMethod<T> forDelete() {
        return this.delete;
    }

    public DaoMethod<T> forSelect() {
        return this.select;
    }
}
