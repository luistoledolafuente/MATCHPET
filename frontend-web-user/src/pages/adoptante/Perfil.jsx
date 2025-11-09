import React, { useState, useEffect } from "react";
import { useAuth } from "../../contexts/AuthContext";
// Iconos de Lucide React (Asegúrate de tenerlos instalados con: npm install lucide-react)
import { Camera, User, Phone, MapPin, Heart, Lock, Trash2, Edit, Save } from "lucide-react";

// Función para obtener solo el primer nombre
const getFirstName = (fullName) => {
  if (typeof fullName !== 'string' || !fullName) return 'Usuario';
  return fullName.split(' ')[0];
};

export default function Perfil() {
  const { user, loading } = useAuth();
  const [isEditingProfile, setIsEditingProfile] = useState(false); // Controla si se puede editar
  
  // Estado para los datos del formulario
  const [profileData, setProfileData] = useState({
    nombre: '',
    apellido: '',
    telefono: 'No especificado',
    ubicacion: 'No especificada',
  });
  
  // Estado para el formulario de cambio de contraseña
  const [passwordFields, setPasswordFields] = useState({
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: '',
  });

  // Efecto para inicializar los datos del perfil cuando el objeto 'user' está disponible
  useEffect(() => {
    if (user) {
      // Intenta extraer nombre y apellido de nombreCompleto
      const nameParts = user.nombreCompleto ? user.nombreCompleto.split(' ') : [];
      const nombre = nameParts[0] || '';
      const apellido = nameParts.slice(1).join(' ') || ''; 
      
      setProfileData({
        nombre: nombre,
        apellido: apellido,
        // Usamos los campos reales del usuario (si existen en el objeto 'user')
        telefono: user.telefono || 'No especificado',
        ubicacion: user.ubicacion || 'No especificada',
      });
    }
  }, [user]); 

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProfileData(prev => ({ ...prev, [name]: value }));
  };

  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setPasswordFields(prev => ({ ...prev, [name]: value }));
  };

  const handleSaveProfile = () => {
    // ESTO ES PENDIENTE DE CONEXIÓN CON EL ENDPOINT PUT/PATCH /api/user/profile
    console.log("Guardando cambios de perfil (PENDIENTE DE API):", profileData);
    setIsEditingProfile(false); // Desactiva el modo edición después de "guardar"
    alert("Datos de perfil actualizados localmente. Falta la conexión con la API.");
  };
  
  const handleEditProfile = () => {
    setIsEditingProfile(true); // Activa el modo edición
  };

  const handleSavePassword = () => {
    // ESTO ES PENDIENTE DE CONEXIÓN CON EL ENDPOINT POST /api/auth/reset-password
    console.log("Enviando solicitud de cambio de contraseña (PENDIENTE DE API):", { newPassword: passwordFields.newPassword });
    alert("Solicitud de cambio de contraseña enviada. Falta la conexión con la API.");
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-full min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#407581]"></div>
      </div>
    );
  }

  // Valores de visualización
  const userEmail = user?.email || 'usuario@email.com';
  const displayUserName = user?.nombreCompleto || 'Ana García';


  return (
    <div className="bg-gradient-to-b from-[#BAE6FD] to-[#FFF7E6] min-h-screen overflow-x-hidden p-4 sm:p-8">
      <div className="max-w-4xl mx-auto bg-white rounded-xl shadow-2xl p-6 sm:p-10">
        
        <h1 className="text-3xl font-extrabold text-[#316B7A] mb-8 border-b pb-4">
          Gestionar Perfil
        </h1>

        {/* --- 1. Información Personal --- */}
        <section className="mb-10 p-6 bg-[#F8F9FA] rounded-lg shadow-inner">
          <h2 className="text-xl font-bold text-[#316B7A] mb-4 flex items-center">
            <User className="w-5 h-5 mr-2" /> Información Personal
          </h2>
          
          <div className="flex flex-col sm:flex-row items-center mb-6 border-b pb-4">
            <div className="w-20 h-20 rounded-full overflow-hidden bg-gray-200 flex items-center justify-center mr-6 mb-4 sm:mb-0">
              {/* Placeholder de imagen */}
              <img 
                src="https://placehold.co/80x80/D1E7DD/316B7A?text=Foto" 
                alt="Foto de perfil" 
                className="w-full h-full object-cover" 
              />
            </div>
            <div className="text-center sm:text-left">
              <p className="text-lg font-semibold text-gray-800">{displayUserName}</p>
              <p className="text-sm text-gray-500">{userEmail}</p>
            </div>
            <button 
              onClick={() => console.log("Cambiar foto")}
              className="ml-0 sm:ml-auto mt-4 sm:mt-0 text-[#407581] hover:text-[#316B7A] text-sm flex items-center transition-colors"
            >
              <Camera className="w-4 h-4 mr-1" /> Cambiar Foto
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <InputField 
              label="Nombre" 
              name="nombre" 
              value={profileData.nombre} 
              onChange={handleChange} 
              readOnly={!isEditingProfile} 
            />
            <InputField 
              label="Apellido" 
              name="apellido" 
              value={profileData.apellido} 
              onChange={handleChange} 
              readOnly={!isEditingProfile} 
            />
            <InputField 
              label="Teléfono" 
              name="telefono" 
              value={profileData.telefono} 
              onChange={handleChange} 
              icon={Phone} 
              readOnly={!isEditingProfile} 
            />
            <InputField 
              label="Ubicación" 
              name="ubicacion" 
              value={profileData.ubicacion} 
              onChange={handleChange} 
              icon={MapPin} 
              readOnly={!isEditingProfile} 
            />
          </div>

          <div className="flex justify-end mt-6">
            {!isEditingProfile ? (
              <button
                onClick={handleEditProfile}
                className="px-6 py-2 bg-[#FDB2A0] text-white font-semibold rounded-xl hover:bg-[#fa8c7a] transition-colors shadow-md flex items-center"
              >
                <Edit className="w-4 h-4 mr-2" /> Editar Perfil
              </button>
            ) : (
              <button
                onClick={handleSaveProfile}
                className="px-6 py-2 bg-[#407581] text-white font-semibold rounded-xl hover:bg-[#316B7A] transition-colors shadow-md flex items-center"
              >
                <Save className="w-4 h-4 mr-2" /> Guardar Cambios
              </button>
            )}
          </div>
        </section>


        {/* --- 2. Mis Preferencias de Búsqueda --- */}
        <section className="mb-10 p-6 bg-[#F8F9FA] rounded-lg shadow-inner">
          <h2 className="text-xl font-bold text-[#316B7A] mb-4 flex items-center">
            <Heart className="w-5 h-5 mr-2" /> Mis Preferencias de Búsqueda
          </h2>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
            <SelectField label="Especie" options={["Perro", "Gato", "Ambos"]} defaultValue="Perro" />
            <SelectField label="Rango de Edad" options={["Cachorro (0-1 año)", "Joven (1-5 años)", "Adulto (+5 años)"]} defaultValue="Cachorro (0-1 año)" />
            <SelectField label="Tamaño" options={["Pequeño", "Mediano", "Grande"]} defaultValue="Pequeño" />
          </div>

          <div className="mb-6">
            <label className="block text-gray-700 font-medium mb-2">Compatibilidad</label>
            <div className="flex flex-wrap gap-4">
              <Checkbox label="Apto para niños" name="ninos" defaultChecked />
              <Checkbox label="Apto con otros perros" name="otrosPerros" defaultChecked />
              <Checkbox label="Apto con gatos" name="gatos" />
            </div>
          </div>

          <div className="flex justify-end">
            <button
              onClick={() => console.log("Guardando preferencias")}
              className="px-6 py-2 bg-[#FDB2A0] text-white font-semibold rounded-xl hover:bg-[#fa8c7a] transition-colors shadow-md"
            >
              Guardar Preferencias
            </button>
          </div>
        </section>


        {/* --- 3. Configuración de la Cuenta --- */}
        <section className="p-6 bg-[#F8F9FA] rounded-lg shadow-inner">
          <h2 className="text-xl font-bold text-[#316B7A] mb-4 flex items-center">
            <Lock className="w-5 h-5 mr-2" /> Configuración de la Cuenta
          </h2>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
            <PasswordField label="Contraseña Antigua" name="currentPassword" value={passwordFields.currentPassword} onChange={handlePasswordChange} />
            <PasswordField label="Nueva Contraseña" name="newPassword" value={passwordFields.newPassword} onChange={handlePasswordChange} />
            <PasswordField label="Confirmar Nueva Contraseña" name="confirmNewPassword" value={passwordFields.confirmNewPassword} onChange={handlePasswordChange} />
          </div>

          <div className="flex justify-between items-center mt-6 pt-4 border-t border-gray-200">
            <button
              onClick={() => console.log("Eliminar cuenta")}
              className="text-red-500 hover:text-red-700 text-sm font-semibold flex items-center transition-colors"
            >
              <Trash2 className="w-4 h-4 mr-1" /> Eliminar cuenta
            </button>

            <button
              onClick={handleSavePassword}
              className="px-6 py-2 border border-[#407581] text-[#407581] font-semibold rounded-xl hover:bg-gray-100 transition-colors shadow-sm"
            >
              Cambiar Contraseña
            </button>
          </div>
        </section>

      </div>
    </div>
  );
}

