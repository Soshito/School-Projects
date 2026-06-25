import React from 'react';
import { Navbar, Nav, Container } from 'react-bootstrap';

const CustomNavbar = ({ onHomeClick }) => {
  return (
    <Navbar expand="lg" fixed="top" className='transparent-navbar'>
      <Container>
        <Navbar.Brand href="#home">MISSION TO PSYCHE</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link href="#home" onClick={onHomeClick}>Home</Nav.Link>
            <Nav.Link href="https://psyche.asu.edu" target="_blank" rel="noopener noreferrer">Psyche Mission Website</Nav.Link>
            <Nav.Link href="#learnmore">Learn More</Nav.Link>
            <Nav.Link href="#videos">Videos</Nav.Link>
            <Nav.Link href="#credits">Credits</Nav.Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default CustomNavbar;
