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

import ucam.edu.gestortareas.backend.emails.EmailService;
import ucam.edu.gestortareas.backend.models.entity.Actor;
import ucam.edu.gestortareas.backend.models.entity.Comentario;
import ucam.edu.gestortareas.backend.models.entity.Plantilla;
import ucam.edu.gestortareas.backend.models.entity.Presupuesto;
import ucam.edu.gestortareas.backend.models.entity.Tarea;
import ucam.edu.gestortareas.backend.models.entity.Tarea.Estado;
import ucam.edu.gestortareas.backend.models.entity.Usuario;
import ucam.edu.gestortareas.backend.models.services.IActorService;
import ucam.edu.gestortareas.backend.models.services.IComentarioService;
import ucam.edu.gestortareas.backend.models.services.IPlantillaService;
import ucam.edu.gestortareas.backend.models.services.IPresupuestoService;
import ucam.edu.gestortareas.backend.models.services.ITareaService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class TareaRestController {
	
	@Autowired
    private EmailService emailService;
	
	@Autowired
	private ITareaService tareaService;
	
	@Autowired
	private IPlantillaService plantillaService;
	
	@Autowired
	private IActorService actorService;
	
	@Autowired
	private IComentarioService comentarioService;
	
	@Autowired
	private IPresupuestoService presupuestoService;
	
	@GetMapping("/tareas/estados")
	public Tarea.Estado[] sectores() {
		return Tarea.Estado.values();
	}
	
	@GetMapping("/tareas")
	public List<Tarea> index(){
		return tareaService.findAll();
	}
	
	@GetMapping("/tareas/{id}")
	public ResponseEntity<?> show(@PathVariable Long id){
		Tarea tarea = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			tarea = tareaService.findById(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(tarea == null) {
			response.put("mensaje", "El ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Tarea>(tarea, HttpStatus.OK); 
	}
	
	@PostMapping("/tareas")
	public ResponseEntity<?> create(@Valid @RequestBody Tarea tarea, BindingResult result) {
		Tarea tareaNueva = null;
		Map<String, Object> response = new HashMap<String, Object>();
		if(result.hasErrors()) {
			
			List<String> errores = new ArrayList<String>();
			for(FieldError err: result.getFieldErrors()) {
				errores.add("El campo '" + err.getField() + "' " + err.getDefaultMessage());
			}
			
			response.put("errores", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		response = comprobarTarea(tarea);
		if((boolean) response.get("fallo")) {
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.PRECONDITION_FAILED);
		}
		
		try {
			tareaNueva = tareaService.save(tarea);
			this.comprobarEstados(tareaNueva.getEstado(), tareaNueva);
			this.emailService.creacionTarea(tareaNueva);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "La tarea ha sido creada con éxito!");
		response.put("tarea", tareaNueva);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); 
	}
	
	
	@GetMapping("/tareas/plantillas/{id}/{titulo}")
	public ResponseEntity<?> usarPlantilla(@PathVariable Long id, @PathVariable String titulo) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		ArrayList<Long> idsPlantillas = new ArrayList<Long>();
		ArrayList<Long> idsTareasNuevas = new ArrayList<Long>();
		
		Plantilla plantilla = this.plantillaService.findById(id);
		
		Tarea tarea = new Tarea();
		Tarea nuevaTarea = null;

		tarea.setTitulo(titulo);
		tarea.setDescripcion(plantilla.getDescripcion());
		tarea.setEspacio(plantilla.getEspacio());
		tarea.setFechaMax(plantilla.getFechaMax());
		tarea.setSector(plantilla.getSector());
		tarea.setDiasAviso(plantilla.getDiasAviso());
		tarea.setDiasAviso2(plantilla.getDiasAviso2());
		DataBinder binder = new DataBinder(tarea);
		BindingResult result = binder.getBindingResult();
		
		nuevaTarea = (Tarea)((Map<String, Object>) this.create(tarea, result).getBody()).get("tarea");
		
		idsPlantillas.add(plantilla.getId());
		idsTareasNuevas.add(nuevaTarea.getId());
		
		this.crearSubtareas(idsPlantillas, idsTareasNuevas, plantilla);
		
		
		response.put("tarea", nuevaTarea);
		response.put("mensaje", "Las tareas de la plantilla "+ plantilla.getTitulo() +" han sido creadas con éxito!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); 
	}
	
	private void crearSubtareas(ArrayList<Long>idsPlantillas, ArrayList<Long> idsTareasNuevas, Plantilla plantillaPadre) {
		List<Plantilla> plantillasNivel = this.plantillaService.findSubPlantillasById(plantillaPadre.getId()).stream().filter(plantillaX -> plantillaX.getNivel()==1).collect(Collectors.toList());
		for(int i = 1;  !plantillasNivel.isEmpty(); i++ ) {
			for(Plantilla plantilla: plantillasNivel) {
				Tarea tarea = new Tarea();
				Tarea nuevaTarea = null;
	
				tarea.setTitulo(plantilla.getTitulo());
				tarea.setDescripcion(plantilla.getDescripcion());
				tarea.setEspacio(plantilla.getEspacio());
				tarea.setFechaMax(plantilla.getFechaMax());
				tarea.setSector(plantilla.getSector());
				tarea.setDiasAviso(plantilla.getDiasAviso());
				tarea.setDiasAviso2(plantilla.getDiasAviso2());
				DataBinder binder = new DataBinder(tarea);
				BindingResult result = binder.getBindingResult();
				
				List<Tarea> tareasPrecedentes = new ArrayList<Tarea>();
					for(Plantilla precedentePlantilla: plantilla.getPlantillasPrecedentes()) {
						if(idsPlantillas.contains(precedentePlantilla.getId())) {
							tareasPrecedentes.add(this.tareaService.findById(idsTareasNuevas.get(idsPlantillas.indexOf(precedentePlantilla.getId()))));
						}
					} 
					tarea.setTareasPrecedentes(tareasPrecedentes);
					tarea.setTareaPadre(this.tareaService.findById(idsTareasNuevas.get(idsPlantillas.indexOf(plantilla.getPlantillaPadre().getId()))));
				
				
				nuevaTarea = (Tarea)((Map<String, Object>) this.create(tarea, result).getBody()).get("tarea");
				
				idsPlantillas.add(plantilla.getId());
				idsTareasNuevas.add(nuevaTarea.getId());
				
				this.crearSubtareas(idsPlantillas, idsTareasNuevas, plantilla);
			}
			int j=i+1;
			plantillasNivel = this.plantillaService.findSubPlantillasById(plantillaPadre.getId()).stream().filter(plantillaX -> plantillaX.getNivel()==j).collect(Collectors.toList());
		}
	}
	
	@PutMapping("/tareas/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Tarea tarea, BindingResult result, @PathVariable Long id) {
		Tarea tareaActual = null;
		Tarea tareaNueva = null;
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
			tareaActual = tareaService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(tareaActual == null) {
			response.put("mensaje", "Error: no se pudo editar, la tarea ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		Estado estadoAnterior=tareaActual.getEstado();
		List<Usuario> usuariosAnteriores = tareaActual.getUsuarios();
		List<Actor> actoresAnteriores = this.actorService.findByIdTarea(id);
		
		tareaActual.setTitulo(tarea.getTitulo());
		tareaActual.setDescripcion(tarea.getDescripcion());
		tareaActual.setEspacio(tarea.getEspacio());
		tareaActual.setFechaMax(tarea.getFechaMax());
		tareaActual.setSector(tarea.getSector());
		tareaActual.setTareaPadre(tarea.getTareaPadre());
		tareaActual.setTareasPrecedentes(tarea.getTareasPrecedentes());
		tareaActual.setUsuarios(tarea.getUsuarios());
		tareaActual.setEstado(tarea.getEstado());
		tareaActual.setDiasAviso(tarea.getDiasAviso());
		tareaActual.setDiasAviso2(tarea.getDiasAviso2());
		tareaActual.setActores(tarea.getActores());
		tareaActual.setComentarios(tarea.getComentarios());
		tareaActual.setPresupuestoEscogidoId(tarea.getPresupuestoEscogidoId());
		
		
		response = comprobarTarea(tareaActual);
		if((boolean) response.get("fallo")) {
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.PRECONDITION_FAILED);
		}
		
		try {
			tareaNueva = tareaService.save(tareaActual);
			this.comprobarEstados(estadoAnterior, tareaNueva);
			ArrayList<String> toEmails = new ArrayList<String>();
			if(tareaNueva.getUsuarios()!=null) {
				for(Usuario usuario: tareaNueva.getUsuarios()) {
	        		if(!usuariosAnteriores.contains(usuario)) {
	        			toEmails.add(usuario.getEmail());
	        		}
	        	}
			}
			
			for(Actor actor: this.actorService.findByIdTarea(id)) {
        		if(!actoresAnteriores.contains(actor)) {
        			toEmails.add(actor.getEmail());
        		}
        	}
			if(toEmails.size()!=0) {
				this.emailService.addUsuariosTarea(tareaNueva, toEmails);
			}
			this.emailService.actualizacionTarea(tareaNueva);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el update en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		response.put("mensaje", "La tarea ha sido actualizada con éxito!");
		response.put("tarea", tareaNueva);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/tareas/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<String, Object>();
		Tarea tarea;
		tarea = tareaService.findById(id);
		try {
			this.eliminarTarea(tarea);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el delete en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "La tarea ha sido eliminada con éxito!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	private void eliminarTarea(Tarea tarea) {
		for(Tarea subTarea : tareaService.findSubTareasById(tarea.getId())) {
			eliminarTarea(subTarea);
		}
		this.emailService.eliminacionTarea(tarea);
		this.cambioTareasPrecedentes(tarea);
		for(Presupuesto presupuesto : tarea.getPresupuestos()) {
			presupuestoService.delete(presupuesto.getId());
		}
		for(Comentario comentario : tarea.getComentarios()) {
			comentarioService.delete(comentario.getId());
		}
		tareaService.delete(tarea.getId());
		
		
	}
	
	private void cambioTareasPrecedentes(Tarea tarea) {
			for(Tarea tareaSiguiente: tarea.getTareasSiguientes()) {
				
				tareaSiguiente.getTareasPrecedentes().remove(tarea);
				
				for(Tarea tareaPrecedente: tarea.getTareasPrecedentes()) {
					tareaSiguiente.getTareasPrecedentes().add(tareaPrecedente);
				}
				this.establecerDiasyNivel(tareaSiguiente);
			}
		
		
	}

	@GetMapping("/tareas/mistareas/{email}")
	public ResponseEntity<?> misTareas(@PathVariable String email){
		List<Tarea> tareas = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			tareas = tareaService.findByEmailUsuario(email);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(tareas == null) {
			response.put("mensaje", "El usuario con email: "+ email + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		
		
		return new ResponseEntity<List<Tarea>>(tareas, HttpStatus.OK); 
	}
	
	@GetMapping("/tareas/tareasPadre")
	public List<Tarea> tareasPadre(){
		return  tareaService.findTareasPadre();
	}
	
	@GetMapping("/tareas/subTareas/{id}")
	public ResponseEntity<?> subTareas(@PathVariable Long id){
		List<Tarea> tareas = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			tareas = tareaService.findSubTareasById(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(tareas == null) {
			response.put("mensaje", "La tarea con ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Tarea>>(tareas, HttpStatus.OK);
	}
	
	@GetMapping("/tareas/ramaTareas/{id}")
	public ResponseEntity<?> ramaTareas(@PathVariable Long id){
		List<Tarea> tareas = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			tareas = tareaService.findRamaTareasById(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(tareas == null) {
			response.put("mensaje", "La tarea con ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Tarea>>(tareas, HttpStatus.OK);
	}
	
	@GetMapping("tareas/tareasPrecedentes/{id}")
	public ResponseEntity<?> tareasPrecedentes(@PathVariable Long id){
		List<Tarea> tareas = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			tareas = tareaService.findTareasPrecedentesById(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(tareas == null) {
			response.put("mensaje", "La tarea con ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Tarea>>(tareas, HttpStatus.OK);
	}
	
	
	private Map<String, Object> comprobarTarea(Tarea tarea) {
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("fallo", false);
		if(tarea.getTareasPrecedentes()!=null) {
			if(!tarea.getTareasPrecedentes().isEmpty()) {
				this.corregirTareasPrecedentes(tarea);
				for(Tarea tareaPrecedente: tarea.getTareasPrecedentes()) {
					if(tarea.getFechaMax().before(tareaPrecedente.getFechaMax())) {
						response.put("fallo",true);
						response.put("mensaje", "Error: la tarea no puede tener una fecha máxima inferior a su tarea precedente!"
								+ "Fecha Máxima tarea precedente: "+tareaPrecedente.getFechaMax());
						return response;
					}
				}
				
				for(Tarea tareaPrecedente: tarea.getTareasPrecedentes()) {
					if(tarea.getTareasSiguientes()!=null) {
						if(!tarea.getTareasSiguientes().isEmpty()) {
							if(this.buscarConflictoTareasSiguientes(tareaPrecedente, tarea)) {
								response.put("fallo",true);
								response.put("mensaje", "Error: la tarea precedente no puede ser una tarea siguiente!");
								return response;
							}
						}
					}
					
				}
			} else {
				if(tarea.getTareaPadre()!=null) {
					if(tarea.getTareaPadre().getDias()!=-1) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(tarea.getTareaPadre().getFechaMax());
						calendar.add(calendar.DAY_OF_YEAR, -tarea.getTareaPadre().getDias());
						if(calendar.getTime().after(tarea.getFechaMax())) {
							response.put("fallo",true);
							response.put("mensaje", "Error: la tarea no puede tener una fecha máxima inferior a "+calendar.getTime());
							return response;
						}
					}
				}
			}
		}
		
		
		if(tarea.getTareaPadre()!=null) {
			if(tarea.getFechaMax().after(tarea.getTareaPadre().getFechaMax())) {
				response.put("fallo",true);
				response.put("mensaje", "Error: la tarea no puede tener una fecha máxima superior a su tarea padre!\n"
						+ "Fecha Máxima tarea padre: "+tarea.getTareaPadre().getFechaMax());
				return response;
			}
			
			if(tarea.getSubTareas()!=null) {
				for(Tarea subTarea: tarea.getSubTareas()) {
					if(subTarea.getId() == tarea.getTareaPadre().getId()) {
						response.put("fallo",true);
						response.put("mensaje", "Error: la tarea padre no puede ser una tarea subtarea!");
						return response;
					}
					
				}
			}
			
		}
		
		this.establecerDiasyNivel(tarea);
		
		return response;
	}
	
	private boolean buscarConflictoTareasSiguientes(Tarea tareaPrecedente, Tarea tarea) {
		for(Tarea tareaSiguiente: tarea.getTareasSiguientes()) {
			if(!tareaSiguiente.getTareasSiguientes().isEmpty()) {
				if(this.buscarConflictoTareasSiguientes(tareaPrecedente, tareaSiguiente)) {
					return true;
				}
			}
			if(tareaSiguiente.getId()==tareaPrecedente.getId()) {
				return true;
			}
		}
		
		return false;
		
	}

	private void corregirTareasPrecedentes(Tarea tarea) {
		List<Tarea> tareasPrecedentesExcluyente;
		for (Tarea tareaActual : tarea.getTareasPrecedentes()) {
			tareasPrecedentesExcluyente = tarea.getTareasPrecedentes().stream()
					.filter(tareaX -> tareaX.getId()!= tareaActual.getId()).collect(Collectors.toList());

			for (Tarea tareaBusqueda : tareasPrecedentesExcluyente) {
				if(!tareaBusqueda.getTareasPrecedentes().isEmpty()) {
					if(this.buscarConflictoTareasPrecedentesAmbiguas(tareaActual, tareaBusqueda)) {
						tarea.getTareasPrecedentes().remove(tareaActual);
						return;
					}
				}
			}
		}
	}
	
	private boolean buscarConflictoTareasPrecedentesAmbiguas(Tarea tareaActual, Tarea tareaBusqueda) {
		for(Tarea tareaPrecedente: tareaBusqueda.getTareasPrecedentes()) {
			if(!tareaPrecedente.getTareasPrecedentes().isEmpty()) {
				if(this.buscarConflictoTareasPrecedentesAmbiguas(tareaActual, tareaPrecedente)) {
					return true;
				}
			}
			if(tareaPrecedente.getId()==tareaActual.getId()) {
				return true;
			}
		}
		
		return false;
		
	}
	
	private void establecerDiasyNivel(Tarea tarea) {
		int dias = -1;
		int nivel = 1;
		tarea.setDias(dias);
		tarea.setNivel(nivel);
		if(tarea.getTareasPrecedentes()!=null) {
			if(!tarea.getTareasPrecedentes().isEmpty()) {
				for(Tarea tareaPrecedente: tarea.getTareasPrecedentes()) {
					if(tareaPrecedente.getTareaPadre()==null || tarea.getTareaPadre()==null) {
						if(tareaPrecedente.getTareaPadre() == tarea.getTareaPadre()) {
							dias = (int) ((tarea.getFechaMax().getTime()-tareaPrecedente.getFechaMax().getTime())/86400000);
							if(tarea.getDias()>dias || tarea.getDias()==-1) {
								tarea.setDias(dias);
							}
							
							if(nivel < tareaPrecedente.getNivel()) {
								nivel = tareaPrecedente.getNivel();
							}
							tarea.setNivel(nivel+1);
						}
					}else if(tareaPrecedente.getTareaPadre().getId()==tarea.getTareaPadre().getId()) {
						dias = (int) ((tarea.getFechaMax().getTime()-tareaPrecedente.getFechaMax().getTime())/86400000);
						if(tarea.getDias()>dias || tarea.getDias()==-1) {
							tarea.setDias(dias);
						}
						
						if(nivel < tareaPrecedente.getNivel()) {
							nivel = tareaPrecedente.getNivel();
						}
						tarea.setNivel(nivel+1);
					}
					
				}
			}
		}
		if(tarea.getTareasSiguientes() != null) {
			for(Tarea tareaSiguiente: tarea.getTareasSiguientes()) {
				this.establecerDiasyNivel(tareaSiguiente);
			}
		}
		
		
	}
	
	private void comprobarEstados(Estado estadoAnterior, Tarea tarea){
		Estado estadoAnteriorAux;
		
		if(estadoAnterior!=tarea.getEstado()) {
			switch(tarea.getEstado()) {
				case  Finalizada:
					this.emailService.finalizacionTarea(tarea);
					for (Tarea siguiente : tarea.getTareasSiguientes()) {
						if(this.esDisponible(siguiente)) {
							estadoAnteriorAux = siguiente.getEstado();
							siguiente.setEstado(Estado.Disponible);
							this.comprobarEstados(estadoAnteriorAux, this.tareaService.save(siguiente));
						} 
						
					}
					break;
					
				case  Disponible:
					this.emailService.disponibilidadTarea(tarea);
					break;
			}
		}else {
			switch(tarea.getEstado()) {
				case Pendiente:
					if(this.esDisponible(tarea)) {
						estadoAnteriorAux = tarea.getEstado();
						tarea.setEstado(Estado.Disponible);
						this.comprobarEstados(estadoAnteriorAux, this.tareaService.save(tarea));
					}
					break;
				
				case Disponible:
					if(!this.esDisponible(tarea)) {
						estadoAnteriorAux = tarea.getEstado();
						tarea.setEstado(Estado.Pendiente);
						this.comprobarEstados(estadoAnteriorAux, this.tareaService.save(tarea));
					}
				}
		}
	}
	
	private boolean esDisponible(Tarea tarea) {
		
		boolean disponible = true;
		if(tarea.getTareasPrecedentes()!=null) {
			for(Tarea tareaPrecedentes : tarea.getTareasPrecedentes()) {
				if(tareaPrecedentes.getId()!=tarea.getId() && !tareaPrecedentes.getEstado().equals(Estado.Finalizada)) {
					disponible = false;
				}
			}
		}
		return disponible;
	}
		
}
