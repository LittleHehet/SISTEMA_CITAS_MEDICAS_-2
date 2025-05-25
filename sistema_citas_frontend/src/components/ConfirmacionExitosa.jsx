import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles.css';

function ConfirmacionExitosa() {
    const navigate = useNavigate();

    return (
        <div className="containerConfirmarCita">
            <h1>Cita Reservada Exitosamente!!</h1>
            <button onClick={() => navigate('/BuscarCita')} className="btn btn-return">
                Regresar
            </button>
        </div>
    );
}

export default ConfirmacionExitosa;