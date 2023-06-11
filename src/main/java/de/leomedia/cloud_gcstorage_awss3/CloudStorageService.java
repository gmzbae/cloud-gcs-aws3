package de.leomedia.cloud_gcstorage_awss3;

import java.io.IOException;
import java.net.URL;

/**
 * An interface that implements the common methods for uploading objects and generating presigned URLs.
 */
public interface CloudStorageService {

    /**
     * A method that uploads objects to S3 or Cloud Storage
     *
     * @param bucketName the name of a bucket
     * @param key the name of an object
     * @param file the full file path of an object
     * @param encryptionKey the encryption key generated by SSE KMS
     * @param storageClass the storage class in which the object will be stored in S3
     */
    void uploadObject(String bucketName, String key, String file, String encryptionKey, String storageClass) throws IOException;

    /**
     * This method generates a presigned URL for a given object.
     *
     * @param bucketName the name of a bucket
     * @param key the name of the object
     * @param minutes given timestamp in minutes in which the url is valid
     * @param encryptionKey given encryption key for decrypting objects
     * @return generated signed {@link URL}
     */
    URL getPresignedUrl(String bucketName, String key, Integer minutes, String encryptionKey);
}

