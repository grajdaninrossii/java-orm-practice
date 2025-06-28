package org.stackbook.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Lombok standard
 * <p>
 * pros:
 * - simple code
 * cons:
 * - don't consistency in hash structures
 * - don't support Proxy with Base class for equals and hashCode
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserIdLombokInclude {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
