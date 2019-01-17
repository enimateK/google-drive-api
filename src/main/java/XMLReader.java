import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.net.URL;

class XMLReader {
    public GoogleSheetsFile getGoogleSheetsFile(String version) {
        GoogleSheetsFile googleSheetsFile = new GoogleSheetsFile();
        try {
            URL url = getClass().getResource("/version_"+ version +".xml");
            File file = new File(url.getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(GoogleSheetsFile.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            googleSheetsFile = (GoogleSheetsFile) jaxbUnmarshaller.unmarshal(file);

        } catch ( JAXBException e) {
            e.printStackTrace();
        }

        return googleSheetsFile;
    }
}
