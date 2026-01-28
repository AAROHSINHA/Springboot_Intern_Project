# Springboot_Intern_Project
## NOTE
`Note : ` After login route returns response, please copy the jwt token from it and add in authorization field at top right of swagger. Then only wou will be marked login and will be able to access protected (authenticated) routes. 
=> There are 2 types of routes here, public and private. Any one can access public routes. Private routes can be accessed by people who have logged in. We implement private routes by running a security filter chain. View `java\com\example\demo\config\SecurityConfig.java` for complete context!

## NOTES
### 1.🏗️ DATA SEEDING
Firstly, I created the entities in the folder src/main/java/com/example/demo/entities. These entities were designed to replicate the required structure and are mapped to the PostgreSQL database using JPA, which helps in modeling the data and defining relationships. After that, I wrote the data seeding logic inside CourseService, accessing the database through CourseRepository. The Topic and Subtopic entities are connected to Course through entity relationships, making them available during persistence. Finally, during application startup, a check is performed to see if courses already exist in the database. If courses are present, the process returns early since no additional seeding is required. If no courses are found, the database is populated using the JSON data. If there are no courses, we populate the 3 tables of our database - `courses`, `topics` and `subtopics` as - 
    
    create Course entity
    
    for each topic in course:
        create Topic
        set topic.course = course

        for each subtopic in topic:
            create Subtopic
            set subtopic.topic = topic

    save course (cascade saves everything)

 ### 2. 🔍 Search Functionality
1.For the search feature, we implemented a straightforward keyword matching logic that scans across the entire course hierarchy. When a user enters a query, the system checks for matches in course titles and descriptions, topic titles, and subtopic titles or content. By using a case-insensitive partial matching approach, a search for "velo" will successfully return results for "velocity." We then group these findings by course so the user can clearly see the context of each match, whether it appeared in a high-level title or deep within the markdown content. Whenever a match is found, the course is returned along with its topics and subtopics, so the user can see where the keyword appeared. This is just the straightforward, baseline version of the search — no ranking or fuzzy matching has been added yet.

2. After implementing the baseline logic, I decided to add fuzzy search logic as a fallback instead of main functionality. Because fuzzy search is expensive, and if user simply searches correct keyword, there would be unoptimal expensive fuzzy search. So now, for any query, first baseline runs. If baseline is unable to find results, fuzzy search comes into play. the system automatically triggers Fuzzy Logic using PostgreSQL's Trigram similarity (pg_trgm).

3. example json on success - 

```
{
  "courseId": "...",
  "courseTitle": "...",
  "matches": [
    {
      "type": "subtopic",
      "topicTitle": "...",
      "subtopicId": "...",
      "subtopicTitle": "...",
      "snippet": "..."
    },
    {
      "type": "content",
      "topicTitle": "...",
      "subtopicId": "...",
      "subtopicTitle": "...",
      "snippet": "..."
    }
  ]
}
```

### 3. 🔑 Authentication
1. First I implemented a simple register api. Simply for user to create an account. User simply needs to add his email and password. We check if that email already exists. If not, we create the account. If user exists we provide 409 status code and error as -
```
{
  "timestamp": "2026-01-27T17:16:31.835682200Z",
  "error": "Conflict",
  "message": "User with email 'aarohsinha.programming@gmail.com' already exists"
}
```
Now for registering, I simply have the user service in the codebase. There we first hash the password using `bcrypt`. It was not mentioned in the assignment whether we need to hash or not, but hashing is the most basic security step in any project, so I also added it here. After this the user is registered and the database is updated!

2. For login, what we do is simply take in user email and password, and check if it matches. it it does, we return a response as -
```
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "student@example.com",
  "expiresIn": 86400
}
```

### 4. 🎓 Course Enrollment
For course enrollment, we had the enrollments table, which stores 2 key attributes user_id and course_id. When user enters the api for enrolling course, we have the course_id and the user (this is authenticated route, the protected one). So we first check if any such course exist, as you cannot register in a course not present. After that we also check if user is not already registered in the course. This is because we cannot have 2 similar rows in the database too. If all the conditions match, we simply create a enrollment and save to the database!
If this happens, we recieve the required output
```
{
  "enrolledAt": "2026-01-28T18:27:50.443917800Z",
  "enrollmentId": 6,
  "courseId": "physics-101",
  "courseTitle": "Introduction to Physics"
}
```
**enrollmentId must be noted by the user as it is required in future to check progress**

