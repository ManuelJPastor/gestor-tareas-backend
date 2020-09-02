package ucam.edu.gestortareas.backend.models.services;

import java.util.List;

import ucam.edu.gestortareas.backend.models.entity.Actor;

public interface IActorService{

	public List<Actor> findAll();
	
	public Actor findById(Long id);
	
	public Actor save(Actor actor);
	
	public void delete(Long id);
	
	public List<Actor> findByIdSector(Long id);
	
	public List<Actor> findByIdTarea(Long id);
}
