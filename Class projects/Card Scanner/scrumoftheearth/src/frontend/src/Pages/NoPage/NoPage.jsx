//Name: Sam Miller
//Project: Attendance App - This is a full stack attendance tracking and managament software
//Purpose: Frontend page for handling path errors

// Import the CSS for styling the error page
import './NoPage.css';

//NoPage component displays a 404 Not Found error message.
export default function NoPage() {
    return (
        // Container for the error page
        <div className='NoPage'>
            <div className='bg'> {/* This div could be used for background styling */}
                <h2>Error 404: Not Found</h2> {/* Display the error message */}
            </div>
        </div>
    );
}
