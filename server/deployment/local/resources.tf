resource "terraform_data" "docker_up" {
  triggers_replace = {
    nginx_conf_content = module.nginx.config_content
  }

  provisioner "local-exec" {
    working_dir = path.module
    command     = "docker compose -f compose.yml up --build -d"
  }
}

resource "terraform_data" "docker_down_clean" {
  provisioner "local-exec" {
    when        = destroy
    working_dir = path.module
    command     = "docker compose -f compose.yml down --volumes --remove-orphans --rmi local"
  }
}
