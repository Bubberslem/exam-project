package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.SkillDTO;
import dat.entities.Skill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SkillDAO implements IDAO<SkillDTO, Integer> {

    private static SkillDAO instance;
    private static EntityManagerFactory emf;

    public static SkillDAO getInstance(EntityManagerFactory _emf) {
        emf = _emf;
        if (instance == null) {
            instance = new SkillDAO();
        }
        return instance;
    }


    @Override
    public SkillDTO read(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Skill skill = em.find(Skill.class, id);
            if (skill != null) {
                return new SkillDTO(skill);
            } else {
                return null;
            }
        }
    }

    @Override
    public List<SkillDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            // JPQL constructor expression must pass scalar fields matching the DTO constructor
            TypedQuery<SkillDTO> query = em.createQuery(
                "SELECT new dat.dtos.SkillDTO(s.id, s.name, s.category, s.description) FROM Skill s",
                SkillDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public SkillDTO create(SkillDTO skillDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill skill = new Skill();
            // populate fields from DTO before persisting to satisfy NOT NULL DB constraints
            skill.setName(skillDTO.getName());
            skill.setCategory(skillDTO.getCategory());
            skill.setDescription(skillDTO.getDescription());
            em.persist(skill);
            em.getTransaction().commit();
            return new SkillDTO(skill);
        }
    }


    @Override
    public SkillDTO update(Integer integer, SkillDTO skillDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill skill = em.find(Skill.class, integer);
            skill.setName(skillDTO.getName());
            skill.setCategory(skillDTO.getCategory());
            skill.setDescription(skillDTO.getDescription());
            Skill mergedSkill = em.merge(skill);
            em.getTransaction().commit();
            return mergedSkill != null ? new SkillDTO(mergedSkill) : null;

        }
    }

    @Override
    public void delete(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill skill = em.find(Skill.class, integer);
            if (skill != null) {
                em.remove(skill);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Skill skill = em.find(Skill.class, integer);
            return skill != null;
        }
    }
}