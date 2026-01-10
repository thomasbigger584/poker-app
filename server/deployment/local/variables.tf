variable "profile" {
  type        = string
  default     = "local"
}

variable "project_name" {
  description = "The project identifier used for naming resources"
  type    = string
  default = "poker-app"
}

variable "root_domain" {
  description = "The root domain for the server name"
  type    = string
  default = "taila8b6c7.ts.net"
}
