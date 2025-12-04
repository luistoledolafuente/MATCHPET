from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import (
    UsuarioViewSet, RefugioViewSet, AnimalViewSet,
    SolicitudAdopcionViewSet, DonacionViewSet,
    RazaViewSet, EspecieViewSet
)

# El router crea automáticamente las URLs como: /api/animales/, /api/animales/1/, etc.
router = DefaultRouter()
router.register(r'usuarios', UsuarioViewSet)
router.register(r'refugios', RefugioViewSet)
router.register(r'animales', AnimalViewSet)
router.register(r'solicitudes', SolicitudAdopcionViewSet)
router.register(r'donaciones', DonacionViewSet)
router.register(r'razas', RazaViewSet)
router.register(r'especies', EspecieViewSet)

urlpatterns = [
    path('', include(router.urls)),
]