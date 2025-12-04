import { useCrud } from '../hooks/useCrud';
import DataTable from '../components/DataTable';
import { Card, Title, Badge } from '@tremor/react';

export default function Usuarios() {
    // Solo cambiamos el endpoint a 'usuarios' y listo
    const { data, remove } = useCrud('usuarios');

    const columns = [
        { key: 'id', label: 'ID' },
        { key: 'email', label: 'Email' },
        { key: 'nombre', label: 'Nombre' },
        { key: 'telefono', label: 'Teléfono' },
        { key: 'esta_activo', label: 'Estado', render: (item) => (
            <Badge color={item.esta_activo ? 'emerald' : 'red'}>
                {item.esta_activo ? 'Activo' : 'Inactivo'}
            </Badge>
        )},
    ];

    return (
        <div className="space-y-6">
            <Title>Usuarios Registrados</Title>
            <Card>
                <DataTable 
                    data={data} 
                    columns={columns} 
                    onDelete={remove}
                    onEdit={() => {}} 
                />
            </Card>
        </div>
    );
}