package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.CandidateDAO;
import dat.daos.impl.SkillDAO;
import dat.dtos.CandidateDTO;
import dat.entities.Candidate;
import dat.enums.Category;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class CandidateController implements IController<CandidateDTO, Integer> {

    private final CandidateDAO dao;
    private final SkillDAO skillDao;

    public CandidateController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = CandidateDAO.getInstance(emf);
        this.skillDao = SkillDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        // Request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // DTO
        CandidateDTO candidateDTO = dao.read(id);
        // Response
        if (candidateDTO != null) {
            ctx.res().setStatus(200);
            ctx.json(candidateDTO, CandidateDTO.class);
        } else {
            ctx.res().setStatus(404);
            ctx.json("Candidate not found");
        }
    }

    @Override
    public void readAll(Context ctx) {
        // If a category query param is present, filter by it
        String categoryParam = ctx.queryParam("category");
        if (categoryParam != null && !categoryParam.isBlank()) {
            try {
                Category category = Category.valueOf(categoryParam);
                List<CandidateDTO> candidateDTOS = dao.readAllByCategory(category);
                ctx.res().setStatus(200);
                ctx.json(candidateDTOS, CandidateDTO.class);
                return;
            } catch (IllegalArgumentException e) {
                ctx.res().setStatus(400);
                ctx.json("Invalid category");
                return;
            }
        }

        // No filter: return all candidates
        List<CandidateDTO> candidateDTOS = dao.readAll();
        ctx.res().setStatus(200);
        ctx.json(candidateDTOS, CandidateDTO.class);
    }

    @Override
    public void create(Context ctx) {
        // Request
        CandidateDTO jsonRequest = ctx.bodyAsClass(CandidateDTO.class);
        // DTO
        CandidateDTO candidateDTO = dao.create(jsonRequest);
        // Response
        ctx.res().setStatus(201);
        ctx.json(candidateDTO, CandidateDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // dto
        CandidateDTO candidateDTO = dao.update(id, validateEntity(ctx));
        // response
        ctx.res().setStatus(200);
        ctx.json(candidateDTO, Candidate.class);
    }

    @Override
    public void delete(Context ctx) {
        // Request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        dao.delete(id);

        // Response
        ctx.res().setStatus(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        return dao.validatePrimaryKey(integer);
    }

    @Override
    public CandidateDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(CandidateDTO.class)
                .check(c -> c.getName() != null && !c.getName().isEmpty(), "Name must not be empty")
                .check(c -> c.getPhone() != null && !c.getPhone().isEmpty(), "Phone must not be empty")
                .check(c -> c.getEducationBackground() != null && !c.getEducationBackground().isEmpty(), "Education background must not be empty")
                .get();
    }


    public void populate(Context ctx) {
        dao.populate();
        ctx.res().setStatus(200);
        ctx.json("Candidate data populated");
    }

    // Handler for PUT /candidate/{candidateId}/skill/{skillId}
    public void addSkillToCandidate(Context ctx) {
        // read path params (will throw 400 if not integer)
        int candidateId = ctx.pathParamAsClass("candidateId", Integer.class).get();
        int skillId = ctx.pathParamAsClass("skillId", Integer.class).get();

        CandidateDTO updated = dao.addSkillToCandidate(candidateId, skillId);

        if (updated == null) {
            ctx.res().setStatus(404);
            ctx.json("Candidate or Skill not found");
            return;
        }

        ctx.res().setStatus(200);
        ctx.json(updated, CandidateDTO.class);
    }

}