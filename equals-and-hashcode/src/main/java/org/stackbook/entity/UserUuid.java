package org.stackbook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

/**
 * Base recommended by Vlad Mihalcea
 * <p>
 * pros:
 * - simple code
 * - support Proxy with Base class for equals
 * - consistency in hash structures
 * cons:
 * - hard to check whether an entity is newly created or persisted
 * - query by example (QBE)
 * - it will be hard to guarantee consistent test data
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "USERS_UUID")
public class UserUuid {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id = UUID.randomUUID();

    private String name;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserUuid user)) return false;
        return getId() == user.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
