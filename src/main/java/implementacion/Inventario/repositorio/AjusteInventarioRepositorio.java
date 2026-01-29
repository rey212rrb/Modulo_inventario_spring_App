package implementacion.Inventario.repositorio;

import implementacion.Inventario.modelo.AjusteInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface AjusteInventarioRepositorio extends JpaRepository<AjusteInventario, Integer> {

    List<AjusteInventario> findByProductoId(Integer idProducto);

    @Transactional
    void deleteByProductoId(Integer idProducto);

    List<AjusteInventario> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    List<AjusteInventario> findByProductoCategoria(String categoria);
    List<AjusteInventario> findByProductoUbicacion(String ubicacion);

}
