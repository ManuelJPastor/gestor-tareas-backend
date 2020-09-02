package ucam.edu.gestortareas.backend.models.services;

import java.util.List;

import ucam.edu.gestortareas.backend.models.entity.Comentario;

public interface IComentarioService {

	public List<Comentario> findAll();
	
	public Comentario findById(Long id);
	
	public Comentario save(Comentario comentario);
	
	public void delete(Long id);
	
	public List<Comentario> findByIdTarea(Long id);
}
