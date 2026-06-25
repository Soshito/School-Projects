//Name: Sam Miller
//Project: Attendance App - This is a full stack attendance tracking and managament software
//Purpose: Frontend page for users to login. Currently the home page.

// Import necessary React components and hooks, styles, and other utilities
import React, { useState, useEffect } from 'react';
import './LoginForm.css'; // Styles specific to the LoginForm
import { FaUser, FaLock } from "react-icons/fa"; // Icons for user interface
import Cookies from "universal-cookie"; // Library to manage cookies

/**
 * LoginForm component manages user authentication. It captures user inputs for username
 * and password, submits them to the server, and handles the response.
 */
const LoginForm = () => {
    const cookies = new Cookies();
    const [username, setUsername] = useState(''); // State for username input
    const [password, setPassword] = useState(''); // State for password input
    const [jwtToken, setJwtToken] = useState(''); // State for storing the JWT token
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

    /**
     * Handles the click event of the login form submission. 
     * Submits the user credentials to the server and processes the response.
     */
    const handleClick = (e) => {
        e.preventDefault();
        const User = { username, password }; // Prepare user data for submission
        const postArgs = {
            method: "POST",
            headers: { 
                "Content-Type": "application/json",
                "Accept-Encoding": "gzip, deflate, br",
                "Connection": "keep-alive"
            },
            body: JSON.stringify(User) // Convert user data into JSON string
        };

        // Perform the POST request to the login endpoint
        fetch('http://localhost:8080/login', postArgs)
        .then(response => {
            const contentType = response.headers.get('content-type');
            if (response.status !== 404 && contentType && contentType.includes('application/json')) {
                return response.json(); // Process the JSON response
            } else {
                // Update feedback message on failure
                setFeedbackMessage(`Error logging in!`);
                setIsError(true);
                throw new Error('Login failed: incorrect credentials.');
            }
        })
        .then(data => {
            const token = data.token; // Extract JWT token from response
            const role = data.roleId;
            const userId = data.userId;
            setJwtToken(token); // Update state with the JWT token
            cookies.set("jwt_authorization", token, { path: '/' }); // Store JWT token in cookies
            setFeedbackMessage('Login successful!');
            setIsError(false);

            if(role == 1){
                window.location.href = "/layout"
            }
            if(role == 2){
                window.location.href = "/professorViewAttendanceLogs"
            }
            if(role == 3){
                cookies.set("studentId", userId);
                window.location.href = "/studentView"
            }
        }).catch(error => {
            setFeedbackMessage(`${error.message}`);
            setIsError(true);
        })
    };

    // Render the login form
    return (
        <div className='wrapper'>
            <form onSubmit={handleClick}>
                <h1>Login</h1>
                {feedbackMessage && (
                <div className={`feedback-message ${isError ? 'error-message' : 'success-message'}`}>
                    {feedbackMessage}
                </div>
                )}
                <div className='input-box'>
                    <input
                        type="text"
                        placeholder='Username'
                        value={username}
                        onChange={(e) => setUsername(e.target.value)} // Update username state on change
                        required
                    />
                    <FaUser className='icon' />
                </div>
                <div className='input-box'>
                    <input
                        type="password"
                        placeholder='Password'
                        value={password}
                        onChange={(e) => setPassword(e.target.value)} // Update password state on change
                        required
                    />
                    <FaLock className='icon' />
                </div>
                <button type="submit">Login</button>
            </form>
        </div>
    );
};

export default LoginForm; // Export LoginForm for use in other parts of the application
