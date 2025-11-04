package dat.routes;

import dat.controllers.impl.SkillController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class SkillRoute {

    private final SkillController skillController = new SkillController();

    protected EndpointGroup getRoutes() {

        return () -> {
            get("/", skillController::readAll, Role.ANYONE);
            post("/", skillController::create, Role.USER);
            get("/{id}", skillController::read, Role.ANYONE);
            put("/{id}", skillController::update, Role.USER);
            delete("/{id}", skillController::delete, Role.USER);
        };
    }
}