package org.example.lesson1First.entity.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.lesson1First.enums.TokenType;

import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "token")
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean revoked;
    private boolean expired;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_password_email")
    private UserPassword user;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TokenEntity that = (TokenEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(token, that.token) && tokenType == that.tokenType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, tokenType);
    }

    @Override
    public String toString() {
        return "TokenEntity{" +
               "expired=" + expired +
               ", revoked=" + revoked +
               ", tokenType=" + tokenType +
               ", token='" + token + '\'' +
               ", id=" + id +
               '}';
    }
}
