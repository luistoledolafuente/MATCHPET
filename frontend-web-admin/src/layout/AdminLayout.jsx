import React from "react";
import { Link, Outlet } from "react-router-dom";  // Outlet renderiza el contenido de la ruta activa

const AdminLayout = () => {
    return (
        <div className="flex h-screen bg-gray-100">
            {/* Sidebar */}
            <div className="w-64 bg-indigo-600 text-white p-4">
                <h2 className="text-2xl font-bold mb-6">Admin Dashboard</h2>
                <ul>
                    <li className="mb-4">
                        <Link to="/" className="hover:text-indigo-300">Dashboard</Link>
                    </li>
                    <li className="mb-4">
                        <Link to="/create" className="hover:text-indigo-300">Crear Adoptante</Link>
                    </li>
                    <li className="mb-4">
                        <Link to="/adoptantes" className="hover:text-indigo-300">Adoptantes</Link>
                    </li>
                    {/* Puedes agregar más enlaces aquí */}
                </ul>
            </div>

            {/* Main content area */}
            <div className="flex-1 p-6">
                <Outlet /> {/* Aquí se renderizará el componente correspondiente a la ruta activa */}
            </div>
        </div>
    );
};

export default AdminLayout;
