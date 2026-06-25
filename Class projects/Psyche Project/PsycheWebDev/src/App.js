import React, { useState, useEffect } from 'react';
import ARPage from './components/ARPage';
import CustomNavbar from './components/Navbar';
import './App.css';
import psycheVideo from './assets/psyche.mp4'

const App = () => {
  const [selectedMode, setSelectedMode] = useState(null);

  const handleStartAR = () => {
    setSelectedMode('AR');
  };

  const handleBack = () => {
    setSelectedMode(null);
  };

  const handleHomeClick = () => {
    setSelectedMode(null);
  };

  useEffect(() => {
    document.body.classList.add('dark-mode');
  }, []);

  return (
    <div className={`App ${selectedMode === 'AR' ? 'ar-mode' : ''}`}>
      <CustomNavbar onHomeClick={handleHomeClick} />
      {selectedMode !== 'AR' && (
        <div className="start-container">
          {/* Video Background */}
          <video autoPlay muted loop playsInline className="background-video">
            <source src={psycheVideo} type="video/mp4" />
            <source src="/psyche.mp4" type="video/webm" />
            Your browser does not support the video tag.
          </video>
          <button className="start-button" onClick={handleStartAR}>
            START AR
          </button>
        </div>
      )}
      {selectedMode === 'AR' && (
        <ARPage onBack={handleBack} />
      )}
    </div>
  );
};

export default App;