### 5. 📈 Progress Tracking - Marking Subtopic as complete
For progress tracking we have the `subtopic-id` from the route parameter and the `user-email` from the jwt-token. So we simply
1. Fetch the subtopic from database.
2. Then we walk up the database from subtopic -> topics -> courses (it's parents)
3. We do this to get course and check if user is enrolled in course, which contains the subtopic.
4. If user is enrolled, we check if subtopic is already complete. If not we mark it as complete

> ### 5. View Progress
For this we simply - 
1. take in enrollment id (returned from course enrollment) and we also have user id and gmails
2. We check if user is enrolled or not in this course. simply matching user id from enrollment id in the table.
3. We do a simple sql query to get the completed subtopics -
   ```
      SELECT sp
        FROM SubtopicProgress sp
        WHERE sp.user.id = :userId
          AND sp.completedAt IS NOT NULL
          AND sp.subtopic.topic.course.id = :courseId
   ```
4. Once we get all course, topics and subtopics we simply perform the calculations and return the DTO as response required

### 6. 🛠️ ERROR HANDLING
1. I created custom exception classes for different error cases (like unauthorized access, already enrolled, course not found). Each one represents a clear mistake that can happen in the app.
2. I added a GlobalExceptionHandler that acts like a single safety net. Whenever any exception is thrown anywhere in the app, it comes here automatically.
3. This handler converts every error into the same JSON format (error, message, timestamp), so the frontend always gets clean and predictable responses.
4.  Based on the exception type, it also sets the correct HTTP status (400, 403, 404, 409, 500), instead of random or unclear errors.


## 🏗️ PROJECT STRUCTURE
Project Structure:
```
src/
 └─ main/
     └─ java/
         └─ com/example/demo/
             ├─ config/
             │   ├─ JwtFilter.java
             │   ├─ JwtUtil.java
             │   ├─ OpenApiConfig.java
             │   └─ SecurityConfig.java
             │
             ├─ controllers/
             │   ├─ AuthController.java
             │   ├─ CourseController.java
             │   ├─ EnrollmentController.java
             │   ├─ EnrollmentProgressController.java
             │   ├─ ProgressController.java
             │   └─ SearchController.java
             │
             ├─ dto/
             │   ├─ CompletedSubtopicDTO.java
             │   ├─ CourseDetailDTO.java
             │   ├─ CourseSearchResult.java
             │   ├─ CourseSummaryDTO.java
             │   ├─ EnrollmentProgressResponse.java
             │   ├─ MatchDetail.java
             │   ├─ SubtopicDTO.java
             │   └─ TopicDTO.java
             │
             ├─ exceptions/
             │   ├─ AlreadyEnrolledException.java
             │   ├─ AuthenticationFailedException.java
             │   ├─ AuthInternalException.java
             │   ├─ CourseNotFoundException.java
             │   ├─ EnrollmentNotFoundException.java
             │   ├─ GlobalExceptionHandler.java
             │   ├─ InvalidCredentialsException.java
             │   ├─ InvalidInputException.java
             │   ├─ NotEnrolledException.java
             │   ├─ SubtopicNotFoundException.java
             │   ├─ UnauthorizedAccessException.java
             │   └─ UserAlreadyExistsException.java
             │
             ├─ repositories/
             │   ├─ CourseRepository.java
             │   ├─ EnrollmentRepository.java
             │   ├─ SubtopicProgressRepository.java
             │   ├─ SubtopicRepository.java
             │   ├─ TopicRepository.java
             │   └─ UserRepository.java
             │
             ├─ services/
             │   ├─ AuthService.java
             │   ├─ CourseService.java
             │   ├─ EnrollmentProgressService.java
             │   ├─ EnrollmentService.java
             │   ├─ ProgressService.java
             │   └─ SearchService.java
             │
             ├─ DataSeeder.java
             ├─ DemoApplication.java
             └─ HelloController.java
```

## API Routes
> **`POST /api/subtopics/{subtopicId}/complete`** – Mark a subtopic as completed by user
> 
> **`POST /api/courses/{courseId}/enroll`** – Enroll a user in the selected course
> 
> *`*POST /api/auth/register`** – Register a new user account securely
> 
> **`POST /api/auth/login`** – Authenticate user and return access token
> 
> **`GET /hello`** – Basic endpoint to check server status
> 
> **`GET /api/search`** – Search for courses or subtopics
> 
> **`GET /api/enrollments/{enrollmentId}/progress`** – Fetch progress details for user enrollment
> 
> **`GET /api/courses`** – Retrieve list of all available courses
> 
> **`GET /api/courses/{id}`** – Retrieve details for a specific course
