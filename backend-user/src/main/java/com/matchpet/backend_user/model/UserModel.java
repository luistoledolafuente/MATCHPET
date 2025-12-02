package com.matchpet.backend_user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
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

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido_paterno", nullable = false, length = 100)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false, length = 100)
    private String apellidoMaterno;

    @Column(nullable = false, length = 20)
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(
            name = "Perfil_Refugio",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "refugio_id")
    )
    private Refugio refugio;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PerfilAdoptante adoptante;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Adoptante_Favoritos",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "animal_id")
    )
    private Set<Animal> favoritos = new HashSet<>();


    // --- MÉTODOS CUSTOM PARA RETROCOMPATIBILIDAD ---

    /**
     * MÉTODOS DE COMPATIBILIDAD. Permite que los servicios antiguos
     * que esperan un solo campo 'nombreCompleto' sigan funcionando.
     */
    public String getNombreCompleto() {
        return this.nombre + " " + this.apellidoPaterno + " " + this.apellidoMaterno;
    }

    // El setNombreCompleto no se necesita en el modelo, solo en los DTOs de registro.

    // --- Métodos de UserDetails ---

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