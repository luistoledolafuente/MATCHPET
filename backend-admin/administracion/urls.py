from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import *
from .views import *

router = DefaultRouter()
router.register(r'usuarios', UsuarioViewSet)
router.register(r'refugios', RefugioViewSet)
router.register(r'animales', AnimalViewSet)
router.register(r'solicitudes', SolicitudAdopcionViewSet)
router.register(r'donaciones', DonacionViewSet)
router.register(r'adoptantes', AdoptanteViewSet)
router.register(r'donantes', DonanteViewSet)

# Catálogos
router.register(r'roles', RolViewSet)
router.register(r'razas', RazaViewSet)
router.register(r'especies', EspecieViewSet)
router.register(r'temperamentos', TemperamentoViewSet)
router.register(r'generos', GeneroViewSet)
router.register(r'tamanos', TamanoViewSet)
router.register(r'niveles_energia', NivelEnergiaViewSet)
router.register(r'estados_adopcion', EstadoAdopcionViewSet)
router.register(r'estados_solicitud', EstadoSolicitudViewSet)
router.register(r'estados_pago', EstadoPagoViewSet)

urlpatterns = [path('', include(router.urls)), path('dashboard/stats/', dashboard_stats, name='dashboard-stats'),]