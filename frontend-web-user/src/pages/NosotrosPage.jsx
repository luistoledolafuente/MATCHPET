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

const fadeIn = {
  hidden: { opacity: 0, x: -50 },
  visible: { opacity: 1, x: 0, transition: { duration: 0.8 } },
};

const fadeInRight = {
  hidden: { opacity: 0, x: 50 },
  visible: { opacity: 1, x: 0, transition: { duration: 0.8 } },
};

const sections = [
  {
    title: "Misión",
    text: "Facilitar la adopción responsable y el bienestar animal, conectando mascotas con hogares llenos de amor y cuidado. Queremos que cada animal encuentre su lugar perfecto y que cada familia descubra un compañero leal.",
    image: MisionImg,
  },
  {
    title: "Visión",
    text: "Ser la plataforma líder que inspire compromiso, cariño y responsabilidad hacia todas las mascotas y sus familias. Aspiramos a que los refugios tengan más apoyo y espacio para cuidar a quienes más lo necesitan.",
    image: VisionImg,
  },
  {
    title: "Valores",
    text: "Compasión, transparencia y compromiso son la base de cada acción que realizamos. Creemos en educar sobre adopción responsable, en proteger a los animales vulnerables y en construir una comunidad consciente y amorosa.",
    image: ValoresImg,
  },
  {
    title: "Filosofía",
    text: "En MatchPet creemos que cada animal merece amor, seguridad y un hogar que lo valore. Nuestra filosofía se basa en conectar corazones, educar a adoptantes y apoyar a refugios para que juntos podamos cambiar vidas, una mascota a la vez.",
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
          Conectamos corazones y patas, trabajando por el bienestar de cada mascota y apoyando a refugios para que puedan ayudar a más animales.
        </p>
      </motion.section>

      <section className="flex flex-wrap gap-10 justify-center mb-28">
        {sections.map((sec, i) => (
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
          Estas son algunas de las mascotas que hemos rescatado y ayudado a encontrar un hogar lleno de amor.
        </p>
      </section>
    </div>
  );
}
