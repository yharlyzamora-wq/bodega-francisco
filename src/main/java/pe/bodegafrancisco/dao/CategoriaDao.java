package pe.bodegafrancisco.dao;

import java.util.List;
import pe.bodegafrancisco.model.Categoria;

/** Consulta de categorías. */
public interface CategoriaDao {
    List<Categoria> listar();
}