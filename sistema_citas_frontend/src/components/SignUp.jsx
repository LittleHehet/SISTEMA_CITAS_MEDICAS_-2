import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function SignUp() {
    const [form, setForm] = useState({
        cedula: '',
        nombre: '',
        apellido: '',
        clave: '',
        confirmPassword: '',
        perfil: 'PACIENTE'
    });

    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm({ ...form, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (form.clave !== form.confirmPassword) {
            setError('Las contraseñas no coinciden');
            return;
        }

        try {
            const response = await axios.post(`${API_BASE_URL}/api/signup`, {
                cedula: parseInt(form.cedula),
                nombre: form.nombre,
                apellido: form.apellido,
                clave: form.clave,
                perfil: form.perfil
            });

            alert('Usuario registrado exitosamente');
            navigate('/Login');
        } catch (err) {
            if (err.response && err.response.data) {
                setError(err.response.data);
            } else {
                setError('Error inesperado al registrar');
            }
        }
    };

    return (
        <div className="sign-up">
            <div className="sign-up-box">
                <h2>Registro</h2>

                {error && (
                    <div className="message error">
                        <p>{error}</p>
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <label htmlFor="userid">Usuario (Cédula):</label>
                    <input
                        type="text"
                        id="userid"
                        name="cedula"
                        value={form.cedula}
                        onChange={handleChange}
                        placeholder="Usuario"
                        required
                        pattern="\d{1,9}"
                        title="Solo números, hasta 9 dígitos"
                        maxLength="9"
                    />

                    <label htmlFor="name">Nombre:</label>
                    <input
                        type="text"
                        id="name"
                        name="nombre"
                        value={form.nombre}
                        onChange={handleChange}
                        placeholder="Nombre"
                        required
                        pattern="^[A-Za-zÁÉÍÓÚáéíóúÑñ]+$"
                        title="Solo letras, sin espacios"
                    />


                    <label htmlFor="last-name">Apellido:</label>
                    <input
                        type="text"
                        id="last-name"
                        name="apellido"
                        value={form.apellido}
                        onChange={handleChange}
                        placeholder="Apellido"
                        required
                        pattern="^[A-Za-zÁÉÍÓÚáéíóúÑñ]+$"
                        title="Solo letras, sin espacios"
                    />


                    <label htmlFor="password">Contraseña:</label>
                    <input
                        type="password"
                        id="password"
                        name="clave"
                        value={form.clave}
                        onChange={handleChange}
                        placeholder="Contraseña"
                        required
                        pattern="^\S+$"
                        title="La contraseña no puede contener espacios"
                    />

                    <label htmlFor="confirmPassword">Confirmar Contraseña:</label>
                    <input
                        type="password"
                        id="confirmPassword"
                        name="confirmPassword"
                        value={form.confirmPassword}
                        onChange={handleChange}
                        placeholder="Confirmar Contraseña"
                        required
                        pattern="^\S+$"
                        title="La contraseña no puede contener espacios"
                    />

                    <label htmlFor="profile">Perfil:</label>
                    <select
                        id="profile"
                        name="perfil"
                        value={form.perfil}
                        onChange={handleChange}
                        required
                    >
                        <option value="ROLE_PACIENTE">Paciente</option>
                        <option value="ROLE_MEDICO">Médico</option>
                    </select>

                    <button type="submit">Registrarse</button>
                </form>

                <div>
                    <p>¿Ya tienes una cuenta?</p>
                    <button onClick={() => navigate('/Login')}>Iniciar sesión</button>
                </div>
            </div>
        </div>
    );
}

export default SignUp;
