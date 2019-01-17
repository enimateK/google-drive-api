import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

class XMLReader {

    private String mFileId;

    XMLReader(String fileId) {
        mFileId = fileId;
    }

    public static GoogleSheetsFile getGoogleSheetsFile(String version) {System.out.println("toto");
        GoogleSheetsFile googleSheetsFile = new GoogleSheetsFile();
        try {
            File file = new File("../resources/version_"+ version +".xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(GoogleSheetsFile.class);
//            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//
//            googleSheetsFile = (GoogleSheetsFile) jaxbUnmarshaller.unmarshal(file);
//
        } catch ( JAXBException e) {
            e.printStackTrace();
        }

        return googleSheetsFile;
    }
}
