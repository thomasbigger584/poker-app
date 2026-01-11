variable "duckdns_token" {
  description = "The private token from your DuckDNS account"
  type        = string
  sensitive   = true
}

variable "project_name" {
  description = "The project identifier used for naming resources"
  type        = string
  default     = "poker-app"
}
