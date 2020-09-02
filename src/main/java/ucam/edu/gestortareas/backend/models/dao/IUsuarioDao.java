package ucam.edu.gestortareas.backend.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ucam.edu.gestortareas.backend.models.entity.Role;
import ucam.edu.gestortareas.backend.models.entity.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long>{

	public Usuario findByEmail(String email);
	
	@Query(value = "select * from usuarios where sector_id = ?1", nativeQuery = true)
	public List<Usuario> findBySector(long id);
}
