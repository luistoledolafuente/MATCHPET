package com.matchpet.backend_user.model;

import jakarta.persistence.*; // ¡Asegúrate de que esté importado!
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Usuarios")
public class UserModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "hash_contraseña", nullable = false, columnDefinition = "TEXT")
    private String hashContrasena;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    private String telefono;

    @CreationTimestamp
    @Column(name = "fecha_creacion_perfil", updatable = false)
    private Timestamp fechaCreacionPerfil;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private Timestamp fechaActualizacion;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "Usuario_Roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<RolModel> roles;

    // --- ¡CAMBIO IMPORTANTE AQUÍ! ---
    // Este es el campo que faltaba y que causaba el error en el builder

    /**
     * Un Usuario (con rol "Refugio") puede tener UN perfil de Refugio.
     * Esta anotación crea la relación usando la tabla 'Perfil_Refugio'.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(
            name = "Perfil_Refugio",
            joinColumns = @JoinColumn(name = "usuario_id"), // FK a esta entidad (Usuario)
            inverseJoinColumns = @JoinColumn(name = "refugio_id") // FK a la otra entidad (Refugio)
    )
    private Refugio refugio;

    // --- FIN DEL CAMBIO ---


    // ... (El resto de tus métodos de UserDetails se quedan igual)

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombreRol()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.hashContrasena;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.estaActivo;
    }
}