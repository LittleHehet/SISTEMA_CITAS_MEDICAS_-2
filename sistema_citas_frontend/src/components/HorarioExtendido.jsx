import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import '../styles.css';

function HorarioExtendido() {
    const [medico, setMedico] = useState(null);
    const [semanaCompleta, setSemanaCompleta] = useState([]);
    const [error, setError] = useState('');

    const navigate = useNavigate();
    const location = useLocation();

    const queryParams = new URLSearchParams(location.search);
    const medicoId = queryParams.get('medicoId');

    useEffect(() => {
        const token = localStorage.getItem('token');
        const headers = token ? { Authorization: `Bearer ${token}` } : {};

        if (!medicoId) {
            setError('No se proporcionó ID de médico');
            return;
        }

        axios.get(`http://localhost:8080/api/HorarioExtendido?medicoId=${medicoId}`, {
            headers,
            withCredentials: true
        })
            .then(res => {
                setMedico(res.data.medico);
                setSemanaCompleta(res.data.semanaCompleta);
            })
            .catch(err => {
                setError('Error al cargar los horarios extendidos');
                console.error(err);
            });
    }, [medicoId]);

    if (error) return <div className="error">{error}</div>;
    if (!medico) return <div>Cargando información del médico...</div>;

    return (
        <div className="container">
            <div className="view-all-button">
                <button onClick={() => navigate('/BuscarCita')}>Regresar</button>
            </div>

            <div className="medico-info">
                <p><strong>Nombre del Médico:</strong> {medico.nombre} {medico.apellido}</p>
                <p><strong>Especialidad:</strong> {medico.especialidad}</p>
                <p><strong>Localidad:</strong> {medico.localidad}</p>
            </div>

            <div className="horarios">
                {semanaCompleta.map((dia, i) => (
                    <div key={i} className="dia-column">
                        <p>Día: {dia.nombre} - {new Date(dia.fecha).toLocaleDateString('es-ES')}</p>
                        {dia.horarios.map((horario, j) => (
                            <button key={j} className="button-busqueda">
                                Horario: {horario.horainicio} - {horario.horafin}
                            </button>
                        ))}
                    </div>
                ))}
            </div>
        </div>
    );
}

export default HorarioExtendido;