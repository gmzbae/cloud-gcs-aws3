This project compares the cloud storages AWS S3 and Google Cloud Storage.

# Set Up Project

To start using this library, you can decide which service you prefer.
To choose one service, you can provide the cloudProvider string variable "AWS" or "Google Cloud".

## AWS

### Credentials

Provide your AWS credentials through the default credential profile file. It is typically located at ```~/.aws/credentials```.
To load these credentials, the project uses the ```ProfileCredentialsProvider``` class.

You can create the file by using the ```aws configure``` command provided by the AWS CLI,
or you can create it by editing the file with a text editor of your choice.

For more information about the credentials file format,
see [AWS Credentials File Format](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html#credentials-file-format).

For production mode, it is recommended to use tools like the AWS Toolkit for Intellij Idea to authenticate to AWS.

### Terraform

Terraform creates a bucket in AWS S3 with the following configurations:

- Object Ownership: BucketOwnerPreferred
- Bucket ACl: private
- BUcket Logging

To use terraform, execute the following commands:

- ```terraform init```
- ```terraform plan```
- ```terraform apply```

Or

- ```terraform destroy```

## Google Cloud

### Credentials: Application Default Credentials (ADC)

For production code, it is recommended to use user-managed service acccounts to provide credentials to ADC.

It is recommended to create a service account and generate a key file.
This key can be used to authenticate by providing the path to the key file when instantiating the ```GoogleCloudStorageService``` class.


Provide credentials to ADC with the following methods:

- GOOGLE_APPLICATION_CREDENTIALS environment variable
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

Terraform creates a bucket in Google Cloud Storage with the following configurations:

- Storage class: Nearline
- BUcket Logging
- Public access prevention: enforced
- Bucket Versioning: enabled

To use terraform, execute the following commands:

- ```terraform init```
- ```terraform plan```
- ```terraform apply```

Or

- ```terraform destroy```
