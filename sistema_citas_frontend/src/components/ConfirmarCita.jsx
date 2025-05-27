import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import '../styles.css';

function ConfirmarCita() {
    const [medico, setMedico] = useState(null);
    const [dia, setDia] = useState('');
    const [fecha, setFecha] = useState('');
    const [horaInicio, setHoraInicio] = useState('');
    const [horaFin, setHoraFin] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const medicoId = params.get('medicoId');
        const diaParam = params.get('dia');
        const fechaParam = params.get('fecha');
        const horaInicioParam = params.get('horaInicio');
        const horaFinParam = params.get('horaFin');

        setDia(diaParam);
        setFecha(fechaParam);
        setHoraInicio(horaInicioParam);
        setHoraFin(horaFinParam);

        const token = localStorage.getItem('token');
        const headers = token ? { Authorization: `Bearer ${token}` } : {};

        axios.get(`http://localhost:8080/api/ConfirmarCita`, {
            params: { medicoId, dia: diaParam, fecha: fechaParam, horaInicio: horaInicioParam, horaFin: horaFinParam },
            headers,
            withCredentials: true
        })
            .then(res => {
                setMedico(res.data.medico);
            })
            .catch(err => {
                setError('No se pudo cargar la información del médico.');
                console.error(err);
            });
    }, [location.search]);

    const handleConfirmar = async (e) => {
        e.preventDefault();

        const token = localStorage.getItem('token');
        const headers = token ? { Authorization: `Bearer ${token}` } : {};

        axios.post(`http://localhost:8080/api/ConfirmarCita`, null, {
            params: {
                medicoId: medico.id,
                dia,
                fecha,
                horaInicio,
                horaFin
            },
            headers,
            withCredentials: true
        })
            .then(res => {
                navigate('/ConfirmacionExitosa');
            })
            .catch(err => {
                if (err.response) {
                    if (err.response.status === 409) {
                        navigate('/ConfirmacionFallida');
                    } else if (err.response.status === 401) {
                        navigate('/Login');
                    } else {
                        setError('Ocurrió un error al confirmar la cita.');
                    }
                } else {
                    setError('Ocurrió un error al confirmar la cita.');
                }
            });
    };

    if (!medico) return <div>Cargando información...</div>;

    return (
        <div className="containerConfirmarCita">
            <h1 className="titleConfirmarCita">Información del Médico Seleccionado</h1>

            <div className="form-groupConfirmarCita">
                <img
                    src={`http://localhost:8080/api/medico/foto?id=${medico.id}`}
                    alt="Foto del médico"
                    width="70"
                    height="70"
                    style={{borderRadius: '50%', objectFit: 'cover', marginBottom: '10px'}}
                    onError={(e) => {
                        e.target.onerror = null;
                        e.target.replaceWith(document.createTextNode("No hay foto"));
                    }}
                />

                <label><p>Nombre Completo:</p></label>
                <p>{medico.usuarios.nombre} {medico.usuarios.apellido}</p>
            </div>

            <div className="form-groupConfirmarCita">
                <label>Especialidad:</label>
                <p>{medico.especialidad?.especialidadNombre}</p>
            </div>

            <div className="form-groupConfirmarCita">
                <label>Localidad:</label>
                <p>{medico.localidad?.localidadNombre}</p>
            </div>

            <div className="form-groupConfirmarCita">
                <label>Horario Seleccionado:</label>
                <p>{dia} - {horaInicio} a {horaFin}</p>
            </div>

            {error && (
                <div className="alert alert-danger" style={{ color: 'red', fontWeight: 'bold' }}>
                    {error}
                </div>
            )}

            <form onSubmit={handleConfirmar}>
                <button type="submit" className="submit-buttonConfirmarCita">Confirmar</button>
            </form>

            <button onClick={() => navigate('/BuscarCita')} className="btn btn-return">
                Regresar
            </button>
        </div>
    );
}

export default ConfirmarCita;