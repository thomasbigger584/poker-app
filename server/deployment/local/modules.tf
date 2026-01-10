module "nginx" {
  source      = "../../nginx"
  server_name = "${var.project_name}.${var.root_domain}"
}
