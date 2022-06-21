package com.tma.recruit.model.entity;

import com.tma.recruit.model.enums.TokenType;
import com.tma.recruit.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "token")
public class Token extends BaseEntity {

    @Lob
    @Column(name = "token")
    private String token;

    @Column(name = "expired_time")
    private Date expiredTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TokenType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Token(String token, TokenType type) {
        this.token = token;
        this.type = type;
        if (type.equals(TokenType.PASSWORD_RESET_TOKEN)) {
            expiredTime = new Date(new Date().getTime() + Constant.PASSWORD_RESET_TOKEN_EXPIRATION_TIME);
        } else if (type.equals(TokenType.ACCESS_TOKEN)) {
            expiredTime = new Date(new Date().getTime() + Constant.ACCESS_TOKEN_EXPIRATION_TIME);
        }
    }

    public Token(String token, TokenType type, User user) {
        this(token, type);
        this.user = user;
    }
}