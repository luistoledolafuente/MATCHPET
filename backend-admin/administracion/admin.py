from django.contrib import admin
from django.utils.html import format_html
from .models import (
    UsuarioSpring, PerfilAdoptante, Refugio, Animal, AnimalFoto,
    SolicitudAdopcion, Donacion, Donante,
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


# --- ADMINS PERSONALIZADOS ---

@admin.register(UsuarioSpring)
class UsuarioSpringAdmin(admin.ModelAdmin):
    list_display = ('id', 'email', 'nombre_completo', 'telefono', 'ver_activo', 'fecha_creacion_perfil')
    search_fields = ('email', 'nombre', 'apellido_paterno')
    # Opcional: Mostrar perfil inline si existe
    inlines = [PerfilAdoptanteInline]

    def nombre_completo(self, obj):
        return f"{obj.nombre} {obj.apellido_paterno}"

    # Método para mostrar el icono boolean (✅/❌) basado en la propiedad
    @admin.display(boolean=True, description='Activo')
    def ver_activo(self, obj):
        return obj.esta_activo


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