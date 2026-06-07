# Job Portal Backend

## Description
Job Portal Backend is a REST API built using Spring Boot that allows recruiters to post jobs and candidates to apply for jobs. It provides authentication, job management, application tracking, and resume upload functionality, Email notification about application status.

## Features
- User Registration and Login
- JWT Authentication
- Role-based Authorization
- Job Posting Management
- Apply for Jobs
- Resume Upload and Download
- AWS S3 Integration
- REST APIs
- Email notification

## Technologies Used
- Java 17
- Spring Boot 4.0.6
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL (AWS arrora RDS)
- JWT
- AWS S3
- Maven
- Docker
  

## Architecture
src/main/java

### Controller
- Contains REST API endpoints that handle HTTP requests and responses.
### Services
- Contains business logic and application processing.
### Repository
- Responsible for database operations using Spring Data JPA.
### Model
- Contains JPA entity classes that map to database tables.
### DTO
- Contains Data Transfer Objects used for request and response payloads.
### SecurityService
- Contains JWT authentication, and Spring Security configuration.
### Exception
- Contains custom exceptions and global exception handlers.
### Config
- Contains application configuration classes- S3client, OpenApiConfig(swagger configurattion), SecurityConfig.

## API Endpoints
- Postman api collection link - https://www.postman.com/niwasbondar07-5576148/workspace/jobportal-backend


## Setup Instructions
1. Clone repository
git clone [https://github.com/niwasbb/JobPortalBackend.git](https://github.com/niwasbb/JobPortalBackend)
2. Configure MySQL database
3. Configure AWS S3 client (i deployed this project on AWS EC2 and using IAM role so EC2 can directly communicate S3 bucket. You can directly use S3 bucket by using ACCES KEY and ID, configure S3Client accordingly.)
4. Update application.properties or setup Environment variables / .env file , make changes in docker-compose.YML file acordingly.
5. create docker image file - .\mvnw compile jib:dockerBuild
6. run container in docker - docker compose up -d
7. you can check docker container running with name 'jobportal-backend-c'

## Environment Variables
- MYSQL_URL= MySql database url
- MYSQL_USER= username
- MYSQL_PASSWORD= password
- JWT_SECRET= JWT key
- AWS_BUCKET_NAME= S3 bucket name
- MAIL_USERNAME= email id to send email notification
- MAIL_PASSWORD= app generated password (XXXX XXXX XXXX XXXX)

## Future Improvements
- Email Notifications as seperate microservice
- Microservices Architecture
- chaching using Redis
  

## Author
Niwas Bondar
https://github.com/niwasbb
