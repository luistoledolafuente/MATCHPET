import { useCrud } from '../hooks/useCrud';
import DataTable from '../components/DataTable';
import { Card, Title, Button } from '@tremor/react'; // Usamos Tremor para contenedores bonitos
import { Plus } from 'lucide-react';

export default function Animales() {
    // 1. Usamos el hook para conectar a Django
    const { data, remove } = useCrud('animales');

    // 2. Definimos qué columnas mostrar
    const columns = [
        { key: 'id', label: 'ID' },
        // Accedemos a la URL de la primera foto si existe
        { key: 'foto', label: 'Foto', render: (item) => (
            <img 
                src={item.fotos && item.fotos.length > 0 ? item.fotos[0].url_foto : 'https://placehold.co/50'} 
                className="w-10 h-10 rounded-full object-cover" 
            />
        )},
        { key: 'nombre', label: 'Nombre' },
        { key: 'nombre_raza', label: 'Raza' },     // Viene del serializer de Django
        { key: 'nombre_refugio', label: 'Refugio' }, // Viene del serializer de Django
        { key: 'nombre_estado', label: 'Estado' },
        { key: 'compatible_ninos', label: 'Niños', render: (item) => item.compatible_ninos ? '✅' : '❌' },
    ];

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <Title>Gestión de Animales</Title>
                <button className="bg-indigo-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-indigo-700 transition">
                    <Plus size={18} /> Nuevo Animal
                </button>
            </div>

            <Card>
                <DataTable 
                    data={data} 
                    columns={columns} 
                    onDelete={remove}
                    onEdit={(item) => console.log('Editar', item)} 
                />
            </Card>
        </div>
    );
}