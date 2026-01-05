package implementacion.Inventario;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class InventarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventarioApplication.class, args);
	}

    @Bean
    CommandLineRunner runner(DataSource dataSource){

        return DataArgs -> {

            System.out.println("Conexion Exitosa" + dataSource.getConnection().getMetaData().getURL());

        };

    }

}
