import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import App from './App.jsx'
import './index.css'
import { AuthProvider } from './contexts/AuthContext';
import axios from 'axios';

const saved = localStorage.getItem('userToken');
if (saved) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${saved}`;
  console.debug('Applied saved token to axios.defaults');
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <BrowserRouter>
    <AuthProvider>
      <App />
    </AuthProvider>
  </BrowserRouter>
);
