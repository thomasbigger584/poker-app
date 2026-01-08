variable "project_name" {
  type    = string
  default = "poker-app"
}

resource "local_file" "nginx_default_conf" {
  content = templatefile("${path.module}/../nginx/conf.d/default.conf.tpl", {
    server_name = "${var.project_name}.taila8b6c7.ts.net"
  })
  filename = "${path.module}/../nginx/conf.d/tfgen_default.conf"
}

resource "null_resource" "docker_compose_up" {
  triggers = {
    nginx_conf_content = local_file.nginx_default_conf.content
  }

  provisioner "local-exec" {
    working_dir = "${path.module}/.."
    command     = "docker compose up"
  }

  provisioner "local-exec" {
    when        = destroy
    working_dir = "${path.module}/.."
    command     = "docker compose down"
  }
}
