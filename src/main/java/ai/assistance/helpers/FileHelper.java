package ai.assistance.helpers;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class FileHelper {

    //extracting file text and returning as a String
    public static String extractTextFromFile(MultipartFile file) throws Exception{
        Tika tika = new Tika();
        return tika.parseToString(file.getInputStream());
    }

    //generate hash code of file content
    public static String fileHash(MultipartFile file)
            throws IOException {
        return DigestUtils.sha256Hex(file.getInputStream());
    }
}
