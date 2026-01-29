package implementacion.Inventario.servicio;

import implementacion.Inventario.modelo.AjusteInventario;
import implementacion.Inventario.modelo.Producto;
import implementacion.Inventario.repositorio.AjusteInventarioRepositorio;
import implementacion.Inventario.repositorio.ProductoRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioService {

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private AjusteInventarioRepositorio ajusteInventarioRepositorio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    @Transactional
    public void realizarAjuste(Integer idProducto, Integer cantidad, String motivo){

        //Buscar el producto
        Producto producto = productoRepositorio.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        //Guardar Stock antes de tocarlo
        Integer stockAnterior = producto.getStockActual();

        //Calcular nuevo Stock
        Integer stocknuevo = stockAnterior + cantidad;

        //Update
        producto.setStockActual(stocknuevo);
        productoRepositorio.save(producto);

        if (producto.getStockMinimo() != null && stocknuevo < producto.getStockMinimo()) {

            System.out.println("El stock es menor al mínimo.");
            enviarAlertaBajoStock(producto);

        } else {

            System.out.println("Correo no enviado.");

            if (producto.getStockMinimo() == null) {

                System.out.println("El prodducto, no rebasa el stock minimo");

            } else {

                System.out.println("Aun hay Stock suficiente" + stocknuevo + " no es menor que " + producto.getStockMinimo());

            }
        }
        System.out.println("----------------------------------------------------");

        if(stocknuevo < 0){

            throw new RuntimeException("Error. El ajuste dejaría el stock en negativo. Operación cancelada.");

        }

        //Update
        producto.setStockActual(stocknuevo);
        productoRepositorio.save(producto);

        if (producto.getStockMinimo() != null && stocknuevo < producto.getStockMinimo()) {
            enviarAlertaBajoStock(producto);
        }

        //Guardamos en el Historial bb
        AjusteInventario ajusteInventario = new AjusteInventario();
        ajusteInventario.setProducto(producto);
        ajusteInventario.setCantidadAjustada(cantidad);
        ajusteInventario.setMotivo(motivo);
        ajusteInventario.setStockAlMomento(stockAnterior);

        ajusteInventarioRepositorio.save(ajusteInventario);

        System.out.println("Ajuste inventario realizado con Exito :D");


    }

    public List<AjusteInventario> listarTodosLosAjustes() {

        return ajusteInventarioRepositorio.findAll();

    }

    public List<Producto> obtenerReporteBajoStock() {

        return productoRepositorio.buscarProductosConBajoStock();

    }

    private void enviarAlertaBajoStock(Producto producto) {

        String destinatario = "rosales.basurto@outlook.com";
        String asunto = "Aviso de stock: " + producto.getNombre();

        String mensaje = String.format("""
                Hola Administrador,
                
                El producto '%s' ha bajado de su nivel mínimo permitido.
                
                --------------------------------------
                Stock Actual:    %d unidades
                Stock Minimo: %d unidades
                --------------------------------------
                
                Por favor, resurtir producto.
                """,
                producto.getNombre(),
                producto.getStockActual(),
                producto.getStockMinimo());

        try {

            notificacionServicio.enviarNotificacion(destinatario, asunto, mensaje);

        } catch (Exception e) {

            System.err.println("No se pudo enviar el correo de alerta: " + e.getMessage());

        }
    }

    public List<AjusteInventario> reportePorCategoria(String categoria) {

        return ajusteInventarioRepositorio.findByProductoCategoria(categoria);

    }

    public List<AjusteInventario> reportePorUbicacion(String ubicacion) {

        return ajusteInventarioRepositorio.findByProductoUbicacion(ubicacion);

    }
}



