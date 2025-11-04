package dat.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final CandidateRoute candidateRoute = new CandidateRoute();
    private final SkillRoute skillRoute = new SkillRoute();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", ctx -> ctx.result("Welcome to the API"));
            path("/candidate", candidateRoute.getRoutes());
            path("/skill", skillRoute.getRoutes());
        };
    }
}