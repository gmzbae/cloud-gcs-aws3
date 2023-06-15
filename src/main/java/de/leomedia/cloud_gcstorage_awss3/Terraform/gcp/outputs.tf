output "bucket_url" {
  value = google_storage_bucket.gc_tf_bucket.url
}

output "bucket_name" {
  value = google_storage_bucket.gc_tf_bucket.id
}
