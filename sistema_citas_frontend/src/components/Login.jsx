import React, { useEffect, useState } from 'react';
import { Link , useLocation } from 'react-router-dom';
import '../styles.css';
import axios from 'axios';

function Login({ onLoginSuccess }) {
    const [cedula, setCedula] = useState('');
    const [clave, setClave] = useState('');
    const [error, setError] = useState('');
    const location = useLocation();
    const [mensajeRedireccion, setMensajeRedireccion] = useState('');

    useEffect(() => {
        if (location.state?.mensaje) {
            setMensajeRedireccion(location.state.mensaje);
        }
    }, [location.state]);


    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/login/login', {
                cedula: parseInt(cedula),
                clave,

            }, { withCredentials: true });

            // Guardar el token JWT
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('perfil', response.data.perfil);
            onLoginSuccess(response.data.perfil); // opcional: pasar el rol
        } catch (err) {
            setError('Credenciales inválidas');
        }

    };


    return (

        <div className="login">
            <div className="login-box">
                <h1>Iniciar sesión</h1>

                {mensajeRedireccion && (
                    <div className="message error">{mensajeRedireccion}</div>
                )}

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
            <div className="register-link">
                <p>¿No tienes una cuenta?</p>
                <Link to="/Sign-up">Regístrate aquí</Link>
            </div>
        </div> // fin de login-box
    );

}

export default Login;
