/*
Name: Dylan Brodie
Project: Attendance App - This is a full stack attendance tracking and managament software
Purpose: Page to view users in our database
*/
import React, { useState, useEffect } from 'react';
import Cookies from 'js-cookie';
import './ViewUsersPage.css';
import '../../Components/Styles/GruvboxTheme.css';

function useFetchJWT() {
    const [jwtToken, setJwtToken] = useState('');
    useEffect(() => {
        const jwt = Cookies.get('jwt_authorization');
        setJwtToken(jwt);
    }, []);
    return jwtToken;
}

const ViewUsersPage = () => {
    const [users, setUsers] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const jwtToken = useFetchJWT();

    useEffect(() => {
        if (jwtToken) {
            fetchUsers();
        }
    }, [page, pageSize, jwtToken]);

    const fetchUsers = () => {
        const url = new URL('http://localhost:8080/users');
        url.searchParams.append('page', page);
        url.searchParams.append('size', pageSize);
        url.searchParams.append('sort', `id,asc`);

        fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${jwtToken}`,
            },
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                  setUsers(data || []);
                  setTotalPages(data.content.length / pageSize);
            })
            .catch(error => {
                console.error('Error fetching users:', error);
            });
    };

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

    function createTable(Users) {     
      return (
        <table className="user-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Student ID Card</th>
                        <th>Username</th>
                        <th>Role</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map(user => (
                        <tr key={user.id}>
                            <td>{user.id}</td>
                            <td>{user.firstName}</td>
                            <td>{user.lastName}</td>
                            <td>{user.studentCardId}</td>
                            <td>{user.username}</td>
                            <td>{getRole(user.roleId)}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        )
    }

    function getRole(roleId) {
        switch (roleId) {
            case 1: return "Administrator";
            case 2: return "Professor";
            case 3: return "Student";
            default: return "Unknown";
        }
    }
    
    return (
        <div className='wrapper'>
            {createTable(users)}
            <div className='button-container' style={{ marginTop: '20px', marginBottom: '20px' }}>
                <button onClick={handlePreviousButton}>Previous</button>
                <button onClick={handleNextButton}>Next</button>
            </div>
        </div>
    );
};

export default ViewUsersPage;