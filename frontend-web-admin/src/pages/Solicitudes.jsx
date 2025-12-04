import { useState } from 'react';
import { useCrud } from '../hooks/useCrud';
import { useFetchOptions } from '../hooks/useFetchOptions'; // Importante para los selects
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import DynamicForm from '../components/DynamicForm';
import { Card, Title, Badge } from '@tremor/react';
import { Plus } from 'lucide-react';

export default function Solicitudes() {
    const { data, create, update, remove, loading } = useCrud('solicitudes');
    
    // Cargar opciones para los desplegables
    // Nota: 'usuarios' devuelve todos, idealmente filtraríamos solo adoptantes, pero por ahora sirve.
    const usuariosOpts = useFetchOptions('usuarios', 'email'); 
    const animalesOpts = useFetchOptions('animales', 'nombre');
    const estadosOpts = useFetchOptions('estados_solicitud', 'nombre');

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingItem, setEditingItem] = useState(null);

    const formFields = [
        { name: 'usuario', label: 'Solicitante (Email)', type: 'select', options: usuariosOpts, required: true },
        { name: 'animal', label: 'Mascota Interesada', type: 'select', options: animalesOpts, required: true },
        { name: 'estado_solicitud', label: 'Estado', type: 'select', options: estadosOpts, required: true },
        { name: 'fecha_solicitud', label: 'Fecha Solicitud', type: 'date', required: true },
        { name: 'mensaje_adoptante', label: 'Mensaje del Adoptante', type: 'textarea', fullWidth: true },
        { name: 'mensaje_al_adoptante', label: 'Respuesta al Adoptante', type: 'textarea', fullWidth: true },
        { name: 'notas_internas', label: 'Notas Internas (Admin)', type: 'textarea', fullWidth: true },
    ];

    const handleCreate = () => { 
        setEditingItem(null); 
        setIsModalOpen(true); 
    };
    
    const handleEdit = (item) => { 
        setEditingItem(item); 
        setIsModalOpen(true); 
    };
    
    const handleSave = async (formData) => {
        // Aseguramos fecha de actualización
        formData.fecha_actualizacion = new Date().toISOString();
        if (!formData.fecha_solicitud) formData.fecha_solicitud = new Date().toISOString();

        try {
            if (editingItem) await update(editingItem.id, formData);
            else await create(formData);
            setIsModalOpen(false);
        } catch (error) {
            console.error(error);
        }
    };

    const getBadgeColor = (estado) => {
        switch(estado?.toLowerCase()) {
            case 'aprobada': return 'emerald';
            case 'rechazada': return 'rose';
            case 'pendiente': case 'enviada': return 'amber';
            default: return 'slate';
        }
    };

    const columns = [
        { key: 'id', label: 'Folio' },
        { key: 'nombre_animal', label: 'Mascota', render: (item) => (
            <span className="font-bold text-gray-700">🐶 {item.nombre_animal || '...'}</span>
        )},
        { key: 'email_usuario', label: 'Solicitante', render: (item) => (
            <div className="flex flex-col">
                <span className="text-sm">{item.email_usuario}</span>
                <span className="text-xs text-gray-400">ID: {item.usuario}</span>
            </div>
        )},
        { key: 'fecha_solicitud', label: 'Fecha', render: (item) => new Date(item.fecha_solicitud).toLocaleDateString() },
        { key: 'nombre_estado', label: 'Estado', render: (item) => (
            <Badge color={getBadgeColor(item.nombre_estado)}>
                {item.nombre_estado || 'Desconocido'}
            </Badge>
        )},
    ];

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <Title>Gestión de Solicitudes</Title>
                <button onClick={handleCreate} className="bg-indigo-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-indigo-700 transition">
                    <Plus size={18} /> Nueva Solicitud
                </button>
            </div>

            <Card>
                <DataTable data={data} columns={columns} onDelete={remove} onEdit={handleEdit} />
            </Card>

            <Modal 
                isOpen={isModalOpen} 
                onClose={() => setIsModalOpen(false)} 
                title={editingItem ? "Gestionar Solicitud" : "Crear Solicitud Manual"}
            >
                <DynamicForm 
                    fields={formFields}
                    defaultValues={editingItem}
                    onSubmit={handleSave}
                    onCancel={() => setIsModalOpen(false)}
                    isLoading={loading}
                />
            </Modal>
        </div>
    );
}