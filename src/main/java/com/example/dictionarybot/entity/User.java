package com.example.dictionarybot.entity;

import com.example.dictionarybot.entity.enums.Menu;
import com.example.dictionarybot.entity.enums.Role;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long chatId;
    private String name;
    private String username;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Menu menu;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Unit selectedUnit;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Book selectedBook;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}