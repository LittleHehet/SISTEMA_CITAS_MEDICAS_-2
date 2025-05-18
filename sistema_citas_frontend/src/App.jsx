import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Head from './components/Head';
import Footer from './components/Footer';
import Login from './components/Login';
import Header from './components/Header';
import About from './components/About';

function App() {
    const [perfil, setPerfil] = useState(null);

    useEffect(() => {
        const storedPerfil = localStorage.getItem('perfil');
        if (storedPerfil) {
            setPerfil(storedPerfil);
        }
    }, []);

    const handleLoginSuccess = (perfil) => {
        setPerfil(perfil);
        localStorage.setItem('perfil', perfil);
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('perfil');
        setPerfil(null);
    };

    return (
        <Router>
            <Head />
            <Header perfil={perfil} onLogout={handleLogout} />
            <main>
                <Routes>
                    <Route
                        path="/"
                        element={
                            !perfil ? (
                                <Login onLoginSuccess={handleLoginSuccess} />
                            ) : (
                                <div>Contenido principal para perfil: {perfil}</div>
                            )
                        }
                    />
                    <Route path="/About" element={<About />} />
                </Routes>
            </main>
            <Footer />
        </Router>
    );
}

export default App;

// npm install
// npm run dev
