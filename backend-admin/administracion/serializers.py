from rest_framework import serializers
from django.db import transaction
from django.utils import timezone
import bcrypt
from .models import *

# --- FUNCIÓN AUXILIAR PARA ENCRIPTAR (Compatibilidad Spring Boot) ---
def encriptar_password(password_raw):
    # Genera el hash con bcrypt
    salt = bcrypt.gensalt()
    hashed = bcrypt.hashpw(password_raw.encode('utf-8'), salt)
    # Convertimos a string y cambiamos la versión $2b$ a $2a$ para que Spring Java no se queje
    return hashed.decode('utf-8').replace('$2b$', '$2a$')

# --- SERIALIZERS CATÁLOGOS ---
class EspecieSerializer(serializers.ModelSerializer):
    class Meta: model = Especie; fields = '__all__'


class RazaSerializer(serializers.ModelSerializer):
    nombre_especie = serializers.CharField(source='especie.nombre', read_only=True)

    class Meta: model = Raza; fields = '__all__'


class GeneroSerializer(serializers.ModelSerializer):
    class Meta: model = Genero; fields = '__all__'


class TamanoSerializer(serializers.ModelSerializer):
    class Meta: model = Tamano; fields = '__all__'


class NivelEnergiaSerializer(serializers.ModelSerializer):
    class Meta: model = NivelEnergia; fields = '__all__'


class EstadoAdopcionSerializer(serializers.ModelSerializer):
    class Meta: model = EstadoAdopcion; fields = '__all__'


class EstadoSolicitudSerializer(serializers.ModelSerializer):
    class Meta: model = EstadoSolicitud; fields = '__all__'


class EstadoPagoSerializer(serializers.ModelSerializer):
    class Meta: model = EstadoPago; fields = '__all__'


class TemperamentoSerializer(serializers.ModelSerializer):
    class Meta: model = Temperamento; fields = '__all__'


class RolSerializer(serializers.ModelSerializer):
    class Meta: model = Rol; fields = '__all__'


# --- SERIALIZERS PRINCIPALES ---

class UsuarioSpringSerializer(serializers.ModelSerializer):
    esta_activo = serializers.BooleanField(read_only=True)
    es_adoptante = serializers.SerializerMethodField()
    es_refugio = serializers.SerializerMethodField()
    roles = serializers.SerializerMethodField()

    class Meta:
        model = UsuarioSpring
        fields = ['id', 'email', 'nombre', 'apellido_paterno', 'apellido_materno', 'telefono', 'esta_activo',
                  'fecha_creacion_perfil', 'es_adoptante', 'es_refugio', 'roles']

    def get_es_adoptante(self, obj): return PerfilAdoptante.objects.filter(usuario=obj).exists()

    def get_es_refugio(self, obj): return PerfilRefugio.objects.filter(usuario=obj).exists()

    def get_roles(self, obj): return UsuarioRol.objects.filter(usuario=obj).values_list('rol__nombre_rol', flat=True)


class RefugioSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True, required=False)
    fecha_registro = serializers.DateTimeField(read_only=True)
    fecha_actualizacion = serializers.DateTimeField(read_only=True)

    class Meta:
        model = Refugio; fields = '__all__'

    def create(self, validated_data):
        raw_password = validated_data.pop('password', 'Refugio123!')
        hashed_password = encriptar_password(raw_password)
        now = timezone.now()

        with transaction.atomic():
            # 1. Crear Refugio (con fechas obligatorias)
            refugio = Refugio.objects.create(fecha_registro=now, fecha_actualizacion=now, **validated_data)

            # 2. Crear Usuario asociado
            parts = refugio.persona_contacto.split()
            nom = parts[0] if parts else "Admin"
            ape = parts[1] if len(parts) > 1 else "Refugio"

            usuario = UsuarioSpring.objects.create(
                email=refugio.email,
                nombre=nom,
                apellido_paterno=ape,
                apellido_materno="",  # NOT NULL en DB, enviamos vacío
                telefono=refugio.telefono,
                esta_activo_raw=b'\x01',
                fecha_creacion_perfil=now,
                hash_contrasena=hashed_password
            )

            # 3. Crear enlace Perfil_Refugio
            PerfilRefugio.objects.create(usuario=usuario, refugio=refugio)

            # 4. Asignar Rol 2 (Refugio)
            try:
                rol = Rol.objects.get(nombre_rol='Refugio')
                UsuarioRol.objects.create(usuario=usuario, rol=rol)
            except Rol.DoesNotExist:
                raise serializers.ValidationError(
                    {"rol": "Error crítico: El rol 'Refugio' no existe en la base de datos."}
                )

        return refugio

    def update(self, instance, validated_data):
        validated_data['fecha_actualizacion'] = timezone.now()
        return super().update(instance, validated_data)


class AnimalFotoSerializer(serializers.ModelSerializer):
    es_principal = serializers.BooleanField(read_only=True)

    class Meta: model = AnimalFoto; fields = '__all__'


class AnimalSerializer(serializers.ModelSerializer):
    nombre_raza = serializers.CharField(source='raza.nombre', read_only=True)
    nombre_especie = serializers.CharField(source='raza.especie.nombre', read_only=True)
    nombre_refugio = serializers.CharField(source='refugio.nombre', read_only=True)
    nombre_estado = serializers.CharField(source='estado_adopcion.nombre', read_only=True)
    fotos = AnimalFotoSerializer(many=True, read_only=True)
    fecha_actualizacion = serializers.DateTimeField(read_only=True)

    class Meta: model = Animal; fields = '__all__'

    def create(self, validated_data):
        validated_data['fecha_actualizacion'] = timezone.now()
        return super().create(validated_data)

    def update(self, instance, validated_data):
        validated_data['fecha_actualizacion'] = timezone.now()
        return super().update(instance, validated_data)


