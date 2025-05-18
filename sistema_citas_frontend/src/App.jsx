import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Head from './components/Head';
import Footer from './components/Footer';
import Login from './components/Login';
import Header from './components/Header';
import About from './components/About';

function App() {
    const [perfil, setPerfil] = useState(null);

    const handleLoginSuccess = (perfil) => {
        setPerfil(perfil);
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
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
                    {/* Otras rutas aquí */}
                </Routes>
            </main>
            <Footer />
        </Router>
    );
}

export default App;

// npm install
// npm run dev
