package ucam.edu.gestortareas.backend.models.services;

import java.util.List;

import ucam.edu.gestortareas.backend.models.entity.Tarea;

public interface ITareaService {

	public List<Tarea> findAll();
	
	public Tarea findById(Long id);
	
	public Tarea save(Tarea tarea);
	
	public void delete(Long id);
	
	public List<Tarea> findByEmailUsuario(String email);
	
	public List<Tarea> findTareasPadre();
	
	public List<Tarea> findSubTareasById(Long id);
	
	public List<Tarea> findTareasPrecedentesById(Long id);
	
	public List<Tarea> findRamaTareasById(Long id);
}
