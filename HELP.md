# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.4/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.3.4/maven-plugin/build-image.html)
* [Spring Data JDBC](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#data.sql.jdbc)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#web)

### Guides

The following guides illustrate how to use some features concretely:

* [Using Spring Data JDBC](https://github.com/spring-projects/spring-data-examples/tree/master/jdbc/basics)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

## CI/CD Jenkins vers AWS

Le `Jenkinsfile` enchaine le checkout, le nettoyage Maven, les tests, l'analyse SonarQube optionnelle, la construction du JAR, la publication Nexus optionnelle, la construction Docker et le deploiement ECR/EKS via Terraform et Kubernetes.

### Agent et plugins Jenkins

Utiliser un agent Linux avec Java 17, Docker, AWS CLI v2, Terraform 1.10 ou plus, kubectl et les outils du pipeline Jenkins. Installer les plugins Pipeline, SonarQube Scanner, Config File Provider, Credentials Binding et AWS Credentials. L'agent doit pouvoir acceder a Docker et aux services AWS.

Configurer le serveur SonarQube avec le nom `SonarQube` et son webhook vers `/sonarqube-webhook/`. Ajouter dans Jenkins un fichier Maven gere avec l'ID `maven-settings`; il doit contenir un serveur Maven dont l'ID est `nexus` et les identifiants Nexus. Renseigner l'URL du depot Maven Nexus au lancement du job.

### Credentials Jenkins

- `aws-credentials` : credential de type AWS Credentials autorise a gerer EKS, ECR, VPC et les roles IAM necessaires.
- `database-url` : credential Secret text avec l'URL JDBC d'une base MySQL joignable depuis les pods EKS.
- `database-credentials` : credential Username with password pour cette base.

Ne pas enregistrer de secrets AWS ou MySQL dans Git. Le bucket S3 utilise pour le state Terraform doit etre cree avant le premier lancement, avec le versioning active. Le role AWS Jenkins doit pouvoir lire/ecrire ce bucket et utiliser le verrouillage S3.

### Lancement

Creer un job Pipeline lie au depot et selectionner le `Jenkinsfile`. Les cases SonarQube, Nexus et deploiement AWS sont desactivees par defaut. Activer les options necessaires apres configuration des services et renseigner le bucket S3 de state.

Pour deployer sur EKS, remplacer aussi la valeur par defaut `127.0.0.1/32` du parametre `JENKINS_EGRESS_CIDRS` par l'adresse CIDR de sortie publique de l'agent Jenkins. L'API EKS est limitee a cette plage. Terraform cree un VPC, un cluster EKS, un noeud gere, un depot ECR et un service AWS LoadBalancer; ces ressources peuvent engendrer des frais tant qu'elles existent. La base MySQL n'est pas creee par Terraform et doit deja etre accessible depuis le cluster.

Apres un deploiement reussi, recuperer l'adresse externe avec `kubectl get service gestion-station-skii -n gestion-station-skii`. L'interface Swagger sera disponible sur `http://<adresse-externe>/stationSki/swagger-ui.html`.

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

