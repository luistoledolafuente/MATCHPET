import { useCrud } from '../hooks/useCrud';
import DataTable from '../components/DataTable';
import { Card, Title } from '@tremor/react';
import { Plus, MapPin, Phone, Mail } from 'lucide-react';

export default function Refugios() {
    const { data, remove } = useCrud('refugios');

    const columns = [
        { key: 'id', label: 'ID' },
        { key: 'nombre', label: 'Refugio', render: (item) => (
            <span className="font-medium text-indigo-600">{item.nombre}</span>
        )},
        { key: 'ciudad', label: 'Ubicación', render: (item) => (
            <div className="flex items-center gap-2 text-gray-500">
                <MapPin size={16} /> {item.ciudad}, {item.pais}
            </div>
        )},
        { key: 'email', label: 'Contacto', render: (item) => (
            <div className="flex flex-col gap-1 text-sm">
                <span className="flex items-center gap-2"><Mail size={14}/> {item.email}</span>
                <span className="flex items-center gap-2"><Phone size={14}/> {item.telefono}</span>
            </div>
        )},
        { key: 'persona_contacto', label: 'Encargado' },
    ];

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <Title>Directorio de Refugios</Title>
                <button className="bg-indigo-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-indigo-700 transition">
                    <Plus size={18} /> Registrar Refugio
                </button>
            </div>

            <Card>
                <DataTable 
                    data={data} 
                    columns={columns} 
                    onDelete={remove}
                    onEdit={(item) => console.log('Editar refugio', item)} 
                />
            </Card>
        </div>
    );
}