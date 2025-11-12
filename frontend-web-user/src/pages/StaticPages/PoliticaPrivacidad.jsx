import React from "react";

export default function PoliticaPrivacidad() {
  return (
    <div className="bg-gradient-to-br from-[#D7F0F7] via-[#79B5C0]/60 to-[#407581]/10 min-h-screen px-6 py-12">
      <section className="max-w-5xl mx-auto text-center mb-12">
        <h1 className="text-5xl font-serif italic font-extrabold text-[#407581] mb-6">
          Política de Privacidad
        </h1>
        <p className="text-lg text-[#4B4033cc]">
          En MatchPet, nos tomamos muy en serio la privacidad de nuestros usuarios. Esta política describe cómo recopilamos, utilizamos y protegemos la información personal de nuestros usuarios, tanto refugios como adoptantes, con el fin de ofrecer una experiencia segura y efectiva en la búsqueda de hogares para mascotas.
        </p>
      </section>

      <section className="max-w-5xl mx-auto space-y-10 text-[#4B4033]">
        <div>
          <h2 className="text-2xl font-bold mb-3">1. Información que recopilamos</h2>
          <p className="mb-2">
            Para brindar un servicio eficiente, recopilamos información de registro que incluye nombre, correo electrónico, teléfono y datos específicos sobre las mascotas o las preferencias de adopción. Además, podemos recopilar información técnica como la dirección IP, tipo de dispositivo y actividad dentro de la plataforma para mejorar la experiencia del usuario.
          </p>
          <p>
            Toda información proporcionada por los usuarios es voluntaria, pero esencial para que MatchPet pueda generar coincidencias precisas entre refugios y adoptantes.
          </p>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-3">2. Uso de la información</h2>
          <p className="mb-2">
            La información que recopilamos se utiliza para los siguientes fines:
          </p>
          <ul className="list-disc list-inside space-y-1">
            <li>Facilitar el proceso de adopción conectando refugios con adoptantes adecuados.</li>
            <li>Optimizar nuestro sistema de “match” mediante inteligencia artificial.</li>
            <li>Enviar notificaciones y actualizaciones relevantes sobre la plataforma.</li>
            <li>Realizar análisis internos para mejorar los servicios y la experiencia de usuario.</li>
            <li>Garantizar la seguridad y prevenir actividades fraudulentas o malintencionadas.</li>
          </ul>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-3">3. Seguridad y protección de datos</h2>
          <p className="mb-2">
            Implementamos medidas de seguridad técnicas, administrativas y físicas para proteger la información de nuestros usuarios. Esto incluye almacenamiento seguro, cifrado de datos sensibles y control de acceso para evitar usos no autorizados.
          </p>
          <p>
            Aunque hacemos todo lo posible para proteger la información, ningún sistema es completamente infalible. Por ello, recomendamos a los usuarios no compartir su contraseña ni información confidencial fuera de la plataforma.
          </p>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-3">4. Compartir información con terceros</h2>
          <p>
            MatchPet no vende ni comparte información personal de los usuarios con terceros para fines comerciales. La información solo se comparte cuando es estrictamente necesario para el funcionamiento de la plataforma, por ejemplo, entre refugios y adoptantes para concretar la adopción, o cuando la ley lo requiera.
          </p>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-3">5. Derechos de los usuarios</h2>
          <p className="mb-2">
            Los usuarios tienen derecho a acceder, rectificar o eliminar su información personal en cualquier momento. También pueden solicitar la limitación del uso de sus datos y retirar el consentimiento otorgado para el tratamiento de los mismos. Para ejercer estos derechos, pueden contactarnos mediante el correo electrónico <strong>contacto@matchpet.com</strong>.
          </p>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-3">6. Uso de cookies y tecnologías similares</h2>
          <p>
            Utilizamos cookies y tecnologías similares para mejorar la experiencia en la plataforma, analizar el comportamiento de los usuarios y personalizar contenidos. Los usuarios pueden administrar las preferencias de cookies desde su navegador.
          </p>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-3">7. Cambios en la política de privacidad</h2>
          <p>
            MatchPet se reserva el derecho de actualizar esta política de privacidad en cualquier momento. Recomendamos revisar esta página periódicamente para estar al tanto de los cambios y asegurarte de que comprendes cómo se maneja tu información.
          </p>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-3">8. Contacto</h2>
          <p>
            Si tienes dudas, inquietudes o deseas ejercer tus derechos sobre tus datos, puedes contactarnos en <strong>contacto@matchpet.com</strong>. Estamos comprometidos a responder de manera clara y rápida.
          </p>
        </div>
      </section>
    </div>
  );
}
