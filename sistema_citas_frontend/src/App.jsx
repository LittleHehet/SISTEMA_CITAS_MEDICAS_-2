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
import ProtectedRoute from './components/ProtectedRoute';
import VerDetalleCita from "./components/VerDetalleCita.jsx";


function App() {
    const [perfil, setPerfil] =  useState(localStorage.getItem('perfil'));
    const [perfilCompleto, setPerfilCompleto] = useState(() => localStorage.getItem('perfilCompleto') === 'true');


    useEffect(() => {
        const storedPerfil = localStorage.getItem('perfil');
        const storedPerfilCompleto = localStorage.getItem('perfilCompleto');
        if (storedPerfil) {
            setPerfil(storedPerfil);
        }
        setPerfilCompleto(storedPerfilCompleto === 'true');
    }, []);


    const handleLoginSuccess = (perfil, perfilCompletoBackend) => {
        setPerfil(perfil);
        setPerfilCompleto(perfilCompletoBackend === true);
        localStorage.setItem('perfil', perfil);
        localStorage.setItem('perfilCompleto', perfilCompletoBackend === true ? 'true' : 'false');
    };


    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('perfil');
        localStorage.removeItem('perfilCompleto');

        setPerfil(null);
    };

    return (
        <Router>
            <Head />
            <Header perfil={perfil} perfilCompleto={perfilCompleto} onLogout={handleLogout} />
            <main className="main-content">
                <Routes>
                    {/* Rutas comunes */}
                    <Route path="/" element={<Navigate to="/BuscarCita" />} />
                    <Route path="/About" element={<About />} />
                    <Route path="/Sign-up" element={<SignUp />} />
                    <Route path="/Login" element={<Login onLoginSuccess={handleLoginSuccess} />} />
                    <Route path="/BuscarCita" element={<BuscarCita />} />
                    <Route path="/HorarioExtendido" element={<HorarioExtendido />} />
                    <Route path="/VerDetalleCita" element={<VerDetalleCita />} />
                    <Route path="/ErrorPage" element={<ErrorPage />} />


                    {/* Rutas protegidas para PACIENTES */}
                    <Route
                        path="/ConfirmacionExitosa"
                        element={
                            <ProtectedRoute perfil={perfil} allowedRoles={['ROLE_PACIENTE']}>
                                <ConfirmacionExitosa />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/ConfirmacionFallida"
                        element={
                            <ProtectedRoute perfil={perfil} allowedRoles={['ROLE_PACIENTE']}>
                                <ConfirmacionFallida />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/ConfirmarCita"
                        element={
                            <ProtectedRoute perfil={perfil} allowedRoles={['ROLE_PACIENTE', null]}>
                                <ConfirmarCita />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/HistoricoPaciente"
                        element={
                            <ProtectedRoute perfil={perfil} allowedRoles={['ROLE_PACIENTE']}>
                                <HistoricoPaciente />
                            </ProtectedRoute>
                        }
                    />

                    {/* Rutas protegidas para MÉDICOS */}
                    <Route
                        path="/GestionCitas"
                        element={
                            <ProtectedRoute perfil={perfil} allowedRoles={['ROLE_MEDICO']}>
                                <GestionCitas />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/Medico-Perfil"
                        element={
                            <ProtectedRoute perfil={perfil} allowedRoles={['ROLE_MEDICO']}>
                                <MedicoPerfil
                                    onPerfilCompletoChange={(nuevoValor) => {
                                        setPerfilCompleto(nuevoValor);
                                        localStorage.setItem('perfilCompleto', nuevoValor);
                                    }}
                                />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/EditarNota"
                        element={
                            <ProtectedRoute perfil={perfil} allowedRoles={['ROLE_MEDICO']}>
                                <EditarNota />
                            </ProtectedRoute>
                        }
                    />

                    {/* Rutas protegidas para ADMINISTRADORES */}
                    <Route
                        path="/ApproveDoctors"
                        element={
                            <ProtectedRoute perfil={perfil} allowedRoles={['ROLE_ADMINISTRADOR']}>
                                <ApproveDoctors />
                            </ProtectedRoute>
                        }
                    />

                    {/* Ruta no encontrada */}
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
