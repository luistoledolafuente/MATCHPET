from django.db import models


# ==========================================
# 1. TABLAS CATÁLOGO (LOOKUPS)
# ==========================================

class Especie(models.Model):
    id = models.AutoField(primary_key=True, db_column='especie_id')
    nombre = models.CharField(max_length=50, db_column='nombre_especie')

    class Meta: managed = False; db_table = 'especies'; verbose_name = 'Especie'

    def __str__(self): return self.nombre


class Raza(models.Model):
    id = models.AutoField(primary_key=True, db_column='raza_id')
    nombre = models.CharField(max_length=100, db_column='nombre_raza')
    especie = models.ForeignKey(Especie, on_delete=models.DO_NOTHING, db_column='especie_id')

    class Meta: managed = False; db_table = 'razas'; verbose_name = 'Raza'

    def __str__(self): return self.nombre


class Genero(models.Model):
    id = models.AutoField(primary_key=True, db_column='genero_id')
    nombre = models.CharField(max_length=255, db_column='nombre')

    class Meta: managed = False; db_table = 'generos'; verbose_name = 'Género'

    def __str__(self): return self.nombre


class Tamano(models.Model):
    id = models.AutoField(primary_key=True, db_column='tamano_id')
    nombre = models.CharField(max_length=255, db_column='nombre')

    class Meta: managed = False; db_table = 'tamanos'; verbose_name = 'Tamaño'

    def __str__(self): return self.nombre


class NivelEnergia(models.Model):
    id = models.AutoField(primary_key=True, db_column='nivel_energia_id')
    nombre = models.CharField(max_length=255, db_column='nombre')

    class Meta: managed = False; db_table = 'niveles_energia'; verbose_name = 'Nivel Energía'

    def __str__(self): return self.nombre


class EstadoAdopcion(models.Model):
    id = models.AutoField(primary_key=True, db_column='estado_adopcion_id')
    nombre = models.CharField(max_length=255, db_column='nombre')

    class Meta: managed = False; db_table = 'estados_adopcion'; verbose_name = 'Estado Adopción'

    def __str__(self): return self.nombre


class EstadoSolicitud(models.Model):
    id = models.AutoField(primary_key=True, db_column='estado_solicitud_id')
    nombre = models.CharField(max_length=255, db_column='nombre')

    class Meta: managed = False; db_table = 'estados_solicitud'; verbose_name = 'Estado Solicitud'

    def __str__(self): return self.nombre


class EstadoPago(models.Model):
    id = models.AutoField(primary_key=True, db_column='estado_pago_id')
    nombre = models.CharField(max_length=255, db_column='nombre')

    class Meta: managed = False; db_table = 'estados_pago'; verbose_name = 'Estado Pago'

    def __str__(self): return self.nombre


class Temperamento(models.Model):
    id = models.AutoField(primary_key=True, db_column='temperamento_id')
    nombre = models.CharField(max_length=100, db_column='nombre_temperamento')

    class Meta: managed = False; db_table = 'temperamentos'; verbose_name = 'Temperamento'

    def __str__(self): return self.nombre


class Rol(models.Model):
    id = models.AutoField(primary_key=True, db_column='rol_id')
    nombre_rol = models.CharField(max_length=255, db_column='nombre_rol')

    class Meta: managed = False; db_table = 'roles'; verbose_name = 'Rol'

    def __str__(self): return self.nombre_rol


# ==========================================
# 2. USUARIOS Y PERFILES
# ==========================================

