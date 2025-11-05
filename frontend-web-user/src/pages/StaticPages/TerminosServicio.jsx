import React from "react";

export default function TerminosServicio() {
  return (
    <div className="bg-gradient-to-br from-[#D7F0F7] via-[#79B5C0]/60 to-[#407581]/10 min-h-screen px-6 py-12">
      {/* Título principal */}
      <section className="max-w-4xl mx-auto text-center mb-12">
        <h1 className="text-5xl font-serif italic font-extrabold text-[#407581] mb-6">
          Términos y Condiciones
        </h1>
        <p className="text-lg text-[#4B4033cc]">
          Al usar MatchPet, aceptas estos Términos y Condiciones. Nuestra plataforma conecta refugios y adoptantes para promover la adopción responsable de mascotas mediante inteligencia artificial, ayudando a que las mascotas encuentren el hogar ideal y las familias encuentren la mascota adecuada.
        </p>
      </section>

      {/* Contenido principal */}
      <section className="max-w-4xl mx-auto space-y-8 text-[#4B4033] leading-relaxed">
        <div>
          <h2 className="text-2xl font-bold mb-2">1. Registro del Usuario</h2>
          <p>
            Para usar MatchPet, los usuarios deben registrarse proporcionando información veraz sobre su perfil, ya sea como refugio o adoptante. Los refugios pueden listar mascotas disponibles y gestionar información relevante de cada una. Los adoptantes pueden indicar sus preferencias para recibir sugerencias de mascotas compatibles mediante nuestro sistema de inteligencia artificial.
          </p>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-2">2. Contraseña y Seguridad</h2>
          <p>
            Una vez registrado, el usuario dispondrá de un nombre de usuario y contraseña que le permitirá el acceso seguro y personalizado. Es responsabilidad del usuario mantener la confidencialidad de sus credenciales. MatchPet no se hace responsable de accesos indebidos si las credenciales se comparten con terceros.
          </p>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-2">3. Uso de la plataforma</h2>
          <p>
            La plataforma está diseñada para facilitar la conexión entre refugios y adoptantes. Los usuarios se comprometen a utilizar la plataforma de manera responsable, sin publicar información falsa ni intentar engañar a otros usuarios. MatchPet se reserva el derecho de suspender cuentas que incumplan estas normas.
          </p>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-2">4. Derechos del Usuario</h2>
          <p>
            Los usuarios tienen derecho a la protección de sus datos personales y a la transparencia en el uso de la plataforma. Pueden solicitar la actualización, rectificación o eliminación de su información conforme a la legislación vigente sobre protección de datos.
          </p>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-2">5. Donaciones y pagos</h2>
          <p>
            MatchPet ofrece la posibilidad de realizar donaciones para apoyar a los refugios y el mantenimiento de la plataforma. Los pagos se realizan de manera segura mediante pasarela de pago confiable. La información de tarjetas nunca es almacenada por MatchPet. Al realizar una donación, aceptas que esta se utilizará exclusivamente para los fines mencionados.
          </p>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-2">6. Propiedad de contenido</h2>
          <p>
            Todo el contenido publicado en la plataforma, incluyendo textos, imágenes y datos de mascotas, es propiedad de MatchPet o de los usuarios que lo proporcionan. Está prohibida la reproducción sin autorización expresa.
          </p>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-2">7. Responsabilidad</h2>
          <p>
            MatchPet actúa como intermediario para conectar refugios y adoptantes, pero no se hace responsable de decisiones de adopción individuales ni de las acciones de los usuarios fuera de la plataforma. Se recomienda verificar toda la información y actuar de manera responsable.
          </p>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-2">8. Modificaciones de los Términos</h2>
          <p>
            MatchPet se reserva el derecho de actualizar o modificar estos Términos y Condiciones en cualquier momento y sin previo aviso. Se recomienda a los usuarios revisar periódicamente esta sección.
          </p>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-2">9. Contacto</h2>
          <p>
            Para cualquier consulta sobre los Términos y Condiciones, puedes escribirnos a <strong>contacto@matchpet.com</strong>. Nuestro equipo estará encantado de resolver tus dudas.
          </p>
        </div>
      </section>
    </div>
  );
}
