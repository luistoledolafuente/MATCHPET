import React from "react";
import { Link } from "react-router-dom";
import { faFacebook, faInstagram, faTwitter } from '@fortawesome/free-brands-svg-icons'; 
import { useAuth } from "../../contexts/AuthContext";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPaw, faUsers,faHandHoldingHeart,faChartLine,faHeart,faDog,} from "@fortawesome/free-solid-svg-icons";

export default function DashboardHome() {
  const { user } = useAuth();
  const refugioName = user?.nombreCompleto || "Refugio Amigable";

  // Datos simulados (hasta conectar a la API real :))
  const stats = {
    mascotasActivas: 12,
    adopcionesAprobadas: 8,
    donacionesRecientes: 550.0,
  };

  return (
    <div className="bg-gradient-to-b from-[#BAE6FD] to-[#FFF7E6] min-h-screen overflow-x-hidden">
      <br />
      <br />
      <div className="text-center py-24 mb-12">
        <div className="text-center mb-6 text-[#316B7A]">
          <FontAwesomeIcon icon={faPaw} className="text-5xl" />
        </div>

        <h1 className="text-4xl md:text-5xl font-extrabold text-[#316B7A] mb-4">
          ¡Bienvenido, {refugioName}! 
        </h1>
        <p className="text-gray-700 text-lg max-w-2xl mx-auto mb-10">
          Este es tu panel de refugio. Desde aquí puedes gestionar tus mascotas,
          revisar solicitudes y seguir el impacto que generas en MatchPet 
        </p>

      
        <div className="flex justify-center gap-8 flex-wrap">
          <Link
            to="/dashboard/refugio/mascotas/nueva"
            className="px-8 py-4 bg-[#316B7A] text-white rounded-xl hover:bg-teal-800 transition-colors text-lg"
          >
            Registrar Nueva Mascota
          </Link>
          <Link
            to="/dashboard/refugio/estadisticas"
            className="px-8 py-4 bg-[#FDB2A0] text-white rounded-xl hover:bg-[#fa8c7a] transition-colors text-lg"
          >
            Ver Estadísticas
          </Link>
        </div>
      </div>

     
      <div className="relative left-1/2 right-1/2 -mx-[50vw] w-screen bg-white py-20 mb-20">
        <div className="max-w-6xl mx-auto px-6 md:px-0">
          <div className="text-center mb-16">
            <h3 className="text-2xl font-extrabold text-[#316B7A] mb-4">
              Nuestra misión en MatchPet
            </h3>
            <p className="text-gray-700 text-md max-w-2xl mx-auto">
              En MatchPet, ayudamos a refugios como{" "}
              <strong>{refugioName}</strong> a conectar animales con familias
              responsables. Juntos promovemos la adopción responsable y el
              bienestar animal.
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8 text-center">
            <FeatureCard
              icon={faDog}
              title="Gestión de Mascotas"
              text="Administra fácilmente las mascotas disponibles y mantenlas visibles para los adoptantes."
            />
            <FeatureCard
              icon={faUsers}
              title="Conecta con Adoptantes"
              text="Revisa solicitudes, aprueba adopciones y da seguimiento a familias interesadas."
            />
            <FeatureCard
              icon={faHandHoldingHeart}
              title="Recibe Apoyo"
              text="Consulta estadísticas e información sobre las donaciones que recibe tu refugio."
            />
          </div>
        </div>
      </div>

      {/* --- Sección de estadísticas (impacto visual) --- */}
      <div className="max-w-6xl mx-auto grid md:grid-cols-3 gap-8 mb-20 px-6">
        <StatCard
          icon={faDog}
          color="text-[#316B7A]"
          title="Mascotas Activas"
          value={stats.mascotasActivas}
        />
        <StatCard
          icon={faHeart}
          color="text-green-600"
          title="Adopciones Exitosas"
          value={stats.adopcionesAprobadas}
        />
        <StatCard
          icon={faChartLine}
          color="text-[#FDB2A0]"
          title="Donaciones Recientes"
          value={`$${stats.donacionesRecientes.toFixed(2)}`}
        />
      </div>

      {/* --- Galería inspiracional --- */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-8 justify-center max-w-6xl mx-auto mb-16 px-6">
        <GalleryImage src="/src/assets/images/perro_dash.jpg" alt="Mascota 1" />
        <GalleryImage src="/src/assets/images/abrazo.png" alt="Mascota 2" />
        <GalleryImage src="/src/assets/images/familia.jpg" alt="Adopción" />
        <GalleryImage src="/src/assets/images/vision.png" alt="Refugio" />
      </div>
      
      <div className="mt-16 text-center text-gray-500 text-sm mb-8 space-y-2">
        <p>
          Términos de Servicio | Política de Privacidad | Contacto
        </p>
        <div className="flex justify-center gap-6 text-gray-500">
          <a href="https://facebook.com" target="_blank" rel="noopener noreferrer">
            <FontAwesomeIcon icon={faFacebook} className="text-lg hover:text-blue-600 transition-colors" />
          </a>
          <a href="https://instagram.com" target="_blank" rel="noopener noreferrer">
            <FontAwesomeIcon icon={faInstagram} className="text-lg hover:text-pink-500 transition-colors" />
          </a>
          <a href="https://twitter.com" target="_blank" rel="noopener noreferrer">
            <FontAwesomeIcon icon={faTwitter} className="text-lg hover:text-blue-400 transition-colors" />
          </a>
        </div>
        <p>© 2025 MatchPet. Todos los derechos reservados.</p>
      </div>
    </div>
  );
}

/* --- Tarjetas de características --- */
const FeatureCard = ({ icon, title, text }) => (
  <div className="p-8 rounded-xl shadow-lg hover:shadow-xl transition-shadow bg-[#FFF7E6] flex flex-col items-center">
    <FontAwesomeIcon icon={icon} className="text-4xl mb-4 text-[#316B7A]" />
    <h3 className="text-[#316B7A] text-xl font-semibold mb-4">{title}</h3>
    <p className="text-gray-600">{text}</p>
  </div>
);

/* --- Tarjetas de estadísticas --- */
const StatCard = ({ icon, color, title, value }) => (
  <div className="bg-white rounded-xl shadow-lg p-8 text-center hover:scale-[1.02] transition-transform border-t-4 border-gray-100">
    <FontAwesomeIcon icon={icon} className={`text-3xl mb-4 ${color}`} />
    <h3 className="text-3xl font-extrabold text-gray-800">{value}</h3>
    <p className="text-gray-600 font-medium mt-2">{title}</p>
  </div>
);

/* --- Imágenes de galería --- */
const GalleryImage = ({ src, alt }) => (
  <div className="h-60 bg-gray-200 rounded-lg border">
    <img src={src} alt={alt} className="w-full h-full object-cover rounded-lg" />
  </div>
);
