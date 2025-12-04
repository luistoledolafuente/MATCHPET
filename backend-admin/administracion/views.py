from rest_framework import viewsets
from rest_framework.permissions import AllowAny
from .models import *
from .serializers import *

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