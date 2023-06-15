# create bucket
resource "google_storage_bucket" "gc_tf_bucket" {
  name     = var.gc_bucket
  location = var.region
  public_access_prevention = "enforced"
  storage_class = var.storage_class
  uniform_bucket_level_access = true

  # configure lifecycle settings to delete object after 10 years
  lifecycle_rule {
    action {
      type = "Delete"
    }
    condition {
      age = 3652 # 10 years retention
    }
  }

  # enable versioning
  versioning {
    enabled = true
  }

  # create log bucket
  logging {
    log_bucket = var.gc_log_bucket
    log_object_prefix = "log/"
  }

}

# create kms key ring
resource "google_kms_key_ring" "default" {
  location = var.region
  name     = var.kms_key_ring
}

# create kms crypto key name
resource "google_kms_crypto_key" "key" {
  key_ring = google_kms_key_ring.default.id
  name     = var.crypto_key_name
  rotation_period = var.rotation_period
}

# needed to get the google project number
data "google_project" "project" {}

# configure to give permission to service account to use key
resource "google_kms_crypto_key_iam_binding" "crypto_key" {
  crypto_key_id = google_kms_crypto_key.key.id
  members       = ["serviceAccount:service-${data.google_project.project.number}@compute-system.iam.gserviceaccount.com",]
  role          = "roles/cloudkms.cryptoKeyEncrypterDecrypter"
}