class UsuarioSpring(models.Model):
    id = models.AutoField(primary_key=True, db_column='usuario_id')
    email = models.CharField(max_length=255, unique=True)
    nombre = models.CharField(max_length=100)
    apellido_paterno = models.CharField(max_length=100, db_column='apellido_paterno')
    # OJO: apellido_materno es NOT NULL en tu SQL
    apellido_materno = models.CharField(max_length=100, db_column='apellido_materno')
    telefono = models.CharField(max_length=20)

    hash_contrasena = models.TextField(db_column='hash_contraseña')

    # Manejo de bit(1) como binario
    esta_activo_raw = models.BinaryField(db_column='esta_activo')

    fecha_creacion_perfil = models.DateTimeField(db_column='fecha_creacion_perfil', null=True)
    fecha_actualizacion = models.DateTimeField(db_column='fecha_actualizacion', null=True)

    @property
    def esta_activo(self):
        if isinstance(self.esta_activo_raw, bytes):
            return self.esta_activo_raw == b'\x01'
        return bool(self.esta_activo_raw)

    class Meta:
        managed = False
        db_table = 'usuarios'
        verbose_name = 'Usuario App'

    def __str__(self): return f"{self.nombre} ({self.email})"


class UsuarioRol(models.Model):
    # PK compuesta simulada
    usuario = models.OneToOneField(UsuarioSpring, primary_key=True, on_delete=models.DO_NOTHING, db_column='usuario_id')
    rol = models.ForeignKey(Rol, on_delete=models.DO_NOTHING, db_column='rol_id')

    class Meta: managed = False; db_table = 'usuario_roles'; unique_together = (('usuario', 'rol'),)


class PerfilAdoptante(models.Model):
    usuario = models.OneToOneField(UsuarioSpring, primary_key=True, on_delete=models.DO_NOTHING, db_column='usuario_id')
    ciudad = models.CharField(max_length=100, null=True)
    direccion = models.TextField(null=True)
    fecha_nacimiento = models.DateField(db_column='fecha_nacimiento')
    pais = models.CharField(max_length=100, null=True)

    class Meta: managed = False; db_table = 'perfil_adoptante'; verbose_name = 'Perfil de Adoptante'


# ==========================================
# 3. ENTIDADES PRINCIPALES
# ==========================================

class Refugio(models.Model):
    id = models.AutoField(primary_key=True, db_column='refugio_id')
    nombre = models.CharField(max_length=255)
    direccion = models.TextField()
    ciudad = models.CharField(max_length=100)
    pais = models.CharField(max_length=100)
    telefono = models.CharField(max_length=20)
    email = models.CharField(max_length=255, unique=True)
    persona_contacto = models.CharField(max_length=255, db_column='persona_contacto')
    descripcion = models.TextField()
    url_sitio_web = models.CharField(max_length=255, db_column='url_sitio_web')

    # En tu SQL son NOT NULL, debemos llenarlos al crear
    fecha_registro = models.DateTimeField(db_column='fecha_registro')
    fecha_actualizacion = models.DateTimeField(db_column='fecha_actualizacion')

    class Meta: managed = False; db_table = 'refugios'; verbose_name = 'Refugio'

    def __str__(self): return self.nombre


class PerfilRefugio(models.Model):
    # Tabla intermedia perfil_refugio
    usuario = models.OneToOneField(UsuarioSpring, primary_key=True, on_delete=models.DO_NOTHING, db_column='usuario_id')
    refugio = models.OneToOneField(Refugio, on_delete=models.DO_NOTHING, db_column='refugio_id')

    class Meta: managed = False; db_table = 'perfil_refugio'


class Animal(models.Model):
    id = models.AutoField(primary_key=True, db_column='animal_id')
    nombre = models.CharField(max_length=100)
    fecha_nacimiento_aprox = models.DateField(db_column='fecha_nacimiento_aprox')
    descripcion_personalidad = models.TextField(db_column='descripcion_personalidad')

    compatible_ninos = models.BooleanField(db_column='compatible_niños')
    compatible_otras_mascotas = models.BooleanField(db_column='compatible_otras_mascotas')
    esta_vacunado = models.BooleanField(db_column='esta_vacunado')
    esta_esterilizado = models.BooleanField(db_column='esta_esterilizado')
    historial_medico = models.TextField(db_column='historial_medico')

    fecha_ingreso_refugio = models.DateField(db_column='fecha_ingreso_refugio')
    fecha_actualizacion = models.DateTimeField(db_column='fecha_actualizacion')

    refugio = models.ForeignKey(Refugio, on_delete=models.DO_NOTHING, db_column='refugio_id')
    raza = models.ForeignKey(Raza, on_delete=models.DO_NOTHING, db_column='raza_id')
    genero = models.ForeignKey(Genero, on_delete=models.DO_NOTHING, db_column='genero_id')
    tamano = models.ForeignKey(Tamano, on_delete=models.DO_NOTHING, db_column='tamano_id')
    nivel_energia = models.ForeignKey(NivelEnergia, on_delete=models.DO_NOTHING, db_column='nivel_energia_id')
    estado_adopcion = models.ForeignKey(EstadoAdopcion, on_delete=models.DO_NOTHING, db_column='estado_adopcion_id')

    class Meta: managed = False; db_table = 'animales'; verbose_name = 'Animal'

    def __str__(self): return self.nombre


