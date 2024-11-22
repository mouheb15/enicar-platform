package tn.enicar.enicar_platforme;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.security.crypto.password.PasswordEncoder;
import tn.enicar.enicar_platforme.role.Role;
import tn.enicar.enicar_platforme.role.RoleRepository;
import tn.enicar.enicar_platforme.user.User;
import tn.enicar.enicar_platforme.user.UserRepository;


import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class EnicarPlatformeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnicarPlatformeApiApplication.class, args);
	}

	@Configuration
	public static class DataInitializer {

		@Value("${roles:STUDENT,PROFESSOR,ADMIN}")
		private List<String> roles;

		@Value("${admin-user.firstname}")
		private String adminFirstName;

		@Value("${admin-user.lastname}")
		private String adminLastName;

		@Value("${admin-user.date-of-birth}")
		private String adminDateOfBirth;

		@Value("${admin-user.cin}")
		private int adminCin;

		@Value("${admin-user.email}")
		private String adminEmail;

		@Value("${admin-user.password}")
		private String adminPassword;

		@Bean
		public CommandLineRunner runner(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
			return args -> {
				// Create roles if not present
				roles.forEach(roleName -> {
					if (roleRepository.findByName(roleName).isEmpty()) {
						roleRepository.save(Role.builder().name(roleName).build());
					}
				});



					var adminRole = roleRepository.findByName("ADMIN").get();
					var user = User.builder()
							.firstname(adminFirstName)
							.lastname(adminLastName)
							.dateOfBirth(LocalDate.parse(adminDateOfBirth))
							.cin(adminCin)
							.enabled(true)
							.accountLocked(false)
							.password(passwordEncoder.encode(adminPassword))
							.email(adminEmail)
							.role(adminRole)
							.build();
					userRepository.save(user);

			};
		}
	}
}