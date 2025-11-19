import React from 'react';
import AnimalForm from '../../components/refugio/animalForm';
import { useNavigate } from 'react-router-dom';
import { PawPrint } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';

export default function NuevaMascota() {
    const navigate = useNavigate();
    const { token } = useAuth();  

    const handleSuccess = () => {
        navigate('/dashboard/refugio/mis-mascotas', { state: { successMessage: 'Animal registrado exitosamente.' } });
    };

    const handleClose = () => {
        navigate('/dashboard/refugio/mis-mascotas');
    };

    return (
        <div className="bg-[#FFF7E6] min-h-screen p-8 md:pl-72 font-sans">
            <div className="max-w-4xl mx-auto space-y-8">
                <header className="pb-4 border-b border-[#FDB2A0]">
                    <h1 className="text-4xl font-extrabold text-[#316B7A] flex items-center">
                        <PawPrint className="w-8 h-8 mr-3 text-[#FDB2A0]" />
                        Registrar Nueva Mascota
                    </h1>
                    <p className="text-gray-600 mt-2">
                        Completa el siguiente formulario para añadir un nuevo animal a la lista de adopción.
                    </p>
                </header>

                <AnimalForm
                    animal={null}
                    onClose={handleClose}
                    onSuccess={handleSuccess}
                    token={token}            
                />
            </div>
        </div>
    );
}
