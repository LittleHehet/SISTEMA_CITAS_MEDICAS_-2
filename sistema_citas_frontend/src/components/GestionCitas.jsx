import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate, useSearchParams } from 'react-router-dom';
import '../styles.css';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function GestionCitas() {
    const [citas, setCitas] = useState([]);
    const [usuarios, setUsuarios] = useState([]);
    const [params] = useSearchParams();
    const estado = params.get('estado') || 'all';
    const usuarioId = params.get('usuarioId') || '0';
    const navigate = useNavigate();

    const fetchCitas = () => {
        const token = localStorage.getItem('token');
        axios.get(`${API_BASE_URL}/api/gestion/citas?estado=${estado}&usuarioId=${usuarioId}`, {
            headers: { Authorization: `Bearer ${token}` },
            withCredentials: true
        })
            .then(res => setCitas(res.data))
            .catch(err => console.error('Error cargando citas', err));
    };

    const fetchUsuarios = () => {
        const token = localStorage.getItem('token');
        axios.get(`${API_BASE_URL}/api/gestion/usuarios`, {
            headers: { Authorization: `Bearer ${token}` },
            withCredentials: true
        })
            .then(res => setUsuarios(res.data))
            .catch(err => console.error('Error cargando usuarios', err));
    };

    useEffect(() => {
        fetchCitas();
        fetchUsuarios();
    }, [estado, usuarioId]);

    const handleAccion = async (id, accion) => {
        const url = accion === 'completar'
            ? `${API_BASE_URL}/api/gestion/completar`
            : `${API_BASE_URL}/api/gestion/cancelar`;

        try {
            await axios.post(url, null, {
                params: { id },
                headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
                withCredentials: true
            });
            fetchCitas(); // Actualiza sin recargar la página
        } catch (err) {
            console.error(`Error al ${accion} la cita`, err);
        }
    };

    const handleFilterChange = (e) => {
        e.preventDefault();
        const estado = e.target.estado.value;
        const usuarioId = e.target.usuarioId.value;
        navigate(`/GestionCitas?estado=${estado}&usuarioId=${usuarioId}`);
    };

    return (
        <div className="Gestion">
            <h1>Médico - Historial de Citas</h1>

            <div className="filter">
                <form onSubmit={handleFilterChange}>
                    <label htmlFor="estado">Estado:</label>
                    <select id="estado" name="estado" defaultValue={estado}>
                        <option value="all">Todos</option>
                        <option value="pendiente">Pendiente</option>
                        <option value="completada">Atendida</option>
                        <option value="cancelada">Cancelada</option>
                    </select>

                    <label htmlFor="usuarioId">Usuario:</label>
                    <select id="usuarioId" name="usuarioId" defaultValue={usuarioId}>
                        <option value="0">Todos</option>
                        {usuarios.map(u => (
                            <option key={u.id} value={u.id}>
                                {u.nombre} {u.apellido}
                            </option>
                        ))}
                    </select>

                    <button type="submit" className="search-btn">Buscar</button>
                </form>
            </div>

            <table className="table">
                <thead>
                <tr className="table-header">
                    <th>Usuario</th>
                    <th>Fecha</th>
                    <th>Hora inicio</th>
                    <th>Hora fin</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                {citas.length === 0 ? (
                    <tr>
                        <td colSpan="6">No hay citas con los filtros seleccionados.</td>
                    </tr>
                ) : (
                    citas.map(c => (
                        <tr key={c.id}>
                            <td>{c.usuarioNombre} {c.usuarioApellido}</td>
                            <td>{new Date(c.fechaHora).toLocaleDateString()}</td>
                            <td>{c.horainicio}</td>
                            <td>{c.horafinal}</td>
                            <td>{c.estado}</td>
                            <td>
                                {c.estado === 'pendiente' ? (
                                    <>
                                        <button
                                            className="submit-button"
                                            onClick={() => navigate(`/VerDetalleCita?id=${c.id}`)}
                                        >
                                            Completar
                                        </button>

                                        <button className="submit-button" onClick={() => handleAccion(c.id, 'cancelar')}>Cancelar</button>
                                    </>
                                ) : (
                                    <button className="submit-button" onClick={() => navigate(`/verDetalleCita?id=${c.id}`)}>Ver</button>
                                )}
                            </td>
                        </tr>
                    ))
                )}
                </tbody>
            </table>
        </div>
    );
}

export default GestionCitas;
