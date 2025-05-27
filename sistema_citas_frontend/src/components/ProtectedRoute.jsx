
import React from 'react';
import { Navigate } from 'react-router-dom';

function ProtectedRoute({ perfil, allowedRoles, children }) {
    console.log("Perfil actual:", perfil);


    return allowedRoles.includes(perfil) ? children : <Navigate to="/ErrorPage" />;
}

export default ProtectedRoute;
