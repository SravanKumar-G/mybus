package com.mybus.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.mybus.SystemProperties;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;

@Service
public class AWSClient {
    private static final Logger logger = LoggerFactory.getLogger(AWSClient.class);

    private static final Tika tika = new Tika();

    private AmazonS3Client s3Client;
    @Autowired
    private AWSCredentialsProvider awsCredentialsProvider;

    @Autowired
    private SystemProperties systemProperties;

    @PostConstruct
    public void init() {
        s3Client = new AmazonS3Client(awsCredentialsProvider);
    }

    public String putObject(String s3Bucket, String fileName, byte[] fileContent) throws IOException {
        String s3FileName = getS3URI(s3Bucket, fileName);
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(s3Bucket, s3FileName, new ByteArrayInputStream(fileContent),
                        null);
        s3Client.putObject(putObjectRequest);
        return fileName;
    }

    private String getS3URI(String s3Bucket, String s3FileName) {
        return "s3://" + s3Bucket + "/" + s3FileName;
    }

    /**
     * Uploads a file to S3 and returns the s3 file key.  The bucket that is used is configured the properties file via
     * s3.bucket
     *
     * @param s3Bucket the s3 bucket name
     * @param localFile the local file to be uploaded
     * @param s3FileKey the s3 file key that should be used
     * @return a 2-element array, where element 0 is the s3 bucket and element 1 is the s3 file key
     */
    public String[] uploadFileToS3(String s3Bucket, final Path localFile, final String s3FileKey)
            throws IOException, InterruptedException {
        if (localFile == null) {
            throw new NullPointerException("localFile was null.");
        }
        if (isEmpty(s3FileKey)) {
            throw new NullPointerException("objectFileKey cannot be null");
        }
        if (logger.isTraceEnabled()) {
            logger.trace(format("uploadFileToS3(%s)", localFile.getFileName().toString()));
        }
        AWSCredentials awsCredentials = AmazonAWSHelper.getCredentials();
        TransferManager tx = new TransferManager(awsCredentials);

        ObjectMetadata metadata = new ObjectMetadata();
        final String contentType = detectContentTypeFromFilename(s3FileKey);
        if (logger.isDebugEnabled()) {
            logger.debug(format("Setting contentType to '%s' in metadata for S3 object '%s'", contentType, s3FileKey));
        }
        metadata.setContentType(contentType);
        Upload myUpload = tx.upload(s3Bucket, s3FileKey, Files.newInputStream(localFile), metadata);

        myUpload.waitForCompletion();

        String[] retval = {s3Bucket, s3FileKey};
        if (logger.isDebugEnabled()) {
            logger.debug(format("Upload to S3 was successful.  bucket: '%s', file key: '%s'", s3Bucket, s3FileKey));
        }
        return retval;
    }
    private static String detectContentTypeFromFilename(final String filename) {
        return tika.detect(filename);
    }

}
