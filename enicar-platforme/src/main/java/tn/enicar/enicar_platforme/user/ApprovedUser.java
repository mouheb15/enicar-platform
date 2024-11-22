package tn.enicar.enicar_platforme.user;

import jakarta.persistence.*;
import lombok.*;
import tn.enicar.enicar_platforme.role.Role;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="approved_user")
public class ApprovedUser {
    @Id
    @GeneratedValue
    private Integer id;
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    @Column(unique = true,nullable = false)
    private String email;
    @Column(unique = true)
    private Integer cin;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

}
