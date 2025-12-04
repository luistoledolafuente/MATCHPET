from django.db import models


# ==========================================
# 1. TABLAS CATÁLOGO (LOOKUPS)
# ==========================================

class Especie(models.Model):
    id = models.AutoField(primary_key=True, db_column='especie_id')
    # CORRECCIÓN: En Java es 'nombre_especie'
    nombre = models.CharField(max_length=50, db_column='nombre_especie')

    class Meta:
        managed = False
        db_table = 'especies'
        verbose_name = 'Especie'
        verbose_name_plural = 'Catálogo: Especies'

    def __str__(self): return self.nombre


class Raza(models.Model):
    id = models.AutoField(primary_key=True, db_column='raza_id')
    # CORRECCIÓN: En Java es 'nombre_raza'
    nombre = models.CharField(max_length=100, db_column='nombre_raza')
    # CORRECCIÓN FK: Apuntamos al modelo Especie correcto
    especie = models.ForeignKey(Especie, on_delete=models.DO_NOTHING, db_column='especie_id')

    class Meta:
        managed = False
        db_table = 'razas'
        verbose_name = 'Raza'
        verbose_name_plural = 'Catálogo: Razas'

    def __str__(self): return self.nombre


# Los siguientes suelen usar 'nombre' por defecto en Hibernate si no se especifica @Column(name=...)
class Genero(models.Model):
    id = models.AutoField(primary_key=True, db_column='genero_id')
    nombre = models.CharField(max_length=50, db_column='nombre')

    class Meta:
        managed = False
        db_table = 'generos'
        verbose_name = 'Género'

    def __str__(self): return self.nombre


class Tamano(models.Model):
    id = models.AutoField(primary_key=True, db_column='tamano_id')
    nombre = models.CharField(max_length=50, db_column='nombre')

    class Meta:
        managed = False
        db_table = 'tamanos'
        verbose_name = 'Tamaño'

    def __str__(self): return self.nombre


class NivelEnergia(models.Model):
    id = models.AutoField(primary_key=True, db_column='nivel_energia_id')
    nombre = models.CharField(max_length=50, db_column='nombre')

    class Meta:
        managed = False
        db_table = 'niveles_energia'
        verbose_name = 'Nivel Energía'

    def __str__(self): return self.nombre


class EstadoAdopcion(models.Model):
    id = models.AutoField(primary_key=True, db_column='estado_adopcion_id')
    nombre = models.CharField(max_length=50, db_column='nombre')

    class Meta:
        managed = False
        db_table = 'estados_adopcion'
        verbose_name = 'Estado Adopción'

    def __str__(self): return self.nombre


class EstadoSolicitud(models.Model):
    id = models.AutoField(primary_key=True, db_column='estado_solicitud_id')
    nombre = models.CharField(max_length=50, db_column='nombre')

    class Meta:
        managed = False
        db_table = 'estados_solicitud'
        verbose_name = 'Estado Solicitud'

    def __str__(self): return self.nombre


# ==========================================
# 2. USUARIOS Y PERFILES
# ==========================================

class UsuarioSpring(models.Model):
    id = models.AutoField(primary_key=True, db_column='usuario_id')
    email = models.CharField(max_length=255, unique=True)
    nombre = models.CharField(max_length=100)
    apellido_paterno = models.CharField(max_length=100, db_column='apellido_paterno')
    apellido_materno = models.CharField(max_length=100, db_column='apellido_materno')
    telefono = models.CharField(max_length=20)

    # --- CORRECCIÓN CRÍTICA ---
    # Leemos el dato crudo como binario para que no falle
    esta_activo_raw = models.BinaryField(db_column='esta_activo')

    fecha_creacion_perfil = models.DateTimeField(db_column='fecha_creacion_perfil')

    # Propiedad mágica que convierte el byte b'\x01' a True
    @property
    def esta_activo(self):
        # Si es bytes (b'\x01'), comparamos. Si ya es int/bool, lo usamos.
        if isinstance(self.esta_activo_raw, bytes):
            return self.esta_activo_raw == b'\x01'
        return bool(self.esta_activo_raw)

    class Meta:
        managed = False
        db_table = 'usuarios'
        verbose_name = 'Usuario App'
        verbose_name_plural = 'Usuarios Registrados'

    def __str__(self):
        return f"{self.nombre} ({self.email})"


class PerfilAdoptante(models.Model):
    usuario = models.OneToOneField(UsuarioSpring, primary_key=True, on_delete=models.DO_NOTHING, db_column='usuario_id')
    fecha_nacimiento = models.DateField(db_column='fecha_nacimiento')
    direccion = models.TextField()
    ciudad = models.CharField(max_length=100)
    pais = models.CharField(max_length=100)

    class Meta:
        managed = False
        db_table = 'perfil_adoptante'
        verbose_name = 'Perfil de Adoptante'
        verbose_name_plural = 'Perfiles de Adoptantes'

    def __str__(self):
        return f"Perfil: {self.usuario.email}"


# ==========================================
# 3. ENTIDADES PRINCIPALES (REFUGIO Y ANIMAL)
# ==========================================

