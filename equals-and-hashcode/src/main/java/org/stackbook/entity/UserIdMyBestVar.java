package org.stackbook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Base recommended by Vlad Mihalcea
 * <p>
 * pros:
 * - simple code
 * - support Proxy with Base class for equals
 * - consistency in hash structures
 * cons:
 * - Cannot be redefined the equals and hashCode
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class UserIdMyBestVar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof UserIdMyBestVar that)) return false;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public final int hashCode() {
        return UserIdMyBestVar.class.hashCode();
    }
}
