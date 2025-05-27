import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles.css';

function ErrorPage() {
    const navigate = useNavigate();
    const [perfil, setPerfil] = useState(null);

    useEffect(() => {
        const storedPerfil = localStorage.getItem('perfil');
        setPerfil(storedPerfil);
    }, []);

    const handleGoBack = () => {
            switch (perfil) {
                case 'ROLE_PACIENTE':
                    navigate('/BuscarCita');
                    break;
                case 'ROLE_MEDICO':
                    navigate('/GestionCitas');
                    break;
                case 'ROLE_ADMINISTRADOR':
                    navigate('/ApproveDoctors');
                    break;
                default:
                    navigate('/Login');
            }
    };

    return (
        <div className="container">
            <h1 style={{ color: '#e74c3c' }}>⚠️ Acceso Denegado</h1>
            <p>No tenés permisos para acceder a esta página. 😢</p>
            <p>Perfil actual: <strong>{perfil ? perfil : 'No autenticado'}</strong></p>
            <button className="submit-button" onClick={handleGoBack}>
                Volver
            </button>
        </div>
    );
}

export default ErrorPage;
