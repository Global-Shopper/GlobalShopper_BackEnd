package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.GenderEnum;
import com.sep490.gshop.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        })
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User extends BaseEntity {

    private String name;
    private String email;
    private String password;
    private long dateOfBirth;

    private String phone;

    private String address;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FCMToken> tokens;

    private boolean isActive = true;

    private boolean isEmailVerified = false;

    public List<String> getFCMTokenList() {
        return tokens.stream().filter(FCMToken::getIsActive).map(FCMToken::getToken).toList();
    }

}
