from django.contrib import admin
from django.utils.html import format_html
from django import forms
import bcrypt
from .models import (
    UsuarioSpring, UsuarioRol, PerfilAdoptante, PerfilRefugio,
    Refugio, Animal, AnimalFoto, SolicitudAdopcion, Donacion, Donante,
    Raza, Especie, Temperamento, Rol,
    EstadoSolicitud, EstadoAdopcion, EstadoPago, Genero, Tamano, NivelEnergia
)

# Configuración del Header del Panel
admin.site.site_header = "MatchPet - Panel de Control"
admin.site.site_title = "MatchPet Admin"
admin.site.index_title = "Administración del Sistema"


# --- INLINES (Para ver relaciones dentro de otras fichas) ---

class AnimalFotoInline(admin.TabularInline):
    model = AnimalFoto
    extra = 1
    fields = ('url_foto', 'es_principal', 'preview_imagen')
    readonly_fields = ('preview_imagen',)

    def preview_imagen(self, obj):
        if obj.url_foto:
            return format_html('<img src="{}" style="height: 50px; border-radius: 5px;" />', obj.url_foto)
        return "Sin imagen"


class PerfilAdoptanteInline(admin.StackedInline):
    model = PerfilAdoptante
    can_delete = False
    verbose_name_plural = 'Perfil de Adoptante'


class UsuarioSpringAdminForm(forms.ModelForm):
    password_nueva = forms.CharField(
        widget=forms.PasswordInput,
        required=False,
        label="Contraseña (Login)",
        help_text="Escribe aquí para asignar/cambiar la contraseña. Se encriptará automáticamente para Spring Boot."
    )

    class Meta:
        model = UsuarioSpring
        fields = '__all__'
        widgets = {
            'hash_contrasena': forms.HiddenInput(),
            'esta_activo_raw': forms.CheckboxInput(), # Para editar el bit(1) como checkbox
        }

    def save(self, commit=True):
        usuario = super().save(commit=False)
        password = self.cleaned_data.get('password_nueva')
        if password:
            # Encriptación compatible con Spring Security (BCrypt)
            salt = bcrypt.gensalt()
            hashed = bcrypt.hashpw(password.encode('utf-8'), salt)
            usuario.hash_contrasena = hashed.decode('utf-8')
        if commit:
            usuario.save()
        return usuario

# --- INLINES (Pestañas dentro del Usuario) ---

class UsuarioRolInline(admin.TabularInline):
    model = UsuarioRol
    extra = 1
    max_num = 1
    verbose_name = "Asignar Rol"
    verbose_name_plural = "Rol del Usuario (Obligatorio)"
    # Esto permite seleccionar 'Adoptante' o 'Refugio' desde un dropdown

class PerfilAdoptanteInline(admin.StackedInline):
    model = PerfilAdoptante
    can_delete = False
    verbose_name = "Datos de Adoptante"
    verbose_name_plural = "Perfil de Adoptante (Llenar si es Adoptante)"
    extra = 0 # No se muestra abierto por defecto, solo si se agrega

class PerfilRefugioInline(admin.StackedInline):
    model = PerfilRefugio
    can_delete = False
    verbose_name = "Vinculación con Refugio"
    verbose_name_plural = "Perfil de Refugio (Llenar si es Refugio)"
    extra = 0


@admin.register(UsuarioSpring)
class UsuarioSpringAdmin(admin.ModelAdmin):
    form = UsuarioSpringAdminForm

    # Columnas que ves en la lista principal
    list_display = ('id', 'email', 'nombre_completo', 'ver_rol', 'ver_activo')
    list_filter = ('usuariorol__rol__nombre_rol', 'esta_activo_raw')
    search_fields = ('email', 'nombre', 'apellido_paterno')

    # AQUÍ OCURRE LA MAGIA: Todo en una sola pantalla
    inlines = [UsuarioRolInline, PerfilAdoptanteInline, PerfilRefugioInline]

    def nombre_completo(self, obj):
        return f"{obj.nombre} {obj.apellido_paterno}"

    # Ver si está activo (convirtiendo el binario a boolean visual)
    @admin.display(boolean=True, description='Activo')
    def ver_activo(self, obj):
        if isinstance(obj.esta_activo_raw, bytes):
            return obj.esta_activo_raw == b'\x01'
        return bool(obj.esta_activo_raw)

    # Ver el rol en la lista
    def ver_rol(self, obj):
        # 'usuariorol' es el nombre automático que Django le da a la relación inversa
        if hasattr(obj, 'usuariorol'):
            return obj.usuariorol.rol.nombre_rol
        return "-"

    ver_rol.short_description = "Rol"


@admin.register(Animal)
class AnimalAdmin(admin.ModelAdmin):
    list_display = ('id', 'nombre', 'mostrar_foto', 'raza', 'refugio', 'estado_adopcion', 'compatible_ninos')
    list_filter = ('estado_adopcion', 'refugio', 'genero', 'raza__especie')
    search_fields = ('nombre', 'refugio__nombre', 'raza__nombre')
    inlines = [AnimalFotoInline]  # ¡Aquí está la magia de las fotos!

    def mostrar_foto(self, obj):
        # Busca la foto principal
        foto = obj.fotos.filter(es_principal_raw=b'\x01').first()
        if not foto:
            foto = obj.fotos.first()

        if foto and foto.url_foto:
            return format_html('<img src="{}" style="height: 40px; border-radius: 50%; object-fit: cover;" />',
                               foto.url_foto)
        return "-"

    mostrar_foto.short_description = "Foto"


@admin.register(Refugio)
class RefugioAdmin(admin.ModelAdmin):
    list_display = ('id', 'nombre', 'ciudad', 'email', 'telefono')
    search_fields = ('nombre', 'email')
    list_filter = ('ciudad', 'pais')


@admin.register(SolicitudAdopcion)
class SolicitudAdmin(admin.ModelAdmin):
    list_display = ('id', 'usuario_email', 'animal', 'estado_solicitud', 'fecha_solicitud')
    list_filter = ('estado_solicitud', 'fecha_solicitud')
    search_fields = ('usuario__email', 'animal__nombre')

    def usuario_email(self, obj):
        return obj.usuario.email

    usuario_email.short_description = "Solicitante"


@admin.register(Donacion)
class DonacionAdmin(admin.ModelAdmin):
    list_display = ('id', 'donante_nombre', 'monto', 'moneda', 'estado_pago', 'fecha_donacion')
    list_filter = ('estado_pago', 'fecha_donacion')

    def donante_nombre(self, obj):
        return obj.donante.nombre_completo

    donante_nombre.short_description = "Donante"


@admin.register(Donante)
class DonanteAdmin(admin.ModelAdmin):
    list_display = ('id', 'nombre_completo', 'email', 'fecha_creacion')
    search_fields = ('nombre_completo', 'email')


@admin.register(PerfilAdoptante)
class PerfilAdoptanteAdmin(admin.ModelAdmin):
    list_display = ('usuario', 'ciudad', 'pais', 'fecha_nacimiento')
    search_fields = ('usuario__email',)


# --- REGISTRO DE CATÁLOGOS (Simples) ---
admin.site.register(Raza)
admin.site.register(Especie)
admin.site.register(Temperamento)
admin.site.register(Rol)
admin.site.register(EstadoSolicitud)
admin.site.register(EstadoAdopcion)
admin.site.register(EstadoPago)
admin.site.register(Genero)
admin.site.register(Tamano)
admin.site.register(NivelEnergia)