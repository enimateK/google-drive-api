import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Sheet {
        private List<Column> columns;
        private String name;

        public List<Column> getColumnsList() {
            return columns;
        }

        @XmlElement(name = "column")
        public void setColumnsList(List<Column> columns) {
            this.columns = columns;
        }
}
