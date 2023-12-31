package com.matchUpSports.boundedContext.member.entity;

import com.matchUpSports.base.Role;
import com.matchUpSports.base.convert.CustomConverter;
import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder(toBuilder = true)
@Where(clause = "delete_date is null")
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime modifiedDate;
    private LocalDateTime deleteDate;
    private String username;
    private String nickname;
    private String email;
    private String phoneNumber;
    private int win;
    private int lose;
    private String bigDistrict;
    private String smallDistrict;
    private int tier;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL})
    @Builder.Default // @Builder 가 있으면 ` = new ArrayList<>();` 가 작동하지 않는다. 그래서 이걸 붙여야 한다.
    private List<FutsalField> futsalFields = new ArrayList<>();

    @Convert(converter = CustomConverter.class)
    @Builder.Default
    private Set<Role> authorities = new HashSet<>();

    private String accessToken;

    private int point;

    private LocalDateTime accessTokenTime;

    public List<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream().map(i -> new SimpleGrantedAuthority("ROLE_" + i.name())).toList();
    }

    public void addRole(Role role) {
        this.authorities.add(role);
    }

    public void receivePaidPoints(int point) {
        this.point = getPoint() + point;
    }


}