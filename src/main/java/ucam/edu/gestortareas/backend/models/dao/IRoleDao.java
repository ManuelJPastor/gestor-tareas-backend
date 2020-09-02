package ucam.edu.gestortareas.backend.models.dao;

import org.springframework.data.repository.CrudRepository;

import ucam.edu.gestortareas.backend.models.entity.Role;

public interface IRoleDao  extends CrudRepository<Role, Long>{

}
