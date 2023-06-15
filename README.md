# Introduction

---

## Cloud GCS AWS S3

This project uses the AWS S3 and Google Cloud Storage SDK libraries to upload objects and generate signed URLs for retrieving.
The user can choose between both cloud providers through the environment variable ```cloud_provider```.

For more information about the functionality of this project, you can clone the github repository:

````console
git clone https://github.com/gmzbae/bachelor-thesis-latex.git
git clone git@github.com:gmzbae/bachelor-thesis-latex.git
````

## Technology Stack

- Spring Boot v3
- Spring Cloud v2022.0.3
- Maven v3.9.1
- Java SDK v17
- AWS SDK v2.0
- Google Client Libraries
- Terraform: 
  - hashicorp/google: v4.62.1
  - hashicopr/aws: v4.63.0

# Run the project in development mode

---

## Set up environment variables

The `application.properties` file in the resources directory contains every environment variable.
These can be set through the IDE or the system.

```
CLOUD_PROVIDER=${cloud_provider}
PROJECT_ID=${GOOGLE_PROJECT_ID}
BUCKET_NAME=${bucket_name}
GC_JSON_KEY_PATH=${GOOGLE_SERVICE_ACCOUNT_FILEPATH}
ENCRYPTION_KEY=${sse_kms_key_id_arn}
STORAGE_CLASS=${storage_class}
```
These 6 variables are required to run the project.
- The ``cloud_provider`` takes one of two values: "aws" or "google cloud". The user can switch between these providers.
- The ``storage_class`` is only valid for AWS to upload objects to the specified class.
- The ``sse_kms_id_arn`` is either the KMS Key ARN of AWS or the KMS Key Ring of GCP

```
TEST_BUCKET_NAME=${test_bucket_name}
TEST_FILES_PATH=${test_files_path}
TEST_OBJECT_KEY_PATTERN=${test_object_key_pattern}
TEST_FILE_COUNT=${test_file_count}
TEST_GC_ENCRYPTION_KEY=${test_gc_encryption_key}
TEST_AWS_ENCRYPTION_KEY=${test_aws_encryption_key}
```
These 6 TEST variables can be set when running the ``PerformanceTest`` Class.
- The ``test_files_path`` defines the path where the generated files will be located.
- The ``test_object_key_pattern``defines the object name pattern. As an example: "a1.txt" where "a" is the object pattern.
- The `test_file_count` defines how many files will be generated, uploaded and how many signed URLs will be generated.
- The `test_gc_encryption_key` defines the KMS Key Ring of GCP
- The `test_aws_encryption_key` defines the KMS Key ARN of AWS

Additionally, before running the project, the local Variables in the ``CloudGcStorageAwsS3Application`` Main Class should be set:

```
//Define object key name and file path to be uploaded and retrieved
String key = "";
String filePath = "";
```

## Set up credentials


### AWS

Provide your AWS credentials through the default credentials profile. It is typically located at ```~/.aws/credentials```.
To load these credentials, the project uses the ```ProfileCredentialsProvider``` class.

You can create the file by using the ```aws configure``` command provided by the AWS CLI,
or you can create it by editing the file with a text editor of your choice.

For more information about the credentials file format,
see [AWS Credentials File Format](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html#credentials-file-format).

For production mode, it is recommended to use plugins like AWS Toolkit to authenticate to AWS.

### GCP

For production mode, it is recommended to use user-managed service acccounts to provide credentials to ADC.

It is recommended to create a service account and generate a json key file.
This key file can be used to authenticate by providing the path to the key file when instantiating the ```GoogleCloudStorageService``` class.


Provide credentials to ADC with the following methods:

- `GOOGLE_APPLICATION_CREDENTIALS` environment variable
  - provide location of credentials file: service account or workload identity federation
- User credentials provided by using the gcloud CLI
  - ```gcloud auth application-default login```
  - Linux, macOS: ```$HOME/.config/gcloud/application_default_credentials.json```
  - Windows: ```%APPDATA%\gcloud\application_default_credentials.json```
- The attached service account
  1. Create a user-managed service account.
  2. Grant that service account the least privileged IAM roles possible.
  3. Attach the service account to the resource where your code is running.

Links:
- [Set Up ADC](https://cloud.google.com/docs/authentication/provide-credentials-adc)
- [How ADC works](https://cloud.google.com/docs/authentication/application-default-credentials)

## Terraform

### AWS

Terraform configures the following:

- Create Bucket
- Create Log Bucket
- Bucket Versioning
- Bucket Ownership Controls
- Bucket/Log Bucket ACL
- Bucket Logging
- Create Bucket SSE KMS
- Bucket Privacy
- Bucket Lifecycle
- Create KMS Alias

Before executing, create a `terraform.tfvars` file in the `Terraform/aws` directory with the following variables:

```
aws_region = ""
kms_key_description = ""
s3_bucket = ""
s3_log_bucket = ""

# Options available
# SYMMETRIC_DEFAULT, RSA_2048, RSA_3072,
# RSA_4096, ECC_NIST_P256, ECC_NIST_P384,
# ECC_NIST_P521, or ECC_SECG_P256K1
key_spec = ""

enabled = true
rotation_enabled = true
alias = ""
```

To use terraform, execute the following commands in the `Terraform/aws` directory:

- ```terraform init```
- ```terraform plan```
- ```terraform apply```

Or

- ```terraform destroy```


### GCP

Terraform configures the following:

- Create Bucket
- Bucket Lifecycle
- Bucket Versioning
- Create Log Bucket
- Logging
- Create KMS Key Ring
- Create KMS Crypto Key
- Public Access Prevention
- Storage Class
- Uniform Bucket Level Access
  
Before executing, create a `terraform.tfvars` file in the `Terraform/gcp` directory with the following variables:

```
project_id = ""
region = ""
kms_key_ring = ""
crypto_key_name = ""
gc_bucket = ""
gc_log_bucket = ""
storage_class = ""
rotation_period = ""
```

To use terraform, execute the following commands in the `Terraform/gcp` directory:

- ```terraform init```
- ```terraform plan```
- ```terraform apply```

Or

- ```terraform destroy```
