import React from 'react';
import { Link } from 'react-router-dom';

function Header({ perfil, onLogout }) {
    return (
        <header className="header">
            <div className="header-logo">
                <div><img className="logo" src="/images/doctor-patient.png" alt="Medical Appointments Logo" /></div>
                <p>Citas Medicas</p>
            </div>

            <div className="phone-number">
                <i className='bx bxs-phone-call'></i>
                <p>+506 5467 0937</p>
            </div>

            <nav className="navbar">
                <Link to="/About">Acerca de</Link>
                {perfil === 'PACIENTE' && (
                    <>
                        <Link to="/BuscarCita">Búsqueda</Link>
                        <Link to="/historicoPaciente">Historial</Link>
                        <button onClick={onLogout}>Salir</button>
                        <span className="user-badge">👤 Paciente</span>
                    </>
                )}
                {perfil === 'MEDICO' && (
                    <>
                        <Link to="/GestionCitas">Citas</Link>
                        <Link to="/Medico-Perfil">Perfil</Link>
                        <button onClick={onLogout}>Salir</button>
                        <span className="user-badge">🩺 Médico</span>
                    </>
                )}
                {perfil === 'ADMINISTRADOR' && (
                    <>
                        <Link to="/Approve">Administrar</Link>
                        <button onClick={onLogout}>Salir</button>
                        <span className="user-badge">🛠️ Admin</span>
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
