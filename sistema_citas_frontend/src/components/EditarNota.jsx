import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

function EditarNota() {
    const { id } = useParams(); // Supone que la ruta viene con /editarNota/:id
    const [nota, setNota] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        axios.get(`http://localhost:8080/verDetalleCita?id=${id}`)
            .then(res => {
                if (res.data && res.data.cita) {
                    setNota(res.data.cita.nota || '');
                }
            });
    }, [id]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        await axios.post('http://localhost:8080/guardarNota', null, {
            params: { id, nota }
        });
        navigate('/GestionCitas');
    };

    return (
        <div className="container">
            <h1>Editar Nota para la Cita</h1>
            <form onSubmit={handleSubmit}>
        <textarea
            name="nota"
            id="nota"
            rows="6"
            style={{ width: '100%' }}
            value={nota}
            onChange={(e) => setNota(e.target.value)}
        />
                <button type="submit" className="submit-button">Guardar Nota</button>
            </form>
        </div>
    );
}

export default EditarNota;
