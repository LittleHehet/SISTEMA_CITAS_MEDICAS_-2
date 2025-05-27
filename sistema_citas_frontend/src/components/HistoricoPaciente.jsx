import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const HistoricoPaciente = () => {
    const navigate = useNavigate();
    const [usuario, setUsuario] = useState({});
    const [citas, setCitas] = useState([]);
    const [medicos, setMedicos] = useState([]);
    const [filtros, setFiltros] = useState({ estado: 'all', medicoId: 0 });
    const [error, setError] = useState(null);

    // Función para obtener el token de localStorage y crear headers con Authorization
    const getAuthHeaders = () => {
        const token = localStorage.getItem('token');
        if (!token) {
            setError('No hay token disponible');
            return null;
        }
        return {
            headers: {
                Authorization: `Bearer ${token}`
            }
        };
    };

    useEffect(() => {
        obtenerPerfil();
        obtenerMedicos();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        if (usuario.id) {
            obtenerCitas();
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [usuario]);

    const obtenerPerfil = async () => {
        setError(null);
        const config = getAuthHeaders();
        if (!config) return;

        try {
            const response = await axios.get('http://localhost:8080/api/historicoPaciente/perfil', config);
            setUsuario(response.data);
        } catch (error) {
            console.error("Error al obtener el perfil:", error);
            setError('Error al obtener el perfil. Intente iniciar sesión nuevamente.');
        }
    };

    const obtenerCitas = async () => {
        setError(null);
        const config = getAuthHeaders();
        if (!config) return;

        try {
            const params = {};
            if (filtros.medicoId && filtros.medicoId !== 0) {
                params.medicoId = filtros.medicoId;
            }
            if (filtros.estado !== 'all') {
                params.estado = filtros.estado;
            }

            const response = await axios.get('http://localhost:8080/api/historicoPaciente/historico', {
                ...config,
                params: params
            });

            setCitas(response.data.citas);
            //setMedicos(response.data.medicos);
        } catch (error) {
            console.error("Error al obtener las citas:", error);
            setError('Error al obtener las citas. Intente nuevamente.');
        }
    };

    const handleFiltroChange = (e) => {
        setFiltros({
            ...filtros,
            [e.target.name]: e.target.value
        });
    };

    const handleBuscar = (e) => {
        e.preventDefault();
        obtenerCitas();
    };

    const obtenerMedicos = async () => {
        const config = getAuthHeaders();
        if (!config) return;

        try {
            const response = await axios.get('http://localhost:8080/api/historicoPaciente/medicosDelPaciente', config);
            setMedicos(response.data);
        } catch (error) {
            console.error("Error al obtener médicos:", error);
        }
    };


    return (
        <div className="historic">
            <h1>Paciente - {usuario.nombre || usuario.nombreUsuario || usuario.firstName} - Historial de Citas</h1>

            {error && <div style={{ color: 'red', marginBottom: '1rem' }}>{error}</div>}

            <div className="filter">
                <form onSubmit={handleBuscar}>
                    <label htmlFor="filter-status">Estado:</label>
                    <select id="filter-status" name="estado" value={filtros.estado} onChange={handleFiltroChange}>
                        <option value="all">Todos</option>
                        <option value="pendiente">Pendiente</option>
                        <option value="completada">Atendida</option>
                    </select>

                    <label htmlFor="filter-doctor">Doctor:</label>
                    <select id="filter-doctor" name="medicoId" value={filtros.medicoId} onChange={handleFiltroChange}>
                        <option value={0}>Todos</option>
                        {medicos.map((medico) => (
                            <option key={medico.id} value={medico.id}>
                                {medico.nombre} {medico.apellido}
                            </option>
                        ))}
                    </select>

                    <button type="submit" className="search-btn">Buscar</button>
                </form>
            </div>

            <table className="table">
                <thead>
                <tr className="table-header">
                    <th></th>
                    <th>Doctor</th>
                    <th>Fecha</th>
                    <th>Hora de inicio</th>
                    <th>Hora de fin</th>
                    <th>Estado</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                {citas.map((cita) => (
                    <tr key={cita.id}>
                        <td>
                            <img
                                src={`http://localhost:8080/api/medico/foto?id=${cita.medico.id}`}
                                alt="Foto del médico"
                                width="50"
                                height="50"
                                style={{ borderRadius: '50%', objectFit: 'cover' }}
                                onError={(e) => {
                                    e.target.onerror = null;
                                    e.target.src = 'https://via.placeholder.com/50x50?text=No+Foto';
                                }}
                            />
                        </td>
                        <td>{cita.medico.nombre} {cita.medico.apellido}</td>
                        <td>{new Date(cita.fechaHora).toLocaleDateString()}</td>
                        <td>{cita.horainicio}</td>
                        <td>{cita.horafinal}</td>
                        <td>{cita.estado}</td>
                        <td>
                            <button className="submit-button" onClick={() => navigate(`/verDetalleCita?id=${cita.id}`)}>Ver</button>

                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default HistoricoPaciente;
