
import React from 'react';

const LoadingSpinner: React.FC = () => {
  return (
    <div
      className="h-12 w-12 animate-spin rounded-full border-4 border-solid border-indigo-500 border-r-transparent"
      role="status"
    >
      <span className="sr-only">Loading...</span>
    </div>
  );
};

export default LoadingSpinner;
