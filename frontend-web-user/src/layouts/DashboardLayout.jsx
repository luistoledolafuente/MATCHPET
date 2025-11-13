import React from "react";
import { Outlet } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

import AdoptanteSidebar from "../components/dashboard/AdoptanteSidebar";
import RefugioSidebar from "../components/dashboard/RefugioSidebar";



// Loader opcional mientras se verifica el tipo de usuario
const DashboardLayout = () => {
    const { user, userType, profileLoading } = useAuth();

    // 1. Muestra un loader mientras se carga el perfil 
    if (profileLoading) {
        return (
            <div className="flex items-center justify-center min-h-screen bg-gray-50">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#407581]"></div>
            </div>
        );
    }

    // 2. Si no se pudo determinar el tipo de usuario
    if (!userType) {
        return (
            <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 text-gray-700">
                <p className="text-lg">Error de sesión. Por favor, inicia sesión nuevamente.</p>
            </div>
        );
    }

    // CRÍTICO: El contenedor principal usa 'flex' para colocar los elementos horizontalmente (Sidebar + Contenido).
    return (
        <div className="min-h-screen">
            
            {/* 1. Sidebar (Barra Lateral) */}
            {userType === "refugio" ? (
                <RefugioSidebar user={user} /> 
            ) : (
                <AdoptanteSidebar user={user} />
            )}

            {/* 2. Contenido principal (DashboardHome, etc.) */}
            <main className="min-h-screen pl-64">
                <Outlet />
            </main>
        </div>
    );
}

export default DashboardLayout;