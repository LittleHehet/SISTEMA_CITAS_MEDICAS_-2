import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { Navigate } from 'react-router-dom';
import Head from './components/Head';
import Footer from './components/Footer';
import Login from './components/Login';
import Header from './components/Header';
import About from './components/About';
import SignUp from './components/SignUp';
import MedicoPerfil from './components/MedicoPerfil';
import ApproveDoctors from './components/ApproveDoctors';
import HistoricoPaciente from './components/HistoricoPaciente';
import GestionCitas from './components/GestionCitas';
import EditarNota from './components/EditarNota';
import BuscarCita from './components/BuscarCita';




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
            <main className="main-content">
                <Routes>
                    {/*<Route path="/" element={!perfil ? <Login onLoginSuccess={handleLoginSuccess} /> : <div>Contenido principal para perfil: {perfil}</div>} />*/}
                    <Route path="/About" element={<About />} />
                    <Route path="/Sign-up" element={<SignUp />} />
                    <Route path="/Login" element={<Login onLoginSuccess={handleLoginSuccess} />} />
                    <Route path="*" element={<Navigate to="/Login" />} />
                    <Route
                        path="/Medico-Perfil"
                        element={perfil === 'ROLE_MEDICO' ? <MedicoPerfil /> : <Navigate to="/Login" />}
                    />
                    <Route
                        path="/GestionCitas"
                        element={perfil === 'ROLE_MEDICO' ? <GestionCitas /> : <Navigate to="/Login" />}
                    />
                    <Route
                        path="/EditarNota"
                        element={perfil === 'ROLE_MEDICO' ? <EditarNota /> : <Navigate to="/Login" />}
                    />
                    <Route
                        path="/ApproveDoctors"
                        element={perfil === 'ROLE_ADMINISTRADOR' ? <ApproveDoctors /> : <Navigate to="/Login" />}
                    />
                    <Route
                        path="/HistoricoPaciente"
                        element={perfil === 'ROLE_PACIENTE' ? <HistoricoPaciente /> : <Navigate to="/Login" />}></Route>
                    <Route path="/Medico-Perfil" element={<MedicoPerfil />} />
                    <Route path="/ApproveDoctors" element={<ApproveDoctors/>} />
                    <Route path="/BuscarCita" element={<BuscarCita />} />

                </Routes>
            </main>
            <Footer />
        </Router>

    );
}

export default App;

// npm install
// npm run dev
