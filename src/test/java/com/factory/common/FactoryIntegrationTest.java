package com.factory.common;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;

@DataJpaTest
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {FactoryIntegrationTest.Initializer.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql({
        "file:sql/v0_create_schema.sql",
        "file:sql/v1_create_users_table.sql",
        "file:sql/v2_create_roles_table.sql",
        "file:sql/v3_create_user_roles_table.sql",
})
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FactoryIntegrationTest {

    class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @ClassRule
        public static PostgreSQLContainer postgreSQLContainer = createPostgreSQLContainer();

        private static PostgreSQLContainer createPostgreSQLContainer() {
            try (var result = (PostgreSQLContainer) new PostgreSQLContainer("postgres:10.4")
                    .withDatabaseName("factory_users")
                    .withUsername("factory_data_user")
                    .withPassword("factory_data_user")
                    .withStartupTimeout(Duration.ofSeconds(600))) {
                return result;

            }
        }


        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            postgreSQLContainer.start();
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.sql.init.mode=always",
                    "spring.jpa.defer-datasource-initialization=true"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
