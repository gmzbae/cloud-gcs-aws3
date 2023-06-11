package de.leomedia.cloud_gcstorage_awss3;

import de.leomedia.cloud_gcstorage_awss3.AWS.AWSS3StorageService;
import de.leomedia.cloud_gcstorage_awss3.GC.GCStorageService;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import java.io.IOException;

/**
 * This class implements the function to factor between AWS and Google Cloud.
 */
public class CloudStorageServiceFactory {

    /**
     * This method compares the cloudProvider parameter and returns either
     * the {@link AWSS3StorageService} or {@link GCStorageService}.
     *
     * @param cloudProvider the given cloud provider whether "aws" or "google cloud"
     * @param projectId     the id of the Google Cloud project
     * @param jsonKeyPath   the service account file from Google Cloud
     * @param presigner     the Presigner class for AWS
     * @param s3Client      the S3Client class for AWS
     * @return new {@link AWSS3StorageService} or new {@link GCStorageService}
     * @throws IOException if given cloud provider is invalid
     */
    public static CloudStorageService getCloudStorageService(String cloudProvider, String projectId, String jsonKeyPath, S3Presigner presigner, S3Client s3Client) throws IOException {
        if ("AWS".equalsIgnoreCase(cloudProvider)) {
            return new AWSS3StorageService(s3Client, presigner);
        } else if ("Google Cloud".equalsIgnoreCase(cloudProvider)) {
            return new GCStorageService(projectId, jsonKeyPath);
        } else {
            throw new IllegalArgumentException("Invalid cloud provider: " + cloudProvider);
        }
    }
}
