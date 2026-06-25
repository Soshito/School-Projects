/*
Name: Dylan Brodie
Project: Attendance App - This is a full stack attendance tracking and managament software
Purpose: Frontend page container for the homepage and all page updates
*/

// Import React for JSX support and specific components and styles
import React from 'react';
import '../Layout/Layout.css'; // CSS for styling the layout
import { Outlet } from 'react-router-dom'; // Outlet component from react-router for nested routing
import SideBar from '../SideBar/SideBar'; // Import the Sidebar component

/**
 * The Layout component defines the overall page structure for the application,
 * incorporating a sidebar for navigation and an outlet for rendering nested routes.
 * This setup is typical for a dashboard or an application with multiple views handled by routing.
 */
const Layout = () => {
  return (
    // Base container for the layout
    <div className="body">
      <SideBar className="sidebar"></SideBar>
      <div className="main-content">
        <Outlet />
      </div>
    </div>
  );
};

export default Layout; // Export the Layout component for use in the app
