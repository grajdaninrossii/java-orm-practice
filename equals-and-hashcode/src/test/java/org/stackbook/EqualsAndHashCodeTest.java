package org.stackbook;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.stackbook.entity.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@Sql(scripts = "/scripts/test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EqualsAndHashCodeTest {

    private static final Class<?> USER_UUID = UserUuid.class;
    private static final UUID UUID = java.util.UUID.fromString("309b57b4-8b6b-49fe-9654-cc9a97f45c36");
    private static final UUID UUID_OTHER = java.util.UUID.fromString("309b57b4-8b6b-49fe-9654-cc9a97f45c37");
    private static final Map<Class<?>, Function<Object, Object>> FABRIC_ENTITIES = Map.of(
            UserIdMyBestVar.class, id -> {
                var user = new UserIdMyBestVar();
                user.setId((Long) id);
                return user;
            },
            UserIdJpaBuddy.class, id -> {
                var user = new UserIdJpaBuddy();
                user.setId((Long) id);
                return user;
            },
            UserId.class, id -> {
                var user = new UserId();
                user.setId((Long) id);
                return user;
            },
            UserIdLombokBase.class, id -> {
                var user = new UserIdLombokBase();
                user.setId((Long) id);
                return user;
            },
            UserIdLombokInclude.class, id -> {
                var user = new UserIdLombokInclude();
                user.setId((Long) id);
                return user;
            },
            UserIdDefault.class, id -> {
                var user = new UserIdDefault();
                user.setId((Long) id);
                return user;
            },
            UserIdNatural.class, id -> {
                var user = new UserIdNatural();
                user.setId((Long) id);
                user.setGen("gen");
                return user;
            },
            USER_UUID, id -> {
                var user = new UserUuid();
                if (id == null) {
                    return user;
                }
                user.setId((UUID) id);
                return user;
            }
    );

    @Autowired
    private SessionFactory sessionFactory;

    @PersistenceContext
    private EntityManager entityManager;

    private Statistics stats;

    private final Set<Object> entities = new HashSet<>();

    public Stream<Arguments> getStreamRepo() {
        return FABRIC_ENTITIES.keySet().stream().map(Arguments::of);
    }

    @BeforeEach
    void setUp() {
        stats = sessionFactory.getStatistics();
        stats.clear();
    }

    @AfterEach
    public void after() {
        entities.clear();
    }

    @MethodSource("getStreamRepo")
    @ParameterizedTest(name = "Имя сущности: {0}")
    @DisplayName("Нахождение сущности во множестве с прокси объектом вне транзакции")
    public void testContainsSet_withoutTransactionalAndWithProxy(Class<?> type) {
        Object userProxy = getUserReferenceById(type);

        entities.add(userProxy);
        var user = FABRIC_ENTITIES.get(type).apply(USER_UUID.equals(type) ? UUID : 1L);

        assertTrue(
                entities.contains(user),
                "The entity is not found in the Set with proxy"
        );
        assertThat(stats.getEntityLoadCount())
                .withFailMessage("The entity added in the set with query in db").isEven();
    }

    @Transactional
    @MethodSource("getStreamRepo")
    @ParameterizedTest(name = "Имя сущности: {0}")
    @DisplayName("Нахождение прокси объекта во множестве с сущностью в транзакции")
    public void testContainsSet_withTransactional(Class<?> type) {
        Object user = findUser(type);

        entities.add(user);
        var userProxy = getUserReferenceById(type);

        assertTrue(
                entities.contains(userProxy),
                "The entity proxy is not found in the Set with entity"
        );
        assertThat(stats.getEntityLoadCount())
                .withFailMessage("The entity added in the set with query in db").isOne();
    }

    @Transactional
    @MethodSource("getStreamRepo")
    @ParameterizedTest(name = "Имя сущности: {0}")
    @DisplayName("Отсутствие сущности с другим идентификатором")
    public void testContainsSet_withOtherId(Class<?> type) {
        Object user = findUser(type);

        entities.add(user);
        var userObject = FABRIC_ENTITIES.get(type).apply(USER_UUID.equals(type) ? UUID_OTHER : 2L);

        if (UserIdNatural.class.equals(userObject.getClass())) {
            ((UserIdNatural) userObject).setGen("gen2");
        }

        assertFalse(
                entities.contains(userObject),
                "The entity is found in the Set with other entity"
        );
        assertThat(stats.getEntityLoadCount()).isOne();
    }

    @Transactional
    @MethodSource("getStreamRepo")
    @ParameterizedTest(name = "Имя сущности: {0}")
    @DisplayName("Присутствие сущности во множестве после сохранения")
    public void testContainsSet_afterPersist(Class<?> type) {
        Object user = FABRIC_ENTITIES.get(type).apply(null);

        if (UserIdNatural.class.equals(user.getClass())) {
            ((UserIdNatural) user).setGen("gen2");
        }

        entities.add(user);

        entityManager.persist(user);
        entityManager.flush();

        assertTrue(
                entities.contains(user),
                "The saved entity is not found in the Set with entity"
        );
        assertThat(stats.getFlushCount()).isOne();
    }

    @Transactional
    @MethodSource("getStreamRepo")
    @ParameterizedTest(name = "Имя сущности: {0}")
    @DisplayName("Присутствие сущности во множестве после связывания с контекстом")
    public void testContainsSet_afterMerge(Class<?> type) {
        Object user = FABRIC_ENTITIES.get(type).apply(USER_UUID.equals(type) ? UUID : 1L);
        entities.add(user);

        Object savedUser = entityManager.merge(user);

        assertTrue(
                entities.contains(savedUser),
                "The merged entity is not found in the Set with entity"
        );
    }

    private Object getUserReferenceById(Class<?> type) {
        if (USER_UUID.equals(type)) {
            return entityManager.getReference(type, UUID);
        }
        return entityManager.getReference(type, 1L);
    }

    private Object findUser(Class<?> type) {
        if (USER_UUID.equals(type)) {
            return entityManager.find(type, UUID);
        }
        return entityManager.find(type, 1L);
    }
}
