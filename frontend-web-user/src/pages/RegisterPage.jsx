import React, { useState, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import authService from '../services/authService';

// ✅ Componente de input reutilizable
const InputField = ({ id, label, placeholder, type = 'text', className = '', value, onChange }) => (
  <div className={`flex flex-col min-w-0 ${className}`}>
    <label htmlFor={id} className="block text-md font-medium text-gray-700">
      {label}
    </label>
    <input
      id={id}
      type={type}
      placeholder={placeholder}
      required
      value={value}
      onChange={onChange}
      className="w-full px-3 py-2 text-sm text-gray-900 border border-gray-300 rounded-xl 
      focus:outline-none focus:ring-2 focus:ring-[#316B7A] focus:border-[#316B7A]"
    />
  </div>
);

export default function RegisterPage() {
  const [userType, setUserType] = useState('adoptante');
  const [formData, setFormData] = useState({
    nombreCompleto: '',
    nombreRefugio: '',
    email: '',
    password: '',
    confirmPassword: '',
    telefono: '',
    personaContacto: '',
    fechaNacimiento: '',
    ciudad: '',
    direccion: '',
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleChange = useCallback((e) => {
    const { id, value } = e.target;
    setFormData((prev) => ({ ...prev, [id]: value }));
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    if (formData.password !== formData.confirmPassword) {
      setError('Las contraseñas no coinciden');
      setLoading(false);
      return;
    }

    try {
      if (userType === 'adoptante') {
        await authService.register({
          nombreCompleto: formData.nombreCompleto,
          email: formData.email,
          password: formData.password,
          telefono: formData.telefono,
          fechaNacimiento: formData.fechaNacimiento,
          ciudad: formData.ciudad,
          direccion: formData.direccion,
        });
      } else {
        // Refugio
        await authService.registerRefugio({
          nombreRefugio: formData.nombreRefugio,
          emailLogin: formData.email,   // Email para login
          emailRefugio: formData.email, // Email de contacto
          password: formData.password,
          telefonoContacto: formData.telefono,
          personaContacto: formData.personaContacto,
          ciudad: formData.ciudad,
          direccion: formData.direccion,
        });
      }

      setSuccess('¡Registro exitoso! Redirigiendo al inicio de sesión...');
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      console.error('Error API:', err.response ? err.response.data : err.message);
      setError(err.response?.data?.message || err.message || 'Error en el registro. Por favor, intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#FFF7E6] flex items-center justify-center">
      <div className="flex w-full max-w-4xl rounded-xl overflow-hidden shadow-lg bg-white/90">
        {/* Imagen lateral */}
        <div className="hidden lg:flex flex-1">
          <div
            className="w-full h-full bg-cover bg-center"
            style={{ backgroundImage: "url('/src/assets/images/gato_home.png')" }}
          ></div>
        </div>

        {/* Formulario */}
        <div className="flex-1 flex items-center justify-center p-6">
          <div className="w-full max-w-xl space-y-8">
            <div className="text-center">
              <h1 className="text-3xl font-bold text-[#316B7A]">Crea tu cuenta en MatchPet</h1>
              <p className="mt-4 text-gray-500 text-1xl">Únete a la comunidad MatchPet</p>
            </div>

            {/* Tabs tipo usuario */}
            <div className="flex w-full max-w-md mx-auto rounded-xl overflow-hidden shadow-md bg-[#2B677750] mb-6">
              {['adoptante', 'refugio'].map((type) => (
                <button
                  key={type}
                  type="button"
                  onClick={() => setUserType(type)}
                  className={`flex-1 py-2 text-center text-md font-semibold transition-all duration-300 ${
                    userType === type
                      ? 'bg-white text-[#2B6777]'
                      : 'text-black hover:bg-white hover:text-[#2B6777]'
                  } ${type === 'adoptante' ? 'rounded-l-xl' : 'rounded-r-xl'}`}
                >
                  {type === 'adoptante' ? 'Adoptante' : 'Refugio'}
                </button>
              ))}
            </div>

            {/* FORMULARIO */}
            <form onSubmit={handleSubmit} className="space-y-3">
              {userType === 'adoptante' ? (
                <>
                  <InputField id="nombreCompleto" label="Nombre completo" placeholder="Ingrese su nombre completo" value={formData.nombreCompleto} onChange={handleChange} />
                  <InputField id="email" label="Correo Electrónico" placeholder="nombre@gmail.com" type="email" value={formData.email} onChange={handleChange} />

                  <div className="flex gap-4">
                    <InputField id="telefono" label="Teléfono" placeholder="999 999 999" className="flex-1" value={formData.telefono} onChange={handleChange} />
                    <InputField id="fechaNacimiento" label="Fecha de Nacimiento" type="date" className="flex-1" value={formData.fechaNacimiento} onChange={handleChange} />
                  </div>

                  <div className="flex gap-4">
                    <InputField id="ciudad" label="Ciudad" placeholder="Ingresa tu ciudad" className="flex-1" value={formData.ciudad} onChange={handleChange} />
                    <InputField id="direccion" label="Dirección" placeholder="Av. Los Pinos 123, Miraflores" className="flex-1" value={formData.direccion} onChange={handleChange} />
                  </div>
                </>
              ) : (
                <>
                  <InputField id="nombreRefugio" label="Nombre del refugio" placeholder="Ingrese nombre del refugio" value={formData.nombreRefugio} onChange={handleChange} />
                  <InputField id="email" label="Correo Electrónico" placeholder="nombre@gmail.com" type="email" value={formData.email} onChange={handleChange} />

                  <div className="flex gap-4">
                    <InputField id="telefono" label="Teléfono" placeholder="999 999 999" className="flex-1" value={formData.telefono} onChange={handleChange} />
                    <InputField id="personaContacto" label="Persona de contacto" placeholder="Ejemplo: Ana Mendoza" className="flex-1" value={formData.personaContacto} onChange={handleChange} />
                  </div>

                  <div className="flex gap-4">
                    <InputField id="ciudad" label="Ciudad" placeholder="Ingresa tu ciudad" className="flex-1" value={formData.ciudad} onChange={handleChange} />
                    <InputField id="direccion" label="Dirección" placeholder="Av. Los Pinos 123, Miraflores" className="flex-1" value={formData.direccion} onChange={handleChange} />
                  </div>
                </>
              )}

              <InputField id="password" label="Contraseña" placeholder="Crea una contraseña" type="password" value={formData.password} onChange={handleChange} />
              <InputField id="confirmPassword" label="Confirmar contraseña" placeholder="Repite tu contraseña" type="password" value={formData.confirmPassword} onChange={handleChange} />

              {error && <p className="text-red-500">{error}</p>}
              {success && <p className="text-green-500">{success}</p>}

              <button
                type="submit"
                disabled={loading}
                className="w-full py-2 mt-1 bg-[#316B7A] text-white text-md font-semibold rounded-xl hover:bg-[#1f4f5a] transition-colors"
              >
                {loading ? 'Registrando...' : 'Registrarme'}
              </button>

              <p className="text-center text-gray-500">
                ¿Ya tienes una cuenta?{' '}
                <Link to="/login" className="text-[#FDB2A0] font-semibold">
                  Inicia sesión
                </Link>
              </p>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
