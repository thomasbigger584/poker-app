data "aws_ssm_parameter" "ecs_ami" {
  name = "/aws/service/ecs/optimized-ami/amazon-linux-2/recommended/image_id"
}

data "aws_region" "current" {}

resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-cluster"
  tags = {
    Name = "${var.project_name}-cluster"
  }
}

resource "aws_launch_template" "ecs_spot" {
  name_prefix   = "${var.project_name}-lt-"
  image_id      = data.aws_ssm_parameter.ecs_ami.value
  instance_type = "t3.micro"

  iam_instance_profile { name = aws_iam_instance_profile.ecs_node_profile.name }

  network_interfaces {
    associate_public_ip_address = true
    security_groups             = [aws_security_group.ecs_sg.id]
  }

  instance_market_options { market_type = "spot" }

  # Prevent "Unlimited" bursting costs. Throttles CPU if credits are exhausted.
  credit_specification {
    cpu_credits = "standard"
  }

  # Ensure detailed monitoring (extra cost) is disabled
  monitoring {
    enabled = false
  }

  # Optimize storage: Use gp3 and limit size (AMI default might be 30GB)
  block_device_mappings {
    device_name = "/dev/xvda"
    ebs {
      volume_size = 10
      volume_type = "gp3"
      delete_on_termination = true
    }
  }

  tag_specifications {
    resource_type = "instance"
    tags = {
      Project = var.project_name
      Name    = "${var.project_name}-worker"
    }
  }

  tag_specifications {
    resource_type = "volume"
    tags = {
      Project = var.project_name
      Name    = "${var.project_name}-worker-vol"
    }
  }

  user_data = base64encode(templatefile("${path.module}/script/setup_ecs_instance.sh.tpl", {
    ecs_cluster_name = aws_ecs_cluster.main.name
    project_name     = var.project_name
    update_duckdns_content = templatefile("${path.module}/script/update_duckdns.sh.tpl", {
      project_name  = var.project_name
      duckdns_token = var.duckdns_token
    })
    nginx_conf_content = templatefile("${path.module}/nginx/nginx.conf.tpl", {
      project_name = var.project_name
    })
  }))

  tags = {
    Name = "${var.project_name}-lt"
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_autoscaling_group" "ecs_asg" {
  vpc_zone_identifier = [aws_subnet.public.id]
  min_size            = var.asg_min_size
  max_size            = var.asg_max_size
  desired_capacity    = var.asg_desired_capacity
  launch_template {
    id      = aws_launch_template.ecs_spot.id
    version = "$Latest"
  }
  tag {
    key                 = "Name"
    value               = "${var.project_name}-asg"
    propagate_at_launch = false
  }

  lifecycle {
    create_before_destroy = true
    ignore_changes        = [load_balancers, target_group_arns]
  }
  depends_on = [aws_launch_template.ecs_spot]
}

resource "aws_cloudwatch_log_group" "ecs_logs" {
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
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
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
        "awslogs-group"         = aws_cloudwatch_log_group.ecs_logs.name
        "awslogs-region"        = data.aws_region.current.id
        "awslogs-stream-prefix" = "ecs"
      }
    }
  }])
  tags = {
    Name = "${var.project_name}-nginx-td"
  }
}

resource "aws_ecs_service" "main" {
  name            = "nginx-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.nginx.arn
  desired_count   = var.asg_desired_capacity
  launch_type     = "EC2"

  deployment_minimum_healthy_percent = 0
  deployment_maximum_percent         = 100

  tags = {
    Name = "${var.project_name}-service"
  }

  # Ensure IAM permissions are fully propagated before starting the service
  depends_on = [aws_iam_role_policy_attachment.ecs_task_execution_role_policy]
}
