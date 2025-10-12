/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.AccountStatus;
import com.example.emob.constant.Gender;
import com.example.emob.constant.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Account")
public class Account implements UserDetails {
  @Id @UuidGenerator UUID id;

  String fullName;

  @Enumerated(EnumType.STRING)
  Gender gender;

  @Enumerated(EnumType.STRING)
  AccountStatus status;

  String address;

  LocalDate dateOfBirth;

  @Enumerated(EnumType.STRING)
  Role role;

  @Column(unique = true)
  String phone;

  @Column(unique = true)
  String email;

  String password;

  @ManyToOne
  @JoinColumn(name = "dealer_id")
  @JsonIgnore
  Dealer dealer;

  @OneToMany(mappedBy = "createBy", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  Set<Report> reports = new HashSet<>();

  @OneToMany(mappedBy = "salesperson", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  Set<TestDrive> testDrives = new HashSet<>();

  @OneToMany(mappedBy = "createBy", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  Set<Promotion> promotions = new HashSet<>();

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<Quotation> quotations = new HashSet<>();

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    if (this.role != null) {
      authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role));
    }
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
