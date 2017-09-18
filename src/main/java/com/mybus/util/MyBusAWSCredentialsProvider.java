package com.mybus.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

import java.io.IOException;

/**
 * Created by skandula on 1/27/15.
 *
 */
public class MyBusAWSCredentialsProvider implements AWSCredentialsProvider {

    /**
     * Returns AWSCredentials which the caller can use to authorize an AWS request.
     * Each implementation of AWSCredentialsProvider can chose its own strategy for
     * loading credentials.  For example, an implementation might load credentials
     * from an existing key management system, or load new credentials when
     * credentials are rotated.
     *
     * @return AWSCredentials which the caller can use to authorize an AWS request.
     */
    @Override
    public AWSCredentials getCredentials() {
        try {
            return AmazonAWSHelper.getCredentials();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load aws credentials ", e);
        }
    }

    /**
     * Forces this credentials provider to refresh its credentials. For many
     * implementations of credentials provider, this method may simply be a
     * no-op, such as any credentials provider implementation that vends
     * static/non-changing credentials. For other implementations that vend
     * different credentials through out their lifetime, this method should
     * force the credentials provider to refresh its credentials.
     */
    @Override
    public void refresh() {

    }
}
