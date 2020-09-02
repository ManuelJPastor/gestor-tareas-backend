package ucam.edu.gestortareas.backend.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ucam.edu.gestortareas.backend.models.entity.Actor;
import ucam.edu.gestortareas.backend.models.entity.Sector;

public interface ISectorDao extends CrudRepository<Sector, Long>{

	@Query(value = "select s.* from sectores a inner join tareas t on s.id = t.sector_id where t.id = ?1", nativeQuery = true)
	public Sector findByIdTarea(long id);
}