// --- Componentes Reutilizables ---

const InputField = ({ label, name, value, onChange, icon: Icon, readOnly = false }) => (
  <div>
    <label htmlFor={name} className="block text-gray-700 font-medium mb-1">{label}</label>
    <div className="relative">
      {Icon && <Icon className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />}
      <input
        id={name}
        name={name}
        type="text"
        value={value}
        onChange={onChange}
        readOnly={readOnly} // Aquí se aplica el modo de solo lectura
        className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 transition-shadow 
          ${readOnly 
            ? 'bg-gray-100 border-gray-200 text-gray-600 cursor-default' 
            : 'bg-white border-gray-300 focus:ring-[#407581]'
          }
          ${Icon ? 'pl-10' : ''}`}
      />
    </div>
  </div>
);

const PasswordField = ({ label, name, value, onChange }) => (
  <div>
    <label htmlFor={name} className="block text-gray-700 font-medium mb-1">{label}</label>
    <input
      id={name}
      name={name}
      type="password"
      value={value}
      onChange={onChange}
      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#407581] transition-shadow"
    />
  </div>
);

const SelectField = ({ label, options, defaultValue }) => (
  <div>
    <label className="block text-gray-700 font-medium mb-1">{label}</label>
    <select
      defaultValue={defaultValue}
      className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-white focus:outline-none focus:ring-2 focus:ring-[#407581] transition-shadow"
    >
      {options.map(option => (
        <option key={option} value={option}>{option}</option>
      ))}
    </select>
  </div>
);

const Checkbox = ({ label, name, defaultChecked = false }) => (
  <div className="flex items-center">
    <input
      id={name}
      name={name}
      type="checkbox"
      defaultChecked={defaultChecked}
      className="h-4 w-4 text-[#407581] border-gray-300 rounded focus:ring-[#407581] transition-colors"
    />
    <label htmlFor={name} className="ml-2 text-gray-700 text-sm">{label}</label>
  </div>
);