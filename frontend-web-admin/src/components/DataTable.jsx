import { Pencil, Trash2 } from 'lucide-react';

export default function DataTable({ data, columns, onEdit, onDelete }) {
    if (!data || data.length === 0) return <div className="p-4 text-gray-500">No hay registros encontrados.</div>;

    return (
        <div className="overflow-x-auto rounded-lg border border-gray-200 shadow-sm">
            <table className="w-full text-sm text-left text-gray-600">
                <thead className="bg-gray-50 text-xs uppercase text-gray-700 font-bold">
                    <tr>
                        {columns.map((col) => (
                            <th key={col.key} className="px-6 py-3">{col.label}</th>
                        ))}
                        <th className="px-6 py-3 text-right">Acciones</th>
                    </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                    {data.map((item) => (
                        <tr key={item.id} className="hover:bg-gray-50 transition-colors">
                            {columns.map((col) => (
                                <td key={`${item.id}-${col.key}`} className="px-6 py-4">
                                    {/* Si es una imagen, renderízala, si no, texto */}
                                    {col.type === 'image' ? (
                                        <img src={item[col.key] || 'https://placehold.co/40'} alt="img" className="h-10 w-10 rounded-full object-cover" />
                                    ) : col.render ? (
                                        col.render(item)
                                    ) : (
                                        item[col.key]
                                    )}
                                </td>
                            ))}
                            <td className="px-6 py-4 text-right flex justify-end gap-2">
                                <button onClick={() => onEdit(item)} className="p-2 text-blue-600 hover:bg-blue-50 rounded">
                                    <Pencil size={16} />
                                </button>
                                <button onClick={() => onDelete(item.id)} className="p-2 text-red-600 hover:bg-red-50 rounded">
                                    <Trash2 size={16} />
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}