import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [userType, setUserType] = useState('Adoptante');

  const { login, isAuthenticated, loading, error } = useAuth();
  const navigate = useNavigate();

  // Login con Google
  const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8081/oauth2/authorization/google";
  };

  useEffect(() => {
    if (isAuthenticated) {
      if (userType === 'Adoptante') navigate('/dashboard/adoptante');
      else if (userType === 'Refugio') navigate('/dashboard/refugio');
    }
  }, [isAuthenticated, navigate, userType]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    await login(email, password, userType);
  };

  return (
    <div className="min-h-screen bg-[#FFF7E6] flex items-center justify-center">
      <div className="flex w-full max-w-5xl min-h-[60vh] rounded-xl overflow-hidden shadow-lg bg-white/90">

        {/* Formulario */}
        <div className="flex-1 flex items-center justify-center p-6">
          <div className="w-full max-w-md space-y-6">
            <div className="text-center">
              <h1 className="text-4xl font-bold text-[#316B7A]">Bienvenido de nuevo a MatchPet</h1>
              <p className="mt-2 text-gray-500 text-lg">Únete a la comunidad MatchPet</p>
            </div>

            {/* Botones Adoptante / Refugio */}
            <div className="flex w-full max-w-md mx-auto rounded-xl overflow-hidden shadow-md bg-[#2B677750] mb-6">
              <button
                type="button"
                onClick={() => setUserType('Adoptante')}
                className={`flex-1 py-2 text-center text-lg font-semibold transition-all duration-300 rounded-l-xl ${userType === 'Adoptante' ? 'bg-white text-[#2B6777]' : 'text-black hover:bg-white hover:text-[#2B6777]'}`}
              >
                Adoptante
              </button>
              <button
                type="button"
                onClick={() => setUserType('Refugio')}
                className={`flex-1 py-2 text-center text-lg font-semibold transition-all duration-300 rounded-r-xl ${userType === 'Refugio' ? 'bg-white text-[#2B6777]' : 'text-black hover:bg-white hover:text-[#2B6777]'}`}
              >
                Refugio
              </button>
            </div>

            {/* Formulario */}
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label htmlFor="email" className="block text-md font-medium text-gray-700">
                  Correo Electrónico del {userType}
                </label>
                <input
                  id="email"
                  type="email"
                  placeholder="Ingresa tu correo electrónico"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full px-3 py-3 mt-1 text-gray-900 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                />
              </div>

              <div>
                <label htmlFor="password" className="block text-md font-medium text-gray-700">
                  Contraseña
                </label>
                <input
                  id="password"
                  type="password"
                  placeholder="Ingresa tu contraseña"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-3 py-3 mt-1 text-gray-900 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                />
              </div>

              {error && (
                <p className="text-md text-red-600 bg-red-100 p-3 rounded-md text-center">{error}</p>
              )}

              <p className="text-md text-end text-gray-600">¿Olvidaste la contraseña?</p>

              <div>
                <button
                  type="submit"
                  disabled={loading}
                  className="w-full text-lg px-4 py-3 font-bold text-white bg-[#316B7A] rounded-xl hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:bg-primary-400 transition-colors"
                >
                  {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
                </button>
              </div>

              {userType === 'Adoptante' && (
                <>
                  <div className="flex items-center gap-4 my-4">
                    <div className="flex-grow border-t border-gray-300" />
                    <span className="text-gray-500 text-md">O inicia sesión con</span>
                    <div className="flex-grow border-t border-gray-300" />
                  </div>

                  <button
                    type="button"
                    onClick={handleGoogleLogin}
                    className="w-full flex items-center justify-center gap-3 px-4 py-3 border border-gray-300 rounded-xl bg-white text-md font-semibold text-gray-700 hover:bg-gray-50 transition-colors"
                  >
                    <img
                      src="https://www.svgrepo.com/show/475656/google-color.svg"
                      alt="Google logo"
                      className="w-5 h-5"
                    />
                    Inicia sesión con Google
                  </button>
                </>
              )}

              <p className="text-center text-md text-gray-600 mt-2">
                ¿No tienes una cuenta?{' '}
                <Link to="/registro" className="text-[#FDB2A0] font-semibold hover:underline">
                  Regístrate aquí
                </Link>
              </p>
            </form>
          </div>
        </div>

        {/* Imagen lateral */}
        <div className="hidden lg:flex flex-1">
          <div
            className="w-full h-full bg-cover bg-center"
            style={{ backgroundImage: "url('/src/assets/images/dog_login.png')" }}
          ></div>
        </div>

      </div>
    </div>
  );
}
