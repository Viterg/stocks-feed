package ru.viterg.proselyte.stocksfeed.user;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Auditable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "users", schema = "public")
public class RegisteredUser implements Auditable<RegisteredUser, Integer, Instant>, UserDetails {

    @Id
    @Column("id")
    private Integer id;

    @Column("username")
    private String username;

    @Email
    @Column("email")
    private String email;

    @Column("password_hash")
    private String password;

    @Column("role")
    private String role;

    @Column("activation_key")
    private String activationKey;

    @Column("is_active")
    private boolean isActive;

    @Override
    public boolean isNew() {
        return id == null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Role.valueOf(role).getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    @Override
    public Optional<RegisteredUser> getCreatedBy() {
        return Optional.empty();
    }

    @Override
    public void setCreatedBy(RegisteredUser createdBy) {

    }

    @Override
    public Optional<Instant> getCreatedDate() {
        return Optional.empty();
    }

    @Override
    public void setCreatedDate(Instant creationDate) {

    }

    @Override
    public Optional<RegisteredUser> getLastModifiedBy() {
        return Optional.empty();
    }

    @Override
    public void setLastModifiedBy(RegisteredUser lastModifiedBy) {

    }

    @Override
    public Optional<Instant> getLastModifiedDate() {
        return Optional.empty();
    }

    @Override
    public void setLastModifiedDate(Instant lastModifiedDate) {

    }
}
