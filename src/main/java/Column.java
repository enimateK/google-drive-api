import javax.xml.bind.annotation.XmlAttribute;

public class Column {
    private String name;
    private String index;

    public String getName() {
        return name;
    }

    @XmlAttribute(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    @XmlAttribute(name = "index")
    public void setIndex(String index) {
        this.index = index;
    }
}
