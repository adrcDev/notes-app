# Tasks Management Application

## Deployment
### Links
- Frontend (Website): https://notes-app-react-frontend.onrender.com
- Backend REST API: https://notes-app-backend-fxov.onrender.com
- REST API documentation (Swagger UI): https://swagger-ui-rest-api-documentation.onrender.com

### Deployment details
- Frontend (Website) and REST API documentation (Swagger UI) deployed on [Render](https://render.com/) (static site service)
- Backend deployed on [Render](https://render.com/) (web service)
- Database deployed on [Neon](https://neon.com/) (PostgreSQL)
- First request may take up to 4 minutes due to free-tier backend and database cold start.

## Screenshots

### Login page
![Login page](/screenshots/login-page.png)

### Signup page
![Signup page](/screenshots/signup-page.png)

### Forgot password page
![Forgot password page](/screenshots/forgot-password-page.png)

### Home page

#### Top
![Home page top](/screenshots/home-page-top.png)

#### Scrolled
![Home page scrolled](/screenshots/home-page-scrolled.png)

### Edit note page
![Edit note page](/screenshots/edit-note-page.png)

## Technologies used

### Frontend
- HTML
- CSS
- JavaScript
- React
- React Router (client-side routing)
- React Hook Form

### Backend 
- Java
- Spring Boot (REST API)
- Spring Security (JWT authentication with access and refresh tokens)
- PostgreSQL

## Features
- User account creation
- Forgot password flow
- Simulated OTP verification flow for account creation and forgot password flow  
- Account login and logout
- Tasks management per user
- Create, read, update and delete tasks
- Search tasks by title, description and content
- Filter tasks by status, priority and due date
- Sort tasks by due date, priority, creation timestamp and last updation timestamp
- Pagination in tasks search results (10 tasks per page)
- Rich text editing for tasks using [Quill editor](https://quilljs.com/)

## Database schema diagram
![Database schema diagram drawn using crows foot notation.](/db-schema/db-schema.png)