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

variable "asg_min_size" {
  description = "Minimum number of instances in the Auto Scaling Group"
  type        = number
  default     = 0
}

variable "asg_max_size" {
  description = "Maximum number of instances in the Auto Scaling Group"
  type        = number
  default     = 1
}

variable "asg_desired_capacity" {
  description = "The number of instances that should be running in the group"
  type        = number
  default     = 1
}
