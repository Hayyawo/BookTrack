package booktrack.user.dto;

import booktrack.user.Role;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private LocalDateTime createdAt;
}
