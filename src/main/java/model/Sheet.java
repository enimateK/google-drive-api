package model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Sheet {
    private List<Column> columnsInfo;
    private String name;

    public List<Column> getColumnsInfo() {
        return columnsInfo;
    }

    @XmlElement(name = "column")
    public void setColumnsInfo(List<Column> columnsInfo) {
        this.columnsInfo = columnsInfo;
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
        return columnsInfo.toString();
    }
}
