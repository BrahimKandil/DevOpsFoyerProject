package tn.esprit.spring.config;


import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpringDocConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        // Assert config bean loads
        assertThat(context.getBean(SpringDocConfig.class)).isNotNull();

        // Assert OpenAPI bean
        OpenAPI openAPI = context.getBean(OpenAPI.class);
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Gestion d'un foyer");
        assertThat(openAPI.getInfo().getContact().getName()).isEqualTo("Sirine NAIFAR");

        // Assert GroupedOpenApi beans
        assertThat(context.getBean("allPublicApi", GroupedOpenApi.class)).isNotNull();
        assertThat(context.getBean("blocPublicApi", GroupedOpenApi.class)).isNotNull();
        assertThat(context.getBean("chambrePublicApi", GroupedOpenApi.class)).isNotNull();
        assertThat(context.getBean("etudiantPublicApi", GroupedOpenApi.class)).isNotNull();
        assertThat(context.getBean("foyerPublicApi", GroupedOpenApi.class)).isNotNull();
        assertThat(context.getBean("reservationPublicApi", GroupedOpenApi.class)).isNotNull();
        assertThat(context.getBean("universitePublicApi", GroupedOpenApi.class)).isNotNull();
    }
}
