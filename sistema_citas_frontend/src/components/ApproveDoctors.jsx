import React, { useEffect, useState } from 'react';

export function ApproveDoctors() {
    const [usuarios, setUsuarios] = useState([]);
    const [estadoMap, setEstadoMap] = useState({});
    const [confirmacion, setConfirmacion] = useState('');
    const [error, setError] = useState('');

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            setError('No hay token disponible');
            return;
        }

        fetch('http://localhost:8080/api/Approve', {
            headers: { Authorization: `Bearer ${token}` },
        })
            .then((res) => {
                if (!res.ok) throw new Error(`Error ${res.status} al obtener los doctores`);
                return res.json();
            })
            .then((data) => {
                setUsuarios(data);

                // Inicializamos estadoMap solo con estados que vienen del backend sin forzar nada
                const estadosIniciales = {};
                data.forEach((u) => {
                    // Si u.estado existe, normalizar; si no, dejar undefined
                    estadosIniciales[u.cedula] = u.estado ? u.estado.toString().trim().toLowerCase() : undefined;
                });
                setEstadoMap(estadosIniciales);
                setError('');
            })
            .catch((err) => setError(err.message));
    }, []);

    const handleChange = (cedula, valor) => {
        setEstadoMap((prev) => ({
            ...prev,
            [cedula]: valor.toLowerCase(),
        }));
    };

    const handleSubmitSingle = (cedula) => {
        const token = localStorage.getItem('token');
        if (!token) {
            setError('No hay token disponible');
            return;
        }

        const estado = estadoMap[cedula];

        if (!estado) {
            setError('No se puede enviar un estado vacío');
            return;
        }

        fetch('http://localhost:8080/api/Approve/approve', {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                cedula: cedula,
                estado: estado,
            }),
        })
            .then((res) => {
                if (!res.ok) throw new Error('Error al actualizar el estado');
                return res.json();
            })
            .then((data) => {
                setConfirmacion(data.mensaje);
                setError('');

                setEstadoMap((prev) => ({
                    ...prev,
                    [cedula]: estado,
                }));

                setUsuarios((prevUsuarios) =>
                    prevUsuarios.map((u) =>
                        u.cedula === cedula ? { ...u, estado } : u
                    )
                );
            })
            .catch((err) => {
                setError(err.message);
                setConfirmacion('');
            });
    };

    // Solo editable si estado es exactamente "pendiente"
    const isEditable = (estado) => {
        return estado === 'pendiente';
    };

    return (
        <div className="approve">
            <h1 className="approve-title">Médicos</h1>
            {error && <p className="message error">{error}</p>}
            {confirmacion && <p className="message success">{confirmacion}</p>}
            <table className="doctor-table">
                <thead>
                <tr>
                    <th>Cédula</th>
                    <th>Nombre</th>
                    <th>Apellido</th>
                    <th>Aprobación</th>
                    <th>Enviar</th>
                </tr>
                </thead>
                <tbody>
                {usuarios.map((usuario) => {
                    // Si el estado no viene, mostramos 'pendiente' pero no permitimos editar
                    const estadoOriginal = usuario.estado ? usuario.estado.toString().trim().toLowerCase() : ' ';
                    const estadoActual = estadoMap[usuario.cedula] ?? estadoOriginal;
                    const editable = isEditable(estadoOriginal);

                    return (
                        <tr key={usuario.cedula}>
                            <td>{usuario.cedula}</td>
                            <td>{usuario.nombre}</td>
                            <td>{usuario.apellido}</td>
                            <td>
                                {editable ? (
                                    <select
                                        value={estadoActual}
                                        onChange={(e) => handleChange(usuario.cedula, e.target.value)}
                                        className="select-estado"
                                    >
                                        <option value="pendiente">Pendiente</option>
                                        <option value="aprobado">Aprobado</option>
                                    </select>
                                ) : (
                                    <span>
                                            {estadoOriginal.charAt(0).toUpperCase() + estadoOriginal.slice(1)}
                                        </span>
                                )}
                            </td>
                            <td>
                                {editable && (
                                    <button
                                        type="button"
                                        className="submit-button"
                                        onClick={() => handleSubmitSingle(usuario.cedula)}
                                    >
                                        Enviar
                                    </button>
                                )}
                            </td>
                        </tr>
                    );
                })}
                </tbody>
            </table>
        </div>
    );
}

export default ApproveDoctors;
