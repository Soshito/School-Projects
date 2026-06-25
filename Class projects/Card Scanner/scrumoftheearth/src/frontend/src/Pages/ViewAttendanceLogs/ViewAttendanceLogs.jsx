/*
Name: Dylan Brodie
Project: Attendance App - This is a full stack attendance tracking and managament software
Purpose: Page to view attendance logs in our database per student per section
*/

import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Cookies from 'js-cookie';
import './ViewAttendanceLogs.css';
import '../../Components/Styles/GruvboxTheme.css';

const ViewAttendanceLogs = () => {
    // State to store various pieces of data necessary for the component
    const [attendanceLogs, setAttendanceLogs] = useState([]);
    const [studentId, setStudentId] = useState('');
    const [sectionId, setSectionId] = useState('');
    const [students, setStudents] = useState([]);
    const [sections, setSections] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const jwtToken = useFetchJWT();  // Custom hook to fetch JWT from cookies
    const [user, setUser] = useState({});

    // Custom hook to fetch and set JWT token
    function useFetchJWT() {
        const [jwtToken, setJwtToken] = useState('');
        useEffect(() => {
            const jwt = Cookies.get('jwt_authorization');
            setJwtToken(jwt);
        }, []);
        return jwtToken;
    }

    // Effect to fetch students if JWT token is available
    useEffect(() => {
        if (jwtToken) {
            fetchStudents();
        }
    }, [jwtToken]);

    // Effect to fetch sections relevant to a selected student
    useEffect(() => {
        if (studentId) {
            fetchSections(studentId);
        }
    }, [studentId]);

    // Effect to fetch attendance logs when section and student IDs are set
    useEffect(() => {
        if (sectionId && studentId) {
            fetchAttendanceLogs();
        }
    }, [sectionId, studentId]);

    useEffect(() => {
        const fetchUserData = async () => {
            if (studentId) {
                try {
                    const response = await fetch(`http://localhost:8080/users/${studentId}`, {
                        method: "GET",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${jwtToken}`
                        }
                    });
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    const userData = await response.json();
                    setUser(userData || {}); // Set user data
                } catch (error) {
                    console.error('Failed to fetch user data:', error);
                }
            }
        };
    
        fetchUserData();
    }, [studentId, jwtToken]);

    // Async function to fetch attendance logs from the API
    const fetchAttendanceLogs = async () => {
        try {
            const response = await fetch(`http://localhost:8080/attendancelogs/by-studentId-and-sectionId?studentId=${studentId}&sectionId=${sectionId}`, {
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
            setAttendanceLogs(data || []);
            setTotalPages(data.content.length / pageSize);
        } catch (error) {
            console.error('Failed to fetch attendance logs:', error);
        }
    };

    // Async function to fetch students
    const fetchStudents = async () => {
        try {
            const response = await fetch(`http://localhost:8080/users/by-roleId?roleId=3`, {
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
            setStudents(data || []);
        } catch (error) {
            console.error('Failed to fetch students:', error);
        }
    };

    // Async function to fetch sections based on the student ID
    const fetchSections = async (studentId) => {
        if (!studentId) return;
    
        const url = new URL('http://localhost:8080/sections/by-studentId');
        url.searchParams.append('studentId', studentId);
    
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

    // Improved handlers for pagination
    function handlePreviousButton() {
        if (page > 0) {
            const currPage = page;
            setPage(currPage - 1);
        }
    }

    function handleNextButton() {
        if (page < totalPages + 1) {
            const currPage = page;
            setPage(currPage + 1);
        }
    }

    function convertDateTime(dateTimeString) {
        // Create a new Date object using the dateTime string
        const date = new Date(dateTimeString);

        // Get the month and ensure it is in MM format
        const month = ('0' + (date.getMonth() + 1)).slice(-2); // Months are zero-indexed, add one

        // Get the day and ensure it is in DD format
        const day = ('0' + date.getDate()).slice(-2);

        // Get the year and convert it to YY format
        const year = date.getFullYear().toString().slice(-2);

        // Get the hours and ensure it is in HH format
        const hours = ('0' + date.getHours()).slice(-2);

        // Get the minutes and ensure it is in MM format
        const minutes = ('0' + date.getMinutes()).slice(-2);

        // Construct the formatted date string in "MM/DD/YY - HH/MM/SS" format
        return `${month}/${day}/${year} - ${hours}:${minutes}`;
    }

    // Function to render the attendance log table
    function createTable(attendanceLogs) {
      return (
        <div>
            <h1>View Attendance Logs</h1>
            <div className='input-box'>
                <h2>Select Student</h2>
                <select value={studentId} onChange={(e) => setStudentId(e.target.value)} required className="form-select">
                    <option value="">Select a Student</option>
                    {students.map((student) => (
                        <option key={student.id} value={student.id}>{student.firstName} {student.lastName}</option>
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
            <table className="user-table">
                <thead>
                    <tr>
                        <th>Student</th>
                        <th>Section ID</th>
                        <th>Date-time</th>
                        <th>Absent</th>
                    </tr>
                </thead>
                <tbody>
                    {attendanceLogs.map(log => (
                        <tr key={log.id}>
                            <td>{user.firstName} {user.lastName}</td>
                            <td>{log.sectionId}</td>
                            <td>{convertDateTime(log.dateTime)}</td>
                            <td>{log.absent.toString()}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
      )
    }

    // Main component render
    return (
        <div className='wrapper'>
            {createTable(attendanceLogs)}
            <div className='button-container' style={{ marginTop: '20px', marginBottom: '20px' }}>
                <button onClick={handlePreviousButton}>Previous</button>
                <button onClick={handleNextButton}>Next</button>
            </div>
            <button>
                <Link to="/layout/viewAbsences">View Absences Only</Link>
            </button>
        </div>
    );
};

export default ViewAttendanceLogs;