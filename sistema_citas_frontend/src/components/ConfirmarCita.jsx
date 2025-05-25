import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

function ConfirmarCita() {
    const location = useLocation();
    const navigate = useNavigate();
    const queryParams = new URLSearchParams(location.search);

    const [error, setError] = useState(null);
    const [token, setToken] = useState(null);
    const [vistaPrevia, setVistaPrevia] = useState(null);
    const [loading, setLoading] = useState(true);

    const medicoId = queryParams.get("medicoId");
    const dia = queryParams.get("dia");
    const fecha = queryParams.get("fecha");
    const horaInicio = queryParams.get("horaInicio");
    const horaFin = queryParams.get("horaFin");

    // Obtener token localStorage
    useEffect(() => {
        const storedToken = localStorage.getItem('token');
        if (!storedToken) {
            setError('No hay token disponible');
            setLoading(false);
            return;
        }
        setToken(storedToken);
    }, []);

    // Fetch vista previa desde backend
    useEffect(() => {
        if (!token || !medicoId || !dia || !fecha || !horaInicio || !horaFin) {
            setLoading(false);
            if (!error) setError("Faltan parámetros para mostrar la vista previa");
            return;
        }

        setError(null);
        setLoading(true);

        fetch(
            `/api/confirmarCita/vistaPrevia?medicoId=${medicoId}&dia=${encodeURIComponent(dia)}&fecha=${fecha}&horaInicio=${horaInicio}&horaFin=${horaFin}`,
            {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`
                },
                credentials: "include"
            }
        )
            .then(async res => {
                if (!res.ok) {
                    const msg = await res.text();
                    throw new Error(msg || "Error al obtener la vista previa");
                }
                return res.json();
            })
            .then(data => {
                setVistaPrevia(data);
                setLoading(false);
            })
            .catch(err => {
                setError(err.message);
                setLoading(false);
            });
    }, [token, medicoId, dia, fecha, horaInicio, horaFin]);

    // Confirmar cita POST
    const handleConfirmar = () => {
        if (!token) {
            setError("Acceso denegado: token no válido");
            return;
        }
        if (!vistaPrevia) {
            setError("No hay datos para confirmar la cita");
            return;
        }

        const citaDTO = {
            medicoId: vistaPrevia.medico.id,
            dia,
            fecha,
            horaInicio,
            horaFin
        };

        fetch("/api/confirmarCita/vistaPrevia/", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            credentials: "include",
            body: JSON.stringify(citaDTO)
        })
            .then(async res => {
                if (!res.ok) {
                    const msg = await res.text();
                    throw new Error(msg || "Error al confirmar cita");
                }
                return res.text();
            })
            .then(msg => {
                alert(msg);
                navigate("/BuscarCita");
            })
            .catch(err => {
                setError(err.message);
            });
    };

    if (loading) return <p>Cargando...</p>;

    if (error) {
        return (
            <div style={{ color: "red", fontWeight: "bold" }}>
                <p>Error: {error}</p>
                <button onClick={() => navigate("/BuscarCita")}>Regresar</button>
            </div>
        );
    }

    if (!vistaPrevia) {
        return (
            <div>
                <p>No se pudo cargar la información de la cita.</p>
                <button onClick={() => navigate("/BuscarCita")}>Regresar</button>
            </div>
        );
    }

    const medico = vistaPrevia.medico;

    return (
        <div className="containerConfirmarCita">
            <h1 className="titleConfirmarCita">Información del Médico Seleccionado</h1>

            <div className="form-groupConfirmarCita">
                {medico.id ? (
                    <img
                        src={`http://localhost:8080/api/medico/foto?id=${medico.id}`}
                        alt="Foto de perfil"
                        width="70"
                        height="70"
                        style={{ borderRadius: "50%", objectFit: "cover", marginBottom: "10px" }}
                        onError={(e) => {
                            e.target.onerror = null;
                            e.target.replaceWith(document.createTextNode("No hay foto"));
                        }}
                    />
                ) : (
                    <span>No Foto</span>
                )}
                <label>Nombre Completo:</label>
                <p>{medico.nombre} {medico.apellido}</p>
                <p>ID: {medico.id}</p>
            </div>

            <div className="form-groupConfirmarCita">
                <label>Especialidad:</label>
                <p>{medico.especialidadNombre || "Sin especialidad"}</p>
            </div>

            <div className="form-groupConfirmarCita">
                <label>Fecha:</label>
                <p>{fecha}</p>
            </div>

            <div className="form-groupConfirmarCita">
                <label>Localidad:</label>
                <p>{medico.localidadNombre || "Sin localidad"}</p>
            </div>

            <div className="form-groupConfirmarCita">
                <label>Horario Seleccionado:</label>
                <p>{dia} {horaInicio} - {horaFin}</p>
            </div>

            <button
                type="button"
                className="submit-buttonConfirmarCita"
                onClick={handleConfirmar}
            >
                Confirmar
            </button>

            <button className="btn btn-return" onClick={() => navigate("/BuscarCita")}>
                Regresar
            </button>
        </div>
    );
}

export default ConfirmarCita;
