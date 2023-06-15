package de.leomedia.cloud_gcstorage_awss3;

import de.leomedia.cloud_gcstorage_awss3.AWS.AWSS3StorageService;
import de.leomedia.cloud_gcstorage_awss3.GC.GCStorageService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest
class PerformanceTest {

    private final Logger logger = LoggerFactory.getLogger(PerformanceTest.class);

    S3Client s3Client;

    @Value("${TEST_BUCKET_NAME}")
    private String bucketName;

    @Value("${TEST_FILES_PATH}")
    private String filePath;

    @Value("${TEST_OBJECT_KEY_PATTERN}")
    private String key;

    @Value("${TEST_FILE_COUNT}")
    private int fileCount;

    @Value("${PROJECT_ID}")
    private String projectId;

    @Value("${GC_JSON_KEY_PATH}")
    private String gcJsonKeyPath;

    @Value("${TEST_GC_ENCRYPTION_KEY}")
    private String gc_encryption_key;

    @Value("${TEST_AWS_ENCRYPTION_KEY}")
    private String aws_encryption_key;

    @Value("${STORAGE_CLASS}")
    private String storageClass;

    /**
     * This test method calls the given methods and starts the performance runner.
     *
     */
    @Test
    public void performanceRunner() throws IOException {

        generateFiles();

        calculateUploadObjectsToS3_TimeMeasure();
        calculateUploadObjectsToCloudStorage_TimeMeasure();

        calculateDownloadObjectsFromS3_TimeMeasure();
        calculateDownloadObjectsFromCloudStorage_TimeMeasure();

    }

    /**
     * This method uploads multiple objects to S3
     * and measures the current time it took in milliseconds.
     */
    public void calculateUploadObjectsToS3_TimeMeasure() {

        AWSS3StorageService awss3StorageService = new AWSS3StorageService(s3Client, S3Presigner.builder().build());

        long startTime = System.currentTimeMillis();

        for(int i = 1; i <= fileCount; i++){
            awss3StorageService.uploadObject(bucketName, key + i + ".txt", filePath + key + i + ".txt", aws_encryption_key, storageClass);
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        logger.info("Elapsed Time for " + fileCount + "  Object Uploads in S3: " + elapsedTime +  " ms.");

    }

    /**
     * This method downloads multiple objects from S3
     * and measures the current time it took in milliseconds.
     */
    public void calculateDownloadObjectsFromS3_TimeMeasure(){

        AWSS3StorageService awss3StorageService = new AWSS3StorageService(s3Client, S3Presigner.builder().build());

        long startTime = System.currentTimeMillis();

        for(int i = 1; i <= fileCount; i++){
            awss3StorageService.getPresignedUrl(bucketName, key + i + ".txt", 60, aws_encryption_key);
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        logger.info("Elapsed Time for " + fileCount + " Object Downloads in S3: " + elapsedTime +  " ms.");

    }

    /**
     * This method uploads multiple objects to Cloud Storage
     * and measures the current time it took in milliseconds.
     */
    public void calculateUploadObjectsToCloudStorage_TimeMeasure() throws IOException {
        GCStorageService googleCloudStorageService = new GCStorageService(projectId, gcJsonKeyPath);

        long startTime = System.currentTimeMillis();

        for(int i = 1; i <= fileCount; i++){
            googleCloudStorageService.uploadObject(bucketName, key + i + ".txt", filePath + key + i + ".txt", gc_encryption_key, storageClass);
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        logger.info("Elapsed Time for " + fileCount + " Object Uploads in Cloud Storage: " + elapsedTime +  " ms.");

    }

    /**
     * This method downloads multiple objects from Cloud Storage
     * and measures the current time it took in milliseconds.
     */
    public void calculateDownloadObjectsFromCloudStorage_TimeMeasure() throws IOException {

        GCStorageService googleCloudStorageService = new GCStorageService(projectId, gcJsonKeyPath);

        long startTime = System.currentTimeMillis();

        for(int i = 1; i <= fileCount; i++){
            googleCloudStorageService.getPresignedUrl(bucketName, key + i + ".txt", 60, gc_encryption_key);
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        logger.info("Elapsed Time for " + fileCount + " Object Downloads in Cloud Storage: " + elapsedTime +  " ms.");

    }

    /**
     * This method writes the generated content into the generated file.
     */
    public void generateFiles(){

        for(int i = 1; i <= fileCount; i++){
            String filename = filePath + key + i + ".txt";
            String content = generateContent();

            try {
                FileOutputStream fos = new FileOutputStream(filename);
                fos.write(content.getBytes(StandardCharsets.UTF_8));
                fos.close();
                logger.info("File generated: " + filename);
            } catch (IOException e) {
                logger.error("An error occurred while generating the file: " + e.getMessage());
            }
        }
    }

    /**
     * This method generates random string content in size  of 100kb.
     * @return a random generated string content
     */
    public String generateContent() {
        StringBuilder sb = new StringBuilder();
        int contentSize = 100 * 1024; // Convert KB to bytes

        while (sb.length() < contentSize) {
            sb.append("Lorem ipsum dolor sit amet, consectetur adipiscing elit. ");
        }

        logger.info("Generated string content");

        return sb.substring(0, contentSize);
    }
}
