package ucam.edu.gestortareas.backend.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ucam.edu.gestortareas.backend.models.entity.Actor;

public interface IActorDao extends CrudRepository<Actor, Long>{

	@Query(value = "select * from actores where sector_id = ?1", nativeQuery = true)
	public List<Actor> findByIdSector(long id);
	
	@Query(value = "select a.* from actores a inner join tarea_actor ta on a.id = ta.actor_id where ta.tarea_id = ?1", nativeQuery = true)
	public List<Actor> findByIdTarea(long id);
}
