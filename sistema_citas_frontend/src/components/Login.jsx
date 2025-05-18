import React, { useEffect, useState } from 'react';
import '../styles.css';
import axios from 'axios';

function Login({ onLoginSuccess }) {
    const [cedula, setCedula] = useState('');
    const [clave, setClave] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/login/login', {
                cedula: parseInt(cedula),
                clave,

            }, { withCredentials: true });

            // Guardar el token JWT
            localStorage.setItem('token', response.data.token);
            onLoginSuccess(response.data.perfil); // opcional: pasar el rol
        } catch (err) {
            setError('Credenciales inválidas');
        }
    };

    return (
        <div className="login">
            <div className="login-box">
                <h1>Iniciar sesión</h1>
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder="Cédula"
                        value={cedula}
                        onChange={(e) => {
                            const value = e.target.value;
                            if (/^\d{0,9}$/.test(value)) {
                                setCedula(value);
                            }
                        }}
                        required
                        maxLength={9}
                        title="Solo números, máximo 9 dígitos"
                    />
                    <input
                        type="password"
                        placeholder="Contraseña"
                        value={clave}
                        onChange={(e) => setClave(e.target.value)}
                        required
                    />
                    <button type="submit">Iniciar sesión</button>
                </form>
                {error && <div className="message error">{error}</div>}
            </div>
        </div>
    );

}

export default Login;
