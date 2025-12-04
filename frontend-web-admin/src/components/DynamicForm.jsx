import { useForm } from 'react-hook-form';
import { useEffect } from 'react';

export default function DynamicForm({ fields, defaultValues, onSubmit, onCancel, isLoading }) {
    const { register, handleSubmit, reset, formState: { errors } } = useForm();

    useEffect(() => {
        if (defaultValues) {
            reset(defaultValues);
        } else {
            reset({});
        }
    }, [defaultValues, reset]);

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {fields.map((field) => (
                    <div key={field.name} className={`flex flex-col space-y-1 ${field.fullWidth ? 'md:col-span-2' : ''}`}>
                        <label className="text-sm font-medium text-gray-700">
                            {field.label} {field.required && <span className="text-red-500">*</span>}
                        </label>

                        {field.type === 'select' ? (
                            <select
                                {...register(field.name, { required: field.required })}
                                className="border border-gray-300 rounded-lg p-2.5 bg-white focus:ring-2 focus:ring-indigo-500 outline-none transition-all"
                            >
                                <option value="">-- Seleccionar --</option>
                                {field.options?.map(opt => (
                                    <option key={opt.value} value={opt.value}>
                                        {opt.label}
                                    </option>
                                ))}
                            </select>
                        ) : field.type === 'textarea' ? (
                            <textarea
                                {...register(field.name, { required: field.required })}
                                rows={3}
                                className="border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-indigo-500 outline-none transition-all"
                            />
                        ) : field.type === 'checkbox' ? (
                            <div className="flex items-center h-full">
                                <input
                                    type="checkbox"
                                    {...register(field.name)}
                                    className="w-5 h-5 text-indigo-600 rounded border-gray-300 focus:ring-indigo-500"
                                />
                                <span className="ml-2 text-sm text-gray-600">Activar</span>
                            </div>
                        ) : (
                            <input
                                type={field.type || 'text'}
                                // CORRECCIÓN: Autocomplete
                                autoComplete={field.type === 'password' ? 'new-password' : 'off'}
                                {...register(field.name, { required: field.required })}
                                className="border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-indigo-500 outline-none transition-all"
                            />
                        )}
                        
                        {errors[field.name] && <span className="text-xs text-red-500">Este campo es requerido</span>}
                    </div>
                ))}
            </div>

            <div className="flex justify-end gap-3 mt-6 pt-4 border-t border-gray-100">
                <button
                    type="button"
                    onClick={onCancel}
                    className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50"
                >
                    Cancelar
                </button>
                <button
                    type="submit"
                    disabled={isLoading}
                    className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 disabled:opacity-50"
                >
                    {isLoading ? 'Guardando...' : 'Guardar'}
                </button>
            </div>
        </form>
    );
}