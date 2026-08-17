package com.example.oulearning;

import static org.mockito.Mockito.mock;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Import(OulearningApplicationTests.TestConfig.class)
@TestPropertySource(properties = "spring.flyway.enabled=false")
class OulearningApplicationTests {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public DataSource dataSource() {
            return mock(DataSource.class);
        }

        @Bean
        public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
            final var factoryBean = new SqlSessionFactoryBean();
            factoryBean.setDataSource(dataSource);
            final var configuration = new org.apache.ibatis.session.Configuration();
            configuration.setMapUnderscoreToCamelCase(true);
            factoryBean.setConfiguration(configuration);
            return factoryBean.getObject();
        }
    }

    @Test
    void contextLoads() {}
}
