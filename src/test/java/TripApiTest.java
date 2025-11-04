import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.daos.impl.CandidateDAO;
import dat.daos.impl.SkillDAO;
import dat.dtos.CandidateDTO;
import dat.dtos.SkillDTO;
import dat.enums.Category;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TripApiTest {

    private Javalin app;
    private static EntityManagerFactory emf;
    private static SkillDAO skillDAO;
    private static CandidateDAO candidateDAO;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @BeforeAll
    void setUp()  {
        // Configure Hibernate for tests
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        skillDAO = SkillDAO.getInstance(emf);
        candidateDAO = CandidateDAO.getInstance(emf);

        // Start Javalin app
        app = ApplicationConfig.startServer(7070);
        RestAssured.baseURI = "http://localhost:7070/api";
    }

    @BeforeEach
    void cleanDatabase() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // truncate relevant tables (adjust names if different in your schema)
            em.createNativeQuery("TRUNCATE TABLE candidate_skill RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE candidate RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE skill RESTART IDENTITY CASCADE").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @Test
    void testCreateAndReadSkill() {
        // create skill via DAO to avoid auth
        SkillDTO created = skillDAO.create(new SkillDTO(null, "Java", Category.PROG_LANG, "General-purpose language"));

        // verify GET /skill returns the created skill
        when()
                .get("/skill/" + created.getId())
        .then()
                .statusCode(200)
                .body("name", equalTo("Java"))
                .body("category", equalTo("PROG_LANG"))
                .body("description", equalTo("General-purpose language"));
    }

    @Test
    void testCreateCandidateWithSkillAndRead() {
        // create skill first
        SkillDTO skill = skillDAO.create(new SkillDTO(null, "PostgreSQL", Category.DB, "Relational DB"));

        // create candidate referencing existing skill (only id required)
        CandidateDTO candidateReq = new CandidateDTO();
        candidateReq.setName("Alice");
        candidateReq.setPhone("12345678");
        candidateReq.setEducationBackground("CS");
        candidateReq.setSkills(Set.of(new SkillDTO(skill.getId(), null, null, null)));

        CandidateDTO created = candidateDAO.create(candidateReq);

        // verify GET /candidate/{id}
        when()
                .get("/candidate/" + created.getId())
        .then()
                .statusCode(200)
                .body("name", equalTo("Alice"))
                .body("skills.size()", equalTo(1))
                .body("skills[0].name", equalTo("PostgreSQL"));
    }

    @AfterAll
    void tearDown() {
        if (app != null) ApplicationConfig.stopServer(app);
        if (emf != null) emf.close();
    }
}
