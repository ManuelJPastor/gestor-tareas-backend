package ucam.edu.gestortareas.backend.models.services;

import java.util.List;

import ucam.edu.gestortareas.backend.models.entity.Role;
import ucam.edu.gestortareas.backend.models.entity.Usuario;

public interface IUsuarioService {

	public List<Usuario> findAll();
	
	public Usuario findById(Long id);
	
	public Usuario save(Usuario usuario);
	
	public Usuario update(Usuario usuario);
	
	public void delete(Long id);
	
	public Usuario findByEmail(String email);
	
	public List<Usuario> findBySector(long id);
}
