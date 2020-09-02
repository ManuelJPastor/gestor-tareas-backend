package ucam.edu.gestortareas.backend.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ucam.edu.gestortareas.backend.models.entity.Tarea;

public interface ITareaDao extends CrudRepository<Tarea, Long>{

	@Query(value = "select t.* from tareas t inner join tarea_user tu on t.id = tu.tarea_id inner join usuarios u on u.id = tu.user_id where u.email = ?1", nativeQuery = true)
	public List<Tarea> findByEmailUsuario(String email);
	
	@Query(value = "select * from tareas where tarea_padre_id IS NULL", nativeQuery = true)
	public List<Tarea> findTareasPadre();
	
	@Query(value = "select * from tareas where tarea_padre_id = ?1", nativeQuery = true)
	public List<Tarea> findSubTareasById(long id);
	
	@Query(value = "select t.* from tareas t inner join tareas_precedentes tp on t.id = tp.tarea_precedente_id where tp.tarea_id = ?1", nativeQuery = true)
	public List<Tarea> findTareasPrecedentesById(long id);
	
	@Query(value = "select t2.* from tareas t1 inner join tareas t2 on t1.tarea_padre_id <=> t2.tarea_padre_id where t1.id = ?1", nativeQuery = true)
	public List<Tarea> findRamaTareasById(long id);
	 
}
