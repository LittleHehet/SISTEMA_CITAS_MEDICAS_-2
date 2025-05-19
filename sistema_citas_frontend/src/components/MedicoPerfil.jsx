import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../styles.css';

function MedicoPerfil() {
    const [medico, setMedico] = useState(null);
    const [especialidades, setEspecialidades] = useState([]);
    const [localidades, setLocalidades] = useState([]);
    const [mensaje, setMensaje] = useState('');
    const [error, setError] = useState('');
    const [fotoTimestamp, setFotoTimestamp] = useState(Date.now());
    const [foto, setFoto] = useState(null);


    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            setError('No hay token disponible');
            return;
        }

        axios.get('http://localhost:8080/api/medico/perfil', {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
            .then(res => {
                console.log("✅ Datos recibidos:", res.data);  // LOG IMPORTANTE
                setMedico(res.data.medico);
                setEspecialidades(res.data.especialidades);
                setLocalidades(res.data.localidades);
            })
            .catch(err => {
                console.error("❌ Error en GET /perfil:", err.response || err.message);
                setError('Error al cargar los datos del médico');
            });
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;

        if (name === 'especialidad') {
            setMedico(prev => ({
                ...prev,
                especialidad: { ...prev.especialidad, id: parseInt(value) }
            }));
        } else if (name === 'localidad') {
            setMedico(prev => ({
                ...prev,
                localidad: { ...prev.localidad, id: parseInt(value) }
            }));
        } else {
            setMedico(prev => ({ ...prev, [name]: value }));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append('id', medico.id);
        formData.append('costo', medico.costo);
        formData.append('frecuenciaCitas', medico.frecuenciaCitas);
        formData.append('nota', medico.nota);
        formData.append('especialidadId', medico.especialidad?.id || '');
        formData.append('localidadId', medico.localidad?.id || '');
        if (foto) {
            formData.append('archivoFoto', foto);
        }


        try {
            const res = await axios.post('http://localhost:8080/api/medico/actualizar', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    Authorization: `Bearer ${localStorage.getItem('token')}`
                }
            });
            setMensaje('Perfil actualizado correctamente');
            setFotoTimestamp(Date.now()); // 🔄 Fuerza recarga de imagen
            setError('');
        } catch (err) {
            setError('Error al actualizar el perfil');
            setMensaje('');
        }
    };

    if (!medico) return <div>Cargando...</div>;

    return (
        <div className="container">
            <h2 className="title">Actualizar Información del Médico</h2>
            {mensaje && <div className="message success">{mensaje}</div>}
            {error && <div className="message error">{error}</div>}

            <form onSubmit={handleSubmit} encType="multipart/form-data">
                <div className="form-group">
                    <label>Vista previa de la foto actual:</label>
                    <img
                        src={`http://localhost:8080/api/medico/foto?id=${medico.id}&t=${fotoTimestamp}`}
                        alt="Foto actual"
                        width="150"
                        height="150"
                        style={{ borderRadius: '50%', objectFit: 'cover' }}
                        onError={(e) => e.target.style.display = 'none'} // oculta si no hay imagen aún
                    />
                </div>


                <div className="form-group">
                    <label>Foto de Perfil</label>
                    <input type="file" name="archivoFoto" onChange={e => setFoto(e.target.files[0])} accept="image/*" />
                </div>

                <div className="form-group">
                    <label>ID (Cédula)</label>
                    <input type="text" value={medico.usuarios.cedula} readOnly />
                </div>

                <div className="form-group">
                    <label>Nombre</label>
                    <input type="text" value={medico.usuarios.nombre} readOnly />
                </div>

                <div className="form-group">
                    <label>Apellido</label>
                    <input type="text" value={medico.usuarios.apellido} readOnly />
                </div>

                <div className="form-group">
                    <label>Especialidad</label>
                    <select name="especialidad" value={medico.especialidad?.id || ''} onChange={handleChange} required>
                        <option value="">Seleccione una especialidad</option>
                        {especialidades.map(esp => (
                            <option key={esp.id} value={esp.id}>
                                {esp.especialidadNombre}
                            </option>
                        ))}
                    </select>

                </div>

                <div className="form-group">
                    <label>Costo de Consulta</label>
                    <select name="costo" value={medico.costo} onChange={handleChange} required>
                        <option value="">Seleccione un costo</option>
                        {[...Array(10)].map((_, i) => {
                            const val = (i + 1) * 5000;
                            return <option key={val} value={val}>{val} colones</option>;
                        })}
                    </select>
                </div>

                <div className="form-group">
                    <label>Localidad</label>
                    <select name="localidad" value={medico.localidad?.id || ''} onChange={handleChange} required>
                        <option value="">Seleccione una localidad</option>
                        {localidades.map(loc => (
                            <option key={loc.id} value={loc.id}>
                                {loc.localidadNombre}</option>
                        ))}
                    </select>
                </div>

                <div className="form-group">
                    <label>Horario (de prueba)</label>
                    <input
                        type="text"
                        name="horario"
                        value={medico.horario || ''}
                        onChange={handleChange}
                        placeholder="Ej: Lunes 8-12 y 13-17 / Martes 9-14"
                    />
                </div>


                <div className="form-group">
                    <label>Frecuencia de Citas (minutos)</label>
                    <input type="number" name="frecuenciaCitas" value={medico.frecuenciaCitas || ''} onChange={handleChange} min="10" max="120" required />
                </div>

                <div className="form-group">
                    <label>Presentación</label>
                    <textarea name="nota" value={medico.nota || ''} onChange={handleChange} required minLength="10" maxLength="500" />
                </div>


                <button type="submit" className="submit-button">Guardar Cambios</button>
            </form>
        </div>
    );
}

export default MedicoPerfil;