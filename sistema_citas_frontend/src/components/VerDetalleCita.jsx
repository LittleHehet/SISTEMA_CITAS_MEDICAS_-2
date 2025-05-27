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

    // Estado para editar nota solo si es medico
    const [notaEdit, setNotaEdit] = useState('');
    const [isMedico, setIsMedico] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            setError('No autenticado');
            setLoading(false);
            return;
        }

        // Aquí decodifica el token para obtener rol o llama a un endpoint para obtener info del usuario
        // Ejemplo simple si tu token es JWT:
        try {
            const base64Payload = token.split('.')[1];
            const payload = JSON.parse(atob(base64Payload));
            setIsMedico(payload.role === 'MEDICO'); // Ajusta según el nombre de la propiedad de rol
        } catch {
            setIsMedico(false);
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
                setError('Error cargando los detalles de la cita');
                setLoading(false);
                console.error(err);
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
                    <th>Medico</th>
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
                        {isMedico ? (
                            <>
                  <textarea
                      value={notaEdit}
                      onChange={e => setNotaEdit(e.target.value)}
                      rows={4}
                      cols={40}
                  />
                                <br />
                                <button className="submit-button" onClick={handleGuardarNota}>Guardar Nota</button>
                            </>
                        ) : (
                            cita.nota || '-'
                        )}
                    </td>
                </tr>
                </tbody>
            </table>

            <button className="submit-button" onClick={() => navigate('/GestionCitas')}>
                Volver a Gestión de Citas
            </button>
        </div>
    );
}

export default VerDetalleCita;