class Refugio(models.Model):
    id = models.AutoField(primary_key=True, db_column='refugio_id')
    nombre = models.CharField(max_length=255)
    direccion = models.TextField()
    ciudad = models.CharField(max_length=100)
    pais = models.CharField(max_length=100)
    telefono = models.CharField(max_length=20)
    email = models.EmailField(unique=True)
    persona_contacto = models.CharField(max_length=255, db_column='persona_contacto')
    descripcion = models.TextField()
    url_sitio_web = models.CharField(max_length=255, db_column='url_sitio_web')
    fecha_registro = models.DateTimeField(db_column='fecha_registro')
    fecha_actualizacion = models.DateTimeField(db_column='fecha_actualizacion')

    class Meta:
        managed = False
        db_table = 'refugios'
        verbose_name = 'Refugio'
        verbose_name_plural = 'Refugios'

    def __str__(self):
        return self.nombre


class Animal(models.Model):
    id = models.AutoField(primary_key=True, db_column='animal_id')
    nombre = models.CharField(max_length=100)
    fecha_nacimiento_aprox = models.DateField(db_column='fecha_nacimiento_aprox')
    descripcion_personalidad = models.TextField(db_column='descripcion_personalidad')

    # Usamos db_column exacto para evitar problemas con la ñ
    compatible_ninos = models.BooleanField(db_column='compatible_niños', default=False)
    compatible_otras_mascotas = models.BooleanField(db_column='compatible_otras_mascotas', default=False)
    esta_vacunado = models.BooleanField(db_column='esta_vacunado', default=False)
    esta_esterilizado = models.BooleanField(db_column='esta_esterilizado', default=False)

    historial_medico = models.TextField(db_column='historial_medico')
    fecha_ingreso_refugio = models.DateField(db_column='fecha_ingreso_refugio')
    fecha_actualizacion = models.DateTimeField(db_column='fecha_actualizacion')

    # RELACIONES
    refugio = models.ForeignKey(Refugio, on_delete=models.DO_NOTHING, db_column='refugio_id')

    # RELACIONES LOOKUPS (Usan los modelos corregidos arriba)
    raza = models.ForeignKey(Raza, on_delete=models.DO_NOTHING, db_column='raza_id')
    genero = models.ForeignKey(Genero, on_delete=models.DO_NOTHING, db_column='genero_id')
    tamano = models.ForeignKey(Tamano, on_delete=models.DO_NOTHING, db_column='tamano_id')
    nivel_energia = models.ForeignKey(NivelEnergia, on_delete=models.DO_NOTHING, db_column='nivel_energia_id')
    estado_adopcion = models.ForeignKey(EstadoAdopcion, on_delete=models.DO_NOTHING, db_column='estado_adopcion_id')

    class Meta:
        managed = False
        db_table = 'animales'
        verbose_name = 'Animal'
        verbose_name_plural = 'Gestión de Animales'

    def __str__(self):
        return self.nombre


class AnimalFoto(models.Model):
    id = models.AutoField(primary_key=True, db_column='foto_id')
    url_foto = models.TextField(db_column='url_foto')
    es_principal = models.BooleanField(db_column='es_principal', default=False)
    animal = models.ForeignKey(Animal, on_delete=models.DO_NOTHING, db_column='animal_id', related_name='fotos')

    class Meta:
        managed = False
        db_table = 'animal_fotos'
        verbose_name = 'Foto'
        verbose_name_plural = 'Fotos'

    def __str__(self):
        return f"Foto {self.id}"


# ==========================================
# 4. OPERACIONES
# ==========================================

class SolicitudAdopcion(models.Model):
    id = models.AutoField(primary_key=True, db_column='solicitud_id')
    mensaje_adoptante = models.TextField(db_column='mensaje_adoptante')
    notas_internas = models.TextField(db_column='notas_internas', blank=True, null=True)
    mensaje_al_adoptante = models.TextField(db_column='mensaje_al_adoptante', blank=True, null=True)
    fecha_solicitud = models.DateTimeField(db_column='fecha_solicitud')
    fecha_actualizacion = models.DateTimeField(db_column='fecha_actualizacion')

    # Relaciones
    usuario = models.ForeignKey(UsuarioSpring, on_delete=models.DO_NOTHING, db_column='usuario_id')
    animal = models.ForeignKey(Animal, on_delete=models.DO_NOTHING, db_column='animal_id')
    estado_solicitud = models.ForeignKey(EstadoSolicitud, on_delete=models.DO_NOTHING, db_column='estado_solicitud_id')

    class Meta:
        managed = False
        db_table = 'solicitudes_adopcion'
        verbose_name = 'Solicitud'
        verbose_name_plural = 'Gestión de Solicitudes'


class Donacion(models.Model):
    id = models.AutoField(primary_key=True, db_column='donacion_id')
    monto = models.DecimalField(max_digits=10, decimal_places=2)
    fecha_donacion = models.DateTimeField(db_column='fecha_donacion')
    mensaje_donante = models.TextField(db_column='mensaje_donante', blank=True)

    # Asumimos que existe la FK a donante o usuario, si no, lo dejamos comentado
    # donante = models.ForeignKey(Donante, ...)

    class Meta:
        managed = False
        db_table = 'donaciones'
        verbose_name = 'Donación'
        verbose_name_plural = 'Donaciones'