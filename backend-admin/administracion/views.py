from rest_framework import viewsets, permissions
from rest_framework.permissions import IsAuthenticated, AllowAny
from .models import (
    UsuarioSpring, Refugio, Animal, SolicitudAdopcion, Donacion,
    Raza, Especie
)
from .serializers import (
    UsuarioSpringSerializer, RefugioSerializer, AnimalSerializer,
    SolicitudAdopcionSerializer, DonacionSerializer,
    RazaSerializer, EspecieSerializer
)


# Permiso personalizado: Solo administradores pueden modificar,
# pero quizás quieras que (por ahora) cualquiera autenticado pueda ver.
# Ajusta 'permission_classes' según necesites.

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

    # Filtros opcionales (puedes agregar django-filter más adelante)
    def get_queryset(self):
        queryset = Animal.objects.all()
        estado = self.request.query_params.get('estado')
        if estado:
            queryset = queryset.filter(estado_adopcion__id=estado)
        return queryset


class SolicitudAdopcionViewSet(viewsets.ModelViewSet):
    queryset = SolicitudAdopcion.objects.all()
    serializer_class = SolicitudAdopcionSerializer
    permission_classes = [AllowAny]


class DonacionViewSet(viewsets.ModelViewSet):
    queryset = Donacion.objects.all()
    serializer_class = DonacionSerializer
    permission_classes = [AllowAny]


# Catálogos (útiles para llenar selects en el frontend)
class RazaViewSet(viewsets.ModelViewSet):
    queryset = Raza.objects.all()
    serializer_class = RazaSerializer
    permission_classes = [AllowAny]


class EspecieViewSet(viewsets.ModelViewSet):
    queryset = Especie.objects.all()
    serializer_class = EspecieSerializer
    permission_classes = [AllowAny]