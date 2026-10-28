package pe.upeu.ecorecicla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class EcoReciclaApplication {
  public static void main(String[] args) {
    SpringApplication.run(EcoReciclaApplication.class, args);
  }
}
