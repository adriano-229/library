package com.adriano.library.business.domain.entity;

import com.adriano.library.business.domain.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class User extends BaseEntity {

    @Email
    private String email;

    // BCrypt encoding on service layer
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER; // Default is User

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();

}
