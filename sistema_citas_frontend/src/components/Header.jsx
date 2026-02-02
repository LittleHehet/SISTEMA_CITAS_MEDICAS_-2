import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import logo from '../assets/doctor-patient.png';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function Header({ perfil, perfilCompleto, onLogout }) {

    const navigate = useNavigate();

    const handleLogoutClick = () => {
        // Limpiar sesión
        localStorage.removeItem('token');
        localStorage.removeItem('perfil');
        localStorage.removeItem('perfilCompleto');

        onLogout(); // notifica al componente App para actualizar el estado

        // Redirigir al login
        setTimeout(() => {
            navigate('/Login', {
                replace: true,
                state: { mensaje: 'Sesión cerrada correctamente.' },
            });
        }, 0); // pequeño retraso

    };


    return (
        <header className="header">
            <div className="header-logo">
                <div>
                    <img className="logo" src={logo} alt="Medical Appointments Logo" />
                </div>
                <p>Citas Medicas</p>
            </div>
            <div className="phone-number">
                <i className='bx bxs-phone-call'></i>
                <p>+506 5467 0937</p>
            </div>

            <nav className="navbar">
                <Link to="/About">Acerca de</Link>
                {perfil === 'ROLE_PACIENTE' && (
                    <div className="perfil-wrapper">
                        <Link to="/BuscarCita" className="perfil-link">Búsqueda</Link>
                        <Link to="/historicoPaciente" className="perfil-link">Historial</Link>
                        <span className="user-badge">👤 PACIENTE</span>
                        <button className="logout-button" onClick={handleLogoutClick}>Salir</button>
                    </div>
                )}
                {perfil === 'ROLE_MEDICO' && (
                    <>
                        {perfilCompleto && (
                            <Link to="/GestionCitas">Citas</Link>
                        )}
                        <div className="perfil-wrapper">
                            <Link to="/Medico-Perfil" className="perfil-link">Perfil</Link>
                            <span className="user-badge">🩺 MÉDICO</span>
                            <button className="logout-button" onClick={handleLogoutClick}>Salir</button>
                        </div>
                    </>
                )}




                {perfil === 'ROLE_ADMINISTRADOR' && (
                    <>
                        <Link to="/ApproveDoctors">Administrar</Link>
                        <div className="perfil-wrapper">
                            <span className="user-badge">🛠️ ADMIN</span>
                            <button className="logout-button" onClick={handleLogoutClick}>Salir</button>
                        </div>
                    </>
                )}

                {!perfil && (
                    <>
                        <Link to="/BuscarCita">Busqueda</Link>
                        <Link to="/Login">Iniciar sesión</Link>
                    </>
                )}
            </nav>
        </header>
    );
}

export default Header;
