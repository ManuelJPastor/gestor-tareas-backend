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

import ucam.edu.gestortareas.backend.models.entity.Sector;
import ucam.edu.gestortareas.backend.models.services.ISectorService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class SectorRestController {

	@Autowired
	private ISectorService sectorService;
	
	@GetMapping("/sectores")
	public List<Sector> index(){
		return sectorService.findAll();
	}
	
	@GetMapping("/sectores/{id}") 
	public ResponseEntity<?> show(@PathVariable Long id){
		Sector sector = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			sector = sectorService.findById(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(sector == null) {
			response.put("mensaje", "El sector ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Sector>(sector, HttpStatus.OK); 
	}
	
	@PostMapping("/sectores")
	public ResponseEntity<?> create(@Valid @RequestBody Sector sector, BindingResult result) {
		Sector sectorNuevo = null;
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
			sectorNuevo = sectorService.save(sector);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El sector ha sido creado con éxito!");
		response.put("sector", sectorNuevo);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); 
	}
	
	@PutMapping("/sectores/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Sector sector, BindingResult result, @PathVariable Long id) {
		Sector sectorActual = null;
		Sector sectorNuevo = null;
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
			sectorActual = sectorService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(sectorActual == null) {
			response.put("mensaje", "Error: no se pudo editar, el sector ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		sectorActual.setSector(sector.getSector());
		sectorActual.setActores(sector.getActores());
		
		try {
			sectorNuevo = sectorService.save(sectorActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el update en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		response.put("mensaje", "El sector ha sido actualizado con éxito!");
		response.put("sector", sectorNuevo);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/sectores/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			sectorService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el delete en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El sector ha sido eliminado con éxito!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
