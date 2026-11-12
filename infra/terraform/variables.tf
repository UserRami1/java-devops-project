variable "aws_region" {
  description = "AWS region used for the EKS cluster and ECR repository."
  type        = string
  default     = "eu-west-3"
}

variable "cluster_name" {
  description = "Name of the EKS cluster."
  type        = string
  default     = "gestion-station-skii"
}

variable "cluster_version" {
  description = "Kubernetes version for EKS."
  type        = string
  default     = "1.33"
}

variable "repository_name" {
  description = "Name of the ECR repository."
  type        = string
  default     = "gestion-station-skii"
}

variable "vpc_cidr" {
  description = "CIDR range for the EKS VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "jenkins_egress_cidrs" {
  description = "Comma-separated CIDRs allowed to reach the public EKS API endpoint. Set this to the Jenkins agent's public egress IP."
  type        = string
  default     = "127.0.0.1/32"
}