import React, { useState } from "react";
import { Link, Outlet, useLocation } from "react-router-dom";
import { 
    HomeIcon, 
    UserGroupIcon, 
    BuildingOfficeIcon, 
    HeartIcon,          // <--- NUEVO ICONO PARA ANIMALES
    ChevronDownIcon, 
    ChevronRightIcon,
    Bars3Icon
} from "@heroicons/react/24/outline";
import clsx from "clsx";

const AdminLayout = () => {
    const [isSidebarOpen, setSidebarOpen] = useState(true);
    
    return (
        <div className="flex h-screen bg-gray-100 font-sans">
            {/* Sidebar */}
            <aside 
                className={clsx(
                    "bg-slate-900 text-slate-300 flex flex-col transition-all duration-300 ease-in-out shadow-xl",
                    isSidebarOpen ? "w-64" : "w-20"
                )}
            >
                {/* Logo Area */}
                <div className="h-16 flex items-center justify-center border-b border-slate-800 bg-slate-950">
                    {isSidebarOpen ? (
                        <h1 className="text-xl font-bold text-white tracking-wide">
                            Match<span className="text-indigo-500">Pet</span> Admin
                        </h1>
                    ) : (
                        <span className="text-xl font-bold text-indigo-500">MP</span>
                    )}
                </div>

                {/* Menu Items Container */}
                <div className="flex-1 overflow-y-auto py-4 custom-scrollbar">
                    <nav className="space-y-1 px-2">
                        
                        {/* 1. DASHBOARD */}
                        <SidebarItem 
                            to="dashboard" 
                            icon={<HomeIcon className="w-6 h-6" />} 
                            label="Dashboard" 
                            isOpen={isSidebarOpen} 
                        />

                        {/* SEPARADOR USUARIOS */}
                        {isSidebarOpen && (
                            <div className="px-4 mt-6 mb-2 text-xs font-semibold text-slate-500 uppercase tracking-wider">
                                Gestión de Usuarios
                            </div>
                        )}

                        {/* 2. ADOPTANTES */}
                        <SidebarDropdown 
                            icon={<UserGroupIcon className="w-6 h-6" />} 
                            label="Adoptantes" 
                            isOpen={isSidebarOpen}
                            activePath="/adoptantes"
                        >
                            <SidebarSubItem to="/adoptantes" label="Listado Completo" />
                            <SidebarSubItem to="/adoptantes/create" label="Registrar Nuevo" />
                        </SidebarDropdown>

                        {/* 3. REFUGIOS */}
                        <SidebarDropdown 
                            icon={<BuildingOfficeIcon className="w-6 h-6" />} 
                            label="Refugios" 
                            isOpen={isSidebarOpen}
                            activePath="/refugios"
                        >
                            <SidebarSubItem to="/refugios" label="Directorio Refugios" />
                            <SidebarSubItem to="/refugios/create" label="Registrar Nuevo" />
                        </SidebarDropdown>

                        {/* SEPARADOR CATÁLOGO (NUEVO) */}
                        {isSidebarOpen && (
                            <div className="px-4 mt-6 mb-2 text-xs font-semibold text-slate-500 uppercase tracking-wider">
                                Gestión de Catálogo
                            </div>
                        )}

                        {/* 4. ANIMALES / MASCOTAS (NUEVO) */}
                        <SidebarDropdown 
                            icon={<HeartIcon className="w-6 h-6" />} 
                            label="Mascotas" 
                            isOpen={isSidebarOpen}
                            activePath="/animales"
                        >
                            <SidebarSubItem to="/animales" label="Inventario Total" />
                            <SidebarSubItem to="/animales/create" label="Registrar Mascota" />
                        </SidebarDropdown>

                    </nav>
                </div>

                {/* Footer del Sidebar */}
                <div className="border-t border-slate-800 p-4 bg-slate-950">
                    <button 
                        onClick={() => setSidebarOpen(!isSidebarOpen)}
                        className="flex items-center justify-center w-full p-2 rounded-md hover:bg-slate-800 text-slate-400 hover:text-white transition-colors"
                    >
                        <Bars3Icon className="w-6 h-6" />
                    </button>
                </div>
            </aside>

            {/* Main Content Area */}
            <main className="flex-1 overflow-x-hidden overflow-y-auto bg-gray-50">
                {/* Header Superior */}
                <header className="bg-white shadow-sm h-16 flex items-center px-6 sticky top-0 z-10 justify-between">
                    <h2 className="text-gray-700 font-medium text-lg">Panel de Administración</h2>
                    <div className="flex items-center space-x-4">
                        {/* Aquí podrías poner el perfil del admin o notificaciones */}
                        <div className="w-8 h-8 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-700 font-bold text-sm">
                            A
                        </div>
                    </div>
                </header>

                <div className="p-6">
                    <Outlet />
                </div>
            </main>
        </div>
    );
};

