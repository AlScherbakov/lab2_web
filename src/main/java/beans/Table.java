package beans;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private List<Row> entries;

    public Table() {
        this(new ArrayList<>());
    }

    public Table(List<Row> entries) {
        this.entries = entries;
    }

    public List<Row> getRows() {
        return entries;
    }

    public void setRows(List<Row> entries) {
        this.entries = entries;
    }
}
