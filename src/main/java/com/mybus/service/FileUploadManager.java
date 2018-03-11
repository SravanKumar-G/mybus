package com.mybus.service;

import com.mybus.SystemProperties;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;

@Service
public class FileUploadManager {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadManager.class);

    @Autowired
    private SystemProperties props;

    private static final Tika tika = new Tika();

    /**
     * Saves a multipart file to a temp file, and returns it.
     *
     * @param file the file
     * @return the newly saved temp file stored on the local disk
     */
    public Path saveMultipartFile(final MultipartFile file) throws IOException {
        final Path tempFile = Files.createTempFile(null, null);
        if (logger.isDebugEnabled()) {
            logger.debug(format("Temp file for '%s' is: '%s'",
                    file.getOriginalFilename(), tempFile.toAbsolutePath().toString()));
        }
        file.transferTo(tempFile.toFile());
        return tempFile;
    }






    public Path downloadFileToTempDir(String fileUrl, String filenameSuffix) throws IOException {
        URL website = new URL(fileUrl);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        Path tempFile = Files.createTempFile(null, filenameSuffix);
        FileOutputStream fos = new FileOutputStream(tempFile.toString());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        return tempFile;
    }
    private static String detectContentTypeFromFilename(final String filename) {
        return tika.detect(filename);
    }

}
