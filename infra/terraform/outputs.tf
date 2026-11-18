output "cluster_name" {
  description = "EKS cluster name."
  value       = module.eks.cluster_name
}

output "ecr_repository_url" {
  description = "URL of the application image repository."
  value       = aws_ecr_repository.application.repository_url
}

output "load_balancer_service_name" {
  description = "Kubernetes Service that receives the public AWS load balancer."
  value       = "gestion-station-skii"
}