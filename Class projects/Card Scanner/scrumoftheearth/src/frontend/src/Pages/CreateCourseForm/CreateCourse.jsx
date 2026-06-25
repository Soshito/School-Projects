//Name: Sam Miller
//Project: Attendance App - This is a full stack attendance tracking and management software
//Purpose: Frontend page for users to create courses.

// Importing necessary modules and styles
import React, { useState, useEffect } from 'react';
import Cookies from 'js-cookie'; // Library to handle cookies
import '../../Components/Styles/GruvboxTheme.css'; // Theme CSS import

// CreateCourse component definition
const CreateCourse = () => {
    // State hooks for managing course name and JWT
    const [name, setName] = useState(''); // State for storing the course name input
    const [jwt, setJwt] = useState(''); // State for storing the JWT token
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

    // Effect hook to get the JWT token from cookies when the component mounts
    useEffect(() => {
        const jwtToken = Cookies.get('jwt_authorization'); // Retrieve JWT token from cookies
        setJwt(jwtToken); // Update the JWT state with the retrieved token
    }, []);

    // Handler for form submission
    const handleSubmit = (e) => {
        e.preventDefault(); // Prevent default form submission behavior
        const course = { name }; // Create a course object with the name from state

        // Making a POST request to create a new course
        fetch('http://localhost:8080/courses', {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + jwt // Include the JWT in the authorization header
            },
            body: JSON.stringify(course) // Send the course object as JSON in the request body
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to create course'); // Throw an error if the response is not OK
            }
            return response.json(); // Parse the JSON response
        })
        .then(() => {
            setFeedbackMessage(`Course created successfully!`);
            setIsError(false);
        })
        .catch(error => {
            setFeedbackMessage(`Error creating course: ${error.message}`);
            setIsError(true);
        });
    };
    
    // Handles undoing the most recent course on the stack
    const handleUndo = (e) => {
        e.preventDefault();
        //config post
        const postArgs = {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + jwt
            },
            body: JSON.stringify()
        };
    

        //send post to the API
        fetch(`http://localhost:8080/commands/undo`, postArgs)
        .then(data => {
            setFeedbackMessage(`Course undone successfully!`);
            
        })
        .catch(error => {
            setFeedbackMessage(`Error undoing course: ${error.message}`);
            
        });
    }
    // Handles redoing the most recent course on the stack
    const handleRedo = (e) => {
        e.preventDefault();
        // Configuration for the POST request
        const postArgs = {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + jwt
            },
            body: JSON.stringify()
        };

        // Send POST request to backend API
        fetch('http://localhost:8080/commands/redo', postArgs)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to create course');
                }
                return response.json();
            })
            .then(data => {
                setFeedbackMessage(`Course created successfully!`);
                setIsError(false);
            })
            .catch(error => {
                setFeedbackMessage(`Course created successfully!`);
                setIsError(false);
            });
    }


    // JSX for rendering the Create Course form
    return (
        <div className='wrapper'>
            <form onSubmit={handleSubmit}>
                <h1>Create Course</h1>
                {feedbackMessage && (
                <div className={`feedback-message ${isError ? 'error-message' : 'success-message'}`}>
                    {feedbackMessage}
                </div>
                )}
                <div className='input-box'>
                    <h2>Course Name</h2>
                    <input 
                        type='text'
                        placeholder='Ex: Algebra'
                        value={name}
                        onChange={(e) => setName(e.target.value)} // Update state on input change
                        required
                    />
                </div>
                <div>
                <button type="submit">Submit</button>
                </div>
                <div>
                    <button onClick={handleUndo}>Undo</button>
                    <button onClick={handleRedo}>Redo</button>
                </div>
            </form>
        </div>
    );
};

export default CreateCourse; // Export the component for use in other parts of the app
