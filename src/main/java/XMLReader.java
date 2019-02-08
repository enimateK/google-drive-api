import model.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.URL;

class XMLReader {
    public File getGoogleSheetsFile(String version) {
        File googleSheetsFile = new File();
        try {
            URL url = getClass().getResource("/version_"+ version +".xml");
            java.io.File file = new java.io.File(url.getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(File.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            googleSheetsFile = (File) jaxbUnmarshaller.unmarshal(file);

        } catch ( JAXBException e) {
            e.printStackTrace();
        }

        return googleSheetsFile;
    }
}
