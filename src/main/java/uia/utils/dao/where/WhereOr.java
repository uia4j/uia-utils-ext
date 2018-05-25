package uia.utils.dao.where;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WhereOr extends Where {

    private ArrayList<Where> wheres;

    public WhereOr() {
        this.wheres = new ArrayList<Where>();
    }

    public WhereOr add(Where where) {
        this.wheres.add(where);
        return this;
    }

    @Override
    public String generate() {
        List<String> ws = this.wheres.stream().map(w -> "(" + w.generate() + ")").collect(Collectors.toList());
        return String.join(" or ", ws);
    }

    @Override
    public int accept(PreparedStatement ps, int index) throws SQLException {
        int i = index;
        for (Where w : this.wheres) {
            i = w.accept(ps, i);
        }
        return i;
    }

}
