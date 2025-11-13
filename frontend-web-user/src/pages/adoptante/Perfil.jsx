import React, { useState, useEffect } from "react";
import {
  User, Mail, Phone, Edit, Save, Lock, Send, RefreshCw,
  AlertCircle, CheckCircle,
} from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";

const BASE_URL = "http://localhost:8081";

const StatusMessage = ({ type, text }) => {
  if (!text) return null;
  const styles = type === "error"
    ? "bg-red-100 text-red-700 border-red-400"
    : "bg-green-100 text-green-700 border-green-400";
  const Icon = type === "error" ? AlertCircle : CheckCircle;
  return (
    <div className={`flex items-center p-3 mb-4 rounded-xl border ${styles} font-medium shadow-sm`}>
      <Icon className="w-5 h-5 mr-3" />
      <p className="text-sm">{text}</p>
    </div>
  );
};

export default function Perfil() {
  const { user, userType, logout } = useAuth();
  const [editMode, setEditMode] = useState(false);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    nombreCompleto: "",
    telefono: "",
  });

  const [forgotEmail, setForgotEmail] = useState("");
  const [resetData, setResetData] = useState({ token: "", newPassword: "" });
  const [status, setStatus] = useState(null);

  useEffect(() => {
    if (user) {
      setFormData({
        nombreCompleto: user.nombreCompleto || "",
        telefono: user.telefono || "",
      });
    }
  }, [user]);

  const handleChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSave = async () => {
    setLoading(true);
    setStatus(null);
    try {
      // Cambié user.id por user.usuario_id
      const response = await fetch(`${BASE_URL}/api/adoptantes/${user.adoptante.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("userToken")}`,
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        setStatus({ type: "success", text: "¡Perfil actualizado exitosamente!" });
        setEditMode(false);
      } else {
        const data = await response.json();
        setStatus({ type: "error", text: data.message || "Error al guardar cambios." });
      }
    } catch {
      setStatus({ type: "error", text: "Error de conexión con el servidor." });
    } finally {
      setLoading(false);
    }
  };


  const handleForgotPassword = async () => {
    setLoading(true);
    setStatus(null);
    try {
      const response = await fetch(`${BASE_URL}/api/auth/forgot-password`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: forgotEmail || user.email }),
      });
      if (response.ok) {
        setStatus({ type: "success", text: "Email enviado. Revisa tu correo para el token." });
      } else {
        setStatus({ type: "error", text: "No se pudo enviar el email. Verifica tu dirección." });
      }
    } catch {
      setStatus({ type: "error", text: "Error de conexión con el servidor." });
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async () => {
    if (resetData.newPassword.length < 6) {
      setStatus({ type: "error", text: "La contraseña debe tener al menos 6 caracteres." });
      return;
    }
    setLoading(true);
    setStatus(null);
    try {
      const response = await fetch(`${BASE_URL}/api/auth/reset-password`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(resetData),
      });
      if (response.ok) {
        setStatus({ type: "success", text: "Contraseña cambiada correctamente. 🎉" });
        setResetData({ token: "", newPassword: "" });
      } else {
        setStatus({ type: "error", text: "Token inválido o expirado." });
      }
    } catch {
      setStatus({ type: "error", text: "Error al conectar con el servidor." });
    } finally {
      setLoading(false);
    }
  };

  if (!user) return <div className="text-center p-10">Cargando perfil...</div>;

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#BAE6FD] to-[#FFF7E6] py-10 px-6 font-sans relative overflow-hidden">
      <div className="max-w-6xl mx-auto bg-white rounded-3xl shadow-2xl p-10 grid grid-cols-1 lg:grid-cols-2 gap-8 relative z-10">

        {/* 🧍 IZQUIERDA — DATOS PERSONALES */}
        <div>
          <h1 className="text-3xl font-extrabold text-[#316B7A] mb-6 flex items-center">
            <User className="w-7 h-7 mr-3 text-[#FDB2A0]" />
            Datos Personales
          </h1>

          {status && <StatusMessage type={status.type} text={status.text} />}

          <div className="space-y-5">
            <Input label="Nombre Completo" name="nombreCompleto" value={formData.nombreCompleto} onChange={handleChange} disabled={!editMode} icon={User} />
            <Input label="Correo Electrónico" value={user.email} disabled icon={Mail} />
            <Input label="Teléfono" name="telefono" value={formData.telefono} onChange={handleChange} disabled={!editMode} icon={Phone} />
          </div>

          <div className="flex justify-between items-center mt-8 border-t border-gray-200 pt-5">
            {!editMode ? (
              <button
                onClick={() => setEditMode(true)}
                className="px-6 py-2 bg-[#FDB2A0] text-white font-semibold rounded-xl hover:bg-[#fa8c7a] shadow-md flex items-center"
              >
                <Edit className="w-4 h-4 mr-2" /> Editar
              </button>
            ) : (
              <div className="flex gap-3">
                <button
                  onClick={handleSave}
                  className="px-6 py-2 bg-[#407581] text-white font-semibold rounded-xl hover:bg-[#316B7A] shadow-md flex items-center"
                >
                  <Save className="w-4 h-4 mr-2" /> Guardar
                </button>
                <button
                  onClick={() => setEditMode(false)}
                  className="px-6 py-2 border border-gray-300 rounded-xl text-gray-700 hover:bg-gray-100"
                >
                  Cancelar
                </button>
              </div>
            )}
          </div>
        </div>

        {/* 🔐 DERECHA — CONTRASEÑA + IMAGEN */}
        <div>
          <div className="flex justify-center mb-8">
            <div className="relative w-40 h-40 rounded-full bg-gradient-to-tr from-[#BAE6FD] to-[#FDB2A0] p-[3px] shadow-xl">
              <div className="w-full h-full rounded-full bg-white flex items-center justify-center overflow-hidden">
                <img
                  src="/src/assets/images/luna.png"
                  alt="Foto de perfil"
                  className="w-full h-full object-cover rounded-full transform transition-transform duration-300 hover:scale-105"
                />
              </div>
              <div className="absolute bottom-1 right-1 bg-[#407581] text-white p-2 rounded-full shadow-md hover:bg-[#316B7A] cursor-pointer transition-colors duration-300">
                <Edit className="w-4 h-4" />
              </div>
            </div>
          </div>


          <h2 className="text-2xl font-extrabold text-[#316B7A] mb-4 flex items-center">
            <Lock className="w-6 h-6 mr-3 text-[#FDB2A0]" />
            Gestión de Contraseña
          </h2>

          <div className="mb-6 p-4 bg-[#F8F9FA] rounded-xl border border-[#BAE6FD] shadow-inner">
            <h3 className="font-semibold text-[#407581] mb-2 flex items-center">
              <Send className="w-4 h-4 mr-2" /> Solicitar Token
            </h3>
            <input
              type="email"
              placeholder={`Tu email (${user.email})`}
              value={forgotEmail}
              onChange={(e) => setForgotEmail(e.target.value)}
              className="w-full mb-3 border border-gray-300 rounded-lg p-3 focus:ring-2 focus:ring-[#407581] outline-none"
            />
            <button
              onClick={handleForgotPassword}
              className="w-full bg-[#407581] hover:bg-[#316B7A] text-white font-semibold py-2 rounded-lg flex items-center justify-center"
            >
              <Send className="w-4 h-4 mr-2" /> Enviar Email
            </button>
          </div>

          <div className="p-4 bg-[#F8F9FA] rounded-xl border border-[#BAE6FD] shadow-inner">
            <h3 className="font-semibold text-[#407581] mb-2 flex items-center">
              <RefreshCw className="w-4 h-4 mr-2" /> Cambiar Contraseña
            </h3>
            <input
              type="text"
              placeholder="Token recibido"
              value={resetData.token}
              onChange={(e) => setResetData({ ...resetData, token: e.target.value })}
              className="w-full mb-3 border border-gray-300 rounded-lg p-3 focus:ring-2 focus:ring-[#407581] outline-none"
            />
            <input
              type="password"
              placeholder="Nueva contraseña"
              value={resetData.newPassword}
              onChange={(e) => setResetData({ ...resetData, newPassword: e.target.value })}
              className="w-full mb-3 border border-gray-300 rounded-lg p-3 focus:ring-2 focus:ring-[#407581] outline-none"
            />
            <button
              onClick={handleResetPassword}
              className="w-full bg-[#FDB2A0] hover:bg-[#fa8c7a] text-white font-semibold py-2 rounded-lg flex items-center justify-center"
            >
              <Lock className="w-4 h-4 mr-2" /> Cambiar
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

const Input = ({ label, name, value, onChange, disabled, icon: Icon, type = "text" }) => (
  <div>
    <label htmlFor={name} className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
    <div className="relative">
      {Icon && <Icon className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />}
      <input
        id={name}
        name={name}
        type={type}
        value={value}
        onChange={onChange}
        disabled={disabled}
        className={`w-full px-4 py-3 border rounded-xl outline-none transition-all
        ${disabled ? "bg-gray-100 border-gray-200 cursor-not-allowed" : "bg-white border-gray-300 focus:ring-2 focus:ring-[#FDB2A0]"}`}
        style={{ paddingLeft: Icon ? "2.2rem" : "1rem" }}
      />
    </div>
  </div>
);
