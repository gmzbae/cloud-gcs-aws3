package de.leomedia.cloud_gcstorage_awss3.GC;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import de.leomedia.cloud_gcstorage_awss3.CloudStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A Cloud Storage Service Class that implements from {@link CloudStorageService}
 * It provides functions like uploading objects and generating signed URls for objects.
 */
public class GCStorageService implements CloudStorageService {
    private final Logger logger = LoggerFactory.getLogger(GCStorageService.class);

    private final Storage storage;

    public GCStorageService(String projectId, String jsonKeyPath) throws IOException {

        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonKeyPath));

        this.storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build().getService();
    }

    /**
     * This method configures the {@link BlobId} and {@link BlobInfo}.
     * It sets the sse kms key as the metadata and uploads the object to Cloud Storage
     *
     * @param bucketName the name of a bucket
     * @param key the name of an object
     * @param file the full file path of an object
     * @param encryptionKey the encryption key generated by SSE KMS
     * @param storageClass the storage class in which the object will be stored in S3
     */
    @Override
    public void uploadObject(String bucketName, String key, String file, String encryptionKey, String storageClass) throws IOException {

        Map<String, String> kmsKeyName = new HashMap<>();
        kmsKeyName.put("kmsKeyName", encryptionKey);

        // Get a reference to the bucket
        BlobId blobId = BlobId.of(bucketName, key);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setMetadata(kmsKeyName)
                .build();

        // set a generation-match precondition to avoid potential race
        // conditions and data corruptions. The request returns a 412 error if the
        // preconditions are not met.
        Storage.BlobWriteOption precondition;
        if (this.storage.get(bucketName, key) == null) {
            // For a target object that does not yet exist, set the DoesNotExist precondition.
            // This will cause the request to fail if the object is created before the request runs.
            precondition = Storage.BlobWriteOption.doesNotExist();
        } else {
            // If the destination already exists in your bucket, instead set a generation-match
            // precondition. This will cause the request to fail if the existing object's generation
            // changes before the request runs.
            precondition =
                    Storage.BlobWriteOption.generationMatch(
                            this.storage.get(bucketName, key).getGeneration());
        }
        this.storage.createFrom(blobInfo, Paths.get(file), precondition);

        logger.info("File " + file + " uploaded to bucket " + bucketName + " as " + key);

    }

    /**
     * This method generates a presigned URL for a given object.
     * It configures the {@link BlobInfo} and {@link URL}
     * and sets the kmsKeyName in the metadata of an object.
     *
     * @param bucketName the name of a bucket
     * @param key the name of the object
     * @param minutes given timestamp in minutes in which the url is valid
     * @param encryptionKey given encryption key for decrypting objects
     * @return generated signed {@link URL}
     */
    @Override
    public URL getPresignedUrl(String bucketName, String key, Integer minutes, String encryptionKey) {
        try {
            Map<String, String> kmsKeyName = new HashMap<>();
            kmsKeyName.put("kmsKeyName", encryptionKey);

            // Define resource
            BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, key))
                    .setMetadata(kmsKeyName)
                    .build();

            URL url = this.storage.signUrl(
                    blobInfo,
                    minutes,
                    TimeUnit.MINUTES,
                    Storage.SignUrlOption.withV4Signature()
            );

            logger.info("Generated GET signed URL: " + url);

            return url;

        } catch (StorageException e) {
            logger.error(e.getMessage());
        }

        return null;
    }
}
