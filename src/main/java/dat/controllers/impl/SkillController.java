package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.SkillDAO;
import dat.dtos.SkillDTO;
import dat.entities.Skill;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class SkillController implements IController<SkillDTO, Integer> {

    private final SkillDAO dao;

    public SkillController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = SkillDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        // Request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // DTO
        SkillDTO skillDTO = dao.read(id);
        // Response
        if (skillDTO != null) {
            ctx.res().setStatus(200);
            ctx.json(skillDTO, SkillDTO.class);
        } else {
            ctx.res().setStatus(404);
            ctx.json("Skill not found");
        }
    }

    @Override
    public void readAll(Context ctx) {
        // List of DTOS
        List<SkillDTO> skillDTOS = dao.readAll();
        // response
        ctx.res().setStatus(200);
        ctx.json(skillDTOS, SkillDTO.class);
    }

    @Override
    public void create(Context ctx) {
        // Request
        SkillDTO jsonRequest = ctx.bodyAsClass(SkillDTO.class);
        // DTO
        SkillDTO skillDTO = dao.create(jsonRequest);
        // Response
        ctx.res().setStatus(201);
        ctx.json(skillDTO, SkillDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // dto
        SkillDTO skillDTO = dao.update(id, validateEntity(ctx));
        // response
        ctx.res().setStatus(200);
        ctx.json(skillDTO, Skill.class);
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
    public SkillDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(SkillDTO.class)
                .check(s -> s.getName() != null && !s.getName().isBlank(), "Name must not be null or blank")
                .check(s -> s.getCategory() != null, "Category must not be null or blank")
                .check(s -> s.getDescription() != null && !s.getDescription().isBlank(), "Description must not be null or blank")
                .get();
    }

}