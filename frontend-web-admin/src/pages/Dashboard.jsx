import { Card, Grid, Metric, Text, Title, DonutChart, BarChart } from "@tremor/react";

const chartdata = [
  { name: "Perros", "Cantidad": 248 },
  { name: "Gatos", "Cantidad": 190 },
  { name: "Otros", "Cantidad": 40 },
];

export default function Dashboard() {
    return (
        <div className="space-y-6">
            <Title>Resumen General</Title>
            
            <Grid numItems={1} numItemsSm={2} numItemsLg={3} className="gap-6">
                <Card decoration="top" decorationColor="indigo">
                    <Text>Total Animales</Text>
                    <Metric>478</Metric>
                </Card>
                <Card decoration="top" decorationColor="emerald">
                    <Text>Adopciones este Mes</Text>
                    <Metric>12</Metric>
                </Card>
                <Card decoration="top" decorationColor="amber">
                    <Text>Solicitudes Pendientes</Text>
                    <Metric>5</Metric>
                </Card>
            </Grid>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <Card>
                    <Title>Mascotas por Especie</Title>
                    <DonutChart
                        className="mt-6"
                        data={chartdata}
                        category="Cantidad"
                        index="name"
                        colors={["slate", "violet", "indigo"]}
                    />
                </Card>
                <Card>
                    <Title>Solicitudes vs Adopciones</Title>
                    <BarChart
                        className="mt-6"
                        data={chartdata} // Aquí usarías datos reales de otra API
                        index="name"
                        categories={["Cantidad"]}
                        colors={["blue"]}
                    />
                </Card>
            </div>
        </div>
    );
}