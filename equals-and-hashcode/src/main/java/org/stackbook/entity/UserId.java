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
 * - consistency in hash structures (only in Transaction)
 * cons:
 * - violation of the equals and hashcode contract
 * - the hashCode will create query for Proxy
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class UserId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserId that)) return false;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
