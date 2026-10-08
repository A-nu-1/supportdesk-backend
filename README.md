# SupportDesk — Spring Boot Backend

Backend REST API for **SupportDesk**, an internal IT support ticket management system built as a full-stack portfolio project.

The application models a typical company support workflow where employees raise IT issues, support agents manage assigned tickets, and administrators manage users and ticket categories.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Security
- JWT Authentication
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Bean Validation
- Maven
- JUnit / Mockito
- Swagger / OpenAPI

## Roles

### Employee
- Create support tickets
- View own tickets
- Add comments to own tickets

### Support Agent
- View available tickets
- Claim tickets
- View assigned tickets
- Update ticket status and priority
- Comment on assigned tickets

### Admin
- View and manage tickets
- Manage users and roles
- Activate or deactivate users
- Manage ticket categories

## Ticket Workflow

```text
OPEN
  ↓
ASSIGNED
  ↓
IN_PROGRESS
  ├──→ WAITING_FOR_USER
  │         ↓
  │    IN_PROGRESS
  │
  └──→ RESOLVED
           ↓
         CLOSED
```

Ticket status transitions are validated by the backend rather than relying on frontend controls.

## Security

SupportDesk uses stateless JWT authentication with role-based access control.

Public endpoints are limited to authentication operations. Protected API endpoints require a valid JWT.

Administrative user and category management requires a separate `REAL_ADMIN` authority.

Demo administrator sessions receive the `ADMIN` role for demonstrating ticket-management functionality but do **not** receive `REAL_ADMIN`, preventing demo sessions from modifying users or administrative configuration.

The backend remains the security authority even when the Angular frontend hides or disables restricted controls.

## Main API Areas

```text
/api/auth
/api/tickets
/api/tickets/{ticketId}/comments
/api/categories
/api/users
```

The API includes:

- Registration and login
- Demo role login
- Ticket creation and management
- Ticket search and filtering
- Pagination
- Ticket claiming
- Status and priority updates
- Ticket comments
- Category management
- User and role administration

## Database

PostgreSQL is used as the relational database.

Schema changes are managed through Flyway migrations:

```text
V1__create_users_table.sql
V2__create_ticket_tables.sql
```

Hibernate validates the schema rather than creating or modifying production tables automatically.

## Configuration

Sensitive values are supplied through environment variables and are not committed to Git.

Example:

```properties
spring.datasource.password=${DB_PASSWORD}
app.jwt.secret=${JWT_SECRET}
```

Required environment variables:

```text
DB_PASSWORD
JWT_SECRET
```

## Running Locally

Create a PostgreSQL database named:

```text
supportdesk
```

Then configure the required environment variables and run:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The backend runs locally at:

```text
http://localhost:8080
```

## API Documentation

When the backend is running locally, interactive Swagger documentation is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger supports Bearer JWT authentication for testing protected endpoints.

## Testing

Run the backend test suite with:

```powershell
.\mvnw.cmd clean test
```

The project includes Spring context and ticket service tests covering core ticket workflow behaviour.

## Frontend

SupportDesk also includes a separate Angular frontend application.

Frontend repository:

`supportdesk-frontend`

## Project Status

Core SupportDesk v1 functionality is implemented.

Deployment configuration and production hosting are the next stage of the project.

## Author

**Anupama Rajendra**

Software Engineer focused on full-stack application development, backend systems, and modern web technologies.


## Author

**Anupama Rajendra**

Software Engineer with experience in enterprise application development, production support, and full-stack web development.

- 🌐 [Portfolio](https://A-nu-1.github.io/anupamaportfolio/)
- 💻 [GitHub](https://github.com/A-nu-1)
