package ucam.edu.gestortareas.backend.models.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import ucam.edu.gestortareas.backend.models.entity.Presupuesto;
import ucam.edu.gestortareas.backend.models.entity.Tarea;

public interface IPresupuestoService{

	public List<Presupuesto> findAll();
	
	public Presupuesto findById(Long id);
	
	public Presupuesto save(MultipartFile file, Tarea tarea);
	
	public void delete(Long id);
	
	public List<Presupuesto> findByIdTarea(Long id);
}
