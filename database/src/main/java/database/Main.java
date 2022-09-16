package database;

import java.time.Clock;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import database.models.UserModel;
import database.services.RoleService;
import database.services.UserService;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class Main implements CommandLineRunner
{
	private final RoleService roleService;
	private final UserService userService;
	
	public static void main(String[] args)
	{
		SpringApplication.run(Main.class, args);
	}
	
	@Bean
	public Clock clock()
	{
		return Clock.systemDefaultZone();
	}
	
	@Override
	public void run(String... args) throws Exception
	{
		UserModel admin = new UserModel();
		
		admin.setCpf("00000000000");
		admin.setUsername("admin");
		admin.setPassword("$2a$10$61UWS8XsIbycMYNytQHTouehLewdM.ML82AbY1Szo5V7TovTydpPS");
		admin.setFullName("Administrator");
		admin.setRoles(roleService.findAll());
		
		if(!userService.existsByCpf(admin.getCpf()) && !userService.existsByUsername(admin.getUsername()))
		{
			userService.save(admin);
		}
	}
}