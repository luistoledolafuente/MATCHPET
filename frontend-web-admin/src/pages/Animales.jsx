import { useState } from 'react';
import { useCrud } from '../hooks/useCrud';
import { useFetchOptions } from '../hooks/useFetchOptions'; // Importar hook nuevo
import DataTable from '../components/DataTable';
import Modal from '../components/Modal'; // Importar Modal
import DynamicForm from '../components/DynamicForm'; // Importar Formulario
import { Card, Title } from '@tremor/react';
import { Plus } from 'lucide-react';

export default function Animales() {
    const { data, create, update, remove, loading } = useCrud('animales');
    
    // --- ESTADOS PARA EL MODAL ---
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingItem, setEditingItem] = useState(null);

    // --- CARGAR OPCIONES PARA LOS SELECTS ---
    const refugiosOpts = useFetchOptions('refugios');
    const razasOpts = useFetchOptions('razas');
    const estadosOpts = useFetchOptions('estados_adopcion'); // Asegúrate que este endpoint exista o usa hardcode
    
    // Opciones hardcodeadas para cosas simples
    const generoOpts = [{ value: 1, label: 'Macho' }, { value: 2, label: 'Hembra' }]; 
    // Nota: Si en tu BD usas IDs para genero, usa los IDs correctos. Si usas strings 'Macho', cambia los values.

    // --- CONFIGURACIÓN DE CAMPOS DEL FORMULARIO ---
    const formFields = [
        { name: 'nombre', label: 'Nombre del Animal', required: true },
        { name: 'refugio', label: 'Refugio', type: 'select', options: refugiosOpts, required: true },
        { name: 'raza', label: 'Raza', type: 'select', options: razasOpts, required: true },
        { name: 'genero', label: 'Género', type: 'select', options: generoOpts, required: true },
        { name: 'fecha_nacimiento_aprox', label: 'Fecha Nac. Aprox', type: 'date', required: true },
        { name: 'fecha_ingreso_refugio', label: 'Fecha Ingreso', type: 'date', required: true },
        { name: 'estado_adopcion', label: 'Estado', type: 'select', options: estadosOpts, required: true },
        { name: 'compatible_ninos', label: 'Compatible con Niños', type: 'checkbox' },
        { name: 'compatible_otras_mascotas', label: 'Compatible con Mascotas', type: 'checkbox' },
        { name: 'descripcion_personalidad', label: 'Personalidad', type: 'textarea', fullWidth: true },
        { name: 'historial_medico', label: 'Historial Médico', type: 'textarea', fullWidth: true },
    ];

    // --- MANEJADORES ---
    const handleCreate = () => {
        setEditingItem(null); // Limpiamos para crear
        setIsModalOpen(true);
    };

    const handleEdit = (item) => {
        setEditingItem(item); // Pasamos el item a editar
        setIsModalOpen(true);
    };

    const handleSave = async (formData) => {
        try {
            if (editingItem) {
                await update(editingItem.id, formData);
            } else {
                await create(formData);
            }
            setIsModalOpen(false); // Cerrar modal al terminar
        } catch (error) {
            alert('Error al guardar. Revisa la consola.');
        }
    };

    // --- COLUMNAS TABLA ---
    const columns = [
        { key: 'id', label: 'ID' },
        { key: 'nombre', label: 'Nombre' },
        { key: 'nombre_raza', label: 'Raza' },     
        { key: 'nombre_refugio', label: 'Refugio' }, 
        { key: 'nombre_estado', label: 'Estado' },
        { key: 'compatible_ninos', label: 'Niños', render: (i) => i.compatible_ninos ? '✅' : '❌' },
    ];

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <Title>Gestión de Animales</Title>
                <button 
                    onClick={handleCreate} 
                    className="bg-indigo-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-indigo-700 transition"
                >
                    <Plus size={18} /> Nuevo Animal
                </button>
            </div>

            <Card>
                <DataTable 
                    data={data} 
                    columns={columns} 
                    onDelete={remove}
                    onEdit={handleEdit} // ¡Ahora sí conectamos la edición!
                />
            </Card>

            {/* --- EL MODAL CON EL FORMULARIO --- */}
            <Modal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                title={editingItem ? "Editar Animal" : "Registrar Nuevo Animal"}
            >
                <DynamicForm 
                    fields={formFields}
                    defaultValues={editingItem} // Pasa los datos actuales si estamos editando
                    onSubmit={handleSave}
                    onCancel={() => setIsModalOpen(false)}
                    isLoading={loading}
                />
            </Modal>
        </div>
    );
}