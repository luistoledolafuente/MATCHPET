import React, { useEffect, useState } from "react";
import { User, Mail, Phone, Home, MapPin, Building, Edit, Save, Info, ShieldCheck, Key,} from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";

const BASE_URL = "http://localhost:8081";

const StatusMessage = ({ type, text }) => {
  if (!text) return null;
  const styles =
    type === "error"
      ? "bg-red-100 text-red-700 border-red-300"
      : "bg-green-100 text-green-700 border-green-300";
  return (
    <div className={`flex items-center p-4 mb-6 rounded-xl border ${styles} shadow`}>
      <Info className="w-5 h-5 mr-3" />
      <p className="text-sm font-medium">{text}</p>
    </div>
  );
};

export default function Perfil() {
  const { user, token } = useAuth();

  const [editMode, setEditMode] = useState(false);
  const [status, setStatus] = useState(null);
  const [loading, setLoading] = useState(false);
  const [passwordLoading, setPasswordLoading] = useState(false);
  const [passwordStatus, setPasswordStatus] = useState(null);

  const [formData, setFormData] = useState({
    nombre: "",
    descripcion: "",
    pais: "",
    urlSitioWeb: "",
    direccion: "",
    ciudad: "",
    email: "",
    personaContacto: "",
    telefono: "",
  });

  // Inicializa formData desde user/refugio
  useEffect(() => {
    if (!user) return;

    const r = user.refugio || {};

    setFormData({
      nombre: r.nombre ?? "",
      descripcion: r.descripcion ?? "",
      pais: r.pais ?? "",
      urlSitioWeb: r.urlSitioWeb ?? "",
      direccion: r.direccion ?? "",
      ciudad: r.ciudad ?? "",
      email: r.email ?? user.email ?? "",
      personaContacto: r.personaContacto ?? "",
      telefono: r.telefono ?? "",
    });
  }, [user]);

  const handleChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSave = async () => {
    if (!user?.refugio?.id) return;

    setLoading(true);
    setStatus(null);

    try {
      const response = await fetch(`${BASE_URL}/api/refugios/${user.refugio.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        setStatus({ type: "success", text: "Refugio actualizado exitosamente." });
        setEditMode(false);
      } else {
        const err = await response.json();
        setStatus({
          type: "error",
          text: err.message || "Error al actualizar.",
        });
      }
    } catch {
      setStatus({ type: "error", text: "Error de conexión." });
    } finally {
      setLoading(false);
    }
  };

  const sendPasswordReset = async () => {
    setPasswordLoading(true);
    setPasswordStatus(null);

    try {
      const response = await fetch(`${BASE_URL}/api/auth/forgot-password`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: formData.email }),
      });

      if (response.ok) {
        setPasswordStatus({
          type: "success",
          text: "Se ha enviado un enlace a tu correo para cambiar tu contraseña.",
        });
      } else {
        const err = await response.json();
        setPasswordStatus({
          type: "error",
          text: err.message || "No se pudo enviar el enlace.",
        });
      }
    } catch {
      setPasswordStatus({
        type: "error",
        text: "Error de conexión con el servidor.",
      });
    } finally {
      setPasswordLoading(false);
    }
  };

  if (!user) {
    return (
      <div className="p-10 text-center text-gray-600">
        Cargando perfil...
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#dff3ff] to-[#fde8e4] py-4 px-10">
      <div className="max-w-10xl mx-auto grid grid-cols-3 xl:grid-cols-3 gap-10">
        {/* PANEL INFORMATIVO */}
        <div className="col-span-1">
          <div className="bg-white/80 backdrop-blur-2xl shadow-2xl rounded-3xl pt-4 px-10 pb-10 border border-white">
            <h2 className="text-2xl font-bold text-[#316B7A] flex items-center mb-2">
              <ShieldCheck className="w-7 h-7 mr-2 text-[#FDB2A0]" />
              Información del Refugio
            </h2>
            <div className="flex justify-center mb-8">
              <div className="w-40 h-40 rounded-full overflow-hidden shadow-xl border-4 border-white bg-gray-200">
                <img
                  src="/src/assets/images/refugio_perfil.png"
                  alt="Refugio"
                  className="w-full h-full object-cover"
                />
              </div>
            </div>
            <p className="text-gray-600 text-sm leading-relaxed mb-8">
              Mantén tu información actualizada para generar confianza entre los
              adoptantes y facilitar el proceso de adopción. Los datos de
              contacto serán visibles para usuarios interesados.
            </p>
            <div className="space-y-2">
              <InfoCard label="Nombre" value={formData.nombre} />
              <InfoCard label="Descripción" value={formData.descripcion} />
              <InfoCard label="País" value={formData.pais} />
              <InfoCard label="Sitio Web" value={formData.urlSitioWeb} />
              <InfoCard label="Ciudad" value={formData.ciudad} />
              <InfoCard label="Dirección" value={formData.direccion} />
              <InfoCard label="Persona de Contacto" value={formData.personaContacto} />
              <InfoCard label="Teléfono" value={formData.telefono} />
              <InfoCard label="Email" value={formData.email} />
            </div>
          </div>
        </div>

        {/* FORMULARIO PRINCIPAL + CAMBIAR CONTRASEÑA */}
        <div className="col-span-2 bg-white/90 backdrop-blur-2xl shadow-xl rounded-3xl p-10 border border-white">
          <h2 className="text-2xl font-bold text-[#316B7A] mb-6">Información General</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            <InputField
              label="Nombre"
              name="nombre"
              icon={Home}
              disabled={!editMode}
              value={formData.nombre}
              onChange={handleChange}
            />
            <InputField
              label="Descripción"
              name="descripcion"
              icon={Info}
              disabled={!editMode}
              value={formData.descripcion}
              onChange={handleChange}
            />
            <InputField
              label="País"
              name="pais"
              icon={Building}
              disabled={!editMode}
              value={formData.pais}
              onChange={handleChange}
            />
            <InputField
              label="Sitio Web"
              name="urlSitioWeb"
              icon={Home}
              disabled={!editMode}
              value={formData.urlSitioWeb}
              onChange={handleChange}
            />
            <InputField
              label="Ciudad"
              name="ciudad"
              icon={Building}
              disabled={!editMode}
              value={formData.ciudad}
              onChange={handleChange}
            />
            <InputField
              label="Dirección"
              name="direccion"
              icon={MapPin}
              disabled={!editMode}
              value={formData.direccion}
              onChange={handleChange}
            />
            <InputField
              label="Persona de Contacto"
              name="personaContacto"
              icon={User}
              disabled={!editMode}
              value={formData.personaContacto}
              onChange={handleChange}
            />
            <InputField
              label="Teléfono"
              name="telefono"
              icon={Phone}
              disabled={!editMode}
              value={formData.telefono}
              onChange={handleChange}
            />
            <InputField
              label="Email"
              name="email"
              icon={Mail}
              disabled={!editMode}
              value={formData.email}
              onChange={handleChange}
            />
          </div>

          {/* BOTONES DE EDITAR / GUARDAR */}
<div className="flex gap-3 justify-end">
  {!editMode ? (
    <button
      onClick={() => setEditMode(true)}
      className="px-6 py-2 bg-[#FDB2A0] text-white rounded-2xl shadow-lg hover:bg-[#fa8c7a] text-sm font-semibold flex items-center gap-2"
    >
      <Edit className="w-4 h-4" />
      Editar
    </button>
  ) : (
    <>
      <button
        onClick={() => setEditMode(false)}
        className="px-4 py-2 border border-gray-300 rounded-2xl text-sm"
      >
        Cancelar
      </button>
      <button
        onClick={handleSave}
        disabled={loading}
        className="px-6 py-2 bg-[#407581] text-white rounded-2xl shadow-lg hover:bg-[#316B7A] text-sm font-semibold flex items-center gap-2 disabled:opacity-50"
      >
        <Save className="w-4 h-4" />
        Guardar
      </button>
    </>
  )}
</div>

          {/* CAMBIO DE CONTRASEÑA */}
          <div className="mt-10 bg-white/80 border border-gray-200 rounded-3xl p-6 shadow-lg">
            <h3 className="text-xl font-bold text-[#316B7A] mb-3 flex items-center gap-2">
              <Key className="w-5 h-5 text-[#FDB2A0]" />
              Cambiar contraseña
            </h3>
            <p className="text-gray-600 mb-4 text-sm">
              Te enviaremos un enlace al correo del refugio para cambiar tu contraseña.
            </p>

            {passwordStatus && (
              <StatusMessage type={passwordStatus.type} text={passwordStatus.text} />
            )}

            <div className="mb-4">
              <label className="block text-sm font-semibold text-gray-700 mb-1">
                Correo
              </label>
              <div className="relative">
                <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
                <input
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  className="w-full pl-12 pr-4 py-3 rounded-xl border bg-white border-gray-300 focus:ring-2 focus:ring-[#FDB2A0]"
                />
              </div>
            </div>

            <div className="flex justify-end mt-2">
              <button
                onClick={sendPasswordReset}
                disabled={passwordLoading}
                className="px-6 py-2 bg-[#407581] text-white rounded-xl shadow hover:bg-[#316B7A] flex items-center gap-2 disabled:opacity-50"
              >
                {passwordLoading && (
                  <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
                )}
                Enviar enlace
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

const InputField = ({ label, name, value, onChange, disabled, icon: Icon }) => (
  <div>
    <label className="block text-sm font-semibold text-gray-700 mb-1">{label}</label>
    <div className="relative">
      <Icon className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
      <input
        name={name}
        value={value}
        onChange={onChange}
        disabled={disabled}
        className={`w-full pl-12 pr-4 py-3 rounded-xl border shadow-sm text-lg ${
          disabled
            ? "bg-gray-100 border-gray-200"
            : "bg-white border-gray-300 focus:ring-2 focus:ring-[#FDB2A0]"
        }`}
      />
    </div>
  </div>
);

const InfoCard = ({ label, value }) => (
  <div className="p-2 bg-white border rounded-xl shadow-sm">
    <p className="text-xs text-gray-500">{label}</p>
    <p className="text-sm font-semibold text-gray-700 mt-0.5">{value}</p>
  </div>
);
