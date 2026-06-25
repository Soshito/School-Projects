//Name: Sam Miller
//Project: Attendance App - This is a full stack attendance tracking and management software
//Purpose: Frontend page for users to create sections.

// Import necessary modules and styles
import React, { useState, useEffect } from 'react';
import Cookies from 'js-cookie'; // Library to handle cookies
import '../../Components/Styles/GruvboxTheme.css'; // Theme CSS import

// CreateSection component definition
const CreateSection = () => {
    // State hooks for managing form data and API data
    const [roomNum, setRoomNumber] = useState(''); // State for room number input
    const [professorId, setProfessorId] = useState(''); // State for selected professor ID
    const [numberOfStudent, setNumStudents] = useState(''); // State for the number of students
    const [courseId, setCourseId] = useState(''); // State for selected course ID
    const [professors, setProfessors] = useState([]); // State to store professors fetched from the API
    const [courses, setCourses] = useState([]); // State to store courses fetched from the API
    const [jwtToken, setJwtToken] = useState(''); // State to store the JWT token
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

    // Fetch JWT from cookies when component mounts
    useEffect(() => {
        const jwt = Cookies.get('jwt_authorization');
        setJwtToken(jwt);
    }, []);

    // Fetch professors and courses if JWT token is available
    useEffect(() => {
        if (jwtToken) {
            fetchProfessors();
            fetchCourses();
        }
    }, [jwtToken]); // Re-run this effect when jwtToken changes

    // Function to fetch professor data from the API
    const fetchProfessors = async () => {
        try {
            const response = await fetch(`http://localhost:8080/users/by-roleId?roleId=2`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${jwtToken}` // Use JWT for authorization
                }
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setProfessors(data || []);
        } catch (error) {
            console.error('Failed to fetch professors:', error);
        }
    };

    // Function to fetch course data from the API
    const fetchCourses = async () => {
        try {
            const response = await fetch(`http://localhost:8080/courses`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${jwtToken}` // Use JWT for authorization
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

    // Handler for form submission
    const handleSubmit = (e) => {
        e.preventDefault();
        const section = { roomNum, numberOfStudent, courseId, professorId }; // Gather all form data into an object
        
        // POST request to create a new section
        fetch('http://localhost:8080/sections', {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${jwtToken}` // Use JWT for authorization
            },
            body: JSON.stringify(section) // Send section data as JSON
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to create section');
            }
            return response.json();
        })
        .then(() => {
            setFeedbackMessage(`Section created successfully!`);
            setIsError(false);
        })
        .catch(error => {
            setFeedbackMessage(`Error creating section: ${error.message}`);
            setIsError(true);
        });
    };

    // JSX for rendering the section creation form
    return (
        <div className='wrapper'>
            <form onSubmit={handleSubmit}>
                <h1>Create Section</h1>
                {feedbackMessage && (
                <div className={`feedback-message ${isError ? 'error-message' : 'success-message'}`}>
                    {feedbackMessage}
                </div>
                )}
                <div className='input-box'>
                    <h2>Input Room Number</h2>
                    <input type="text" placeholder="001A" value={roomNum} onChange={(e) => setRoomNumber(e.target.value)} required />
                </div>
                <div className='input-box'>
                    <h2>Select Professor</h2>
                    <select value={professorId} onChange={(e) => setProfessorId(e.target.value)} required className="form-select">
                        <option value="">Select a Professor</option>
                        {professors.map((prof) => (
                            <option key={prof.id} value={prof.id}>{prof.firstName} {prof.lastName}</option>
                        ))}
                    </select>
                </div>
                <div className='input-box'>
                    <h2>Select Course</h2>
                    <select value={courseId} onChange={(e) => setCourseId(e.target.value)} required className="form-select">
                        <option value="">Select a Course</option>
                        {courses.map((course) => (
                            <option key={course.id} value={course.id}>{course.name}</option>
                        ))}
                    </select>
                </div>
                <div className='input-box'>
                    <h2>Input Number of Students</h2>
                    <input type="number" defaultValue="1" min="0" value={numberOfStudent} onChange={(e) => setNumStudents(e.target.value)} required />
                </div>
                <button type='submit'>Submit</button>
            </form>
        </div>
    );
};

export default CreateSection; // Export the component for use in other parts of the app
