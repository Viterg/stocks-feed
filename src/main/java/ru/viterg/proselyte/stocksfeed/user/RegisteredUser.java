package ru.viterg.proselyte.stocksfeed.user;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
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
public class RegisteredUser implements Auditable<RegisteredUser, Long, Instant>, UserDetails {

    @Id
    @Column("id")
    private Long id;

    @Column("username")
    private String username;

    @Email
    @Column("email")
    private String email;

    @Column("password_hash")
    private String password;

    @Column("role")
    private Role role;

    @Column("activation_key")
    private String activationKey;

    @Column("apikey")
    private String apiKey;

    @Column("is_active")
    private boolean isActive;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;

    @CreatedBy
    @Column("created_by")
    private RegisteredUser createdBy;

    @LastModifiedBy
    @Column("updated_by")
    private RegisteredUser updatedBy;

    @Override
    public boolean isNew() {
        return id == null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
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
        return Optional.ofNullable(createdBy);
    }

    @Override
    public void setCreatedBy(RegisteredUser createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Optional<Instant> getCreatedDate() {
        return Optional.ofNullable(createdAt);
    }

    @Override
    public void setCreatedDate(Instant creationDate) {
        createdAt = creationDate;
    }

    @Override
    public Optional<RegisteredUser> getLastModifiedBy() {
        return Optional.ofNullable(updatedBy);
    }

    @Override
    public void setLastModifiedBy(RegisteredUser lastModifiedBy) {
        updatedBy = lastModifiedBy;
    }

    @Override
    public Optional<Instant> getLastModifiedDate() {
        return Optional.ofNullable(updatedAt);
    }

    @Override
    public void setLastModifiedDate(Instant lastModifiedDate) {
        updatedAt = lastModifiedDate;
    }
}