// --- COMPONENTES AUXILIARES ---

const SidebarItem = ({ to, icon, label, isOpen }) => {
    const location = useLocation();
    const isActive = location.pathname === to;

    return (
        <Link
            to={to}
            className={clsx(
                "flex items-center px-3 py-2.5 rounded-lg transition-all duration-200 group",
                isActive 
                    ? "bg-indigo-600 text-white shadow-md" 
                    : "hover:bg-slate-800 hover:text-white"
            )}
        >
            <span className={clsx("shrink-0", isActive ? "text-white" : "text-slate-400 group-hover:text-white")}>
                {icon}
            </span>
            {isOpen && <span className="ml-3 font-medium text-sm">{label}</span>}
        </Link>
    );
};

const SidebarDropdown = ({ icon, label, children, isOpen, activePath }) => {
    const location = useLocation();
    // Auto-abrir si estamos en una ruta hija
    const isChildActive = location.pathname.startsWith(activePath);
    
    const [isExpanded, setIsExpanded] = useState(isChildActive);

    React.useEffect(() => {
        if (!isOpen) setIsExpanded(false);
    }, [isOpen]);

    const handleToggle = () => {
        if (!isOpen) return;
        setIsExpanded(!isExpanded);
    };

    return (
        <div className="mb-1">
            <button
                onClick={handleToggle}
                className={clsx(
                    "w-full flex items-center justify-between px-3 py-2.5 rounded-lg transition-colors group",
                    isChildActive ? "bg-slate-800/50 text-white" : "hover:bg-slate-800 hover:text-white",
                    !isOpen && "justify-center"
                )}
            >
                <div className="flex items-center">
                    <span className={clsx("shrink-0", isChildActive ? "text-indigo-400" : "text-slate-400 group-hover:text-white")}>
                        {icon}
                    </span>
                    {isOpen && <span className="ml-3 font-medium text-sm">{label}</span>}
                </div>
                {isOpen && (
                    <span className="ml-auto">
                        {isExpanded ? (
                            <ChevronDownIcon className="w-4 h-4 text-slate-500" />
                        ) : (
                            <ChevronRightIcon className="w-4 h-4 text-slate-500" />
                        )}
                    </span>
                )}
            </button>

            <div
                className={clsx(
                    "overflow-hidden transition-all duration-300 ease-in-out",
                    isExpanded && isOpen ? "max-h-40 opacity-100 mt-1" : "max-h-0 opacity-0"
                )}
            >
                <div className="bg-slate-950/30 rounded-lg py-1 ml-4 border-l-2 border-slate-700 space-y-1">
                    {children}
                </div>
            </div>
        </div>
    );
};

const SidebarSubItem = ({ to, label }) => {
    const location = useLocation();
    const isActive = location.pathname === to;

    return (
        <Link
            to={to}
            className={clsx(
                "block pl-4 pr-3 py-2 text-sm transition-colors rounded-r-md",
                isActive 
                    ? "text-indigo-400 font-medium bg-slate-800/50 border-l-2 border-indigo-500 -ml-[2px]" 
                    : "text-slate-400 hover:text-white hover:bg-slate-800/30"
            )}
        >
            {label}
        </Link>
    );
};

export default AdminLayout;