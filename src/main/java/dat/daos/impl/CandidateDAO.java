package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.CandidateDTO;
import dat.entities.Candidate;
import dat.entities.Skill;
import dat.enums.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CandidateDAO implements IDAO<CandidateDTO, Integer> {

    private static CandidateDAO instance;
    private static EntityManagerFactory emf;

    Set<Skill> marcusSkills = getMarcusSkills();
    Set<Skill> madsSkills = getMadsSkills();

    public static CandidateDAO getInstance(EntityManagerFactory _emf) {
        emf = _emf;
        if (instance == null) {
            instance = new CandidateDAO();
        }
        return instance;
    }


    @Override
    public CandidateDTO read(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Candidate candidate = em.find(Candidate.class, id);
            if (candidate != null) {
                return new CandidateDTO(candidate);
            } else {
                return null;
            }
        }
    }

    @Override
    public List<CandidateDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<CandidateDTO> query = em.createQuery("SELECT new dat.dtos.CandidateDTO(c) FROM Candidate c", CandidateDTO.class);
            return query.getResultList();
        }
    }

    // Return only candidates who have at least one skill with the given category
    public List<CandidateDTO> readAllByCategory(Category category) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Candidate> query = em.createQuery(
                    "SELECT DISTINCT c FROM Candidate c JOIN c.skills s WHERE s.category = :cat",
                    Candidate.class);
            query.setParameter("cat", category);
            List<Candidate> candidates = query.getResultList();
            return candidates.stream().map(CandidateDTO::new).collect(Collectors.toList());
        }
    }

    @Override
    public CandidateDTO create(CandidateDTO candidateDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Candidate candidate = new Candidate();
            candidate.setName(candidateDTO.getName());
            candidate.setPhone(candidateDTO.getPhone());
            candidate.setEducationBackground(candidateDTO.getEducationBackground());

            // Safely handle both new and existing skills
            if (candidateDTO.getSkills() != null && !candidateDTO.getSkills().isEmpty()) {
                Set<Skill> skills = candidateDTO.getSkills().stream()
                        .map(skillDTO -> {
                            if (skillDTO.getId() != null) {
                                // Existing skill in DB → just reference it
                                return em.getReference(Skill.class, skillDTO.getId());
                            } else {
                                // New skill (no ID) → create a new entity
                                return new Skill(skillDTO);
                            }
                        })
                        .collect(Collectors.toSet());
                candidate.setSkills(skills);
            }

            em.persist(candidate);
            em.getTransaction().commit();

            return new CandidateDTO(candidate);
        }
    }


    @Override
    public CandidateDTO update(Integer integer, CandidateDTO candidateDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate candidate = em.find(Candidate.class, integer);
            candidate.setName(candidateDTO.getName());
            candidate.setPhone(candidateDTO.getPhone());
            candidate.setEducationBackground(candidateDTO.getEducationBackground());
            Candidate mergedCandidate = em.merge(candidate);
            em.getTransaction().commit();
            return mergedCandidate != null ? new CandidateDTO(mergedCandidate) : null;
        }
    }

    @Override
    public void delete(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate candidate = em.find(Candidate.class, integer);
            if (candidate != null) {
                em.remove(candidate);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Candidate candidate = em.find(Candidate.class, integer);
            return candidate != null;
        }
    }

    public void populate() {
        try(var em = emf.createEntityManager()){
            em
                    .getTransaction()
                    .begin();
            Candidate marcus = new Candidate("Marcus", "0912345678", "Datamatiker");
            Candidate mads = new Candidate("Mads", "0987654321", "Database Developer");
            marcus.setSkills(marcusSkills);
            mads.setSkills(madsSkills);
            em.persist(marcus);
            em.persist(mads);
            em
                    .getTransaction()
                    .commit();
        }
    }

    private static Set<Skill> getMarcusSkills() {
        Skill javaSkill = new Skill("Java", Category.PROG_LANG, "Proficient in Java SE and EE");
        Skill sqlSkill = new Skill("PostgreSQL", Category.DB, "Experienced in writing complex SQL queries");
        return Set.of(javaSkill, sqlSkill);
    }

    private static Set<Skill> getMadsSkills() {
        Skill pythonSkill = new Skill("Python Programming", Category.PROG_LANG, "Experienced in data analysis with Python");
        Skill frameworkSkill = new Skill("Spring Boot", Category.FRAMEWORK, "Skilled in building web applications using Django");
        return Set.of(pythonSkill, frameworkSkill);
    }

    // Add a method to link an existing skill to a candidate
    public CandidateDTO addSkillToCandidate(Integer candidateId, Integer skillId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate candidate = em.find(Candidate.class, candidateId);
            Skill skill = em.find(Skill.class, skillId);

            if (candidate == null || skill == null) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                return null;
            }

            // ensure both sides of relationship are updated
            candidate.getSkills().add(skill);
            skill.getCandidates().add(candidate);

            Candidate merged = em.merge(candidate);
            em.getTransaction().commit();
            return new CandidateDTO(merged);
        }
    }
}