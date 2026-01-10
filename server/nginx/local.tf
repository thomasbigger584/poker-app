variable "server_name" {
  type        = string
  description = "The FQDN of the server"
}

resource "local_file" "nginx_default_conf" {
  content = templatefile("${path.module}/conf.d/default.conf.tpl", {
    server_name = var.server_name
  })
  filename = "${path.module}/conf.d/tfgen_default.conf"
}

output "config_content" {
  value       = local_file.nginx_default_conf.content
  description = "The generated NGINX configuration content"
}
