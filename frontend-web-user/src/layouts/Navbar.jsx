import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function Navbar() {
  const { isAuthenticated } = useAuth();

  return (
    <header className="bg-white shadow-md">
      <nav className="container mx-auto px-6 py-4 flex justify-between items-center">
        <Link
                  to="/"
                  className="text-xl font-bold text-[#407581] flex items-center"
                >
                  <img
                    src="/src/assets/images/logo_matchpet.png" 
                    alt="Pata de mascota"
                    className="w-26 h-12 mr-2"
                  />
                  MatchPet
                </Link>
        <div className="flex items-center space-x-6">
          <Link to="/#about" className="text-gray-600 hover:text-[#407581]">¿Cómo Funciona?</Link>
          <Link to="/nosotros" className="text-gray-600 hover:text-[#407581]">Nosotros</Link>

          {!isAuthenticated && (
            <Link to="/login" className="bg-blue-200 text-[#407581] font-bold px-4 py-2 rounded-xl hover:bg-blue-300">
              Iniciar Sesión
            </Link>
          )}
        </div>
      </nav>
    </header>
  );
}
