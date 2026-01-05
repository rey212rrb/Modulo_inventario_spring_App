package implementacion.Inventario.servicio;

import implementacion.Inventario.modelo.Producto;
import implementacion.Inventario.repositorio.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServicio implements IProductoServicio{

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Override
    public List<Producto> getProductos(){

        return productoRepositorio.findAll();

    }

    @Override
    public Producto getProducto(Integer id){

        return productoRepositorio.findById(id).orElse(null);

    }

    public void guardarProducto(Producto producto){

        productoRepositorio.save(producto);

    }

    public void borrarProducto(Integer id){

        productoRepositorio.deleteById(id);

    }



}
