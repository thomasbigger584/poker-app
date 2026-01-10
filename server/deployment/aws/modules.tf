# Nginx Modules
module "nginx" {
  source      = "../../nginx"
  server_name = "${var.project_name}.${var.root_domain}"
}

module "nginx-aws" {
  source = "../../nginx/aws"

  project_name         = var.project_name
  execution_role_arn   = aws_iam_role.ecs_task_execution_role.arn
  cluster_id           = aws_ecs_cluster.main.id
  asg_desired_capacity = var.asg_desired_capacity

  depends_on = [aws_iam_role_policy_attachment.ecs_task_execution_role_policy]
}
