package tn.enicar.enicar_platforme.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import tn.enicar.enicar_platforme.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filename;
    private String fileUrl;
    private LocalDateTime uploadDate;

    @ManyToOne
    @JsonIgnore
    private User user;
}
