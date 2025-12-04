import React, { useState, useEffect } from 'react';
import { fetchDashboardStats } from '../api/fetchDashboard';
import { 
    Card, 
    Grid, 
    Title, 
    Text, 
    Metric, 
    Flex, 
    ProgressBar,
    DonutChart, 
    BarChart, 
    Subtitle 
} from "@tremor/react";
import { UserGroupIcon, HomeModernIcon, HeartIcon, EnvelopeIcon } from "@heroicons/react/24/solid";

const Dashboard = () => {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadStats = async () => {
            try {
                const data = await fetchDashboardStats();
                setStats(data);
            } catch (error) {
                console.error("Error fetching stats");
            } finally {
                setLoading(false);
            }
        };
        loadStats();
    }, []);

    if (loading) return <div className="p-10 text-center text-gray-500">Cargando métricas...</div>;
    if (!stats) return <div className="p-10 text-center text-red-500">Error cargando datos.</div>;

    // Preparar datos para gráficos
    const estadoData = stats.charts.animales_estado.map(item => ({
        name: item.estado_adopcion__nombre,
        value: item.total
    }));

    const especieData = stats.charts.animales_especie.map(item => ({
        name: item.raza__especie__nombre,
        "Cantidad": item.total
    }));

    return (
        <div className="p-6 bg-gray-50 min-h-screen">
            <div className="mb-6">
                <Title className="text-4xl font-semibold text-gray-800">Panel de Control</Title>
                <Text className="text-gray-600">Resumen general de la actividad de MatchPet.</Text>
            </div>

            {/* 1. KPIs (Tarjetas Superiores) */}
            <Grid numItems={1} numItemsSm={2} numItemsLg={4} className="gap-6 mb-6">
                <KpiCard title="Adoptantes" metric={stats.kpis.adoptantes} icon={UserGroupIcon} color="indigo" />
                <KpiCard title="Refugios" metric={stats.kpis.refugios} icon={HomeModernIcon} color="rose" />
                <KpiCard title="Animales" metric={stats.kpis.animales} icon={HeartIcon} color="amber" />
                <KpiCard title="Solicitudes" metric={stats.kpis.solicitudes} icon={EnvelopeIcon} color="emerald" />
            </Grid>

            {/* 2. Gráficos */}
            <Grid numItems={1} numItemsLg={2} className="gap-6 mb-6">
                
                {/* Gráfico de Barras: Especies */}
                <Card className="border border-gray-200 shadow-lg rounded-lg">
                    <Title>Inventario por Especie</Title>
                    <Subtitle className="mb-4 text-gray-500">Distribución de perros, gatos y otros.</Subtitle>
                    <BarChart
                        className="mt-6"
                        data={especieData}
                        index="name"
                        categories={["Cantidad"]}
                        colors={["blue"]}
                        yAxisWidth={48}
                    />
                </Card>

                {/* Gráfico de Donas: Estado de Adopción */}
                <Card className="border border-gray-200 shadow-lg rounded-lg">
                    <Title>Estado de los Animales</Title>
                    <Subtitle className="mb-4 text-gray-500">Proporción de animales disponibles vs adoptados.</Subtitle>
                    <DonutChart
                        className="mt-6"
                        data={estadoData}
                        category="value"
                        index="name"
                        colors={["cyan", "violet", "indigo", "rose", "cyan", "amber"]}
                    />
                </Card>
            </Grid>

            {/* 3. Tabla de Actividad Reciente */}
            <Card className="shadow-lg border border-gray-200 p-6 rounded-lg">
                <Flex justifyContent="start" className="space-x-2 mb-4">
                    <Title>Solicitudes Recientes</Title>
                    <span className="bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-0.5 rounded">Últimas 5</span>
                </Flex>
                
                <div className="overflow-x-auto">
                    <table className="min-w-full text-sm text-left text-gray-500">
                        <thead className="text-xs text-gray-700 uppercase bg-gray-50">
                            <tr>
                                <th className="px-4 py-3">ID</th>
                                <th className="px-4 py-3">Usuario</th>
                                <th className="px-4 py-3">Mascota</th>
                                <th className="px-4 py-3">Estado</th>
                                <th className="px-4 py-3">Fecha</th>
                            </tr>
                        </thead>
                        <tbody>
                            {stats.recent_activity.map((sol) => (
                                <tr key={sol.id} className="bg-white border-b hover:bg-gray-50">
                                    <td className="px-4 py-3 font-medium text-gray-900">#{sol.id}</td>
                                    <td className="px-4 py-3">{sol.usuario}</td>
                                    <td className="px-4 py-3 text-indigo-600 font-medium">{sol.animal}</td>
                                    <td className="px-4 py-3">
                                        <span className={`px-2 py-1 rounded-full text-xs text-white
                                            ${sol.estado === 'Pendiente' ? 'bg-yellow-400' : 
                                              sol.estado === 'Aprobada' ? 'bg-green-500' : 'bg-gray-400'}`}>
                                            {sol.estado}
                                        </span>
                                    </td>
                                    <td className="px-4 py-3">{new Date(sol.fecha).toLocaleDateString()}</td>
                                </tr>
                            ))}
                            {stats.recent_activity.length === 0 && (
                                <tr><td colSpan="5" className="text-center py-4">No hay actividad reciente.</td></tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </Card>
        </div>
    );
};

// Componente pequeño para las tarjetas KPI
const KpiCard = ({ title, metric, icon: Icon, color }) => (
    <Card decoration="top" decorationColor={color} className="shadow-lg">
        <Flex justifyContent="start" className="space-x-4">
            <div className={`p-2 rounded-lg bg-${color}-100`}>
                <Icon className={`w-6 h-6 text-${color}-600`} />
            </div>
            <div>
                <Text className="text-gray-600">{title}</Text>
                <Metric className="text-lg font-semibold">{metric}</Metric>
            </div>
        </Flex>
    </Card>
);

export default Dashboard;
