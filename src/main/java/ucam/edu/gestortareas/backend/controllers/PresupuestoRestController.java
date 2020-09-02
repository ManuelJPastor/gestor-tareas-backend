package ucam.edu.gestortareas.backend.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ucam.edu.gestortareas.backend.models.entity.Comentario;
import ucam.edu.gestortareas.backend.models.entity.Presupuesto;
import ucam.edu.gestortareas.backend.models.entity.Tarea;
import ucam.edu.gestortareas.backend.models.services.IPresupuestoService;
import ucam.edu.gestortareas.backend.models.services.ITareaService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class PresupuestoRestController {

	@Autowired
	private IPresupuestoService presupuestoService;
	
	@Autowired
	private ITareaService tareaService;
	
	@GetMapping("/presupuestos")
	public List<Presupuesto> index(){
		return presupuestoService.findAll();
	}
	
	@GetMapping("/presupuestos/tarea/{id}")
	public ResponseEntity<?> getComentariosByTarea(@PathVariable Long id){
		List<Presupuesto> presupuestos = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			presupuestos = presupuestoService.findByIdTarea(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(presupuestos == null) {
			response.put("mensaje", "La tarea con ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Presupuesto>>(presupuestos, HttpStatus.OK);
	}
	
	@PostMapping("/presupuestos/{id}")
	public ResponseEntity<?> create(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
		Presupuesto presupuestoNuevo = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		
		Tarea tarea = tareaService.findById(id);
		
		try {
			presupuestoNuevo = presupuestoService.save(file, tarea);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El presupuesto ha sido creado con éxito!");
		response.put("presupuesto", presupuestoNuevo);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); 
	}
	
	
	
	@DeleteMapping("/presupuestos/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			presupuestoService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el delete en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El presupuesto ha sido eliminado con éxito!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@GetMapping("/presupuestos/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        // Load file from database
        Presupuesto presupuesto = presupuestoService.findById(id);

        return ResponseEntity.ok()
        		.contentType(MediaType.parseMediaType(presupuesto.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + presupuesto.getNombre() + "\"")
                .body(new ByteArrayResource(presupuesto.getArchivo()));
    }
}
