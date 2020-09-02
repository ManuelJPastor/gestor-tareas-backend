package ucam.edu.gestortareas.backend.models.services;

import java.util.List;

import ucam.edu.gestortareas.backend.models.entity.Sector;

public interface ISectorService {

	public List<Sector> findAll();
	
	public Sector findById(Long id);
	
	public Sector save(Sector sector);
	
	public void delete(Long id);
	
	public Sector findByIdTarea(Long id);
}
