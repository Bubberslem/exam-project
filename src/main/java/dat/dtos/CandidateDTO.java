package dat.dtos;

import dat.entities.Candidate;
import dat.entities.Skill;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CandidateDTO {

    private Integer id;
    private String name;
    private String phone;
    private String educationBackground;
    private Set<SkillDTO> skills;

    // Convert from entity → DTO
    public CandidateDTO(Candidate candidate) {
        if (candidate == null) return;
        this.id = candidate.getId();
        this.name = candidate.getName();
        this.phone = candidate.getPhone();
        this.educationBackground = candidate.getEducationBackground();
        if (candidate.getSkills() != null) {
            this.skills = candidate.getSkills().stream()
                    .map(SkillDTO::new)
                    .collect(Collectors.toSet());
        }
    }
}
