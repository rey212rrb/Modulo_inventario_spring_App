package implementacion.Inventario.servicio;

import implementacion.Inventario.modelo.Producto;

import java.util.List;

public interface IProductoServicio {

    List<Producto> getProductos();
    Producto getProducto(Integer id);
    void guardarProducto(Producto producto);
    void borrarProducto(Integer id);


}