class AnimalFoto(models.Model):
    id = models.AutoField(primary_key=True, db_column='foto_id')
    es_principal_raw = models.BinaryField(db_column='es_principal')
    url_foto = models.TextField(db_column='url_foto')
    animal = models.ForeignKey(Animal, on_delete=models.DO_NOTHING, db_column='animal_id', related_name='fotos')

    @property
    def es_principal(self):
        if isinstance(self.es_principal_raw, bytes):
            return self.es_principal_raw == b'\x01'
        return bool(self.es_principal_raw)

    class Meta: managed = False; db_table = 'animal_fotos'; verbose_name = 'Foto'


class SolicitudAdopcion(models.Model):
    id = models.AutoField(primary_key=True, db_column='solicitud_id')
    mensaje_adoptante = models.TextField(db_column='mensaje_adoptante')
    notas_internas = models.TextField(db_column='notas_internas', blank=True, null=True)
    mensaje_al_adoptante = models.TextField(db_column='mensaje_al_adoptante', blank=True, null=True)
    fecha_solicitud = models.DateTimeField(db_column='fecha_solicitud')
    fecha_actualizacion = models.DateTimeField(db_column='fecha_actualizacion')

    usuario = models.ForeignKey(UsuarioSpring, on_delete=models.DO_NOTHING, db_column='usuario_id')
    animal = models.ForeignKey(Animal, on_delete=models.DO_NOTHING, db_column='animal_id')
    estado_solicitud = models.ForeignKey(EstadoSolicitud, on_delete=models.DO_NOTHING, db_column='estado_solicitud_id')

    class Meta: managed = False; db_table = 'solicitudes_adopcion'; verbose_name = 'Solicitud'


class Donante(models.Model):
    id = models.AutoField(primary_key=True, db_column='donante_id')
    nombre_completo = models.CharField(max_length=255, db_column='nombre_completo')
    email = models.CharField(max_length=255, db_column='email')
    fecha_creacion = models.DateTimeField(db_column='fecha_creacion')
    usuario = models.OneToOneField(UsuarioSpring, on_delete=models.DO_NOTHING, db_column='usuario_id', null=True,
                                   blank=True)

    class Meta: managed = False; db_table = 'donantes'; verbose_name = 'Donante'

    def __str__(self): return self.nombre_completo


class Donacion(models.Model):
    id = models.AutoField(primary_key=True, db_column='donacion_id')
    monto = models.DecimalField(max_digits=10, decimal_places=2)
    moneda = models.CharField(max_length=3)
    fecha_donacion = models.DateTimeField(db_column='fecha_donacion')
    mensaje_donante = models.TextField(db_column='mensaje_donante', blank=True, null=True)
    gateway_transaccion_id = models.CharField(max_length=255, db_column='gateway_transaccion_id', null=True)

    donante = models.ForeignKey(Donante, on_delete=models.DO_NOTHING, db_column='donante_id')
    refugio = models.ForeignKey(Refugio, on_delete=models.DO_NOTHING, db_column='refugio_id', null=True)
    animal = models.ForeignKey(Animal, on_delete=models.DO_NOTHING, db_column='animal_id', null=True)
    estado_pago = models.ForeignKey(EstadoPago, on_delete=models.DO_NOTHING, db_column='estado_pago_id')

    class Meta: managed = False; db_table = 'donaciones'; verbose_name = 'Donación'