class PerfilAdoptanteSerializer(serializers.ModelSerializer):
    id = serializers.IntegerField(source='usuario.id', read_only=True)
    email = serializers.EmailField(source='usuario.email')
    nombre = serializers.CharField(source='usuario.nombre')
    apellido_paterno = serializers.CharField(source='usuario.apellido_paterno')
    apellido_materno = serializers.CharField(source='usuario.apellido_materno', required=False, allow_blank=True)
    telefono = serializers.CharField(source='usuario.telefono')
    password = serializers.CharField(write_only=True, required=False)

    class Meta:
        model = PerfilAdoptante
        fields = ['id', 'email', 'nombre', 'apellido_paterno', 'apellido_materno', 'telefono', 'password', 'ciudad',
                  'direccion', 'fecha_nacimiento', 'pais']

    def create(self, validated_data):
        # 1. Extraemos datos
        usuario_data = validated_data.pop('usuario')
        raw_password = validated_data.pop('password', 'MatchPet123!')
        hashed_password = encriptar_password(raw_password)
        now = timezone.now()

        # Usamos transaction.atomic para que si algo falla, no se guarde nada (integridad)
        with transaction.atomic():
            print(">>> INICIO: Creando Adoptante...")

            # -----------------------------------------------------
            # PASO 1: CREAR USUARIO (Base)
            # -----------------------------------------------------
            usuario = UsuarioSpring.objects.create(
                email=usuario_data['email'],
                nombre=usuario_data['nombre'],
                apellido_paterno=usuario_data['apellido_paterno'],
                apellido_materno=usuario_data.get('apellido_materno', ''),
                telefono=usuario_data['telefono'],
                esta_activo_raw=b'\x01',  # 1 en binario (Activo)
                fecha_creacion_perfil=now,
                hash_contrasena=hashed_password
            )
            print(f">>> PASO 1 OK: Usuario creado (ID: {usuario.id})")

            # -----------------------------------------------------
            # PASO 2: ASIGNAR ROL (Estricto: 'Adoptante')
            # -----------------------------------------------------
            try:
                # Buscamos EXACTAMENTE "Adoptante" como indicaste
                rol = Rol.objects.get(nombre_rol='Adoptante')

                # Creamos la relación en la tabla intermedia
                UsuarioRol.objects.create(usuario=usuario, rol=rol)
                print(f">>> PASO 2 OK: Rol '{rol.nombre_rol}' asignado.")

            except Rol.DoesNotExist:
                # Esto es vital: Si no existe 'Adoptante' en la BD, cancelamos todo.
                print("!!! ERROR CRÍTICO: No existe el rol 'Adoptante' en la base de datos.")
                raise serializers.ValidationError(
                    {"rol": "Error interno: El rol 'Adoptante' no está registrado en la base de datos."}
                )

            # -----------------------------------------------------
            # PASO 3: CREAR PERFIL (Datos específicos)
            # -----------------------------------------------------
            perfil = PerfilAdoptante.objects.create(usuario=usuario, **validated_data)
            print(">>> PASO 3 OK: Perfil de adoptante guardado.")

        return perfil

    def update(self, instance, validated_data):
        usuario_data = validated_data.pop('usuario', {})
        with transaction.atomic():
            for attr, value in validated_data.items(): setattr(instance, attr, value)
            instance.save()
            if usuario_data:
                u = instance.usuario
                u.email = usuario_data.get('email', u.email)
                u.nombre = usuario_data.get('nombre', u.nombre)
                u.apellido_paterno = usuario_data.get('apellido_paterno', u.apellido_paterno)
                u.telefono = usuario_data.get('telefono', u.telefono)
                u.save()
        return instance


class DonanteSerializer(serializers.ModelSerializer):
    class Meta: model = Donante; fields = '__all__'

    def create(self, validated_data):
        if 'fecha_creacion' not in validated_data: validated_data['fecha_creacion'] = timezone.now()
        return super().create(validated_data)


class SolicitudAdopcionSerializer(serializers.ModelSerializer):
    email_usuario = serializers.CharField(source='usuario.email', read_only=True)
    nombre_animal = serializers.CharField(source='animal.nombre', read_only=True)
    nombre_estado = serializers.CharField(source='estado_solicitud.nombre', read_only=True)
    fecha_actualizacion = serializers.DateTimeField(read_only=True)

    class Meta: model = SolicitudAdopcion; fields = '__all__'

    def create(self, validated_data):
        validated_data['fecha_actualizacion'] = timezone.now()
        if 'fecha_solicitud' not in validated_data: validated_data['fecha_solicitud'] = timezone.now()
        return super().create(validated_data)

    def update(self, instance, validated_data):
        validated_data['fecha_actualizacion'] = timezone.now()
        return super().update(instance, validated_data)


class DonacionSerializer(serializers.ModelSerializer):
    nombre_donante = serializers.CharField(source='donante.nombre_completo', read_only=True)
    nombre_estado = serializers.CharField(source='estado_pago.nombre', read_only=True)

    class Meta: model = Donacion; fields = '__all__'