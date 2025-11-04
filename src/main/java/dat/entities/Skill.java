package dat.entities;


import dat.dtos.SkillDTO;
import dat.enums.Category;
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
@Table(name = "skill")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id", nullable = false, unique = true)
    private Integer id;

    @Setter
    @Column(name = "skill_name", nullable = false)
    private String name;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "skill_category", nullable = false)
    private Category category;

    @Setter
    @Column(name = "description", nullable = false)
    private String description;

    @ManyToMany(mappedBy = "skills")
    private Set<Candidate> candidates = new HashSet<>();


    public Skill(String name, Category category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
    }

    public Skill(SkillDTO skillDTO){
        this.id = skillDTO.getId();
        this.name = skillDTO.getName();
        this.category = skillDTO.getCategory();
        this.description = skillDTO.getDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skill skill)) return false;
        return id != null && id.equals(skill.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
