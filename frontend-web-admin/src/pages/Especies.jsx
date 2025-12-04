import { useState } from 'react';
import { useCrud } from '../hooks/useCrud';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import DynamicForm from '../components/DynamicForm';
import { Card, Title } from '@tremor/react';
import { Plus } from 'lucide-react';

export default function Especies() {
    const { data, create, update, remove, loading } = useCrud('especies');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingItem, setEditingItem] = useState(null);

    // CORRECCIÓN: Usamos 'nombre' porque así lo devuelve la API
    const formFields = [
        { name: 'nombre', label: 'Nombre de la Especie', required: true, fullWidth: true },
    ];

    const handleCreate = () => { setEditingItem(null); setIsModalOpen(true); };
    const handleEdit = (item) => { setEditingItem(item); setIsModalOpen(true); };
    
    const handleSave = async (formData) => {
        try {
            if (editingItem) await update(editingItem.id, formData);
            else await create(formData);
            setIsModalOpen(false);
        } catch (error) {
            console.error(error);
        }
    };

    const columns = [
        { key: 'id', label: 'ID' },
        // CORRECCIÓN: La llave debe ser 'nombre'
        { key: 'nombre', label: 'Especie' },
    ];

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <Title>Catálogo: Especies</Title>
                <button onClick={handleCreate} className="bg-indigo-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-indigo-700 transition">
                    <Plus size={18} /> Nueva Especie
                </button>
            </div>
            <Card>
                <DataTable data={data} columns={columns} onDelete={remove} onEdit={handleEdit} />
            </Card>
            <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={editingItem ? "Editar" : "Crear"}>
                <DynamicForm fields={formFields} defaultValues={editingItem} onSubmit={handleSave} onCancel={() => setIsModalOpen(false)} isLoading={loading} />
            </Modal>
        </div>
    );
}