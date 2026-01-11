
# resource "local_file" "nginx_default_conf" {
#   content = file("${path.module}/conf.d/default.conf")
#   filename = "${path.module}/conf.d/tfgen_default.conf"
# }
#
# output "config_content" {
#   value       = local_file.nginx_default_conf.content
#   description = "The generated NGINX configuration content"
# }
