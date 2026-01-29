package implementacion.Inventario.servicio;

import implementacion.Inventario.modelo.Producto;
import implementacion.Inventario.repositorio.AjusteInventarioRepositorio;
import implementacion.Inventario.repositorio.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoServicio implements IProductoServicio{

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private AjusteInventarioRepositorio ajusteInventarioRepositorio;

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

    @Transactional
    public void borrarProducto(Integer id){

        ajusteInventarioRepositorio.deleteByProductoId(id);
        productoRepositorio.deleteById(id);

    }



}
