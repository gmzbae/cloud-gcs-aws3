variable "aws_region" {
  type = string
  description = "AWS Region"
}

variable "kms_key_description" {}

variable "s3_bucket" {
  type    = string
  description = "Name of the bucket"
}

variable "s3_log_bucket" {
  type = string
  description = "Name of the log bucket"
}

variable "key_spec" {
  type = string
  description = ""
}

variable "enabled" {
  type = string
  description = "Enable Value"
}

variable "rotation_enabled" {
  type = string
  description = ""
}

variable "alias" {
  type = string
  description = "Alias for the KMS Key"
}
