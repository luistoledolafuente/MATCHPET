import { useState } from 'react';
import { useCrud } from '../hooks/useCrud';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import DynamicForm from '../components/DynamicForm';
import { Card, Title } from '@tremor/react';
import { Plus, Tag } from 'lucide-react';

// Recibimos 'endpoint', 'title' y 'nameField' (porque unos se llaman 'nombre', otros 'nombre_rol', etc.)
export default function GenericCatalog({ endpoint, title, nameField = 'nombre', label = 'Nombre' }) {
    const { data, create, update, remove, loading } = useCrud(endpoint);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingItem, setEditingItem] = useState(null);

    const formFields = [
        { name: nameField, label: label, required: true, fullWidth: true },
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
        { key: nameField, label: label, render: (item) => <span className="font-medium capitalize">{item[nameField]}</span> },
    ];

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <Title>{title}</Title>
                <button onClick={handleCreate} className="bg-indigo-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-indigo-700 transition">
                    <Plus size={18} /> Nuevo
                </button>
            </div>

            <Card>
                <DataTable data={data} columns={columns} onDelete={remove} onEdit={handleEdit} />
            </Card>

            <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={editingItem ? "Editar Elemento" : "Crear Elemento"}>
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