from rest_framework import viewsets
from rest_framework.permissions import AllowAny
from .models import *
from .serializers import *
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny
from rest_framework.response import Response
from django.db.models import Count, Sum
from django.db.models.functions import TruncMonth

class UsuarioViewSet(viewsets.ModelViewSet):
    queryset = UsuarioSpring.objects.all()
    serializer_class = UsuarioSpringSerializer
    permission_classes = [AllowAny]

class RefugioViewSet(viewsets.ModelViewSet):
    queryset = Refugio.objects.all()
    serializer_class = RefugioSerializer
    permission_classes = [AllowAny]

class AnimalViewSet(viewsets.ModelViewSet):
    queryset = Animal.objects.all()
    serializer_class = AnimalSerializer
    permission_classes = [AllowAny]

class SolicitudAdopcionViewSet(viewsets.ModelViewSet):
    queryset = SolicitudAdopcion.objects.all()
    serializer_class = SolicitudAdopcionSerializer
    permission_classes = [AllowAny]

class DonacionViewSet(viewsets.ModelViewSet):
    queryset = Donacion.objects.all()
    serializer_class = DonacionSerializer
    permission_classes = [AllowAny]

class AdoptanteViewSet(viewsets.ModelViewSet):
    queryset = PerfilAdoptante.objects.all()
    serializer_class = PerfilAdoptanteSerializer
    permission_classes = [AllowAny]

class DonanteViewSet(viewsets.ModelViewSet):
    queryset = Donante.objects.all()
    serializer_class = DonanteSerializer
    permission_classes = [AllowAny]

# Catálogos
class RolViewSet(viewsets.ModelViewSet):
    queryset = Rol.objects.all(); serializer_class = RolSerializer; permission_classes = [AllowAny]
class RazaViewSet(viewsets.ModelViewSet):
    queryset = Raza.objects.all(); serializer_class = RazaSerializer; permission_classes = [AllowAny]
class EspecieViewSet(viewsets.ModelViewSet):
    queryset = Especie.objects.all(); serializer_class = EspecieSerializer; permission_classes = [AllowAny]
class TemperamentoViewSet(viewsets.ModelViewSet):
    queryset = Temperamento.objects.all(); serializer_class = TemperamentoSerializer; permission_classes = [AllowAny]
class EstadoAdopcionViewSet(viewsets.ModelViewSet):
    queryset = EstadoAdopcion.objects.all(); serializer_class = EstadoAdopcionSerializer; permission_classes = [AllowAny]
class EstadoSolicitudViewSet(viewsets.ModelViewSet):
    queryset = EstadoSolicitud.objects.all(); serializer_class = EstadoSolicitudSerializer; permission_classes = [AllowAny]
class EstadoPagoViewSet(viewsets.ModelViewSet):
    queryset = EstadoPago.objects.all(); serializer_class = EstadoPagoSerializer; permission_classes = [AllowAny]
class GeneroViewSet(viewsets.ModelViewSet):
    queryset = Genero.objects.all(); serializer_class = GeneroSerializer; permission_classes = [AllowAny]
class TamanoViewSet(viewsets.ModelViewSet):
    queryset = Tamano.objects.all(); serializer_class = TamanoSerializer; permission_classes = [AllowAny]
class NivelEnergiaViewSet(viewsets.ModelViewSet):
    queryset = NivelEnergia.objects.all(); serializer_class = NivelEnergiaSerializer; permission_classes = [AllowAny]


# --- VISTA PERSONALIZADA PARA EL DASHBOARD ---
@api_view(['GET'])
@permission_classes([AllowAny])
def dashboard_stats(request):
    # 1. Totales Generales (KPIs)
    total_adoptantes = PerfilAdoptante.objects.count()
    total_refugios = Refugio.objects.count()
    total_animales = Animal.objects.count()
    total_solicitudes = SolicitudAdopcion.objects.count()

    # 2. Animales por Estado (Para gráfico de Donas)
    # Retorna: [{'estado_adopcion__nombre': 'Disponible', 'total': 15}, ...]
    animales_por_estado = Animal.objects.values('estado_adopcion__nombre').annotate(total=Count('id'))

    # 3. Animales por Especie (Para gráfico de Barras)
    animales_por_especie = Animal.objects.values('raza__especie__nombre').annotate(total=Count('id'))

    # 4. Solicitudes Recientes (Para tabla resumen)
    ultimas_solicitudes = SolicitudAdopcion.objects.select_related('usuario', 'animal', 'estado_solicitud').order_by(
        '-fecha_solicitud')[:5]

    solicitudes_data = []
    for sol in ultimas_solicitudes:
        solicitudes_data.append({
            "id": sol.id,
            "usuario": sol.usuario.email,
            "animal": sol.animal.nombre,
            "estado": sol.estado_solicitud.nombre,
            "fecha": sol.fecha_solicitud
        })

    return Response({
        "kpis": {
            "adoptantes": total_adoptantes,
            "refugios": total_refugios,
            "animales": total_animales,
            "solicitudes": total_solicitudes
        },
        "charts": {
            "animales_estado": list(animales_por_estado),
            "animales_especie": list(animales_por_especie)
        },
        "recent_activity": solicitudes_data
    })