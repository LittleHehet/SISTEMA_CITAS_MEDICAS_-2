import React, { useEffect, useState } from 'react';
import '../styles.css';

function About() {
    const [info, setInfo] = useState(null);

    useEffect(() => {
        fetch('http://localhost:8080/api/about')
            .then(response => response.json())
            .then(data => setInfo(data))
            .catch(error => console.error('Error al cargar datos:', error));
    }, []);

    if (!info) return <p>Cargando información...</p>;

    return (
        <div className="about">
            <h1>{info.titulo}</h1>
            <p>{info.introduccion}</p>

            <h2>Conoce al Equipo</h2>
            <p>Nuestro equipo está compuesto por estudiantes dedicados que trabajaron juntos para hacer realidad este proyecto:</p>
            <ul>
                {info.equipo.map(integrante => (
                    <li key={integrante.id}><strong>{integrante.nombre}</strong> (ID: {integrante.id})</li>
                ))}
            </ul>

            <h2>Descripción del Proyecto</h2>
            <p>El <strong>Sistema de Citas Médicas</strong> permite a los pacientes:</p>
            <ul>
                {info.descripcion.pacientes.map((item, idx) => <li key={idx}>{item}</li>)}
            </ul>
            <p>Para los médicos, el sistema ofrece herramientas para:</p>
            <ul>
                {info.descripcion.medicos.map((item, idx) => <li key={idx}>{item}</li>)}
            </ul>
            <p>Los administradores pueden:</p>
            <ul>
                {info.descripcion.administradores.map((item, idx) => <li key={idx}>{item}</li>)}
            </ul>

            <h2>Tecnologías Utilizadas</h2>
            <ul>
                {info.tecnologias.map((tec, idx) => <li key={idx}><strong>{tec}</strong></li>)}
            </ul>

            <h2>Nuestra Misión</h2>
            <p>{info.mision}</p>
        </div>
    );
}

export default About;
