import { useState } from 'react';
import { useCrud } from '../hooks/useCrud';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import DynamicForm from '../components/DynamicForm';
import { Card, Title } from '@tremor/react';
import { UserPlus } from 'lucide-react';

export default function Adoptantes() {
    const { data, create, update, remove, loading } = useCrud('adoptantes');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingItem, setEditingItem] = useState(null);

    // --- FORMULARIO ALINEADO AL 100% CON TU CURL ---
    const formFields = [
        { name: 'nombre', label: 'Nombre', required: true },
        { name: 'apellido_paterno', label: 'Apellido Paterno', required: true },
        { name: 'apellido_materno', label: 'Apellido Materno', required: false }, // Agregado para coincidir
        { name: 'email', label: 'Email', type: 'email', required: true },
        { name: 'telefono', label: 'Teléfono', required: true },
        
        // La contraseña es obligatoria si estamos creando (editingItem es null)
        ...(!editingItem ? [{ 
            name: 'password', 
            label: 'Contraseña', 
            type: 'password', 
            required: true 
        }] : []),

        { name: 'fecha_nacimiento', label: 'Fecha Nacimiento', type: 'date', required: true },
        { name: 'pais', label: 'País', required: true },
        { name: 'ciudad', label: 'Ciudad', required: true },
        { name: 'direccion', label: 'Dirección', fullWidth: true },
    ];

    const handleCreate = () => { setEditingItem(null); setIsModalOpen(true); };
    const handleEdit = (item) => { setEditingItem(item); setIsModalOpen(true); };
    
    const handleSave = async (formData) => {
        try {
            if (editingItem) {
                await update(editingItem.id, formData);
            } else {
                await create(formData);
            }
            setIsModalOpen(false);
        } catch (error) {
            console.error(error);
            alert("Error al guardar. Revisa los datos.");
        }
    };

    const columns = [
        { key: 'id', label: 'ID' },
        { key: 'nombre', label: 'Nombre Completo', render: (i) => 
            <span className="font-medium">{i.nombre} {i.apellido_paterno} {i.apellido_materno || ''}</span> 
        },
        { key: 'email', label: 'Email' },
        { key: 'ciudad', label: 'Ubicación', render: (i) => `${i.ciudad || '-'}, ${i.pais || '-'}` },
        { key: 'telefono', label: 'Teléfono' },
    ];

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <Title>Directorio de Adoptantes</Title>
                <button onClick={handleCreate} className="bg-indigo-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-indigo-700 transition">
                    <UserPlus size={18} /> Nuevo Adoptante
                </button>
            </div>

            <Card>
                <DataTable data={data} columns={columns} onDelete={remove} onEdit={handleEdit} />
            </Card>

            <Modal 
                isOpen={isModalOpen} 
                onClose={() => setIsModalOpen(false)} 
                title={editingItem ? "Editar Adoptante" : "Registrar Nuevo Adoptante"}
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