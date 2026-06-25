/*
Name: Dylan Brodie
Project: Attendance App - This is a full stack attendance tracking and managament software
Purpose: This component is for our buttons to be kept on the side of the screen
        no matter what page you are in
*/

import React from 'react';
import { Link } from 'react-router-dom';
import Cookies from 'js-cookie';
import './SideBar.css';
import '../../Components/Styles/GruvboxTheme.css';

const Sidebar = () => {
    function handleClickSignOut() {
        const allCookies = Cookies.get(); // Retrieve all cookies as an object
        Object.keys(allCookies).forEach(cookieName => {
            Cookies.remove(cookieName); // Remove each cookie by name
        });
        window.location.href = "/";
    }

    return (
        <div className="sidebar" style={{ backgroundColor: '#282828', width: '172px' }}>
            <div className="vbox">
                <div className="tooltip">
                    <Link to="createUser" className="button" id="createUser"></Link>
                    <span className="tooltiptext">Create User</span>
                </div>
                <div className="tooltip">
                    <Link to="createCourse" className="button" id="createCourse"></Link>
                    <span className="tooltiptext">Create Course</span>
                </div>
                {/* <div className="tooltip">
                    <Link to="viewCourses" className="button" id="viewCourses"></Link>
                    <span className="tooltiptext">View Courses</span>
                </div> */}
                <div className="tooltip">
                    <Link to="createSection" className="button" id="createSection"></Link>
                    <span className="tooltiptext">Create Section</span>
                </div>
                {/* <div className="tooltip">
                    <Link to="viewSections" className="button" id="viewSections"></Link>
                    <span className="tooltiptext">View Sections</span>
                </div> */}
                <div className="tooltip">
                    <Link to="createMeetingTime" className="button" id="createMeetingTime"></Link>
                    <span className="tooltiptext">Create Meeting Time</span>
                </div>
                <div className="tooltip">
                    <Link to="createEnrollment" className="button" id="createEnrollment"></Link>
                    <span className="tooltiptext">Create Enrollment</span>
                </div>
                <div className="tooltip">
                    <Link to="viewUsers" className="button" id="viewUsers"></Link>
                    <span className="tooltiptext">View Users</span>
                </div>
                <div className="tooltip">
                    <Link to="viewAttendanceLogs" className="button" id="viewAttendanceLogs"></Link>
                    <span className="tooltiptext">View Attendance Logs</span>
                </div>
                <div className="sidbar-bottom">
                    <button className="signoutButton" onClick={handleClickSignOut}>Sign Out</button>
                </div>
            </div>
        </div>
    );
};

export default Sidebar;
