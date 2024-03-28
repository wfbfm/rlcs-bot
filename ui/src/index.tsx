import React from 'react';
import ReactDOM from 'react-dom/client';
import { ChakraProvider, extendTheme } from '@chakra-ui/react';
import './index.css';
import App from './App';

const customTheme = extendTheme({
  fonts: {
    body: "'Work Sans', sans-serif", // Use 'Work Sans' for body text
    heading: "'Work Sans', sans-serif", // Use 'Work Sans' for headings
  },
});

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  <React.StrictMode>
    <ChakraProvider theme={customTheme}>
      <App />
    </ChakraProvider>
  </React.StrictMode>
);