package org.stackbook;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.stackbook.entity.UserId;
import org.stackbook.entity.UserIdJpaBuddy;
import org.stackbook.entity.UserIdMyBestVar;
import org.stackbook.entity.UserUuid;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@DataJpaTest()
//@Sql({"/scripts/test.sql"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EqualsAndHashCodeTest {

    public static final String USER_UUID_REPO = "userUuidRepo";
    public static final UUID UUID = java.util.UUID.fromString("309b57b4-8b6b-49fe-9654-cc9a97f45c36");

    @Autowired
    private Map<String, JpaRepository> repos;

    private final Set<Object> entities = new HashSet<>();

    public Stream<Arguments> getStreamRepo() {
        return repos.entrySet().stream().map(entry -> Arguments.of(entry.getKey(),
                entry.getValue()));
    }

    @AfterAll
    public void after() {
        entities.clear();
    }

    @ParameterizedTest(name = "Repository name: ''{0}''")
    @MethodSource("getStreamRepo")
    public void testContainsSet_withoutTransactionalWithProxy(String nameRepo, JpaRepository repo) {
        Object userProxy = getUserReferenceById(nameRepo, repo);

        entities.add(userProxy);
        var user = getUser(nameRepo);

        Assertions.assertTrue(entities.contains(user));
    }

    private Object getUserReferenceById(String nameRepo, JpaRepository repo) {
        if (USER_UUID_REPO.equals(nameRepo)) {
            return repo.getReferenceById(UUID);
        }
        return repo.getReferenceById(1L);
    }

    private Object getUser(String repoName) {
        return switch (repoName) {
            case "userIdJpaBuddyRepo" -> {
                var user = new UserIdJpaBuddy();
                user.setId(1L);
                yield user;
            }
            case "userIdMyBestVarRepo" -> {
                var user = new UserIdMyBestVar();
                user.setId(1L);
                yield user;
            }
            case "userIdRepo" -> {
                var user = new UserId();
                user.setId(1L);
                yield user;
            }
            case USER_UUID_REPO -> {
                var user = new UserUuid();
                user.setId(UUID);
                yield user;
            }
            default -> throw new IllegalStateException("Unexpected value: " + repoName);
        };
    }
}
