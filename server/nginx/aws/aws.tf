variable "project_name" {
  description = "The project identifier used for naming resources"
  type        = string
}

variable "execution_role_arn" {
  description = "ARN of the ECS task execution role"
  type        = string
}

variable "cluster_id" {
  description = "ID of the ECS cluster"
  type        = string
}

variable "asg_desired_capacity" {
  description = "The number of instances that should be running in the group"
  type        = number
}

data "aws_region" "current" {}

resource "aws_cloudwatch_log_group" "nginx" {
  name              = "/ecs/${var.project_name}-nginx"
  retention_in_days = 7
  tags = {
    Name = "${var.project_name}-logs"
  }
}

resource "aws_ecs_task_definition" "nginx" {
  family                   = "${var.project_name}-nginx"
  network_mode             = "bridge"
  requires_compatibilities = ["EC2"]
  execution_role_arn       = var.execution_role_arn
  cpu                      = 256
  memory                   = 256

  volume {
    name      = "letsencrypt"
    host_path = "/etc/letsencrypt"
  }

  volume {
    name      = "nginx-config"
    host_path = "/etc/nginx-config"
  }

  container_definitions = jsonencode([{
    name         = "nginx"
    image        = "nginx:latest"
    portMappings = [
      { containerPort = 80, hostPort = 80 },
      { containerPort = 443, hostPort = 443 }
    ]

    mountPoints = [
      {
        sourceVolume  = "letsencrypt"
        containerPath = "/etc/letsencrypt"
        readOnly      = true
      },
      {
        sourceVolume  = "nginx-config"
        containerPath = "/etc/nginx/conf.d"
        readOnly      = true
      }
    ]

    healthCheck = {
      command     = ["CMD-SHELL", "curl -f http://localhost/ || exit 1"]
      interval    = 30
      timeout     = 5
      retries     = 3
      startPeriod = 10
    }

    command = ["/bin/sh", "-c", "echo '<h1>Nginx on ECS Spot with DuckDNS</h1>' > /usr/share/nginx/html/index.html && nginx -g 'daemon off;'"]

    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = aws_cloudwatch_log_group.nginx.name
        "awslogs-region"        = data.aws_region.current.id
        "awslogs-stream-prefix" = "ecs"
      }
    }
  }])
  tags = {
    Name = "${var.project_name}-nginx-td"
  }
}

resource "aws_ecs_service" "nginx" {
  name            = "nginx-service"
  cluster         = var.cluster_id
  task_definition = aws_ecs_task_definition.nginx.arn
  desired_count   = var.asg_desired_capacity
  launch_type     = "EC2"

  deployment_minimum_healthy_percent = 0
  deployment_maximum_percent         = 100

  tags = {
    Name = "${var.project_name}-nginx-service"
  }
}
