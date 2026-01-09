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

resource "terraform_data" "tailscale_cleanup" {
  triggers_replace = {
    always_run = timestamp()
  }

  provisioner "local-exec" {
    command = <<EOT
      docker run --rm \
        -v "${replace(abspath(path.module), "\\", "/")}/../env/.secrets.env:/secrets.env" \
        -e PROJECT_NAME="${var.project_name}" \
        alpine:latest sh -c " \
          apk add --no-cache curl jq && \
          . /secrets.env && \
          TS_KEY=\${TAILSCALE_API_KEY} && \
          DEVICES=\$(curl -s -u \"\$TS_KEY:\" https://api.tailscale.com/api/v2/tailnet/-/devices) && \
          echo \"Scanning devices...\" && \
          for ID in \$(echo \$DEVICES | jq -r '.devices[] | select(.name | startswith(\"'\$PROJECT_NAME'\" )) | .id'); do \
            echo \"Deleting device: \$ID\"; \
            curl -s -X DELETE -u \"\$TS_KEY:\" https://api.tailscale.com/api/v2/device/\$ID; \
          done"
    EOT
  }
}

resource "terraform_data" "docker_up" {
  triggers_replace = {
    nginx_conf_content = local_file.nginx_default_conf.content
  }

  depends_on = [terraform_data.tailscale_cleanup]

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
