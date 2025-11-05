import React, { useState } from "react";

const faqs = [
  {
    question: "¿Qué es MatchPet?",
    answer: `MatchPet es una plataforma que conecta refugios y posibles adoptantes para facilitar la adopción responsable de mascotas mediante inteligencia artificial. Nuestro objetivo es ayudar a que los animales encuentren un hogar adecuado y las familias encuentren la mascota ideal.`
  },
  {
    question: "¿Quiénes pueden registrarse en MatchPet?",
    answer: `Pueden registrarse dos tipos de usuarios: refugios que cuidan animales y buscan familias, y adoptantes que desean encontrar una mascota compatible. Cada perfil tiene funcionalidades específicas dentro de la plataforma.`
  },
  {
    question: "¿Cómo funciona el sistema de 'match'?",
    answer: `Nuestra inteligencia artificial analiza las preferencias y características de las mascotas y adoptantes para sugerir las mejores coincidencias posibles, similar a un 'match' en aplicaciones sociales, pero enfocado en el bienestar de los animales.`
  },
  {
    question: "¿Qué información debo proporcionar al registrarme?",
    answer: `Los usuarios deben proporcionar información veraz sobre su perfil, ya sea refugio o adoptante, incluyendo datos de contacto y detalles de la mascota o preferencias de adopción. Esto garantiza coincidencias precisas y seguras.`
  },
  {
    question: "¿Cómo protege MatchPet mis datos personales?",
    answer: `Respetamos la privacidad de nuestros usuarios. Todos los datos se almacenan de manera segura y se utilizan únicamente para mejorar la experiencia dentro de la plataforma. No compartimos información con terceros sin consentimiento.`
  },
  {
    question: "¿Puedo actualizar mi información de perfil?",
    answer: `Sí. Tanto refugios como adoptantes pueden editar sus datos en cualquier momento desde la sección de perfil. Esto asegura que la información sea siempre precisa para generar mejores coincidencias.`
  },
  {
    question: "¿Puedo comunicarme directamente con un refugio o adoptante?",
    answer: `Sí. Una vez que la plataforma genere un 'match', los usuarios podrán comunicarse de manera segura a través del sistema de mensajería interna de MatchPet para coordinar la adopción o conocer más sobre la mascota.`
  },
  {
    question: "¿Cómo puedo ayudar o colaborar con MatchPet?",
    answer: `Puedes colaborar registrando tu refugio, adoptando una mascota a través de la plataforma, o haciendo donaciones y contribuciones que apoyen el mantenimiento y desarrollo de MatchPet.`
  }
];

export default function Ayuda() {
  const [openIndex, setOpenIndex] = useState(null);

  const toggleIndex = (index) => {
    setOpenIndex(openIndex === index ? null : index);
  };

  return (
    <div className="bg-gradient-to-br from-[#D7F0F7] via-[#79B5C0]/60 to-[#407581]/10 min-h-screen px-6 py-12">
      <section className="max-w-4xl mx-auto text-center mb-12">
        <h1 className="text-5xl font-serif italic font-extrabold text-[#407581] mb-6">
          Ayuda
        </h1>
        <p className="text-lg text-[#4B4033cc]">
          Respuestas a preguntas frecuentes sobre cómo usar MatchPet, registrarte, crear perfiles y encontrar la mascota ideal.
        </p>
      </section>

      <section className="max-w-4xl mx-auto space-y-4 text-[#4B4033]">
        {faqs.map((faq, index) => (
          <div key={index} className="border rounded-lg overflow-hidden bg-white shadow-md">
            <button
              onClick={() => toggleIndex(index)}
              className="w-full text-left px-5 py-3 text-lg font-semibold hover:bg-[#D7F0F7] transition flex justify-between items-center"
            >
              {faq.question}
              <span>{openIndex === index ? "−" : "+"}</span>
            </button>
            {openIndex === index && (
              <div className="px-5 py-3 border-t text-gray-700">
                {faq.answer}
              </div>
            )}
          </div>
        ))}
      </section>
    </div>
  );
}
