package com.mybus.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AmazonAWSHelper {
    private static final String ACCESS_KEY_PROPERTY = "aws.accessKey";
    private static final String SECRET_KEY_PROPERTY = "aws.secretKey";

    private static final Logger logger = LoggerFactory.getLogger(AmazonAWSHelper.class);

    private AmazonAWSHelper() {
    }

    public static AWSCredentials getCredentials() throws IOException {
        Properties properties = new Properties();
        File awsPropertiesFile = new File(System.getProperty("user.home"), ".aws.properties");
        if (!awsPropertiesFile.exists()) {
            if (logger.isWarnEnabled()) {
                logger.warn("You have no aws credentials provided in ~/.aws.properties."
                        + "You will not be able to use AWS services until you do."
                        + "Copy config/aws.properties.template into "
                        + "<HOME_DIR>/.aws.properties and add your keys to it.\n\n"
                );
            }

            return new BasicAWSCredentials(null, null);
        }
        InputStream awsPropertiesInputStream = new FileInputStream(awsPropertiesFile);

        properties.load(awsPropertiesInputStream);
        String accessKey = properties.getProperty(ACCESS_KEY_PROPERTY);
        String secretKey = properties.getProperty(SECRET_KEY_PROPERTY);

        return new BasicAWSCredentials(accessKey, secretKey);
    }
}
