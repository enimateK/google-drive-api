import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="google-sheets-file")
public class GoogleSheetsFile {
    private List<Sheet> sheets;

    public List<Sheet> getSheetsList() {
        return sheets;
    }

    @XmlElement(name = "sheet")
    public void setSheetsList(List<Sheet> sheets) {
        this.sheets = sheets;
    }

    @Override
    public String toString() {
        return sheets.toString();
    }
}
