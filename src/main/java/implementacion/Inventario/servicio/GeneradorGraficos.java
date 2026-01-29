package implementacion.Inventario.servicio; // O servicio

import implementacion.Inventario.modelo.AjusteInventario;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneradorGraficos {

    public static void mostrarGraficoBarras(List<AjusteInventario> datos, String titulo) {

        System.out.println("\n " + titulo.toUpperCase());
        System.out.println("==================================================");

        Map<String, Long> conteo = datos.stream()

                .collect(Collectors.groupingBy(

                        a -> a.getProducto().getNombre(),

                        Collectors.counting()

                ));

        if (conteo.isEmpty()) {

            System.out.println("No hay datos para graficar.");
            return;

        }

        conteo.forEach((nombre, cantidad) -> {

            String barra = "█".repeat(cantidad.intValue());
            System.out.printf("%-15s | %s (%d)%n", cortarTexto(nombre, 15), barra, cantidad);

        });

        System.out.println("==================================================\n");
    }

    private static String cortarTexto(String t, int l) {
        return t.length() > l ? t.substring(0, l) : t;
    }
}
