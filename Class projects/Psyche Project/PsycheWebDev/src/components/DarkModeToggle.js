import React from 'react';
import '../DarkModeToggle.css';

const DarkModeToggle = ({ darkMode, onToggle }) => {
  return (
    <label className="switch">
      <input type="checkbox" checked={darkMode} onChange={onToggle} />
      <span className="slider round"></span>
    </label>
  );
};

export default DarkModeToggle;
