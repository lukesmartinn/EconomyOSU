import React, { useState, useMemo, useEffect } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ThemeProvider, CssBaseline } from '@mui/material';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { getTheme } from './theme';
import Layout from './components/Layout';
import PartnerList from './components/PartnerList';
import PartnerForm from './components/PartnerForm';
import PartnerDetail from './components/PartnerDetail';
import Dashboard from './components/Dashboard';

function App() {
  const [darkMode, setDarkMode] = useState(() => {
    const saved = localStorage.getItem('darkMode');
    return saved ? JSON.parse(saved) : false;
  });

  useEffect(() => {
    localStorage.setItem('darkMode', JSON.stringify(darkMode));
  }, [darkMode]);

  const theme = useMemo(() => getTheme(darkMode ? 'dark' : 'light'), [darkMode]);

  const handleToggleDarkMode = () => {
    setDarkMode(!darkMode);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <Layout darkMode={darkMode} onToggleDarkMode={handleToggleDarkMode}>
          <Routes>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/" element={<PartnerList />} />
            <Route path="/partner/new" element={<PartnerForm />} />
            <Route path="/partner/edit/:id" element={<PartnerForm />} />
            <Route path="/partner/:id" element={<PartnerDetail />} />
          </Routes>
        </Layout>
      </BrowserRouter>
      <ToastContainer
        position="bottom-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme={darkMode ? 'dark' : 'light'}
      />
    </ThemeProvider>
  );
}

export default App;
