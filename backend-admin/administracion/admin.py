from django.contrib import admin
from django.utils.html import format_html
from .models import (
    Refugio, Animal, AnimalFoto, UsuarioSpring,
    PerfilAdoptante, SolicitudAdopcion, Donacion,
    # Lookups
    Raza, Especie, EstadoSolicitud, EstadoAdopcion
)

admin.site.site_header = "MatchPet - Super Admin"
admin.site.site_title = "MatchPet Admin"
admin.site.index_title = "Bienvenido al Panel de Control"


class AnimalFotoInline(admin.TabularInline):
    model = AnimalFoto
    extra = 1
    fields = ('url_foto', 'es_principal', 'preview_imagen')
    readonly_fields = ('preview_imagen',)

    def preview_imagen(self, obj):
        if obj.url_foto:
            return format_html('<img src="{}" style="height: 50px; border-radius: 5px;" />', obj.url_foto)
        return "Sin imagen"


@admin.register(Animal)
class AnimalAdmin(admin.ModelAdmin):
    list_display = ('nombre', 'mostrar_foto', 'raza', 'get_especie', 'refugio', 'estado_adopcion')
    # CORRECCIÓN: Filtramos por la relación raza__especie, no por especie directo
    list_filter = ('estado_adopcion', 'refugio', 'raza__especie', 'genero')
    search_fields = ('nombre', 'refugio__nombre', 'raza__nombre')
    inlines = [AnimalFotoInline]

    fieldsets = (
        ('Datos Principales', {
            'fields': ('nombre', 'refugio', 'fecha_ingreso_refugio')
        }),
        ('Características', {
            'fields': ('raza', 'genero', 'tamano', 'nivel_energia', 'fecha_nacimiento_aprox')
        }),
        ('Detalles Médicos y Conducta', {
            'fields': (
            'descripcion_personalidad', 'historial_medico', 'esta_vacunado', 'esta_esterilizado', 'compatible_ninos',
            'compatible_otras_mascotas')
        }),
        ('Estado', {
            'fields': ('estado_adopcion', 'fecha_actualizacion')
        }),
    )

    def mostrar_foto(self, obj):
        foto_principal = obj.fotos.filter(es_principal=True).first()
        if foto_principal:
            return format_html('<img src="{}" style="height: 40px; border-radius: 50%;" />', foto_principal.url_foto)
        return "-"

    mostrar_foto.short_description = "Foto"

    def get_especie(self, obj):
        return obj.raza.especie.nombre if obj.raza and obj.raza.especie else "-"

    get_especie.short_description = "Especie"
    get_especie.admin_order_field = 'raza__especie'


@admin.register(Refugio)
class RefugioAdmin(admin.ModelAdmin):
    list_display = ('nombre', 'ciudad', 'email', 'telefono', 'fecha_registro')
    list_filter = ('ciudad', 'pais')
    search_fields = ('nombre', 'email', 'persona_contacto')


@admin.register(SolicitudAdopcion)
class SolicitudAdmin(admin.ModelAdmin):
    list_display = ('id', 'usuario', 'animal', 'estado_solicitud', 'fecha_solicitud')
    list_filter = ('estado_solicitud', 'fecha_solicitud')
    search_fields = ('usuario__email', 'animal__nombre')
    readonly_fields = ('mensaje_adoptante', 'fecha_solicitud')

    fieldsets = (
        ('Información de la Solicitud', {
            'fields': ('usuario', 'animal', 'fecha_solicitud', 'estado_solicitud')
        }),
        ('Mensajes', {
            'fields': ('mensaje_adoptante', 'mensaje_al_adoptante', 'notas_internas')
        }),
    )


@admin.register(UsuarioSpring)
class UsuarioSpringAdmin(admin.ModelAdmin):
    list_display = ('email', 'nombre_completo', 'telefono', 'ver_activo')
    search_fields = ('email', 'nombre', 'apellido_paterno')

    def nombre_completo(self, obj):
        return f"{obj.nombre} {obj.apellido_paterno}"

    @admin.display(boolean=True, description='Activo')
    def ver_activo(self, obj):
        return obj.esta_activo  # Usa la propiedad que creamos en models.py


@admin.register(PerfilAdoptante)
class PerfilAdoptanteAdmin(admin.ModelAdmin):
    list_display = ('usuario', 'ciudad', 'pais')


@admin.register(Donacion)
class DonacionAdmin(admin.ModelAdmin):
    list_display = ('monto', 'fecha_donacion', 'mensaje_donante')
    list_filter = ('fecha_donacion',)


# Registros simples
admin.site.register(Raza)
admin.site.register(Especie)
admin.site.register(EstadoSolicitud)
admin.site.register(EstadoAdopcion)