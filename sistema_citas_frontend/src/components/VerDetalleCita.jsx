import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import axios from 'axios';

function VerDetalleCita() {
    const [cita, setCita] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [params] = useSearchParams();
    const navigate = useNavigate();

    const id = params.get('id');
    const [notaEdit, setNotaEdit] = useState('');
    const [isMedico, setIsMedico] = useState(false);
    const [perfilStr, setPerfilStr] = useState('');

    useEffect(() => {
        const token = localStorage.getItem('token');
        const perfil = localStorage.getItem('perfil');
        setPerfilStr(perfil);
        setIsMedico(perfil === 'ROLE_MEDICO');

        if (!token) {
            setError('No autenticado');
            setLoading(false);
            return;
        }
        if (!id) {
            setError('ID de cita no proporcionado');
            setLoading(false);
            return;
        }

        axios.get(`http://localhost:8080/api/gestion/cita?id=${id}`, {
            headers: { Authorization: `Bearer ${token}` },
            withCredentials: true,
        })
            .then(res => {
                setCita(res.data);
                setNotaEdit(res.data.nota || '');
                setLoading(false);
            })
            .catch(err => {
                console.error('Error cargando los detalles de la cita:', err.response || err);
                setError('Error cargando los detalles de la cita');
                setLoading(false);
            });
    }, [id]);

    const handleGuardarNota = () => {
        const token = localStorage.getItem('token');
        axios.post(`http://localhost:8080/api/gestion/nota`, null, {
            params: { id, nota: notaEdit },
            headers: { Authorization: `Bearer ${token}` },
            withCredentials: true,
        })
            .then(() => {
                alert('Nota guardada correctamente');
                setCita(prev => ({ ...prev, nota: notaEdit }));
            })
            .catch(err => {
                alert('Error guardando la nota');
                console.error(err);
            });
    };

    if (loading) return <div className="Gestion"><p>Cargando detalles de la cita...</p></div>;
    if (error) return <div className="Gestion"><p>{error}</p></div>;

    return (
        <div className="Gestion">
            <h1>Detalle de la Cita</h1>
            <table className="table">
                <tbody>
                <tr>
                    <th>Médico</th>
                    <td>{cita.medicoNombre} {cita.medicoApellido}</td>
                </tr>
                <tr>
                    <th>Usuario</th>
                    <td>{cita.usuarioNombre} {cita.usuarioApellido}</td>
                </tr>
                <tr>
                    <th>Fecha</th>
                    <td>{new Date(cita.fechaHora).toLocaleDateString()}</td>
                </tr>
                <tr>
                    <th>Hora Inicio</th>
                    <td>{cita.horainicio}</td>
                </tr>
                <tr>
                    <th>Hora Fin</th>
                    <td>{cita.horafinal}</td>
                </tr>
                <tr>
                    <th>Estado</th>
                    <td>{cita.estado}</td>
                </tr>
                <tr>
                    <th>Nota</th>
                    <td>
                        {isMedico && cita.estado === 'pendiente' ? (
                            <>
        <textarea
            value={notaEdit}
            onChange={e => setNotaEdit(e.target.value)}
            rows={4}
            cols={40}
        />
                                <br />
                                <button
                                    className="submit-button"
                                    onClick={() => {
                                        const token = localStorage.getItem('token');
                                        axios.post(`http://localhost:8080/api/gestion/notaYCompletar`, null, {
                                            params: { id, nota: notaEdit },
                                            headers: { Authorization: `Bearer ${token}` },
                                            withCredentials: true,
                                        }).then(() => {
                                            navigate('/GestionCitas');
                                        }).catch(err => {
                                            alert('Error guardando nota');
                                            console.error(err);
                                        });
                                    }}
                                >
                                    Guardar y Completar
                                </button>
                            </>
                        ) : (
                            cita.nota || '-'
                        )}
                    </td>
                </tr>

                </tbody>
            </table>

            <button
                className="submit-button"
                onClick={() => {
                    if (perfilStr === 'ROLE_MEDICO') {
                        navigate('/GestionCitas');
                    } else if (perfilStr === 'ROLE_PACIENTE') {
                        navigate('/historicoPaciente');
                    } else {
                        navigate('/');
                    }
                }}
            >
                Volver
            </button>



        </div>
    );
}

export default VerDetalleCita;
