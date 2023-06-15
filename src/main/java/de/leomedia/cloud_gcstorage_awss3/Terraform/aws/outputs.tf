output "bucket_name" {
  value = aws_s3_bucket.terraform_created_bucket.bucket
}

output "aws_kms_key" {
  value = aws_kms_key.key.id
}

output "aws_kms_alias" {
  value = aws_kms_alias.kms_alias.id
}
