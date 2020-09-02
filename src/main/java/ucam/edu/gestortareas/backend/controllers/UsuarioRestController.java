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

import ucam.edu.gestortareas.backend.models.entity.Role;
import ucam.edu.gestortareas.backend.models.entity.Tarea;
import ucam.edu.gestortareas.backend.models.entity.Usuario;
import ucam.edu.gestortareas.backend.models.services.IRoleService;
import ucam.edu.gestortareas.backend.models.services.IUsuarioService;

@CrossOrigin(origins = {"http://localhost:4200"}/*, maxAge = 3600,
allowedHeaders={"x-auth-token", "x-requested-with", "x-xsrf-token"}*/)
@RestController
@RequestMapping("/api")
public class UsuarioRestController {

	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IRoleService roleService;
	
	@GetMapping("/usuarios")
	public List<Usuario> index(){
		return usuarioService.findAll();
	}
	
	@GetMapping("/usuarios/{id}")
	public ResponseEntity<?> show(@PathVariable Long id){
		Usuario usuario = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			usuario = usuarioService.findById(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(usuario == null) {
			response.put("mensaje", "El cliente ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Usuario>(usuario, HttpStatus.OK); 
	}
	
	@PostMapping("/usuarios")
	public ResponseEntity<?> create(@Valid @RequestBody Usuario usuario, BindingResult result) {
		Usuario usuarioNuevo = null;
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
			usuarioNuevo = usuarioService.save(usuario);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El usuario ha sido creado con éxito!");
		response.put("usuario", usuarioNuevo);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); 
	}
	
	@PutMapping("/usuarios/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Usuario usuario, BindingResult result, @PathVariable Long id) {
		Usuario usuarioActual = null;
		Usuario usuarioNuevo = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		
		
		try {
			usuarioActual = usuarioService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(usuarioActual == null) {
			response.put("mensaje", "Error: no se pudo editar, el cliente ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		boolean cambioContraseña = false;
		usuarioActual.setNombre(usuario.getNombre());
		usuarioActual.setEmail(usuario.getEmail());
		if(!usuario.getPassword().isEmpty()) {
			cambioContraseña = true;
			usuarioActual.setPassword(usuario.getPassword());
		}
		usuarioActual.setSector(usuario.getSector());
		usuarioActual.setRoles(usuario.getRoles());
		
		try {
			if(cambioContraseña) {
				usuarioNuevo = usuarioService.save(usuarioActual);
			} else {
				usuarioNuevo = usuarioService.update(usuarioActual);
			}
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el update en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		response.put("mensaje", "El usuario ha sido actualizado con éxito!");
		response.put("usuario", usuarioNuevo);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/usuarios/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			usuarioService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el delete en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El usuario ha sido eliminado con éxito!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@GetMapping("/usuarios/email/{email}")
	public ResponseEntity<?> getUsuariosByEmail(@PathVariable String email){
		Usuario usuario = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			usuario = usuarioService.findByEmail(email);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(usuario == null) {
			response.put("mensaje", "El cliente con email: "+ email.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Usuario>(usuario, HttpStatus.OK); 
	}
	
	@GetMapping("/usuarios/sector/{id}")
	public ResponseEntity<?> getUsuariosBySector(@PathVariable Long id){
		List<Usuario> usuarios = null;
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			usuarios = usuarioService.findBySector(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos!");
			response.put("error", e.getMessage() + ": " + e.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(usuarios == null) {
			response.put("mensaje", "El sector con ID: "+ id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	@GetMapping("/roles")
	public List<Role> getRoles(){
		return roleService.findAll();
	}
	
}
