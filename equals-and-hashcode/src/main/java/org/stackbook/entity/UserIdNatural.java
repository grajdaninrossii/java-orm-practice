package org.stackbook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import java.util.Objects;

/**
 * NaturalId recommended by Vlad Mihalcea
 * <p>
 * pros:
 * - simple code
 * - consistency in hash structures
 * - the most variant for business keys
 * cons:
 * - don't support Proxy with Base class for equals and hashCode
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class UserIdNatural {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @NaturalId
    private String gen;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserIdNatural user)) return false;
        return Objects.equals(getGen(), user.getGen());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGen());
    }
}