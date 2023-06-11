package de.leomedia.cloud_gcstorage_awss3.AWS;

import de.leomedia.cloud_gcstorage_awss3.CloudStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * A S3 Storage Service Class that implements from {@link CloudStorageService}
 * It provides functions like uploading objects and generating signed URls for objects.
 */
public class AWSS3StorageService implements CloudStorageService {

    private static final Logger logger = LoggerFactory.getLogger(AWSS3StorageService.class);

    private final S3Client s3Client;
    private final S3Presigner presigner;

    public AWSS3StorageService(S3Client s3Client, S3Presigner presigner) {
        this.presigner = presigner;
        this.s3Client = s3Client;
    }

    /**
     * This method configures the {@link PutObjectRequest} and {@link RequestBody}
     * to upload an object to s3.
     * <p>
     * It also configures the aws sse kms method to encrypt the object
     * and sets the storage class of the object.
     *
     * @param bucketName the name of a bucket
     * @param key the name of an object
     * @param file the full file path of an object
     * @param encryptionKey the encryption key generated by SSE KMS
     * @param storageClass the storage class in which the object will be stored in S3
     */
    @Override
    public void uploadObject(String bucketName, String key, String file, String encryptionKey, String storageClass) {
        try {

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .serverSideEncryption(ServerSideEncryption.AWS_KMS)
                    .ssekmsKeyId(encryptionKey)
                    .storageClass(storageClass)
                    .build();

            Path filePath = Paths.get(file);
            byte[] fileBytes = Files.readAllBytes(filePath);
            RequestBody requestBody = RequestBody.fromBytes(fileBytes);

            this.s3Client.putObject(putObjectRequest, requestBody);
            logger.info("File " + file + " uploaded to bucket " + bucketName + " as " + key);

        } catch (S3Exception | IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * This method generates a presigned URL for a given object.
     * It configures the {@link GetObjectRequest} and {@link GetObjectPresignRequest}
     * to set the signatureDuration of the object and takes the encryption key to decrypt the object.
     *
     * @param bucket the name of a bucket
     * @param key the name of the object
     * @param minutes given timestamp in minutes in which the url is valid
     * @param encryptionKey given encryption key for decrypting objects
     * @return generated signed {@link URL}
     */
    @Override
    public URL getPresignedUrl(String bucket, String key, Integer minutes, String encryptionKey) {
        try {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(minutes))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);

            URL url = presignedGetObjectRequest.url();

            logger.info("Presigned URL: " + url);

            return url;

        } catch (S3Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }
}
