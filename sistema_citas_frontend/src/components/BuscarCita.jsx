import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../styles.css';

function BuscarCita() {
    const [especialidades, setEspecialidades] = useState([]);
    const [localidades, setLocalidades] = useState([]);
    const [medicos, setMedicos] = useState([]);
    const [filtros, setFiltros] = useState({ especialidadId: '', localidadId: '' });
    const [error, setError] = useState('');
    const [mensaje, setMensaje] = useState('');

    useEffect(() => {
        cargarBusquedaInicial();
    }, []);
    useEffect(() => {
        console.log("Médicos recibidos:", medicos);
    }, [medicos]);

    const cargarBusquedaInicial = async () => {
        const token = localStorage.getItem('token');
        const headers = token
            ? { Authorization: `Bearer ${token}` }
            : {}; // Usuario anónimo

        try {
            const res = await axios.get('http://localhost:8080/api/BuscarCita/busqueda', {
                headers,
                withCredentials: true
            });
            setEspecialidades(res.data.especialidades);
            setLocalidades(res.data.localidades);
            setMedicos(res.data.medicos);
        } catch (err) {
            console.error("❌ Error en carga inicial:", err);
            setError('Error al cargar los datos iniciales');
        }
    };

    const handleFiltroChange = (e) => {
        const { name, value } = e.target;
        setFiltros(prev => ({ ...prev, [name]: value }));
    };

    const buscarConFiltros = async (e) => {
        e.preventDefault();
        setError('');
        setMensaje('');

        const token = localStorage.getItem('token');
        const headers = token
            ? { Authorization: `Bearer ${token}` }
            : {}; // Usuario anónimo

        try {
            const params = new URLSearchParams();
            if (filtros.especialidadId) params.append("especialidadId", filtros.especialidadId);
            if (filtros.localidadId) params.append("localidadId", filtros.localidadId);

            const res = await axios.get(`http://localhost:8080/api/BuscarCita/busqueda?${params.toString()}`, {
                headers,
                withCredentials: true
            });

            setMedicos(res.data.medicos);
            setMensaje('Médicos actualizados');
        } catch (err) {
            console.error("❌ Error al filtrar:", err);
            setError('Error al aplicar filtros');
        }
    };

    return (
        <div className="container">
            <h2 className="title">Buscar Médicos</h2>

            {mensaje && <div className="message success">{mensaje}</div>}
            {error && <div className="message error">{error}</div>}

            <form onSubmit={buscarConFiltros} className="search-form">
                <div className="form-group">
                    <label>Especialidad</label>
                    <select name="especialidadId" value={filtros.especialidadId} onChange={handleFiltroChange}>
                        <option value="">Seleccione una especialidad</option>
                        {especialidades.map(esp => (
                            <option key={esp.id} value={esp.id}>{esp.especialidadNombre}</option>
                        ))}
                    </select>
                </div>

                <div className="form-group">
                    <label>Localidad</label>
                    <select name="localidadId" value={filtros.localidadId} onChange={handleFiltroChange}>
                        <option value="">Seleccione una localidad</option>
                        {localidades.map(loc => (
                            <option key={loc.id} value={loc.id}>{loc.localidadNombre}</option>
                        ))}
                    </select>
                </div>

                <button type="submit" className="submit-button">Buscar</button>
            </form>

            <div className="medicos-list">
                <h3>Médicos disponibles:</h3>
                {medicos.map(medico => (
                    <div className="medico-card" key={medico.id}>
                        {medico.foto ? (
                            <img
                                src={`http://localhost:8080/api/medico/foto?id=${medico.id}`}
                                alt="Foto"
                                width="50"
                                height="50"
                                style={{borderRadius: '50%', objectFit: 'cover'}}
                            />
                        ) : <span>No Foto</span>}

                        <p>Nombre: {medico.nombre} {medico.apellido}</p>
                        <p>Especialidad: {medico.especialidadNombre || 'No definida'}</p>
                        <p>Provincia: {medico.localidadNombre || 'No definida'}</p>

                        <div className="horarios">
                            {/* ✅ Botón primero */}
                            <div className="view-all-button">
                                <a href={`/HorarioExtendido?medicoId=${medico.id}`}>
                                    <button className="button">Ver todos los horarios</button>
                                </a>
                            </div>

                            {/* 🕒 Lista de horarios disponibles por día */}
                            {medico.disponibilidad.map(dia => (
                                <div key={dia.fecha}>
                                    <p>Día: {dia.nombre} - {new Date(dia.fecha).toLocaleDateString('es-ES')}</p>
                                    {dia.horarios.map((horario, i) => (
                                        <a
                                            key={i}
                                            href={`/ConfirmarCita?medicoId=${medico.id}&dia=${dia.nombre}&fecha=${dia.fecha}&horaInicio=${horario.horainicio}&horaFin=${horario.horafin}`}
                                        >
                                            <button className="button-busqueda">
                                                Horario: {horario.horainicio} - {horario.horafin}
                                            </button>
                                        </a>
                                    ))}
                                </div>
                            ))}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default BuscarCita;
