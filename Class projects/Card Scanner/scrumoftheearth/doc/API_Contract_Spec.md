# HTTP Responses
| Code| Status      | Description       
|-----|-------------|-------------------
| 200 | OK          | Succesful request.
| 201 | CREATED     | New resource created, generally indicates a successful POST.
| 204 | NO CONTENT  | Successful DELETE request
| 400 | BAD REQUEST | Client error regarding request contents. Request should not be repeated.
| 403 | FORBIDDEN   | Client is either unauthenticated or not authorized to access the requested resource. Request should not be repeated.
| 404 | NOT FOUND   | Client has requested a resource that cannot be found.

# Authentication
**POST /login**
----
* **URL Params:**
None
* **Headers:**
    * Content-Type:
        application/json
* **Body:**
```
{
    username: String,
    password: String
}
```
* **Success Response:**
    * Status Code:
        200 OK
    * Body:
        ```
        {
            message: "Authentication successful",
            token: <JWT>,
            roleId: Integer,
            userId: Integer
        }
        ```

&nbsp;

# Commands
**POST /commands/undo**
----
Undo the most recent CRUD operation on Users and Courses
* **URL Params:**
None
* **Headers:**
    * Content-Type:
        application/json
* **Body:**
None
* **Success Response:**
    * Status Code:
        200 OK

**POST /commands/redo**
----
Redo the most recent undone CRUD operation on Users and Courses
* **URL Params:**
None
* **Headers:**
    * Content-Type:
        application/json
* **Body:**
None
* **Success Response:**
    * Status Code:
        200 OK

# User
* UserResponse object:
```
{
    id: int,
    firstName: String
    lastName: String,
    studentCardId: String,
    username: String,
    roleId: int
}
```
**POST /users**
----
Creates a new User and returns the URI of the new object.
* **URL Params:** 
None
* **Headers:**
    * Content-Type:
        application/json
    * Authorization:
        Bearer `<JWT>`
* **Body:**
```
{
    firstName: String,
    lastName: String,
    studentCardId: String,
    username: String,
    password: String,
    roleId: int
}
```
* **Success Response:**
    * Status Code: 
        201 CREATED
    * Header: `Location=/users/{id}`
    * Body: `<User>`

**GET /users/{id}**
---
Get a User by their ID
* **URL Params:**
    * {id} = ID of requested User
* **Headers:**
    * Content-Type:
        application/json
    * Authorization:
        Bearer `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code: 200 OK
    * Body: `<User>`

**GET /users**
---
Get a List out of all existing Users \
Sample URI: "/users?page=0&size=25&sort=lastName,asc"
* **URL Params:**
None
* **Request Params**
    * page: int (default = 0)
    * size: int (default = 100)
    * sort: String (default = "id,asc")
* **Headers:**
    * Content-Type:
        application/json
    * Authroization:
        Bearer: `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code:
        200 OK
    * Body: `List<User>`

**GET /users/by-roleId**
---
Get a List out of all existing Users with the specified roleId
* **URL Params:**
None
* **Request Params**
    * roleId: int
    * page: int (default = 0)
    * size: int (default = 100)
    * sort: String (default = "id,asc")
* **Headers:**
    * Content-Type:
        application/json
    * Authroization:
        Bearer: `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code:
        200 OK
    * Body: `List<User>`

**DELETE /users/{id}**
---
Delete a User
* **URL Params:**
    * {id} = ID of requested User
* **Headers:**
    * Content-Type:
        application/json
    * Authroization:
        Bearer: `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code:
        204 NO CONTENT

&nbsp;

# AttendanceLog
* AttendanceLog object:
```
{
    id: Integer,
    studentId: int,
    sectionId: int,
    dateTime: TimeStamp,
    excused: Boolean,
    absent: boolean
}
```
**POST /attendancelogs**
----
Creates a new AttendenceLog and returns the URI of the new object along with the created object. \
Intended only to be interacted with via IoT scanners. Possession of a student ID card serves as authorization.
* **URL Params:**
None
* **Headers:**
    * Content-Type:
        application/json
    * Authorization:
        Bearer `<JWT>`
* **Body:**
```
{
    studentCardId: String,
    roomNumber: Integer
}
```
* **Success Response:**
    * Status Code:
        201 CREATED
    * Header: `Location=/attendancelogs/{id}`
    * Body: `<AttendanceLog>`

**GET /attendancelogs/{id}**
---
Get an AttendanceLog by its ID
* **URL Params:**
    * {id} = ID of requested AttendanceLog
* **Headers:**
    * Content-Type:
        application/json
    * Authorization:
        Bearer `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code: 200 OK
    * Body: `<AttendanceLog>`

**GET /attendancelogs/by-studentId-and-sectionId**
---
Get a List of AttendanceLogs associated with the specified studentId and sectionId \
Accessible to Users with the Student Role, who may only request AttendanceLogs with their ID
* **URL Params:**
None
* **Request Params**
    * studentId: int
    * sectionId: int
    * page: int (default = 0)
    * size: int (default = 100)
    * sort: String (default = "id,asc")
* **Headers:**
    * Content-Type:
        application/json
    * Authroization:
        Bearer: `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code:
        200 OK
    * Body: `List<AttendanceLog>`

&nbsp;

