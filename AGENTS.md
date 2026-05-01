# AGENTS.md - Job Portal Backend AI Agent Guide

## Project Overview
**Type:** Spring Boot REST API | **Framework:** Spring Boot 4.0.5 | **Java Version:** 17 | **Build:** Maven | **Database:** MySQL

A job portal backend implementing role-based job matching with JWT authentication. Users register as JOB_SEEKER or RECRUITER, receiving corresponding profile entities.

---

## Architecture & Data Flow

### Layered Architecture Pattern
```
Controller → Service → Repository → Model
     ↓
  (Validation + Security)
```

**Key insight:** Always place business logic in Service layer, not Controllers. Controllers only handle HTTP concerns.

### User Registration Flow (Critical Pattern)
1. User calls `POST /register` with Users entity
2. Password is BCrypt-encoded (strength: lowercase + uppercase + digit + special char + 8 chars minimum)
3. UserService creates Users + matching profile (`JobSeekerProfile` OR `RecruiterProfile`)
4. Profile creation is cascaded via `JobSeekerProfile` and `RecruiterProfile` services

**Example:** See `UserService.newUser()` - it creates the profile immediately after user save.

### Authentication & JWT Flow
1. `POST /login` with username/password
2. Spring AuthenticationManager validates credentials
3. JWTService generates token with 5-hour expiration (line 41 in JWTService)
4. JWTFilter intercepts all requests, extracts token from "Authorization: Bearer {token}"
5. Token validated against user's username &password not in token)

**Caveat:** JWT secret is regenerated each service restart (not persisted) - prevents token reuse across deployments.

### Database Relationships
- **Users → JobSeekerProfile** | OneToOne, cascade delete, lazy load
- **Users → RecruiterProfile** | OneToOne, cascade delete, lazy load  
- **JobPost → Recruiter** | Not yet modeled (recruiterId is string, not FK)

**Pattern:** UUID generation for all entities: `@GeneratedValue(strategy = GenerationType.UUID)`

---

## Code Organization & Naming Conventions

### Directory Structure
```
src/main/java/com/JobPortal/JobPortalBackend/
├── Model/              # JPA Entities (Users, JobSeekerProfile, RecruiterProfile, JobPost)
├── DTO/                # Data transfer objects for API responses (UsersDTO, JobSeekerDTO, RecruiterDTO)
├── Controller/         # REST endpoints (@RestController, @RequestMapping)
├── Service/            # Business logic, transactions (UserService, JWTService, etc.)
├── Repository/         # JpaRepository interfaces (UserRepo, JobSeekerProfileRepo)
├── Config/             # Spring configuration (SecurityConfig for filters, beans)
├── SecurityFilter/     # Custom security filters (JWTFilter)
└── Exception/          # Custom exceptions & GlobalExceptionHandler (@ControllerAdvice)
```

### Naming Conventions
- **Entities:** Singular (Users, JobSeekerProfile, RecruiterProfile, JobPost)
- **Services:** `{Entity}Service` (UserService, JobSeekerProfileService)
- **Repositories:** `{Entity}Repo` (UserRepo, JobSeekerProfileRepo)
- **Controllers:** `{Entity}Controller` (UserController, JobSeekerProfileController)
- **DTOs:** `{Entity}DTO` (UsersDTO, JobSeekerDTO, RecruiterDTO)

---

## Critical Patterns & Implementation Details

### DTO Mapping Pattern
Use **ModelMapper** bean (registered in JobPortalBackendApplication):
```java
@Bean
public ModelMapper getModelMapper() {
    return new ModelMapper();
}
```
Access via autowiring: `@Autowired private ModelMapper modelMapper;`

**Usage:** `modelMapper.map(user, UsersDTO.class)` excludes sensitive fields like password.

### Service Constructor Dependency Injection
Always use constructor injection with @Autowired (see UserService):
```java
@Autowired
public UserService(UserRepo userRepo, AuthenticationManager authManager, JWTService jwtService, ...) {
    this.userRepo = userRepo;
    // ...
}
```
This ensures all dependencies are clearly visible and supports testing.

### Exception Handling Strategy
- **GlobalExceptionHandler** (@ControllerAdvice) catches all exceptions centrally
- Custom exceptions: `UserNotFoundException` + `UserNotFoundExceptionHandler`
- Validation errors: Mapped to field → message in BAD_REQUEST response
- **Pattern:** Return structured error responses, not raw exceptions

### Role-Based Authorization
- Enum: `UserRole` = {JOB_SEEKER, RECRUITER, ADMIN}
- ConversionUserRole to authority in MyUserDetails: `"ROLE_" + user.getRole()`
- SecurityConfig allows: `/users`, `/login`, `/register` without authentication

**Next Step:** Add endpoint-level @PreAuthorize("hasRole('RECRUITER')") annotations.

### Repository Query Methods
Use Spring Data naming conventions:
```java
Users findByUsername(String username);
boolean existsByUsername(String username);
```
No @Query annotations needed for simple queries.

---

## Common Developer Workflows

### Building & Running
```powershell
# Maven build (from project root)
./mvnw clean install

# Run application
./mvnw spring-boot:run
```
Application starts on default port 8080.

### Database Setup
MySQL configuration (application.properties):
```
spring.datasource.url=jdbc:mysql://localhost:3306/jobportaldatabase
spring.datasource.username=root
spring.datasource.password=niwas
spring.jpa.hibernate.ddl-auto=update
```
Database auto-creates/updates on startup. **Create database manually first:**
```sql
CREATE DATABASE jobportaldatabase CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Testing API Endpoints

**Register new user:**
```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","email":"john@example.com","password":"Password123!","role":"JOB_SEEKER"}'
```

**Login & get JWT token:**
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"Password123!"}'
```

**Access protected endpoint with JWT:**
```bash
curl -H "Authorization: Bearer {token}" http://localhost:8080/job-seeker-profiles/{userId}
```

---

## Known Issues & Incomplete Features

1. **UserController.getUsers()** calls `userService.getUsersList()` which is commented out → will throw error
2. **Jobs controller** incomplete - only skeleton exists, no service implementation
3. **JWT security gap:** Secret key regenerates on each restart, invalidating old tokens
4. **Missing FK constraint:** JobPost.recruiterId should be foreign key to Users.userId
5. **Incomplete RecruiterProfileService** - needs implementation similar to JobSeekerProfileService

---

## Key Dependencies & Versions
- **Spring Boot:** 4.0.5
- **JWT (JJWT):** 0.13.0
- **ModelMapper:** 3.2.6
- **MySQL Connector:** Latest from parent POM
- **Lombok:** Latest from parent POM (optional at runtime)

---

## When Adding New Features

1. **New Entity?** → Create Model + Repository + Service + Controller + DTO
2. **New Endpoint?** → Add @GetMapping/@PostMapping in Controller, implement in Service, update SecurityConfig if needed
3. **Database migration?** → Hibernate auto-updates, but verify with `spring.jpa.show-sql=true`
4. **New exception type?** → Create in Exception/ folder, add handler in GlobalExceptionHandler
5. **Sensitive fields?** → Exclude from DTOs using ModelMapper.skip()

---

## Security Considerations

- Never hardcode credentials in code (database credentials in application.properties should use environment variables in production)
- JWT expiration: 5 hours (line 41, JWTService) - consider making configurable
- Password regex enforced at entity level + BCrypt(12) at encoding level
- CSRF disabled (stateless API), SessionCreationPolicy set to STATELESS
- Only /users, /login, /register are public endpoints

