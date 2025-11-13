import { useEffect, useState } from "react";
import axios from "axios";

export default function NuevaMascota() {
  // --- Form State ---
  const [nombre, setNombre] = useState("");
  const [fechaNacimiento, setFechaNacimiento] = useState("");
  const [generoId, setGeneroId] = useState("");
  const [tamanoId, setTamanoId] = useState("");
  const [nivelEnergiaId, setNivelEnergiaId] = useState("");
  const [especieId, setEspecieId] = useState("");
  const [razaId, setRazaId] = useState("");
  const [temperamentosSeleccionados, setTemperamentosSeleccionados] = useState([]);
  const [descripcion, setDescripcion] = useState("");
  const [compatibleNinos, setCompatibleNinos] = useState(false);
  const [compatibleOtrasMascotas, setCompatibleOtrasMascotas] = useState(false);
  const [vacunado, setVacunado] = useState(false);
  const [esterilizado, setEsterilizado] = useState(false);
  const [estadoAdopcionId, setEstadoAdopcionId] = useState("");

  // --- Dropdown Data ---
  const [generos, setGeneros] = useState([]);
  const [tamanos, setTamanos] = useState([]);
  const [nivelesEnergia, setNivelesEnergia] = useState([]);
  const [especies, setEspecies] = useState([]);
  const [razas, setRazas] = useState([]);
  const [temperamentos, setTemperamentos] = useState([]);
  const [estadosAdopcion, setEstadosAdopcion] = useState([]);

  // --- Cargar datos de referencia ---
  useEffect(() => {
    const fetchData = async () => {
      try {
        const [
          genRes,
          tamRes,
          eneRes,
          espRes,
          razRes,
          tempRes,
          estRes,
        ] = await Promise.all([
          axios.get("/api/generos"),
          axios.get("/api/tamanos"),
          axios.get("/api/niveles-energia"),
          axios.get("/api/especies"),
          axios.get("/api/razas"),
          axios.get("/api/temperamentos"),
          axios.get("/api/estados-adopcion"),
        ]);

        setGeneros(genRes.data);
        setTamanos(tamRes.data);
        setNivelesEnergia(eneRes.data);
        setEspecies(espRes.data);
        setRazas(razRes.data);
        setTemperamentos(tempRes.data);
        setEstadosAdopcion(estRes.data);
      } catch (error) {
        console.error("Error al cargar datos de referencia", error);
      }
    };

    fetchData();
  }, []);

  // --- Manejar selección de temperamentos ---
  const toggleTemperamento = (id) => {
    if (temperamentosSeleccionados.includes(id)) {
      setTemperamentosSeleccionados(
        temperamentosSeleccionados.filter((tid) => tid !== id)
      );
    } else {
      setTemperamentosSeleccionados([...temperamentosSeleccionados, id]);
    }
  };

  // --- Submit ---
  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const payload = {
        nombre,
        fecha_nacimiento_aprox: fechaNacimiento,
        genero_id: generoId,
        tamano_id: tamanoId,
        nivel_energia_id: nivelEnergiaId,
        raza_id: razaId,
        descripcion_personalidad: descripcion,
        compatible_niños: compatibleNinos,
        compatible_otras_mascotas: compatibleOtrasMascotas,
        esta_vacunado: vacunado,
        esta_esterilizado: esterilizado,
        estado_adopcion_id: estadoAdopcionId,
        temperamentos: temperamentosSeleccionados,
      };

      const res = await axios.post("/api/animales", payload);
      alert("Mascota registrada con éxito!");
      // Limpiar formulario o redirigir
    } catch (error) {
      console.error("Error al registrar mascota", error);
      alert("Ocurrió un error al registrar la mascota");
    }
  };

  // --- Filtrar razas según especie ---
  const razasFiltradas = razas.filter((r) => r.especie_id === parseInt(especieId));

  return (
    <div className="p-8 max-w-3xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">Registrar Nueva Mascota</h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          type="text"
          placeholder="Nombre"
          value={nombre}
          onChange={(e) => setNombre(e.target.value)}
          className="w-full border p-2 rounded"
          required
        />

        <input
          type="date"
          placeholder="Fecha Nacimiento Aproximada"
          value={fechaNacimiento}
          onChange={(e) => setFechaNacimiento(e.target.value)}
          className="w-full border p-2 rounded"
        />

        <select
          value={generoId}
          onChange={(e) => setGeneroId(e.target.value)}
          required
          className="w-full border p-2 rounded"
        >
          <option value="">Selecciona Género</option>
          {generos.map((g) => (
            <option key={g.genero_id} value={g.genero_id}>
              {g.nombre}
            </option>
          ))}
        </select>

        <select
          value={tamanoId}
          onChange={(e) => setTamanoId(e.target.value)}
          className="w-full border p-2 rounded"
        >
          <option value="">Selecciona Tamaño</option>
          {tamanos.map((t) => (
            <option key={t.tamano_id} value={t.tamano_id}>
              {t.nombre}
            </option>
          ))}
        </select>

        <select
          value={nivelEnergiaId}
          onChange={(e) => setNivelEnergiaId(e.target.value)}
          className="w-full border p-2 rounded"
        >
          <option value="">Selecciona Nivel de Energía</option>
          {nivelesEnergia.map((n) => (
            <option key={n.nivel_energia_id} value={n.nivel_energia_id}>
              {n.nombre}
            </option>
          ))}
        </select>

        <select
          value={especieId}
          onChange={(e) => setEspecieId(e.target.value)}
          required
          className="w-full border p-2 rounded"
        >
          <option value="">Selecciona Especie</option>
          {especies.map((e) => (
            <option key={e.especie_id} value={e.especie_id}>
              {e.nombre_especie}
            </option>
          ))}
        </select>

        <select
          value={razaId}
          onChange={(e) => setRazaId(e.target.value)}
          required
          className="w-full border p-2 rounded"
        >
          <option value="">Selecciona Raza</option>
          {razasFiltradas.map((r) => (
            <option key={r.raza_id} value={r.raza_id}>
              {r.nombre_raza}
            </option>
          ))}
        </select>

        <textarea
          placeholder="Descripción de la personalidad"
          value={descripcion}
          onChange={(e) => setDescripcion(e.target.value)}
          className="w-full border p-2 rounded"
        />

        <div>
          <p className="font-semibold mb-1">Temperamentos:</p>
          <div className="flex flex-wrap gap-2">
            {temperamentos.map((t) => (
              <label key={t.temperamento_id} className="flex items-center gap-1">
                <input
                  type="checkbox"
                  checked={temperamentosSeleccionados.includes(t.temperamento_id)}
                  onChange={() => toggleTemperamento(t.temperamento_id)}
                />
                {t.nombre_temperamento}
              </label>
            ))}
          </div>
        </div>

        <div className="flex gap-4">
          <label>
            <input
              type="checkbox"
              checked={compatibleNinos}
              onChange={(e) => setCompatibleNinos(e.target.checked)}
            />{" "}
            Compatible con niños
          </label>
          <label>
            <input
              type="checkbox"
              checked={compatibleOtrasMascotas}
              onChange={(e) => setCompatibleOtrasMascotas(e.target.checked)}
            />{" "}
            Compatible con otras mascotas
          </label>
        </div>

        <div className="flex gap-4">
          <label>
            <input
              type="checkbox"
              checked={vacunado}
              onChange={(e) => setVacunado(e.target.checked)}
            />{" "}
            Está vacunado
          </label>
          <label>
            <input
              type="checkbox"
              checked={esterilizado}
              onChange={(e) => setEsterilizado(e.target.checked)}
            />{" "}
            Está esterilizado
          </label>
        </div>

        <select
          value={estadoAdopcionId}
          onChange={(e) => setEstadoAdopcionId(e.target.value)}
          required
          className="w-full border p-2 rounded"
        >
          <option value="">Selecciona Estado de Adopción</option>
          {estadosAdopcion.map((e) => (
            <option key={e.estado_adopcion_id} value={e.estado_adopcion_id}>
              {e.nombre}
            </option>
          ))}
        </select>

        <button
          type="submit"
          className="px-6 py-3 bg-teal-600 text-white rounded hover:bg-teal-700 transition"
        >
          Registrar Mascota
        </button>
      </form>
    </div>
  );
}
