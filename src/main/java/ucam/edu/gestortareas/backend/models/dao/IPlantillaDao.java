package ucam.edu.gestortareas.backend.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ucam.edu.gestortareas.backend.models.entity.Plantilla;

public interface IPlantillaDao extends CrudRepository<Plantilla, Long>{
	
	@Query(value = "select * from plantillas where plantilla = 1", nativeQuery = true)
	public List<Plantilla> findPlantillas();
	
	@Query(value = "select * from plantillas where plantilla_padre_id IS NULL", nativeQuery = true)
	public List<Plantilla> findPlantillasPadre();
	
	@Query(value = "select * from plantillas where plantilla_padre_id = ?1", nativeQuery = true)
	public List<Plantilla> findSubPlantillasById(long id);
	
	@Query(value = "select t.* from plantillas t inner join plantillas_precedentes tp on t.id = tp.plantilla_precedente_id where tp.plantilla_id = ?1", nativeQuery = true)
	public List<Plantilla> findPlantillasPrecedentesById(long id);
	
	@Query(value = "select t2.* from plantillas t1 inner join plantillas t2 on t1.plantilla_padre_id <=> t2.plantilla_padre_id where t1.id = ?1", nativeQuery = true)
	public List<Plantilla> findRamaPlantillasById(long id);
	
	 
}
