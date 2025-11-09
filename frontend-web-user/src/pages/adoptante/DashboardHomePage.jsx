import React from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

// FontAwesome
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPaw } from '@fortawesome/free-solid-svg-icons';
import { faFacebook, faInstagram, faTwitter } from '@fortawesome/free-brands-svg-icons'; 
import { faRobot } from '@fortawesome/free-solid-svg-icons';
import { faHouse } from '@fortawesome/free-solid-svg-icons';
import { faHeart } from '@fortawesome/free-solid-svg-icons';
export default function DashboardHome() {
  const { user } = useAuth(); 

  return (
    <div className="bg-gradient-to-b from-[#BAE6FD] to-[#FFF7E6] min-h-screen overflow-x-hidden">

      <br />
      <br />
      <div className="text-center py-28 mb-8">
        <div className="text-center mb-6 text-[#316B7A]">
          <FontAwesomeIcon icon={faPaw} className="text-4xl" />
        </div>
        <h1 className="text-4xl md:text-5xl font-extrabold text-[#316B7A] mb-8">
          Encuentra a tu compañero ideal
        </h1>
        <p className="text-gray-700 text-lg max-w-2xl mx-auto mb-12">
          Conecta con refugios, conoce mascotas increíbles y encuentra a tu mejor amigo.
        </p>
       
        <div className="flex justify-center gap-8 flex-wrap">
          <Link
            to="/dashboard/mascotas"
            className="px-8 py-4 bg-[#316B7A] text-white rounded-xl hover:bg-teal-800 transition-colors text-lg" >
            Ver Mascotas
          </Link>
          <Link
            to="/dashboard/refugios"
            className="px-8 py-4 bg-[#FDB2A0] text-white rounded-xl hover:bg-[#fa8c7a] transition-colors text-lg">
            Ver Refugios
          </Link>
        </div>
      </div>

  
      <div className="relative left-1/2 right-1/2 -mx-[50vw] w-screen bg-white py-20 mb-20">
        <div className="max-w-7xl mx-auto px-4 md:px-0">
          <div className="text-center mb-16">
            <h3 className="text-2xl font-extrabold text-[#316B7A] mb-4">
              Bienvenido a MatchPet{user?.nombreCompleto ? `, ${user.nombreCompleto}` : ""} !!
            </h3>
            <p className="text-gray-700 text-md max-w-2xl mx-auto">
              Encuentra a tu compañero ideal. Nuestra misión es conectar a las mascotas necesitadas con hogares amorosos a través de la tecnología.
              Creemos que cada mascota merece una segunda oportunidad y que cada persona merece un compañero leal.
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8 text-center">
            <div className="p-8 rounded-xl shadow-lg hover:shadow-xl transition-shadow bg-[#FFF7E6] flex flex-col items-center">
              <FontAwesomeIcon icon={faRobot} className="text-4xl mb-4 text-[#316B7A]" />
              <h3 className="text-[#316B7A] text-xl font-semibold mb-4">Búsqueda Inteligente</h3>
              <p className="text-gray-600">
                Nuestro sistema de recomendación por IA te ayuda a encontrar la mascota perfecta para tu estilo de vida.
              </p>
            </div>

            <div className="p-8 rounded-xl shadow-lg hover:shadow-xl transition-shadow bg-[#FFF7E6] flex flex-col items-center">
              <FontAwesomeIcon icon={faHouse} className="text-4xl mb-4 text-[#316B7A]" />
              <h3 className="text-[#316B7A] text-xl font-semibold mb-4">Refugios Verificados</h3>
              <p className="text-gray-600">
                Trabajamos con refugios de confianza para garantizar el bienestar de todas las mascotas.
              </p>
            </div>

            <div className="p-8 rounded-xl shadow-lg hover:shadow-xl transition-shadow bg-[#FFF7E6] flex flex-col items-center">
              <FontAwesomeIcon icon={faHeart} className="text-4xl mb-4 text-[#316B7A]" />
              <h3 className="text-[#316B7A] text-xl font-semibold mb-4">Adopción Responsable</h3>
              <p className="text-gray-600">
                Promovemos la adopción responsable para asegurar que cada mascota encuentre un hogar para siempre.
              </p>
            </div>
          </div>

        </div>
      </div>

      
      <div className="grid grid-cols-2 md:grid-cols-4 gap-8 justify-center max-w-7xl mx-auto mb-16 px-4">
        <div className="h-60 bg-gray-200 rounded-lg border border-gray">
          <img src="/src/assets/images/abrazo.png" alt="Imagen 1" className="w-full h-full object-cover rounded-lg" />
        </div>
        <div className="h-60 bg-gray-200 rounded-lg border border-gray">
          <img src="/src/assets/images/cat1.webp" alt="Imagen 2" className="w-full h-full object-cover rounded-lg" />
        </div>
        <div className="h-60 bg-gray-200 rounded-lg border border-gray">
          <img src="/src/assets/images/familia.jpg" alt="Imagen 3" className="w-full h-full object-cover rounded-lg" />
        </div>
        <div className="h-60 bg-gray-200 rounded-lg border border-gray">
          <img src="/src/assets/images/perro_dash.jpg" alt="Imagen 4" className="w-full h-full object-cover rounded-lg" />
        </div>
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
