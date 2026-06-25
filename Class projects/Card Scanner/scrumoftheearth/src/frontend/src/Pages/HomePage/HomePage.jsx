//Name: Dylan Brodie
//Project: Attendance App - This is a full stack attendance tracking and management software
//Purpose: Frontend page that acts as a layout to handle routing and main content display.

// Import React and CSS styles
import React from 'react';
import './HomePage.css'; // Styles specific to the HomePage
import '../../Components/Styles/GruvboxTheme.css'; // Global theme styles

/**
 * HomePage component acts as a layout container for the application.
 * It defines a structure where other components or router-outlet can be placed
 * for displaying main content.
 */
const HomePage = () => {
    return (
        // Base container for the HomePage
        <div className="body">
            <div className="main-content">
                <div className="center-content">
                </div>
            </div>
        </div>
    );
};

export default HomePage; // Export the HomePage component for use in other parts of the app
