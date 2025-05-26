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
import HorarioExtendido from './components/HorarioExtendido';
import ConfirmarCita from './components/ConfirmarCita';
import ConfirmacionExitosa from './components/ConfirmacionExitosa';
import ConfirmacionFallida from './components/ConfirmacionFallida';
import ErrorPage from './components/ErrorPage';



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
                    {/* Rutas comunes */}
                    <Route path="/About" element={<About />} />
                    <Route path="/Sign-up" element={<SignUp />} />
                    <Route path="/Login" element={<Login onLoginSuccess={handleLoginSuccess} />} />
                    <Route path="/BuscarCita" element={<BuscarCita />} />
                    <Route path="/HorarioExtendido" element={<HorarioExtendido />} />
                    <Route path="/ErrorPage" element={<ErrorPage />} />
                    {/* Rutas protegidas para pacientes */}
                    <Route
                        path="/ConfirmacionExitosa"
                        element={perfil === 'ROLE_PACIENTE' ? <ConfirmacionExitosa /> : <Navigate to="/ErrorPage" />}
                    />
                    <Route
                        path="/ConfirmacionFallida"
                        element={perfil === 'ROLE_PACIENTE' ? <ConfirmacionFallida /> : <Navigate to="/ErrorPage" />}
                    />
                    <Route
                        path="/ConfirmarCita"
                        element={
                            (perfil === 'ROLE_PACIENTE' || !perfil) ? <ConfirmarCita /> : <Navigate to="/ErrorPage" />
                        }
                    />
                    <Route
                        path="/HistoricoPaciente"
                        element={perfil === 'ROLE_PACIENTE' ? <HistoricoPaciente /> : <Navigate to="/ErrorPage" />}
                    />

                    {/* Rutas protegidas para médicos */}
                    <Route
                        path="/GestionCitas"
                        element={perfil === 'ROLE_MEDICO' ? <GestionCitas /> : <Navigate to="/ErrorPage" />}
                    />
                    <Route
                        path="/Medico-Perfil"
                        element={perfil === 'ROLE_MEDICO' ? <MedicoPerfil /> : <Navigate to="/ErrorPage" />}
                    />
                    <Route
                        path="/EditarNota"
                        element={perfil === 'ROLE_MEDICO' ? <EditarNota /> : <Navigate to="/ErrorPage" />}
                    />

                    {/* Rutas protegidas para administradores */}
                    <Route
                        path="/ApproveDoctors"
                        element={perfil === 'ROLE_ADMINISTRADOR' ? <ApproveDoctors /> : <Navigate to="/ErrorPage" />}
                    />

                    {/* Página de error para rutas no definidas */}
                    <Route path="*" element={<Navigate to="/ErrorPage" />} />
                </Routes>

            </main>
            <Footer />
        </Router>

    );
}

export default App;

// npm install
// npm run dev
