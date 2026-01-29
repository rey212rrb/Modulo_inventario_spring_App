package implementacion.Inventario;

import implementacion.Inventario.modelo.AjusteInventario;
import implementacion.Inventario.modelo.Producto;
import implementacion.Inventario.servicio.GeneradorGraficos;
import implementacion.Inventario.servicio.InventarioService;
import implementacion.Inventario.servicio.ProductoServicio;
import implementacion.Inventario.servicio.ReporteServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class InventarioApplication implements CommandLineRunner {


    @Autowired
    private ProductoServicio  productoServicio;
    private static final Logger logger = LoggerFactory.getLogger(InventarioApplication.class);
    private String salto = System.lineSeparator();
    private Scanner sc = new Scanner(System.in);
    private ReporteServicio reporteServicio = new ReporteServicio();

    @Autowired
    private InventarioService inventarioService;

	public static void main(String[] args) {

        SpringApplication.run(InventarioApplication.class, args);

	}

    @Override
    public void run(String... args) throws Exception {

        var op = -1;

        do{

            op = getOpcion();

            ejecutarOpcion(op);

            logger.info(salto);


        }while (op != 0);


    }

    public Integer getOpcion(){

        logger.info(salto);

        List<Producto> alertas = inventarioService.obtenerReporteBajoStock();

        if (!alertas.isEmpty()) {

            logger.info("Inventario por de bajo del minimo, ACCIÓN REQUERIDA " + salto);

            for (Producto p : alertas) {
                System.out.printf(" Producto: %-15s | Actual: %-3d | Mín: %-3d  \n",
                        cortarTexto(p.getNombre(), 15),
                        p.getStockActual(),
                        p.getStockMinimo());
            }

        }

        logger.info("""
                
                ***** Modulo Inventario *****
                
                1.- Agregar Poducto
                2.- Modificar Poducto
                3.- Borrar Poducto
                4.- Consultar Poducto
                5.- Descargar reporte de Inventario
                6.- Ajuste Inventario
                7.- Reporte Ajuste Inventario
                0.- Salir 
                
                Seleccionar una opcion: """);


        String entrada = sc.nextLine();
        if (entrada.isEmpty()) return -1;
        return Integer.parseInt(entrada);

    }

    public void ejecutarOpcion(Integer opcion) {

        switch (opcion) {

            case 1 -> agregar();
            case 2 -> modificar();
            case 3 -> borrar();
            case 4 -> consultar();
            case 5 -> menuReportes();
            //case 5 -> reporteInventario();
            case 6 -> ajusteInvetnario();
            case 7 -> reporteAjusteInventario();
            case 0 -> logger.info("Adios.");

            default -> throw new IllegalStateException("Valor inesperado: " + opcion);

        }
    }

    public void agregar(){

        logger.info(" Agregar nuevo Producto: " + salto);

        productoServicio.guardarProducto(leerDatosProducto(null));

    }

    private Producto leerDatosProducto(Integer id){

        Producto producto = new Producto();
        producto.setId(id);

        logger.info("Ingresar datos de la producto: " + salto);

        logger.info("Nombre: ");
        producto.setNombre(sc.nextLine());

        logger.info("Categoria: ");
        producto.setCategoria(sc.nextLine());

        logger.info("Ubicacion: ");
        producto.setUbicacion(sc.nextLine());

        logger.info("Stock Actual: ");
        producto.setStockActual(sc.nextInt());

        logger.info("Stock Minimo: ");
        producto.setStockMinimo(sc.nextInt());

        logger.info("Precio: ");
        producto.setPrecio(sc.nextBigDecimal());

        sc.nextLine();

        return producto;

    }

    public void modificar(){

        logger.info("Modificar Producto: " + salto);

        var id = leerId("Modificar");

        productoServicio.guardarProducto(leerDatosProducto(id));

        logger.info("Producto " + id + " modificado correctamente. " +  salto);

    }

    public void borrar(){

        logger.info("Borrar Producto: " + salto);

        var id = leerId("Borrar");

        productoServicio.borrarProducto(id);

        logger.info("Producto " + id + " borrado correctamente. " +  salto);


    }

    public void consultar(){
        logger.info("Consultar Producto " + salto);

        var id = leerId("Consultar");

        var producto = productoServicio.getProducto(id);

        if(producto != null){

            logger.info(salto);
            System.out.printf("%-15s : %-35s %n", "ID", producto.getId());
            System.out.printf("%-15s : %-35s %n", "Nombre", cortarTexto(producto.getNombre(), 35));
            System.out.printf("%-15s : %-35s %n", "Categoría", cortarTexto(producto.getCategoria(), 35));
            System.out.printf("%-15s : %-35s %n", "Ubicación", cortarTexto(producto.getUbicacion(), 35));

            System.out.printf("%-15s : $%-34.2f %n", "Precio", producto.getPrecio());
            System.out.printf("%-15s : %-35d %n", "Stock Actual", producto.getStockActual());

            System.out.printf("%-15s : %-35d %n", "Stock Mínimo", producto.getStockMinimo());

            logger.info(salto);

        } else {

            logger.info("No se encontro ningun producto con el ID: " + id);

            logger.info(salto);
        }
    }

    public void reporteInventario() {

        logger.info("Reporte de Inventario:" + salto);

        List<Producto> listaProductos = productoServicio.getProductos();

        if (listaProductos.isEmpty()) {

            logger.info("El inventario esta vacío." + salto);
            return;

        }

        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("| %-5s | %-20s | %-15s | %-10s | %-8s | %-5s |%n",
                "ID", "NOMBRE", "CATEGORIA", "PRECIO", "ACTUAL", "MIN");
        System.out.println("-------------------------------------------------------------------------------------");

        for (Producto p : listaProductos) {
            System.out.printf("| %-5d | %-20s | %-15s | $%-9.2f | %-8d | %-5d |%n",
                    p.getId(),
                    cortarTexto(p.getNombre(), 20),
                    cortarTexto(p.getCategoria(), 15),
                    p.getPrecio(),
                    p.getStockActual(),
                    p.getStockMinimo()
            );
        }

        System.out.println("-------------------------------------------------------------------------------------");
        logger.info(salto);
    }

    public void ajusteInvetnario(){

        logger.info("Ajuste Inventario" + salto);

        var id = leerId("Ajuste Inventario");

        logger.info("Cantidad a ajustar (Positivo = Entrada, Negativo = Salida): ");
        int cantidad = Integer.parseInt(sc.nextLine());

        logger.info("Motivo del ajuste: ");
        String motivo = sc.nextLine();

        try {

            inventarioService.realizarAjuste(id, cantidad, motivo);

            logger.info("Ajuste registrado correctamente para el producto ID: " + id + salto);

        } catch (Exception e) {

            logger.info("Error al realizar el ajuste: " + e.getMessage() + salto);

        }

    }

    private void reporteAjusteInventario() {

        logger.info("Historial de Ajustes Inventario" + salto);

        List<AjusteInventario> historial = inventarioService.listarTodosLosAjustes();

        if (historial.isEmpty()) {

            logger.info("No se han registrado movimientos en el inventario." + salto);
            return;

        }

        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.printf("| %-5s | %-20s | %-10s | %-10s | %-30s |%n",
                "ID", "PRODUCTO", "CANTIDAD", "STOCK ANT", "MOTIVO");
        System.out.println("----------------------------------------------------------------------------------------------------");

        for (AjusteInventario a : historial) {

            String signo = a.getCantidadAjustada() > 0 ? "+" : "";

            System.out.printf("| %-5d | %-20s | %-10s | %-10d | %-30s |%n",
                    a.getId(),
                    cortarTexto(a.getProducto().getNombre(), 20),
                    signo + a.getCantidadAjustada(),
                    a.getStockAlMomento(),
                    cortarTexto(a.getMotivo(), 30)
            );
        }

        System.out.println("----------------------------------------------------------------------------------------------------");
        logger.info(salto);
    }

    public Integer leerId(String operacion){

        logger.info("Ingrese el ID del producto a " + operacion + ": ");
        return Integer.parseInt(sc.nextLine());

    }


    private String cortarTexto(String texto, int largo) {

        if (texto == null) return "N/A";
        if (texto.length() > largo) {

            return texto.substring(0, largo);

        }

        return texto;
    }

    private void menuReportes() {

        logger.info("Descargar Reporte" + salto);
        logger.info("1. Filtrar por Categoría" + salto);
        logger.info("2. Filtrar por Ubicación" +  salto);
        logger.info("3. Ver Todo el Historial" +  salto);
        logger.info("0. Salir" +  salto);
        logger.info("Selecciona una opción: ");

        int opcion = Integer.parseInt(sc.nextLine());
        List<AjusteInventario> resultados = null;
        String tituloReporte = "";

        switch (opcion) {

            case 1 -> {

                logger.info("Ingresa la categoria a buscar: ");
                String categoria = sc.nextLine();
                resultados = inventarioService.reportePorCategoria(categoria);
                tituloReporte = "Reporte de Categoría: " + categoria;

            }
            case 2 -> {

                logger.info("Ingresa la ubicacion a buscar: (Norte, Sur, Este o Oeste)");
                String ubi = sc.nextLine();
                resultados = inventarioService.reportePorUbicacion(ubi);
                tituloReporte = "Reporte de Ubicación: " + ubi;

            }
            case 3 -> {

                resultados = inventarioService.listarTodosLosAjustes();
                tituloReporte = "Reporte total de Inventario";

            }

            case 0 -> { return; }

            default -> logger.info("Opcion invalida.");

        }

        if (resultados == null || resultados.isEmpty()) {

            logger.info("No se encontraron datos para ese filtro." + salto);
            return;

        }

        GeneradorGraficos.mostrarGraficoBarras(resultados, tituloReporte);

        logger.info("¿Deseas exportar estos datos?" + salto);
        logger.info("1. Generar PDF" + salto);
        logger.info("2. Generar Excel" + salto);
        logger.info("0. No, gracias" + salto);

        int exportar = Integer.parseInt(sc.nextLine());

        switch (exportar) {

            case 1 -> reporteServicio.exportarPDF(resultados, tituloReporte);
            case 2 -> reporteServicio.exportarExcel(resultados, tituloReporte);
            case 0 -> logger.info("Salir");
            default -> logger.info("Opción no valida.");

        }

        logger.info(salto);
    }
}
