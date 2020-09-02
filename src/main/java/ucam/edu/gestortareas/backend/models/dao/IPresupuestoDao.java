package ucam.edu.gestortareas.backend.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ucam.edu.gestortareas.backend.models.entity.Presupuesto;

public interface IPresupuestoDao extends CrudRepository<Presupuesto, Long>{

	@Query(value = "select * from presupuestos where tarea_id = ?1", nativeQuery = true)
	public List<Presupuesto> findByIdTarea(long id);
}
