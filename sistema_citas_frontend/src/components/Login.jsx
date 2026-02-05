import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import '../styles.css';
import axios from 'axios';
import { GoogleLogin } from '@react-oauth/google';
import { jwtDecode } from 'jwt-decode';


const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function Login({ onLoginSuccess }) {
    const [cedula, setCedula] = useState('');
    const [clave, setClave] = useState('');
    const [error, setError] = useState('');
    const location = useLocation();
    const navigate = useNavigate(); // 👈 nuevo hook para redirección
    const [mensajeRedireccion, setMensajeRedireccion] = useState('');

    useEffect(() => {
        if (location.state?.mensaje) {
            setMensajeRedireccion(location.state.mensaje);
        }
    }, [location.state]);


    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(''); // limpiar error anterior
        try {
            const response = await axios.post(`${API_BASE_URL}/api/login/login`, {
                cedula: parseInt(cedula),
                clave,
            }, { withCredentials: true });

            const { token, perfil, medicoEstado, perfilCompleto, medicoId } = response.data;

            // Validación para médicos no aprobados
            if (perfil === 'ROLE_MEDICO' && medicoEstado?.toLowerCase() !== 'aprobado') {
                setError('Tu cuenta está pendiente de aprobación.');
                return; // 👈 Detener el proceso
            }

            // Guardar datos solo si pasa validaciones
            localStorage.setItem('token', token);
            localStorage.setItem('perfil', perfil);
            localStorage.setItem('perfilCompleto', perfilCompleto);
            onLoginSuccess(perfil, perfilCompleto);

            // Redirección según perfil
            if (perfil === 'ROLE_PACIENTE') {
                navigate('/BuscarCita');
            } else if (perfil === 'ROLE_ADMINISTRADOR') {
                navigate('/ApproveDoctors');
            } else if (perfil === 'ROLE_MEDICO') {
                if (!perfilCompleto && medicoId) {
                    navigate(`/Medico-Perfil?id=${medicoId}`);
                } else {
                    navigate('/GestionCitas');
                }
            } else {
                setError('Perfil desconocido');
            }

        } catch (err) {
            setError('Credenciales inválidas');
        }
    };

    const handleGoogleSuccess = async (credentialResponse) => {
        try {
            const idToken = credentialResponse.credential; // <-- ID TOKEN (JWT)

            // (Opcional) leer email/nombre del token en el front
            const decoded = jwtDecode(idToken);
            // console.log(decoded.email, decoded.name);

            // Guardarlo igual que tu token local (tu app ya usa localStorage.token)
            localStorage.setItem('token', idToken);

            // Pedile al backend que lo valide y te devuelva el perfil real de tu BD
            const me = await axios.get(`${API_BASE_URL}/api/auth/me`, {
                headers: { Authorization: `Bearer ${idToken}` },
            });

            const { perfil, perfilCompleto, medicoEstado, medicoId } = me.data;

            localStorage.setItem('perfil', perfil);
            localStorage.setItem('perfilCompleto', perfilCompleto ? 'true' : 'false');
            onLoginSuccess(perfil, perfilCompleto);

            // Redirección igual que tu login local
            if (perfil === 'ROLE_PACIENTE') navigate('/BuscarCita');
            else if (perfil === 'ROLE_ADMINISTRADOR') navigate('/ApproveDoctors');
            else if (perfil === 'ROLE_MEDICO') {
                if (medicoEstado?.toLowerCase() !== 'aprobado') {
                    setError('Tu cuenta está pendiente de aprobación.');
                    return;
                }
                if (!perfilCompleto && medicoId) navigate(`/Medico-Perfil?id=${medicoId}`);
                else navigate('/GestionCitas');
            } else {
                setError('Perfil desconocido');
            }

        } catch (e) {
            setError('No se pudo iniciar sesión con Google');
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
                    <div style={{ marginTop: '12px' }}>
                        <GoogleLogin
                            onSuccess={handleGoogleSuccess}
                            onError={() => setError('Error con Google Login')}
                            useOneTap={false}
                        />
                    </div>

                </form>
                {error && <div className="message error">{error}</div>}
            </div>
            <div className="register-link">
                <p>¿No tienes una cuenta?</p>
                <Link to="/Sign-up">Regístrate aquí</Link>
            </div>
        </div>
    );
}

export default Login;
