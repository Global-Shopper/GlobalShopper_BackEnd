package com.sep490.gshop.entity;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fcm_tokens", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"token", "deviceType", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FCMToken extends BaseEntity {

    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;
    private Boolean isActive = true;


    public enum DeviceType {
        ANDROID,
        IOS,
        WEB
    }
}
