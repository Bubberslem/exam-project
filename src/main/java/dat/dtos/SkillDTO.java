package dat.dtos;


import dat.entities.Skill;
import dat.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO {
    private Integer id;
    private String name;
    private Category category;
    private String description;

    public SkillDTO(Skill skill) {
        this.id = skill.getId();
        this.name = skill.getName();
        this.category = skill.getCategory();
        this.description = skill.getDescription();
    }
}
