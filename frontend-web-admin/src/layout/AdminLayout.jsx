import { Link, Outlet, useLocation } from 'react-router-dom';
import { LayoutDashboard, Dog, Home, Users, FileText, Heart } from 'lucide-react';

const menuItems = [
    { path: '/', label: 'Dashboard', icon: LayoutDashboard },
    { path: '/animales', label: 'Animales', icon: Dog },
    { path: '/refugios', label: 'Refugios', icon: Home },
    { path: '/usuarios', label: 'Usuarios', icon: Users },
    { path: '/solicitudes', label: 'Solicitudes', icon: FileText },
    { path: '/donaciones', label: 'Donaciones', icon: Heart },
];

export default function AdminLayout() {
    const location = useLocation();

    return (
        <div className="flex h-screen bg-gray-50">
            {/* Sidebar */}
            <aside className="w-64 bg-white border-r border-gray-200 hidden md:flex flex-col">
                <div className="p-6 border-b border-gray-100">
                    <h1 className="text-2xl font-bold text-indigo-600">MatchPet<span className="text-gray-400">.Admin</span></h1>
                </div>
                <nav className="flex-1 p-4 space-y-1">
                    {menuItems.map(({ path, label, icon: Icon }) => (
                        <Link
                            key={path}
                            to={path}
                            className={`flex items-center gap-3 px-4 py-3 text-sm font-medium rounded-lg transition-colors ${
                                location.pathname === path
                                    ? 'bg-indigo-50 text-indigo-600'
                                    : 'text-gray-600 hover:bg-gray-100'
                            }`}
                        >
                            <Icon size={20} />
                            {label}
                        </Link>
                    ))}
                </nav>
            </aside>

            {/* Main Content */}
            <main className="flex-1 overflow-y-auto">
                <header className="bg-white border-b border-gray-200 p-4 sticky top-0 z-10 flex justify-between items-center md:hidden">
                    <span className="font-bold">MatchPet Admin</span>
                    {/* Aquí iría un botón de menú móvil */}
                </header>
                <div className="p-8">
                    <Outlet />
                </div>
            </main>
        </div>
    );
}