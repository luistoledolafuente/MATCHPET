import { useState } from 'react';
import { useCrud } from '../hooks/useCrud';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import DynamicForm from '../components/DynamicForm';
import { Card, Title } from '@tremor/react';
import { HeartHandshake } from 'lucide-react';

export default function Donantes() {
    const { data, create, update, remove, loading } = useCrud('donantes');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingItem, setEditingItem] = useState(null);

    // SQL dice: nombre_completo, email, fecha_creacion (auto)
    const formFields = [
        { name: 'nombre_completo', label: 'Nombre Completo', required: true, fullWidth: true },
        { name: 'email', label: 'Email', type: 'email', required: true, fullWidth: true },
        // 'fecha_creacion' se genera sola en backend
    ];

    const handleCreate = () => { setEditingItem(null); setIsModalOpen(true); };
    const handleEdit = (item) => { setEditingItem(item); setIsModalOpen(true); };
    
    const handleSave = async (formData) => {
        // Agregamos la fecha actual si es nuevo registro, aunque el backend suele hacerlo
        if (!editingItem) {
            formData.fecha_creacion = new Date().toISOString();
        }
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
        { key: 'nombre_completo', label: 'Donante' },
        { key: 'email', label: 'Email' },
        { key: 'fecha_creacion', label: 'Registrado', render: (i) => new Date(i.fecha_creacion).toLocaleDateString() },
    ];

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <Title>Registro de Donantes</Title>
                <button onClick={handleCreate} className="bg-pink-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-pink-700 transition">
                    <HeartHandshake size={18} /> Nuevo Donante
                </button>
            </div>

            <Card>
                <DataTable data={data} columns={columns} onDelete={remove} onEdit={handleEdit} />
            </Card>

            <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={editingItem ? "Editar Donante" : "Nuevo Donante"}>
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