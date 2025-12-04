import { useCrud } from '../hooks/useCrud';
import DataTable from '../components/DataTable';
import { Card, Title, Badge } from '@tremor/react';

export default function Solicitudes() {
    const { data, remove } = useCrud('solicitudes');

    // Función auxiliar para colorear el estado
    const getBadgeColor = (estado) => {
        switch(estado?.toLowerCase()) {
            case 'aprobada': return 'emerald';
            case 'rechazada': return 'rose';
            case 'pendiente': return 'amber';
            default: return 'slate';
        }
    };

    const columns = [
        { key: 'id', label: 'Folio' },
        { key: 'nombre_animal', label: 'Interés en', render: (item) => (
            <span className="font-bold text-gray-700">🐶 {item.nombre_animal || 'Desconocido'}</span>
        )},
        { key: 'email_usuario', label: 'Solicitante', render: (item) => (
            <div className="flex flex-col">
                <span>{item.email_usuario}</span>
                <span className="text-xs text-gray-400">ID Usuario: {item.usuario}</span>
            </div>
        )},
        { key: 'fecha_solicitud', label: 'Fecha', render: (item) => new Date(item.fecha_solicitud).toLocaleDateString() },
        { key: 'nombre_estado', label: 'Estado', render: (item) => (
            <Badge color={getBadgeColor(item.nombre_estado)}>
                {item.nombre_estado || 'Pendiente'}
            </Badge>
        )},
    ];

    return (
        <div className="space-y-6">
            <Title>Solicitudes de Adopción</Title>
            <Card>
                <DataTable 
                    data={data} 
                    columns={columns} 
                    onDelete={remove}
                    onEdit={(item) => console.log('Ver detalles', item)} 
                />
            </Card>
        </div>
    );
}