package tn.enicar.enicar_platforme;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import tn.enicar.enicar_platforme.role.Role;
import tn.enicar.enicar_platforme.role.RoleRepository;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class EnicarPlatformeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnicarPlatformeApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository){
		return args -> {
			if(roleRepository.findByName("USER").isEmpty()){
				roleRepository.save(Role.builder().name("USER").build());
			}

		};
	};

}
