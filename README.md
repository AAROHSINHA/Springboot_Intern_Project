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
1. Before implementing fuzzy search, ranking/scoring or any other advanced methods, I decided to implement basic searching. We can say it baseline or v1 of searching logic. What we do is user enters a keyword. For example `velo`, we search course name -> course description -> topic name -> subtopics -> subtopic name -> subtopic markdown. We return the course if we find the keyword at any step. This is the most basic searching logic, is case-insensitive and allows partial matching. We use this sql logic -

```
SELECT c.*
FROM courses c
LEFT JOIN topics t ON t.course_id = c.id
LEFT JOIN subtopics s ON s.topic_id = t.id
WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%'))
   OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%'))
   OR LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%'))
   OR LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%'))
   OR LOWER(s.content_markdown) LIKE LOWER(CONCAT('%', :query, '%'))
GROUP BY c.id
