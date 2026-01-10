variable "project_name" {
  type    = string
  default = "poker-app"
}

variable "tailnet_domain" {
  type    = string
  default = "taila8b6c7.ts.net"
}

resource "local_file" "nginx_default_conf" {
  content = templatefile("${path.module}/../nginx/conf.d/default.conf.tpl", {
    server_name = "${var.project_name}.${var.tailnet_domain}"
  })
  filename = "${path.module}/../nginx/conf.d/tfgen_default.conf"
}

resource "terraform_data" "docker_up" {
  triggers_replace = {
    nginx_conf_content = local_file.nginx_default_conf.content
  }

  provisioner "local-exec" {
    working_dir = "${path.module}/.."
    command     = "docker compose up --build -d"
  }
}

resource "terraform_data" "docker_down_clean" {
  provisioner "local-exec" {
    when        = destroy
    working_dir = "${path.module}/.."
    command     = "docker compose down --remove-orphans; docker volume rm $(docker volume ls -q); docker system prune -a -f"
  }
}