# Course
* Course object:
```
{
    id: Integer,
    name: String,
    sectionCount: Integer
}
```
**POST /courses**
----
Creates a new Course and returns the URI of the new object along with the created object.
* **URL Params:**
None
* **Headers:**
    * Content-Type:
        application/json
    * Authorization:
        Bearer `<JWT>`
* **Body:**
```
{
    name: String
}
```
* **Success Response:**
    * Status Code:
        201 CREATED
    * Header: `Location=/courses/{id}`
    * Body: `<Course>`

**GET /courses/{id}**
---
Get a Course by its ID
* **URL Params:**
    * {id} = ID of requested Course
* **Headers:**
    * Content-Type:
        application/json
    * Authorization:
        Bearer `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code: 200 OK
    * Body: `<Course>`

**GET /courses**
---
Get a List out of all existing Courses
* **URL Params:**
None
* **Request Params**
    * page: int (default = 0)
    * size: int (default = 100)
    * sort: String (default = "id,asc")
* **Headers:**
    * Content-Type:
        application/json
    * Authroization:
        Bearer: `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code:
        200 OK
    * Body: `List<Course>`

&nbsp;

# Section
* Section object:
```
{
    id: Integer,
    roomNum: int,
    numStudents: Integer,
    courseId: int,
    professorId: int
}
```
**POST /sections**
----
Creates a new Section and returns the URI of the new object along with the created object.
* **URL Params:**
None
* **Headers:**
    * Content-Type:
        application/json
    * Authorization:
        Bearer `<JWT>`
* **Body:**
```
{
    roomNum: int,
    numberOfStudent: int,
    courseId: int,
    professorId: int
}
```
* **Success Response:**
    * Status Code:
        201 CREATED
    * Header: `Location=/sections/{id}`
    * Body: `<Section>`

**GET /sections/{id}**
---
Get a Section by its ID
* **URL Params:**
    * {id} = ID of requested Section
* **Headers:**
    * Content-Type:
        application/json
    * Authorization:
        Bearer `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code: 200 OK
    * Body: `<Section>`

**GET /sections**
---
Get a List out of all existing Sections
* **URL Params:**
None
* **Request Params**
    * page: int (default = 0)
    * size: int (default = 100)
    * sort: String (default = "id,asc")
* **Headers:**
    * Content-Type:
        application/json
    * Authroization:
        Bearer: `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code:
        200 OK
    * Body: `List<Section>`

**GET /sections/by-courseId**
---
Get a List of Sections associated with the specified courseId \
* **URL Params:**
None
* **Request Params**
    * courseId: int
    * page: int (default = 0)
    * size: int (default = 100)
    * sort: String (default = "id,asc")
* **Headers:**
    * Content-Type:
        application/json
    * Authroization:
        Bearer: `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code:
        200 OK
    * Body: `List<Section>`

**GET /sections/by-studentId**
---
Get a List of Sections a User with the provided studentId is enrolled in \
* **URL Params:**
None
* **Request Params**
    * studentId: int
    * page: int (default = 0)
    * size: int (default = 100)
    * sort: String (default = "id,asc")
* **Headers:**
    * Content-Type:
        application/json
    * Authroization:
        Bearer: `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code:
        200 OK
    * Body: `List<Section>`

&nbsp;

# MeetingTime
* MeetingTime object:
```
{
    id: Integer,
    sectionId: int,
    dayOfWeek: int,
    startTime: Time,
    endTime: Time
}
```
**POST /meetingtimes**
----
Creates a new MeetingTime and returns the URI of the new object along with the created object.
* **URL Params:**
None
* **Headers:**
    * Content-Type:
        application/json
    * Authorization:
        Bearer `<JWT>`
* **Body:**
    * Note: It is expected for startTime and endTime to follow "hh:mm" format
```
{
    sectionId: int,
    dayOfWeek: int,
    startTime: String,
    endTime: String
}
```
* **Success Response:**
    * Status Code:
        201 CREATED
    * Header: `Location=/meetingtimes/{id}`
    * Body: `<MeetingTime>`

**GET /meetingtimes/{id}**
---
Get a MeetingTime by its ID
* **URL Params:**
    * {id} = ID of requested MeetingTime
* **Headers:**
    * Content-Type:
        application/json
    * Authorization:
        Bearer `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code: 200 OK
    * Body: `<MeetingTime>`

&nbsp;

# Enrollment
* Enrollment object:
```
{
    id: Integer,
    sectionId: int,
    studentId: int
}
```
**POST /enrollments**
----
Creates a new Enrollment and returns the URI of the new object along with the created object.
* **URL Params:**
None
* **Headers:**
    * Content-Type:
        application/json
    * Authorization:
        Bearer `<JWT>`
* **Body:**
```
{
    sectionId: int,
    studentId: int
}
```
* **Success Response:**
    * Status Code:
        201 CREATED
    * Header: `Location=/enrollments/{id}`
    * Body: `<Enrollment>`

**GET /enrollments/{id}**
---
Get an Enrollment by its ID
* **URL Params:**
    * {id} = ID of requested Enrollment
* **Headers:**
    * Content-Type:
        application/json
    * Authorization:
        Bearer `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code: 200 OK
    * Body: `<Enrollment>`

**DELETE /enrollments/{id}**
---
Delete an Enrollment
* **URL Params:**
    * {id} = ID of requested Enrollment
* **Headers:**
    * Content-Type:
        application/json
    * Authroization:
        Bearer: `<JWT>`
* **Body:**
None
* **Success Response:**
    * Status Code:
        204 NO CONTENT