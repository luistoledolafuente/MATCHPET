import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext.jsx";


export default function OAuth2RedirectHandler() {
  const { setToken, loading, userType } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const accessToken = params.get("accessToken");
    const refreshToken = params.get("refreshToken");

    console.log("🔹 Query params recibidos:", accessToken, refreshToken);

    if (accessToken) {
      // ⚡ setea el token en el context y recarga perfil
      setToken({ accessToken });
    }
  }, [setToken]);

  useEffect(() => {
    // cuando el perfil ya esté cargado, navega al dashboard correcto
    if (!loading && userType) {
      navigate(`/dashboard/${userType}`, { replace: true });
    }
  }, [loading, userType, navigate]);

  return <p>Cargando...</p>;
}

