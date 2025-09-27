# Pipeline as a Code CI/CD Project

This repository contains the **CI/CD pipeline for the VProfile Web Application**, automated using **Jenkins**, **Docker**, and **AWS ECS**. The pipeline builds, tests, packages, and deploys the application seamlessly to AWS.

---

## Project Overview

Sample Website (Vprofile) is a **Java-based web application** packaged as a WAR file and deployed on **Tomcat**. The CI/CD pipeline ensures:  

- Automated **code fetching** from GitHub.  
- **Build and unit tests** using Maven.  
- **Code quality checks** via Checkstyle.  
- **Docker image creation** for the app.  
- **Push to AWS ECR**.  
- **Deployment to AWS ECS** cluster.  
- Cleanup of local Docker images.

---

## Project Structure

```
PAAC-CICD-Project/
│
├── src/                    # Java source code
├── Docker-files/           # Dockerfiles for multi-stage build
│   └── app/
│       └── multistage/
│           └── Dockerfile
├── Jenkinsfile             # Jenkins pipeline definition
├── README.md               # Project documentation
└── target/                 # Maven build artifacts (WAR files)
```

---

## Pipeline Stages

| Stage                     | Description                                                                 |
|----------------------------|-----------------------------------------------------------------------------|
| **Fetch Code**             | Clones the `docker` branch from the GitHub repository.                      |
| **Build**                  | Builds the project using `mvn clean install`. WAR files are archived.       |
| **Unit Tests**             | Runs unit tests via Maven. Stops pipeline if tests fail.                     |
| **Checkstyle Analysis**    | Runs static code analysis. Warnings do not stop the pipeline.               |
| **Build App Image**        | Creates a Docker image for the application using multi-stage Dockerfile.    |
| **Upload App Image**       | Pushes Docker image to **AWS ECR**.                                        |
| **Remove Local Images**    | Cleans up unused Docker images locally.                                      |
| **Deploy to ECS**          | Updates the AWS ECS service to deploy the new image.                         |

---

## Key Features

- **Automated CI/CD pipeline** using Jenkins declarative pipeline.  
- **Docker multi-stage build** for optimized image size.  
- **AWS Integration**: ECR for image registry, ECS for deployment.  
- **Code Quality**: Checkstyle analysis and unit tests.  
- **Artifact Management**: WAR files archived for reference.

---

## Technologies Used

- **Java 17**  
- **Maven 3.9**  
- **Tomcat 10**  
- **Docker**  
- **AWS ECR & ECS**  
- **Jenkins CI/CD**
- **Checkstyle (code quality)**
