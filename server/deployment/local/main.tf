variable "project_name" {
  type    = string
  default = "poker-app"
}

variable "root_domain" {
  type    = string
  default = "taila8b6c7.ts.net"
}

module "nginx" {
  source         = "../../nginx"
  server_name   = "${var.project_name}.${var.root_domain}"
}

resource "terraform_data" "docker_up" {
  triggers_replace = {
    nginx_conf_content = module.nginx.config_content
  }

  provisioner "local-exec" {
    working_dir = "${path.module}"
    command     = "docker compose up --build -d"
  }
}

resource "terraform_data" "docker_down_clean" {
  provisioner "local-exec" {
    when        = destroy
    working_dir = "${path.module}"
    command     = "docker compose down --volumes --remove-orphans --rmi local; docker system prune -a -f"
  }
}
