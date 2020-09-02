package ucam.edu.gestortareas.backend.controllers;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
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

import ucam.edu.gestortareas.backend.models.entity.Plantilla;
import ucam.edu.gestortareas.backend.models.entity.Plantilla.Estado;
import ucam.edu.gestortareas.backend.models.entity.Tarea;
import ucam.edu.gestortareas.backend.models.services.IPlantillaService;
import ucam.edu.gestortareas.backend.models.services.ITareaService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class PlantillaRestController {
	
	@Autowired
	private IPlantillaService plantillaService;
	
	@Autowired
	private ITareaService tareaService;
	
	@GetMapping("/plantillas/estados")
	public Plantilla.Estado[] sectores() {
		return Plantilla.Estado.values();
	}
	
	@GetMapping("/plantillas/todas")
	public List<Plantilla> index(){
		return plantillaService.findAll();
	}
	
	@GetMapping("/plantillas")
	public List<Plantilla> getPlantillas(){
		return plantillaService.findPlantillas();
	}
	
	@GetMapping("/plantillas/{id}")
	public ResponseEntity<?> show(@PathVariable Long id){
		Plantilla plantilla = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			plantilla = plantillaService.findById(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(plantilla == null) {
			response.put("mensaje", "El ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Plantilla>(plantilla, HttpStatus.OK); 
	}
	
	@PostMapping("/plantillas")
	public ResponseEntity<?> create(@Valid @RequestBody Plantilla plantilla, BindingResult result) {
		Plantilla plantillaNueva = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		if(result.hasErrors()) {
			
			List<String> errores = new ArrayList<String>();
			for(FieldError err: result.getFieldErrors()) {
				errores.add("El campo '" + err.getField() + "' " + err.getDefaultMessage());
			}
			
			response.put("errores", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		response = comprobarPlantilla(plantilla);
		if((boolean) response.get("fallo")) {
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.PRECONDITION_FAILED);
		}
		
		
		try {
			plantillaNueva = plantillaService.save(plantilla);
			this.comprobarEstados(plantillaNueva.getEstado(), plantillaNueva);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "La plantilla ha sido creada con éxito!");
		response.put("plantilla", plantillaNueva);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); 
	}
	
	@PostMapping("/plantillas/crear/{titulo}")
	public ResponseEntity<?> crearPlantilla(@Valid @RequestBody Tarea tarea, @PathVariable String titulo) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		ArrayList<Long> idsTareas = new ArrayList<Long>();
		ArrayList<Long> idsPlantillasNuevas = new ArrayList<Long>();
		
		
		Plantilla plantilla = new Plantilla();
		Plantilla nuevaPlantilla = null;

		plantilla.setTitulo(titulo);
		plantilla.setDescripcion(tarea.getDescripcion());
		plantilla.setEspacio(tarea.getEspacio());
		plantilla.setFechaMax(tarea.getFechaMax());
		plantilla.setSector(tarea.getSector());
		plantilla.setDiasAviso(tarea.getDiasAviso());
		plantilla.setDiasAviso2(tarea.getDiasAviso2());
		plantilla.setPlantilla(true);
		DataBinder binder = new DataBinder(plantilla);
		BindingResult result = binder.getBindingResult();
		
		nuevaPlantilla = (Plantilla)((Map<String, Object>) this.create(plantilla, result).getBody()).get("plantilla");
		
		idsTareas.add(tarea.getId());
		idsPlantillasNuevas.add(nuevaPlantilla.getId());
		
		this.crearSubplantillas(idsTareas, idsPlantillasNuevas, tarea);
		
		
		
		response.put("mensaje", "La plantilla "+ titulo +" ha sido creada con éxito!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); 
	}
	
	private void crearSubplantillas(ArrayList<Long>idsTareas, ArrayList<Long> idsPlantillasNuevas, Tarea tareaPadre) {
		List<Tarea> tareasNivel = this.tareaService.findSubTareasById(tareaPadre.getId()).stream().filter(tareaX -> tareaX.getNivel()==1).collect(Collectors.toList());
		for(int i = 1;  !tareasNivel.isEmpty(); i++ ) {
			for(Tarea tarea: tareasNivel) {
				Plantilla plantilla = new Plantilla();
				Plantilla nuevaPlantilla = null;
	
				plantilla.setTitulo(tarea.getTitulo());
				plantilla.setDescripcion(tarea.getDescripcion());
				plantilla.setEspacio(tarea.getEspacio());
				plantilla.setFechaMax(tarea.getFechaMax());
				plantilla.setSector(tarea.getSector());
				plantilla.setDiasAviso(tarea.getDiasAviso());
				plantilla.setDiasAviso2(tarea.getDiasAviso2());
				DataBinder binder = new DataBinder(plantilla);
				BindingResult result = binder.getBindingResult();
				
				List<Plantilla> plantillasPrecedentes = new ArrayList<Plantilla>();
					for(Tarea precedenteTarea: tarea.getTareasPrecedentes()) {
						if(idsTareas.contains(precedenteTarea.getId())) {
							plantillasPrecedentes.add(this.plantillaService.findById(idsPlantillasNuevas.get(idsTareas.indexOf(precedenteTarea.getId()))));
						}
					} 
					plantilla.setPlantillasPrecedentes(plantillasPrecedentes);
					plantilla.setPlantillaPadre(this.plantillaService.findById(idsPlantillasNuevas.get(idsTareas.indexOf(tarea.getTareaPadre().getId()))));
				
				
				nuevaPlantilla = (Plantilla)((Map<String, Object>) this.create(plantilla, result).getBody()).get("plantilla");
				
				idsTareas.add(tarea.getId());
				idsPlantillasNuevas.add(nuevaPlantilla.getId());
				
				this.crearSubplantillas(idsTareas, idsPlantillasNuevas, tarea);
			}
			int j=i+1;
			tareasNivel = this.tareaService.findSubTareasById(tareaPadre.getId()).stream().filter(tareaX -> tareaX.getNivel()==j).collect(Collectors.toList());
		}
	}
	
	@PutMapping("/plantillas/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Plantilla plantilla, BindingResult result, @PathVariable Long id) {
		Plantilla plantillaActual = null;
		Plantilla plantillaNueva = null;
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
			plantillaActual = plantillaService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(plantillaActual == null) {
			response.put("mensaje", "Error: no se pudo editar, la plantilla ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		Estado estadoAnterior=plantillaActual.getEstado();
		
		plantillaActual.setTitulo(plantilla.getTitulo());
		plantillaActual.setDescripcion(plantilla.getDescripcion());
		plantillaActual.setEspacio(plantilla.getEspacio());
		plantillaActual.setFechaMax(plantilla.getFechaMax());
		plantillaActual.setSector(plantilla.getSector());
		plantillaActual.setPlantillaPadre(plantilla.getPlantillaPadre());
		plantillaActual.setPlantillasPrecedentes(plantilla.getPlantillasPrecedentes());
		plantillaActual.setEstado(plantilla.getEstado());
		plantillaActual.setDiasAviso(plantilla.getDiasAviso());
		plantillaActual.setDiasAviso2(plantilla.getDiasAviso2());
		
		response = comprobarPlantilla(plantillaActual);
		if((boolean) response.get("fallo")) {
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.PRECONDITION_FAILED);
		}
		
		try {
			plantillaNueva = plantillaService.save(plantillaActual);
			this.comprobarEstados(estadoAnterior, plantillaNueva);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el update en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		response.put("mensaje", "La plantilla ha sido actualizada con éxito!");
		response.put("plantilla", plantillaNueva);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	
	@DeleteMapping("/plantillas/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<String, Object>();
		Plantilla plantilla;
		plantilla = plantillaService.findById(id);
		try {
			this.eliminarPlantilla(plantilla);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el delete en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "La plantilla ha sido eliminada con éxito!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	private void eliminarPlantilla(Plantilla plantilla) {
		for(Plantilla subPlantilla : plantillaService.findSubPlantillasById(plantilla.getId())) {
			eliminarPlantilla(subPlantilla);
		}
		this.cambioPlantillasPrecedentes(plantilla);
		plantillaService.delete(plantilla.getId());
		
	}
	
	private void cambioPlantillasPrecedentes(Plantilla plantilla) {
		for(Plantilla plantillaSiguiente: plantilla.getPlantillasSiguientes()) {
			
			plantillaSiguiente.getPlantillasPrecedentes().remove(plantilla);
			
			for(Plantilla plantillaPrecedente: plantilla.getPlantillasPrecedentes()) {
				plantillaSiguiente.getPlantillasPrecedentes().add(plantillaPrecedente);
			}
			this.establecerDiasyNivel(plantillaSiguiente);
		}
	}
	
	@GetMapping("/plantillas/plantillasPadre")
	public List<Plantilla> plantillasPadre(){
		return  plantillaService.findPlantillasPadre();
	}
	
	@GetMapping("/plantillas/subPlantillas/{id}")
	public ResponseEntity<?> subPlantillas(@PathVariable Long id){
		List<Plantilla> plantillas = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			plantillas = plantillaService.findSubPlantillasById(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(plantillas == null) {
			response.put("mensaje", "La plantilla con ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Plantilla>>(plantillas, HttpStatus.OK);
	}
	
	@GetMapping("/plantillas/ramaPlantillas/{id}")
	public ResponseEntity<?> ramaPlantillas(@PathVariable Long id){
		List<Plantilla> plantillas = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			plantillas = plantillaService.findRamaPlantillasById(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(plantillas == null) {
			response.put("mensaje", "La plantilla con ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Plantilla>>(plantillas, HttpStatus.OK);
	}
	
	@GetMapping("plantillas/plantillasPrecedentes/{id}")
	public ResponseEntity<?> plantillasPrecedentes(@PathVariable Long id){
		List<Plantilla> plantillas = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			plantillas = plantillaService.findPlantillasPrecedentesById(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(plantillas == null) {
			response.put("mensaje", "La plantilla con ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Plantilla>>(plantillas, HttpStatus.OK);
	}
	
	
	private Map<String, Object> comprobarPlantilla(Plantilla plantilla) {
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("fallo", false);
		if(plantilla.getPlantillasPrecedentes()!=null) {
			if(!plantilla.getPlantillasPrecedentes().isEmpty()) {
				this.corregirPlantillasPrecedentes(plantilla);
				for(Plantilla plantillaPrecedente: plantilla.getPlantillasPrecedentes()) {
					if(plantilla.getFechaMax().before(plantillaPrecedente.getFechaMax())) {
						response.put("fallo",true);
						response.put("mensaje", "Error: la plantilla no puede tener una fecha máxima inferior a su plantilla precedente!"
								+ "Fecha Máxima plantilla precedente: "+plantillaPrecedente.getFechaMax());
						return response;
					}
				}
				
				for(Plantilla plantillaPrecedente: plantilla.getPlantillasPrecedentes()) {
					if(plantilla.getPlantillasSiguientes()!=null) {
						if(!plantilla.getPlantillasSiguientes().isEmpty()) {
							if(this.buscarConflictoPlantillasSiguientes(plantillaPrecedente, plantilla)) {
								response.put("fallo",true);
								response.put("mensaje", "Error: la plantilla precedente no puede ser una plantilla siguiente!");
								return response;
							}
						}
					}
					
				}
			} else {
				if(plantilla.getPlantillaPadre()!=null) {
					if(plantilla.getPlantillaPadre().getDias()!=-1) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(plantilla.getPlantillaPadre().getFechaMax());
						calendar.add(calendar.DAY_OF_YEAR, -plantilla.getPlantillaPadre().getDias());
						if(calendar.getTime().after(plantilla.getFechaMax())) {
							response.put("fallo",true);
							response.put("mensaje", "Error: la plantilla no puede tener una fecha máxima inferior a "+calendar.getTime());
							return response;
						}
					}
				}
			}
		}
		
		
		if(plantilla.getPlantillaPadre()!=null) {
			if(plantilla.getFechaMax().after(plantilla.getPlantillaPadre().getFechaMax())) {
				response.put("fallo",true);
				response.put("mensaje", "Error: la plantilla no puede tener una fecha máxima superior a su plantilla padre!\n"
						+ "Fecha Máxima plantilla padre: "+plantilla.getPlantillaPadre().getFechaMax());
				return response;
			}
			
			if(plantilla.getSubPlantillas()!=null) {
				for(Plantilla subPlantilla: plantilla.getSubPlantillas()) {
					if(subPlantilla.getId() == plantilla.getPlantillaPadre().getId()) {
						response.put("fallo",true);
						response.put("mensaje", "Error: la plantilla padre no puede ser una plantilla subplantilla!");
						return response;
					}
				}
			}
		}
		
		this.establecerDiasyNivel(plantilla);
		
		return response;
	}
	
	private boolean buscarConflictoPlantillasSiguientes(Plantilla plantillaPrecedente, Plantilla plantilla) {
		for(Plantilla plantillaSiguiente: plantilla.getPlantillasSiguientes()) {
			if(!plantillaSiguiente.getPlantillasSiguientes().isEmpty()) {
				if(this.buscarConflictoPlantillasSiguientes(plantillaPrecedente, plantillaSiguiente)) {
					return true;
				}
			}
			if(plantillaSiguiente.getId()==plantillaPrecedente.getId()) {
				return true;
			}
		}
		
		return false;
		
	}

	private void corregirPlantillasPrecedentes(Plantilla plantilla) {
		List<Plantilla> plantillasPrecedentesExcluyente;
		for (Plantilla plantillaActual : plantilla.getPlantillasPrecedentes()) {
			plantillasPrecedentesExcluyente = plantilla.getPlantillasPrecedentes().stream()
					.filter(plantillaX -> plantillaX.getId()!= plantillaActual.getId()).collect(Collectors.toList());

			for (Plantilla plantillaBusqueda : plantillasPrecedentesExcluyente) {
				if(!plantillaBusqueda.getPlantillasPrecedentes().isEmpty()) {
					if(this.buscarConflictoPlantillasPrecedentesAmbiguas(plantillaActual, plantillaBusqueda)) {
						plantilla.getPlantillasPrecedentes().remove(plantillaActual);
						return;
					}
				}
			}
		}
	}
	
	private boolean buscarConflictoPlantillasPrecedentesAmbiguas(Plantilla plantillaActual, Plantilla plantillaBusqueda) {
		for(Plantilla plantillaPrecedente: plantillaBusqueda.getPlantillasPrecedentes()) {
			if(plantillaPrecedente.getPlantillasPrecedentes()!=null) {
				if(!plantillaPrecedente.getPlantillasPrecedentes().isEmpty()) {
					if(this.buscarConflictoPlantillasPrecedentesAmbiguas(plantillaActual, plantillaPrecedente)) {
						return true;
					}
				}
			}
			
			if(plantillaPrecedente.getId()==plantillaActual.getId()) {
				return true;
			}
		}
		
		return false;
		
	}
	
	private void establecerDiasyNivel(Plantilla plantilla) {
		int dias = -1;
		int nivel = 1;
		plantilla.setDias(dias);
		plantilla.setNivel(nivel);
		if(plantilla.getPlantillasPrecedentes()!=null) {
			if(!plantilla.getPlantillasPrecedentes().isEmpty()) {
				for(Plantilla plantillaPrecedente: plantilla.getPlantillasPrecedentes()) {
					if(plantillaPrecedente.getPlantillaPadre()==null || plantilla.getPlantillaPadre()==null) {
						if(plantillaPrecedente.getPlantillaPadre() == plantilla.getPlantillaPadre()) {
							dias = (int) ((plantilla.getFechaMax().getTime()-plantillaPrecedente.getFechaMax().getTime())/86400000);
							if(plantilla.getDias()>dias || plantilla.getDias()==-1) {
								plantilla.setDias(dias);
							}
							
							if(nivel < plantillaPrecedente.getNivel()) {
								nivel = plantillaPrecedente.getNivel();
							}
							plantilla.setNivel(nivel+1);
						}
					}else if(plantillaPrecedente.getPlantillaPadre().getId()==plantilla.getPlantillaPadre().getId()) {
						dias = (int) ((plantilla.getFechaMax().getTime()-plantillaPrecedente.getFechaMax().getTime())/86400000);
						if(plantilla.getDias()>dias || plantilla.getDias()==-1) {
							plantilla.setDias(dias);
						}
						
						if(nivel < plantillaPrecedente.getNivel()) {
							nivel = plantillaPrecedente.getNivel();
						}
						plantilla.setNivel(nivel+1);
					}
					
				}
			}
		}
		if(plantilla.getPlantillasSiguientes() != null) {
			for(Plantilla plantillaSiguiente: plantilla.getPlantillasSiguientes()) {
				this.establecerDiasyNivel(plantillaSiguiente);
			}
		}
		
		
	}
	
	private void comprobarEstados(Estado estadoAnterior, Plantilla plantilla){
		Estado estadoAnteriorAux;
		
		if(estadoAnterior!=plantilla.getEstado()) {
			switch(plantilla.getEstado()) {
				case  Finalizada:
					for (Plantilla siguiente : plantilla.getPlantillasSiguientes()) {
						if(this.esDisponible(siguiente)) {
							estadoAnteriorAux = siguiente.getEstado();
							siguiente.setEstado(Estado.Disponible);
							this.comprobarEstados(estadoAnteriorAux, this.plantillaService.save(siguiente));
						} 
						
					}
					break;
					
				case  Disponible:
					break;
			}
		}else {
			switch(plantilla.getEstado()) {
				case Pendiente:
					if(this.esDisponible(plantilla)) {
						estadoAnteriorAux = plantilla.getEstado();
						plantilla.setEstado(Estado.Disponible);
						this.comprobarEstados(estadoAnteriorAux, this.plantillaService.save(plantilla));
					}
					break;
				
				case Disponible:
					if(!this.esDisponible(plantilla)) {
						estadoAnteriorAux = plantilla.getEstado();
						plantilla.setEstado(Estado.Pendiente);
						this.comprobarEstados(estadoAnteriorAux, this.plantillaService.save(plantilla));
					}
				}
		}
	}
	
	private boolean esDisponible(Plantilla plantilla) {
		
		boolean disponible = true;
		if(plantilla.getPlantillasPrecedentes()!=null) {
			for(Plantilla plantillaPrecedentes : plantilla.getPlantillasPrecedentes()) {
				if(plantillaPrecedentes.getId()!=plantilla.getId() && !plantillaPrecedentes.getEstado().equals(Estado.Finalizada)) {
					disponible = false;
				}
			}
		}
		return disponible;
	}
	
	
}
