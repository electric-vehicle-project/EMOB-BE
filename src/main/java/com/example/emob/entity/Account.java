package com.example.emob.entity;
import com.example.emob.constant.AccountStatus;
import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.Gender;
import com.example.emob.constant.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account implements UserDetails {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", unique = true, updatable = false, nullable = false)
    UUID id;

    String fullName;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Enumerated(EnumType.STRING)
    AccountStatus status;

    String address;

    int age;

    LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    Role role;

    @Column (unique = true)
    @Pattern(regexp = "^[0]\\d{9}$", message = "Phone is invalid")
    String phone;

    @Column (unique = true)
    @Email
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Email is invalid")
    String email;

    @Pattern(regexp = "^.{8,}$", message = "Password must be at least 8 characters long")
    String password;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (this.role != null) authorities.add(new SimpleGrantedAuthority(this.role.toString()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
