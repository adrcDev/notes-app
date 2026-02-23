# Notes website

## Deployment
### Links
- Frontend (Website): https://notes-app-react-frontend.onrender.com
- Backend REST API: https://notes-app-backend-fxov.onrender.com
- REST API documentation (Swagger UI): https://swagger-ui-rest-api-documentation.onrender.com

### Deployment details
- Frontend (Website) and REST API documentation (Swagger UI) deployed on [Render](https://render.com/) (static site service)
- Backend deployed on [Render](https://render.com/) (web service)
- Database deployed on [Neon](https://neon.com/) (PostgreSQL)
- The backend may take a few minutes to respond on the first request due to free-tier cold start

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
- Simulated OTP verification flow for account creation and forgot password  
- Account login and logout
- Notes management per user
- Create, read, update and delete notes
- Search notes by title, description and content
- Filter notes by status, priority and due date
- Sort notes by due date, priority, creation timestamp and last updation timestamp
- Pagination in notes search results (10 notes per page)
- Rich text note editing using [Quill editor](https://quilljs.com/)

## Database schema diagram
![Database schema diagram drawn using crows foot notation.](/db-schema/db-schema.png)