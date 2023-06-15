variable "project_id" {
  type = string
  description = "Name of the google project"
}

variable "region" {
  type = string
  description = "Name of the region"
}

variable "kms_key_ring" {
  type    = string
  description = "Name of the KMS Keyring"
}

variable "crypto_key_name" {
  type = string
  description = "Name of the KMS Key"
}

variable "gc_bucket" {
  type    = string
  description = "Name of the Bucket"
}

variable "gc_log_bucket" {
  type = string
  description = "Name of the Log Bucket"
}

variable "storage_class" {
  type = string
  description = "Storage Class of the Bucket"
}

variable "rotation_period" {
  type = string
  description = "Time in seconds to rotate key"
}
