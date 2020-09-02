package ucam.edu.gestortareas.backend.models.services;

import java.util.List;

import ucam.edu.gestortareas.backend.models.entity.Plantilla;

public interface IPlantillaService {

	public List<Plantilla> findAll();
	
	public List<Plantilla> findPlantillas();
	
	public Plantilla findById(Long id);
	
	public Plantilla save(Plantilla plantilla);
	
	public void delete(Long id);
	
	public List<Plantilla> findPlantillasPadre();
	
	public List<Plantilla> findSubPlantillasById(Long id);
	
	public List<Plantilla> findPlantillasPrecedentesById(Long id);
	
	public List<Plantilla> findRamaPlantillasById(Long id);
}
