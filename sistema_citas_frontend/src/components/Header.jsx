import React from 'react';
import { Link } from 'react-router-dom';
import logo from '../assets/doctor-patient.png';

function Header({ perfil, onLogout }) {
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
                        <button className="logout-button" onClick={onLogout}>Salir</button>
                    </div>
                )}
                {perfil === 'ROLE_MEDICO' && (
                    <>
                        <Link to="/GestionCitas">Citas</Link>
                        <div className="perfil-wrapper">
                            <Link to="/Medico-Perfil" className="perfil-link">Perfil</Link>
                            <span className="user-badge">🩺 MÉDICO</span>
                            <button className="logout-button" onClick={onLogout}>Salir</button>
                        </div>

                    </>
                )}

                {perfil === 'ROLE_ADMINISTRADOR' && (
                    <>
                        <Link to="/ApproveDoctors">Administrar</Link>
                        <div className="perfil-wrapper">
                            <span className="user-badge">🛠️ ADMIN</span>
                            <button className="logout-button" onClick={onLogout}>Salir</button>
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
