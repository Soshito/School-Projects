import React, { useEffect, useRef } from 'react';

const ARCamera = () => {
  const videoRef = useRef(null);

  useEffect(() => {
    // Request access to the user's back camera
    const getCameraStream = async () => {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({
          video: {
            facingMode: { ideal: 'environment' }, // Request the back camera
          },
        });
        if (videoRef.current) {
          videoRef.current.srcObject = stream;
        }
      } catch (error) {
        console.error('Error accessing camera: ', error);
        alert('Unable to access camera. Please make sure camera permissions are granted.');
      }
    };

    getCameraStream();

    return () => {
      // Clean up by stopping the camera stream when the component is unmounted
      if (videoRef.current && videoRef.current.srcObject) {
        const tracks = videoRef.current.srcObject.getTracks();
        tracks.forEach((track) => track.stop());
      }
    };
  }, []);

  return (
    <div style={styles.container}>
      <video
        ref={videoRef}
        autoPlay
        playsInline
        style={styles.video}
      />
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
  video: {
    width: '100%',
    height: '100%',
    objectFit: 'cover',
  },
};

export default ARCamera;
