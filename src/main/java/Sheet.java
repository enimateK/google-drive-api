import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Sheet {
    private List<Column> columns;
    private String name;

    public List<Column> getColumns() {
        return columns;
    }

    @XmlElement(name = "column")
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return columns.toString();
    }
}
