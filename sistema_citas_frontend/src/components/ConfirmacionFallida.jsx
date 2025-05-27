import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles.css';

function ConfirmacionFallida() {
    const navigate = useNavigate();

    return (
        <div className="containerConfirmarCita">
            <h1>Ya existe una cita reservada en este horario y día</h1>
            <button onClick={() => navigate('/BuscarCita')} className="btn btn-return">
                Regresar
            </button>
        </div>
    );
}

export default ConfirmacionFallida;