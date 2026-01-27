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

