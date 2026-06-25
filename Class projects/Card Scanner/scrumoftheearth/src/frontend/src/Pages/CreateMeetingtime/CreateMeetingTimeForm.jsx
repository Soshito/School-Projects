//Name: Sam Miller
//Project: Attendance App - This is a full stack attendance tracking and management software
//Purpose: Frontend page for users to create meeting times.

// Import necessary modules and styles
import React, { useState, useEffect } from 'react';
import './CreateMeetingTime.css'; // CSS for this component
import Cookies from 'js-cookie'; // Library for handling cookies

// Define the CreateMeetingTime component
const CreateMeetingTime = () => {
    // State hooks for storing form data and JWT token
    const [startTime, setStartTime] = useState('');
    const [endTime, setEndTime] = useState('');
    const [sectionId, setSectionId] = useState('');
    const [jwtToken, setJwtToken] = useState('');
    const [sections, setSections] = useState([]);
    const [courses, setCourses] = useState([]);
    const [courseId, setCourseId] = useState('');
    const [feedbackMessage, setFeedbackMessage] = useState('');
    const [isError, setIsError] = useState(false);
    const [daysOfWeek, setDaysOfWeek] = useState([]);

// Handler for checkbox changes
    const handleDayChange = (day) => {
        setDaysOfWeek(prevDays => {
            if (prevDays.includes(day)) {
                return prevDays.filter(d => d !== day); // Remove day if already included
            } else {
                return [...prevDays, day]; // Add day if not included
            }
        });
    };

    // Options for the day of the week dropdown
    const options = [
        { label: "Monday", value: 1 },
        { label: "Tuesday", value: 2 },
        { label: "Wednesday", value: 3 },
        { label: "Thursday", value: 4 },
        { label: "Friday", value: 5 }, 
        { label: "Saturday", value: 6 },
        { label: "Sunday", value: 7 },

    ];

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
        const jwtToken = Cookies.get('jwt_authorization');
        setJwtToken(jwtToken);
    }, []);

    // Effect hook to retrieve JWT token from cookies on component mount
    useEffect(() => {
        const jwt = Cookies.get('jwt_authorization'); // Fetch JWT token from cookies
        setJwtToken(jwt); // Set the JWT token state
    }, []);

    // Fetch students and courses when JWT is available
    useEffect(() => {
        if (jwtToken) {
            fetchCourses();
        }
    }, [jwtToken]);

    // Function to update course ID and fetch sections for that course
    const setNewCourseId = (newCourseId) => {
        setCourseId(newCourseId);
        if (newCourseId) fetchSectionsByCourseId(newCourseId);
    };

    // Async function to fetch courses from backend
    const fetchCourses = async () => {
        try {
            const response = await fetch(`http://localhost:8080/courses`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${jwtToken}`
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
                    "Authorization": `Bearer ${jwtToken}`
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

    const handleSubmit = (e) => {
        e.preventDefault();
        if (daysOfWeek.length === 0) {
            setFeedbackMessage("Error: No day selected for meeting time!");
            setIsError(true);
            return; // Exit the function early if no days are selected
        }
        let successfulDays = []; // To hold the days for which the meeting time was successfully created
    
        Promise.all(daysOfWeek.map(day => {
            const meetingTime = { startTime, endTime, dayOfWeek: day, sectionId };
    
            return fetch('http://localhost:8080/meetingtimes', {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + jwtToken
                },
                body: JSON.stringify(meetingTime)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to create meeting time');
                }
                return response.json();
            })
            .then(() => {
                const dayLabel = options.find(option => option.value === day)?.label || 'Unknown day';
                successfulDays.push(dayLabel); // Add the successful day's label
            });
        }))
        .then(() => {
            if (successfulDays.length > 0) {
                // Sort successfulDays based on the order in options
                successfulDays.sort((a, b) => {
                    let indexA = options.findIndex(opt => opt.label === a);
                    let indexB = options.findIndex(opt => opt.label === b);
                    return indexA - indexB;
                });
    
                // Format the success message to include all successful days
                const formattedDays = successfulDays.join(", ");
                setFeedbackMessage(`Meeting time successfully created for: ${formattedDays}`);
                setIsError(false);
            }
        })
        .catch(error => {
            setFeedbackMessage(`Error creating meeting time: ${error.message}`);
            setIsError(true);
        });
    };

    // JSX to render the form UI
    return (
        <div className='wrapper'>
            <form onSubmit={handleSubmit}>
                <h1>Create Meeting Time</h1>
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
                <div className='input-box' required>
                    <h2>Start Time</h2>
                    <input
                        type="time"
                        value={startTime}
                        onChange={(e) => setStartTime(e.target.value)} // Update startTime state on change
                        required
                    />
                </div>
                <div className='input-box' required>
                    <h2>End Time</h2>
                    <input
                        type="time"
                        value={endTime}
                        onChange={(e) => setEndTime(e.target.value)} // Update endTime state on change
                        required
                    />
                </div>
                <div className='day-checkbox' required>
                    <h2>Meeting Days</h2>
                    {options.map((option) => (
                        <label key={option.value} className="day-checkbox">
                            <input
                                type="checkbox"
                                checked={daysOfWeek.includes(option.value)}
                                onChange={() => handleDayChange(option.value)}
                            />
                            {option.label}
                        </label>
                    ))}
                </div>
                <div>
                    <button type='submit'>Submit</button>
                </div>
            </form>
        </div>
    );
};

export default CreateMeetingTime; // Export the component for use in other parts of the application
