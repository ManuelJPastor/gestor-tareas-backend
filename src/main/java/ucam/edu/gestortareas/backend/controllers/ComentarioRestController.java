package ucam.edu.gestortareas.backend.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RestController;

import ucam.edu.gestortareas.backend.models.entity.Comentario;
import ucam.edu.gestortareas.backend.models.entity.Comentario;
import ucam.edu.gestortareas.backend.models.services.IComentarioService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ComentarioRestController {
	
	@Autowired
	private IComentarioService comentarioService;
	
	@GetMapping("/comentarios")
	public List<Comentario> index(){
		return comentarioService.findAll();
	}
	
	@GetMapping("/comentarios/{id}")
	public ResponseEntity<?> show(@PathVariable Long id){
		Comentario comentario = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			comentario = comentarioService.findById(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(comentario == null) {
			response.put("mensaje", "El comentario ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Comentario>(comentario, HttpStatus.OK); 
	}
	
	@PostMapping("/comentarios")
	public ResponseEntity<?> create(@Valid @RequestBody Comentario comentario, BindingResult result) {
		Comentario comentarioNuevo = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		if(result.hasErrors()) {
			
			List<String> errores = new ArrayList<String>();
			for(FieldError err: result.getFieldErrors()) {
				errores.add("El campo '" + err.getField() + "' " + err.getDefaultMessage());
			}
			
			response.put("errores", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			comentarioNuevo = comentarioService.save(comentario);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El comentario ha sido creado con éxito!");
		response.put("comentario", comentarioNuevo);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); 
	}
	
	@PutMapping("/comentarios/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Comentario comentario, BindingResult result, @PathVariable Long id) {
		Comentario comentarioActual = null;
		Comentario comentarioNuevo = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		if(result.hasErrors()) {
			
			List<String> errores = new ArrayList<String>();
			for(FieldError err: result.getFieldErrors()) {
				errores.add("El campo '" + err.getField() + "' " + err.getDefaultMessage());
			}
			
			response.put("errores", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			comentarioActual = comentarioService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(comentarioActual == null) {
			response.put("mensaje", "Error: no se pudo editar, el comentario ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		comentarioActual.setComentario(comentario.getComentario());
		
		try {
			comentarioNuevo = comentarioService.save(comentarioActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el update en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		response.put("mensaje", "El comentario ha sido actualizado con éxito!");
		response.put("comentario", comentarioNuevo);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/comentarios/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			comentarioService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el delete en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El comentario ha sido eliminado con éxito!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@GetMapping("/comentarios/tarea/{id}")
	public ResponseEntity<?> getComentariosByTarea(@PathVariable Long id){
		List<Comentario> comentarios = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			comentarios = comentarioService.findByIdTarea(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(comentarios == null) {
			response.put("mensaje", "La tarea con ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Comentario>>(comentarios, HttpStatus.OK);
	}
}
