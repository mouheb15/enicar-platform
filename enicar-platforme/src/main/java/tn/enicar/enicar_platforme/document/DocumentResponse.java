package tn.enicar.enicar_platforme.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class DocumentResponse {
    private Long id; // File ID
    private String filename;
    private String fileUrl;
    private LocalDateTime uploadDate;
    private Integer userId; // User ID
    private String userFullName; // User's full name
    private String userRole; // User's role
}
