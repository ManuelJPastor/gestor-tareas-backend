package ucam.edu.gestortareas.backend.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ucam.edu.gestortareas.backend.models.entity.Comentario;
import ucam.edu.gestortareas.backend.models.entity.Tarea;

public interface IComentarioDao extends CrudRepository<Comentario, Long>{

	@Query(value = "select * from comentarios where tarea_id = ?1", nativeQuery = true)
	public List<Comentario> findByIdTarea(Long id);
}
