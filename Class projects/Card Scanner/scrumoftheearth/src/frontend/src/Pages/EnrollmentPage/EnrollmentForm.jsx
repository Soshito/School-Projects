//Name: Sam Miller
//Project: Attendance App - This is a full stack attendance tracking and managament software
//Purpose: Frontend page for Admins to create enrollments for Students.

// Import necessary modules and CSS
import React, { useState, useEffect } from 'react';
import Cookies from 'js-cookie'; // Library to handle cookies

// EnrollmentForm component definition
const EnrollmentForm = () => {
    // State hooks to manage form fields and fetched data
    const [studentId, setStudentId] = useState('');
    const [students, setStudents] = useState([]);
    const [sectionId, setSectionId] = useState('');
    const [sections, setSections] = useState([]);
    const [courses, setCourses] = useState([]);
    const [courseId, setCourseId] = useState('');
    const [jwt, setJwtToken] = useState('');
    const [feedbackMessage, setFeedbackMessage] = useState('');
    const [isError, setIsError] = useState(false);
    
    

    useEffect(() => {
        if (feedbackMessage) {
            const timer = setTimeout(() => {
                setFeedbackMessage('');
            }, 5000);  // Clears feedback after 5 seconds
    
            return () => clearTimeout(timer);
        }
    }, [feedbackMessage]);

    // Function to update course ID and fetch sections for that course
    const setNewCourseId = (newCourseId) => {
        setCourseId(newCourseId);
        if (newCourseId) fetchSectionsByCourseId(newCourseId);
    };

    // Fetch JWT from cookies when component mounts
    useEffect(() => {
        const jwtToken = Cookies.get('jwt_authorization');
        setJwtToken(jwtToken);
    }, []);

    // Fetch students and courses when JWT is available
    useEffect(() => {
        if (jwt) {
            fetchStudents();
            fetchCourses();
        }
    }, [jwt]);
    

    // Async function to fetch students from backend
    const fetchStudents = async () => {
        try {
            const response = await fetch(`http://localhost:8080/users/by-roleId?roleId=3`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${jwt}`
                }
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setStudents(data || []);
        } catch (error) {
            console.error('Failed to fetch students:', error);
        }
    };

    // Async function to fetch courses from backend
    const fetchCourses = async () => {
        try {
            const response = await fetch(`http://localhost:8080/courses`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${jwt}`
                }
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setCourses(data || []);
        } catch (error) {
            console.error('Failed to fetch courses:', error);
        }
    };

    // Fetch sections based on selected course
    const fetchSectionsByCourseId = async (courseId) => {
        if (!courseId) return;
    
        const url = new URL('http://localhost:8080/sections/by-courseId');
        url.searchParams.append('courseId', courseId);
    
        try {
            const response = await fetch(url, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${jwt}`
                }
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setSections(data || []);
        } catch (error) {
            console.error('Failed to fetch sections:', error);
        }
    };
    
    // Handle form submission to create an enrollment
    const handleSubmit = async (e) => {
        e.preventDefault();
        const enrollmentPost = { sectionId, studentId };
        

        try {
            const response = await fetch('http://localhost:8080/enrollments', {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${jwt}`
                },
                body: JSON.stringify(enrollmentPost)
            });

            if (!response.ok) throw new Error('Failed to create enrollment');

            const data = await response.json();
            const ID = data.id // fixx this
            setFeedbackMessage("Enrollment created successfully");
            setIsError(false);
            
        } catch (error) {
            setFeedbackMessage(`Error creating enrollment: ${error.message}`);
            setIsError(false);
        }
    };
   

    // JSX to render the form with dynamic dropdowns
    return (
        <div className='wrapper'>
            <form onSubmit={handleSubmit}>
                <h1>Create Enrollment</h1>
                {feedbackMessage && (
                <div className={`feedback-message ${isError ? 'error-message' : 'success-message'}`}>
                    {feedbackMessage}
                </div>
                )}
                <div className='input-box'>
                    <h2>Select Course</h2>
                    <select value={courseId} onChange={(e) => setNewCourseId(e.target.value)} required className="form-select">
                        <option value="">Select a Course</option>
                        {courses.map((course) => (
                            <option key={course.id} value={course.id}>{course.name}</option>
                        ))}
                    </select>
                </div>
                <div className='input-box'>
                    <h2>Select a Section</h2>
                    <select value={sectionId} onChange={(e) => setSectionId(e.target.value)} required className="form-select">
                        <option value="">Select a Section</option>
                        {sections.map((section) => (
                            <option key={section.id} value={section.id}>{section.roomNum}</option>
                        ))}
                    </select>
                </div>
                <div className='input-box'>
                    <h2>Select Student</h2>
                    <select value={studentId} onChange={(e) => setStudentId(e.target.value)} required className="form-select">
                        <option value="">Select a Student</option>
                        {students.map((student) => (
                            <option key={student.id} value={student.id}>{student.firstName} {student.lastName}</option>
                        ))}
                    </select>
                </div>
                <button type="submit">Submit</button>
            </form>
        </div>
    );
}

export default EnrollmentForm; // Export the component for use in other parts of the application
