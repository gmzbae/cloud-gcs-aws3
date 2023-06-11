package de.leomedia.cloud_gcstorage_awss3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@SpringBootApplication
public class CloudGcStorageAwsS3Application {

    @Value("${STORAGE_CLASS}")
    private String storageClass;

    @Value("${CLOUD_PROVIDER}")
    private String cloudProvider;

    @Value("${BUCKET_NAME}")
    private String bucket_name;

    @Value("${PROJECT_ID}")
    private String projectId;

    @Value("${GC_JSON_KEY_PATH}")
    private String gcJsonKeyPath;

    @Value("${ENCRYPTION_KEY}")
    private String encryptionKey;

    public static void main(String[] args) {
        SpringApplication.run(CloudGcStorageAwsS3Application.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {

            //Define object key name and file path
            String key = "";
            String filePath = "";

            //Create the appropriate CloudStorageService implementation
            CloudStorageService cloudStorageService = CloudStorageServiceFactory.getCloudStorageService(
                    cloudProvider,
                    projectId,
                    gcJsonKeyPath,
                    S3Presigner.builder().build(),
                    S3Client.builder().build()
            );

            //Use the common interface to interact with the cloud storage service
            cloudStorageService.uploadObject(
                    bucket_name,
                    key,
                    filePath,
                    encryptionKey,
                    storageClass
            );

            //Use the common interface to generate a presigned URL
            cloudStorageService.getPresignedUrl(bucket_name, key, 60, encryptionKey);
        };
    }

}
