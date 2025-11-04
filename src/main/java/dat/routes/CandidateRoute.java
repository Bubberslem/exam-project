package dat.routes;

import dat.controllers.impl.CandidateController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class CandidateRoute {
    private final CandidateController candidateController = new CandidateController();

    protected EndpointGroup getRoutes() {
        return () -> {
            // Specialized Endpoints
            post("/populate", candidateController::populate, Role.ANYONE);
            // Link existing Skill to Candidate
            put("/{candidateId}/skill/{skillId}", candidateController::addSkillToCandidate, Role.USER);

            // CRUD Endpoints
            get("/", candidateController::readAll, Role.ANYONE);
            post("/", candidateController::create, Role.USER);
            get("/{id}", candidateController::read, Role.ANYONE);
            put("/{id}", candidateController::update, Role.USER);
            delete("/{id}", candidateController::delete, Role.USER);

        };
    }
}
