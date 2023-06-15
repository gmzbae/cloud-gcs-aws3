# creating the bucket
resource "aws_s3_bucket" "terraform_created_bucket" {
  bucket = var.s3_bucket
}

# creating a log bucket
resource "aws_s3_bucket" "log_bucket" {
  bucket = var.s3_log_bucket
}

# enabling bucket versioning
resource "aws_s3_bucket_versioning" "terraform_versioning" {
  bucket = aws_s3_bucket.terraform_created_bucket.id
  versioning_configuration {
    status = "Enabled"
  }
}

# configuring the object ownership to the bucket owner
resource "aws_s3_bucket_ownership_controls" "terraform_ownership" {
  bucket = aws_s3_bucket.terraform_created_bucket.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

# configure bucket ACL
resource "aws_s3_bucket_acl" "terraform_Acl" {
  depends_on = [aws_s3_bucket_ownership_controls.terraform_ownership]
  bucket = aws_s3_bucket.terraform_created_bucket.id
  acl = "private"
}

# configuring the object ownership to the bucket owner
resource "aws_s3_bucket_ownership_controls" "terraform_ownership_log" {
  bucket = aws_s3_bucket.log_bucket.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

# configure log bucket ACL
resource "aws_s3_bucket_acl" "log_bucket_acl" {
  depends_on = [aws_s3_bucket_ownership_controls.terraform_ownership_log]
  bucket = aws_s3_bucket.log_bucket.id
  acl    = "log-delivery-write"
}

# configure to write logs of terraform_created_bucket to log bucket
resource "aws_s3_bucket_logging" "example" {
  bucket = aws_s3_bucket.terraform_created_bucket.id
  target_bucket = aws_s3_bucket.log_bucket.id
  target_prefix = "log/"
}

# configuring using the SSE KMS encryption method
resource "aws_s3_bucket_server_side_encryption_configuration" "sse_kms_encrypted_bucket" {
  bucket = aws_s3_bucket.terraform_created_bucket.id
  rule {
    apply_server_side_encryption_by_default {
      kms_master_key_id = aws_kms_key.key.key_id
      sse_algorithm = "aws:kms"
    }
  }
}

# configure privacy of bucket
resource "aws_s3_bucket_public_access_block" "bucket_public_access" {
  bucket                  = aws_s3_bucket.terraform_created_bucket.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_kms_key" "key" {
  description             = var.kms_key_description
}

# configure bucket lifecycle
resource "aws_s3_bucket_lifecycle_configuration" "lifecycle_infrequent_access" {
  bucket = aws_s3_bucket.terraform_created_bucket.id
  rule {
    id     = "rule1_IA_transition"
    status = "Enabled"

    expiration {
      days = 3652
    }
  }
}

# configure log bucket lifecycle
resource "aws_s3_bucket_lifecycle_configuration" "lifecycle_log_bucket" {
  bucket = aws_s3_bucket.log_bucket.id
  rule {
    id     = "rule2_logs"
    status = "Enabled"

    filter {
      prefix = "log/"
    }

    expiration {
      days = 7
    }
  }
}

# create kms key
resource "aws_kms_key" "kms_key_name" {
  description = var.kms_key_description
  customer_master_key_spec = var.key_spec
  is_enabled = var.enabled
  enable_key_rotation = var.rotation_enabled
}

# create alias name for the kms key
resource "aws_kms_alias" "kms_alias" {
  target_key_id = aws_kms_key.kms_key_name.key_id
  name = "alias/${var.alias}"
}
