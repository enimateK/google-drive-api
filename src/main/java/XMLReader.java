import model.GoogleSheetsFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.URL;

class XMLReader {
    public GoogleSheetsFile getGoogleSheetsFile(String version) {
        GoogleSheetsFile fileInfo = new GoogleSheetsFile();
        try {
            URL url = getClass().getResource("/version_"+ version +".xml");
            java.io.File file = new java.io.File(url.getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(GoogleSheetsFile.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            fileInfo = (GoogleSheetsFile) jaxbUnmarshaller.unmarshal(file);

        } catch ( JAXBException e) {
            e.printStackTrace();
        }

        return fileInfo;
    }
}
