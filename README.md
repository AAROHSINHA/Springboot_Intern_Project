# Springboot_Intern_Project
## NOTES
> ### 1. DATA SEEDING
Firstly, I created the entities in the folder src/main/java/com/example/demo/entities. These entities were designed to replicate the required structure and are mapped to the PostgreSQL database using JPA, which helps in modeling the data and defining relationships. After that, I wrote the data seeding logic inside CourseService, accessing the database through CourseRepository. The Topic and Subtopic entities are connected to Course through entity relationships, making them available during persistence. Finally, during application startup, a check is performed to see if courses already exist in the database. If courses are present, the process returns early since no additional seeding is required. If no courses are found, the database is populated using the JSON data. If there are no courses, we populate the 3 tables of our database - `courses`, `topics` and `subtopics` as - 
    
    create Course entity
    
    for each topic in course:
        create Topic
        set topic.course = course

        for each subtopic in topic:
            create Subtopic
            set subtopic.topic = topic

    save course (cascade saves everything)

> ### 2. Search Functionality
1.For the search feature, I implemented the basic logic as described in the assignment. When a user enters a keyword, we simply check the course title, course description, topic titles, subtopic titles, and the subtopic content for matches. The search is case-insensitive and allows partial matches. Whenever a match is found, the course is returned along with its topics and subtopics, so the user can see where the keyword appeared. This is just the straightforward, baseline version of the search — no ranking or fuzzy matching has been added yet. example return json -

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

> ### 3. Authentication
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

> ### 4. Course Enrollment
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

> ### 5. Progress Tracking - Marking Subtopic as complete
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
