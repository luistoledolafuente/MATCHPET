from rest_framework import serializers
from .models import (
    UsuarioSpring, PerfilAdoptante, Refugio, Animal, AnimalFoto,
    SolicitudAdopcion, Donacion, Raza, Especie, EstadoSolicitud,
    EstadoAdopcion, Genero, Tamano, NivelEnergia
)


# --- SERIALIZERS PARA CATÁLOGOS (Simples) ---
class EspecieSerializer(serializers.ModelSerializer):
    class Meta: model = Especie; fields = '__all__'


class RazaSerializer(serializers.ModelSerializer):
    nombre_especie = serializers.CharField(source='especie.nombre', read_only=True)

    class Meta: model = Raza; fields = '__all__'


# --- SERIALIZERS PRINCIPALES ---

class UsuarioSpringSerializer(serializers.ModelSerializer):
    # Sobrescribimos el campo para usar la propiedad traducida
    esta_activo = serializers.BooleanField(read_only=True)

    class Meta:
        model = UsuarioSpring
        # Excluimos el campo 'raw' para que no salga en el JSON
        fields = ['id', 'email', 'nombre', 'apellido_paterno', 'telefono', 'esta_activo', 'fecha_creacion_perfil']


class RefugioSerializer(serializers.ModelSerializer):
    class Meta:
        model = Refugio
        fields = '__all__'


class AnimalFotoSerializer(serializers.ModelSerializer):
    class Meta:
        model = AnimalFoto
        fields = '__all__'


class AnimalSerializer(serializers.ModelSerializer):
    # Campos extra de lectura para mostrar nombres en lugar de solo IDs
    nombre_raza = serializers.CharField(source='raza.nombre', read_only=True)
    nombre_especie = serializers.CharField(source='raza.especie.nombre', read_only=True)
    nombre_refugio = serializers.CharField(source='refugio.nombre', read_only=True)
    nombre_estado = serializers.CharField(source='estado_adopcion.nombre', read_only=True)

    # Incluimos las fotos anidadas
    fotos = AnimalFotoSerializer(many=True, read_only=True)

    class Meta:
        model = Animal
        fields = '__all__'


class SolicitudAdopcionSerializer(serializers.ModelSerializer):
    # Mostrar datos útiles del usuario y animal en la respuesta
    email_usuario = serializers.CharField(source='usuario.email', read_only=True)
    nombre_animal = serializers.CharField(source='animal.nombre', read_only=True)
    nombre_estado = serializers.CharField(source='estado_solicitud.nombre', read_only=True)

    class Meta:
        model = SolicitudAdopcion
        fields = '__all__'


class DonacionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Donacion
        fields = '__all__'