import React from 'react';

const ARPage = ({ onBack }) => {
  return (
    <div style={styles.container}>
      <iframe
        title="AR Page"
        src="https://grandmaster619.github.io/"
        style={styles.iframe}
        allow="camera; microphone"
      />
      <button onClick={onBack} style={styles.backButton}>
        Back
      </button>
    </div>
  );
};

const styles = {
  container: {
    position: 'fixed',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    zIndex: 1,
    overflow: 'hidden',
  },
  iframe: {
    width: '100%',
    height: '100%',
    border: 'none',
  },
  backButton: {
    position: 'fixed',
    bottom: '20px',
    left: '50%',
    transform: 'translateX(-50%)',
    padding: '10px 25px',
    fontSize: '18px',
    backgroundColor: '#dc3545',
    color: '#fff',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer',
    boxShadow: '0 2px 4px rgba(220, 53, 69, 0.3)',
    zIndex: 2,
  },
};

export default ARPage;
