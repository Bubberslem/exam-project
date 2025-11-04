package dat.entities;


import dat.dtos.CandidateDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "candidate")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id", nullable = false, unique = true)
    private Integer id;

    @Setter
    @Column(name = "candidate_name", nullable = false)
    private String name;

    @Setter
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Setter
    @Column(name = "education_background", nullable = false)
    private String educationBackground;

    @JoinTable(name = "candidate_skill", joinColumns = {@JoinColumn(name = "candidate_id", referencedColumnName = "candidate_id")}, inverseJoinColumns = {@JoinColumn(name = "skill_id", referencedColumnName = "skill_id")})
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Set<Skill> skills = new HashSet<>();

    public Candidate(String name, String phone, String educationBackground) {
        this.name = name;
        this.phone = phone;
        this.educationBackground = educationBackground;
    }


    public Candidate(CandidateDTO candidateDTO) {
        this.id = candidateDTO.getId();
        this.name = candidateDTO.getName();
        this.phone = candidateDTO.getPhone();
        this.educationBackground = candidateDTO.getEducationBackground();
        if (candidateDTO.getSkills() != null) {
            candidateDTO.getSkills().forEach(skillDTO -> skills.add(new Skill(skillDTO)));
        }
    }

    // Bi-directional relationship for all skills in a candidate
    public void setSkills(Set<Skill> skills) {
        if (skills != null) {
            this.skills = skills;
            for (Skill skill : skills) {
                skill.getCandidates().add(this);
            }
        }
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, phone);
    }
}
