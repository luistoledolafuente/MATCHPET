from django import forms
import bcrypt
from .models import UsuarioSpring


class UsuarioSpringAdminForm(forms.ModelForm):
    # Campo "falso" para escribir la contraseña limpia
    password_nueva = forms.CharField(
        widget=forms.PasswordInput,
        required=False,
        label="Establecer/Cambiar Contraseña",
        help_text="Deja esto vacío si no quieres cambiar la contraseña."
    )

    class Meta:
        model = UsuarioSpring
        fields = '__all__'
        # Ocultamos el campo original para no editar el hash crudo accidentalmente
        widgets = {
            'hash_contrasena': forms.HiddenInput(),
        }

    def save(self, commit=True):
        usuario = super().save(commit=False)

        password = self.cleaned_data.get('password_nueva')
        if password:
            # Generar hash BCrypt compatible con Spring Boot ($2b$...)
            # .encode() convierte string a bytes, .decode() vuelve a string para guardar en DB
            salt = bcrypt.gensalt()
            hashed = bcrypt.hashpw(password.encode('utf-8'), salt)
            usuario.hash_contrasena = hashed.decode('utf-8')

        if commit:
            usuario.save()
        return usuario