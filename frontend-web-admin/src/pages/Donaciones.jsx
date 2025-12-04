import { useCrud } from '../hooks/useCrud';
import DataTable from '../components/DataTable';
import { Card, Title, Metric, Text, Flex } from '@tremor/react';

export default function Donaciones() {
    const { data, remove } = useCrud('donaciones');

    // Calculamos el total en tiempo real
    const totalRecaudado = data.reduce((acc, curr) => acc + Number(curr.monto), 0);

    const columns = [
        { key: 'id', label: 'ID' },
        { key: 'monto', label: 'Monto', render: (item) => (
            <span className="font-mono font-bold text-green-600">${item.monto}</span>
        )},
        { key: 'fecha_donacion', label: 'Fecha', render: (item) => new Date(item.fecha_donacion).toLocaleDateString() },
        { key: 'mensaje_donante', label: 'Mensaje', render: (item) => (
            <span className="italic text-gray-500">"{item.mensaje_donante || 'Sin mensaje'}"</span>
        )},
    ];

    return (
        <div className="space-y-6">
            <Card className="max-w-xs mx-auto md:mx-0 decoration-top decoration-green-500">
                <Text>Total Recaudado</Text>
                <Metric>${totalRecaudado.toLocaleString()}</Metric>
            </Card>

            <div className="flex justify-between items-center mt-8">
                <Title>Historial de Donaciones</Title>
            </div>

            <Card>
                <DataTable 
                    data={data} 
                    columns={columns} 
                    onDelete={remove}
                    onEdit={(item) => console.log('Ver recibo', item)} 
                />
            </Card>
        </div>
    );
}