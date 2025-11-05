import React, { useState } from "react";
import { motion } from "framer-motion";

import MisionImg from "../assets/images/mision.png";
import VisionImg from "../assets/images/vision.png";
import ValoresImg from "../assets/images/valores.png";
import FilosofiaImg from "../assets/images/filosofia.png";

import FirulaisImg from "../assets/images/firulais.png";
import LunaImg from "../assets/images/luna.png";
import NinaImg from "../assets/images/nina.png";
import MaxImg from "../assets/images/max.png";
import RockyImg from "../assets/images/rocky.png";

const sections = [
  {
    title: "Misión",
    text: "En MatchPet nos dedicamos a facilitar la adopción responsable y el bienestar animal, conectando mascotas con familias que puedan brindarles amor, cuidado y un hogar seguro. Queremos que cada mascota encuentre su lugar perfecto y que cada familia descubra un compañero leal y feliz.",
    image: MisionImg,
  },
  {
    title: "Visión",
    text: "Aspiramos a ser la plataforma líder en adopción responsable, creando conciencia sobre el bienestar animal y fortaleciendo a los refugios. Buscamos que más animales encuentren hogares amorosos y que las familias estén bien informadas para tomar decisiones responsables.",
    image: VisionImg,
  },
  {
    title: "Valores",
    text: "Compasión, transparencia y compromiso son los pilares de MatchPet. Educamos sobre adopción responsable, apoyamos a refugios y protegemos a animales vulnerables, fomentando una comunidad consciente, empática y solidaria.",
    image: ValoresImg,
  },
  {
    title: "Filosofía",
    text: "Creemos que cada mascota merece un hogar lleno de amor, seguridad y respeto. Nuestra filosofía se basa en conectar corazones, educar a adoptantes y brindar apoyo continuo a refugios, transformando vidas y fomentando una cultura de cuidado y responsabilidad hacia los animales.",
    image: FilosofiaImg,
  },
];

const rescuedPets = [
  { id: 1, name: "Firulais", image: FirulaisImg },
  { id: 2, name: "Luna", image: LunaImg },
  { id: 3, name: "Nina", image: NinaImg },
  { id: 4, name: "Max", image: MaxImg },
  { id: 5, name: "Rocky", image: RockyImg },
];

export default function NosotrosPage() {
  const [centerIndex, setCenterIndex] = useState(2);

  const handlePrev = () =>
    setCenterIndex((prev) => (prev - 1 + rescuedPets.length) % rescuedPets.length);
  const handleNext = () =>
    setCenterIndex((prev) => (prev + 1) % rescuedPets.length);

  return (
    <div className="bg-gradient-to-br from-[#D7F0F7] via-[#79B5C0]/60 to-[#407581]/10 min-h-screen px-6 py-12">
      {/* Sección principal */}
      <motion.section
        initial={{ opacity: 0, y: -40 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
        className="relative mb-24 text-center"
      >
        <h1 className="text-6xl font-extrabold tracking-tight mb-6 text-[#407581]">
          Nosotros
        </h1>
        <p className="max-w-xl mx-auto text-lg md:text-xl font-light text-[#4B4033cc]">
          Conectamos corazones y patas, trabajando para que cada mascota encuentre un hogar seguro y lleno de amor. Apoyamos a refugios y educamos a la comunidad sobre la adopción responsable, haciendo que cada adopción sea un acto consciente y lleno de cariño.
        </p>
      </motion.section>

      {/* Sección Misión, Visión, Valores y Filosofía */}
      <section className="flex flex-wrap gap-10 justify-center mb-28">
        {sections.map((sec) => (
          <motion.div
            key={sec.title}
            className="w-full sm:w-[45%] bg-white rounded-3xl shadow-lg overflow-hidden flex flex-col md:flex-row-reverse"
            initial={{ opacity: 0, y: 40 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.8 }}
          >
            <div className="w-full md:w-1/2 h-64 md:h-auto overflow-hidden rounded-t-3xl md:rounded-t-none md:rounded-r-3xl">
              <img
                src={sec.image}
                alt={sec.title}
                className="w-full h-full object-cover"
              />
            </div>
            <div className="w-full md:w-1/2 p-6 flex flex-col justify-center text-left">
              <h2 className="text-3xl font-bold mb-4 text-[#407581]">{sec.title}</h2>
              <p className="text-[#4B4033] leading-relaxed">{sec.text}</p>
            </div>
          </motion.div>
        ))}
      </section>  
      
      {/* Sección Mascotas Rescatadas */}
      <section className="max-w-6xl mx-auto my-20">
        <h2 className="text-4xl font-bold text-center text-[#407581] mb-12">
          Mascotas Rescatadas
        </h2>
        <div className="relative flex items-center justify-center">
          <button
            onClick={handlePrev}
            className="absolute left-0 z-20 bg-white p-3 rounded-full shadow-lg hover:bg-[#407581] hover:text-white transition-colors"
          >
            ‹
          </button>
          <div className="flex overflow-hidden w-full justify-center items-center gap-6 relative h-96">
            {rescuedPets.map((pet, i) => {
              const offset = i - centerIndex;
              const scale = offset === 0 ? 1 : 0.75;
              const zIndex = -Math.abs(offset) + 10;
              const x = offset * 200;
              return (
                <motion.div
                  key={pet.id}
                  animate={{ scale, x }}
                  transition={{ type: "spring", stiffness: 300, damping: 30 }}
                  className="absolute cursor-pointer rounded-3xl overflow-hidden shadow-lg w-80 h-96"
                  style={{ zIndex }}
                >
                  <img
                    src={pet.image}
                    alt={pet.name}
                    className="w-full h-full object-cover"
                  />
                  <div className="absolute bottom-0 w-full bg-black bg-opacity-40 text-white text-center py-2 text-lg font-semibold">
                    {pet.name}
                  </div>
                </motion.div>
              );
            })}
          </div>
          <button
            onClick={handleNext}
            className="absolute right-0 z-20 bg-white p-3 rounded-full shadow-lg hover:bg-[#407581] hover:text-white transition-colors"
          >
            ›
          </button>
        </div>
        <p className="mt-6 text-center text-[#4B4033] text-lg max-w-2xl mx-auto">
          Estas son algunas de las mascotas que hemos rescatado y ayudado a encontrar hogares llenos de amor gracias a nuestra comunidad de adoptantes y donantes.
        </p>
      </section>
    </div>
  );
}
