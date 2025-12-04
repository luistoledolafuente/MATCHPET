import { useState } from "react";
import { useSearchParams, Link } from "react-router-dom";
import { Lock, CheckCircle, XCircle } from "lucide-react";

const BASE_URL = "http://localhost:8081";

export default function ResetPassword() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [status, setStatus] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleReset = async () => {
    if (!password || !confirm) {
      setStatus({ type: "error", text: "Completa todos los campos." });
      return;
    }

    if (password !== confirm) {
      setStatus({ type: "error", text: "Las contraseñas no coinciden." });
      return;
    }

    if (!token) {
      setStatus({ type: "error", text: "Token inválido o faltante." });
      return;
    }

    setLoading(true);
    setStatus(null);

    try {
      const response = await fetch(`${BASE_URL}/api/auth/reset-password`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token, newPassword: password }),
      });

      const result = await response.json();

      if (response.ok) {
        setStatus({
          type: "success",
          text: "Tu contraseña fue cambiada exitosamente.",
        });
      } else {
        setStatus({
          type: "error",
          text: result.message || "No se pudo cambiar la contraseña.",
        });
      }
    } catch (error) {
      setStatus({
        type: "error",
        text: "Error de conexión con el servidor.",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#dff3ff] to-[#fde8e4] flex items-center justify-center p-4">
      <div className="bg-white w-full max-w-md rounded-3xl shadow-xl p-8 border">
        <h2 className="text-2xl font-bold text-[#316B7A] mb-6 flex items-center gap-2">
          <Lock className="w-6 h-6 text-[#FDB2A0]" />
          Restablecer contraseña
        </h2>

        {!token && (
          <div className="p-4 bg-red-100 border border-red-300 rounded-lg text-red-700 mb-4 flex items-center gap-2">
            <XCircle className="w-5 h-5" />
            Token inválido. Revisa tu correo nuevamente.
          </div>
        )}

        {status && (
          <div
            className={`p-4 mb-4 rounded-lg border flex items-center gap-3 ${
              status.type === "success"
                ? "bg-green-100 text-green-700 border-green-300"
                : "bg-red-100 text-red-700 border-red-300"
            }`}
          >
            {status.type === "success" ? (
              <CheckCircle className="w-5 h-5" />
            ) : (
              <XCircle className="w-5 h-5" />
            )}
            {status.text}
          </div>
        )}

        {status?.type !== "success" && (
          <>
            <label className="block text-sm font-semibold text-gray-700 mb-1">
              Nueva contraseña
            </label>
            <input
              type="password"
              className="w-full p-3 mb-3 rounded-xl border bg-white border-gray-300 focus:ring-2 focus:ring-[#FDB2A0]"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />

            <label className="block text-sm font-semibold text-gray-700 mb-1">
              Confirmar contraseña
            </label>
            <input
              type="password"
              className="w-full p-3 mb-4 rounded-xl border bg-white border-gray-300 focus:ring-2 focus:ring-[#FDB2A0]"
              value={confirm}
              onChange={(e) => setConfirm(e.target.value)}
            />

            <button
              onClick={handleReset}
              disabled={loading || !token}
              className="w-full py-3 bg-[#407581] text-white rounded-xl shadow hover:bg-[#316B7A] disabled:opacity-50"
            >
              {loading ? (
                <span className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin inline-block"></span>
              ) : (
                "Cambiar contraseña"
              )}
            </button>
          </>
        )}

        {status?.type === "success" && (
          <div className="text-center mt-6">
            <Link
              to="/login"
              className="text-[#316B7A] font-semibold hover:underline"
            >
              Ir al login
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}
