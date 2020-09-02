package ucam.edu.gestortareas.backend.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ucam.edu.gestortareas.backend.models.dao.IUsuarioDao;
import ucam.edu.gestortareas.backend.models.entity.Usuario;
import ucam.edu.gestortareas.backend.models.services.IUsuarioService;

@SpringBootTest
public class UsuarioServiceTests {

	@Autowired
	private IUsuarioDao usuarioDao;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Test
	public void findAllUsuarios() {
		assertTrue(usuarioService.findAll() instanceof List<?>);
		assertNotNull(usuarioService.findAll());
	}
	
	@Test
	public void saveUsuario() {
		Usuario usuario = new Usuario();
		usuario.setNombre("nombre");
		usuario.setEmail("emailprueba1@gmail.com");
		usuario.setEnabled(true);
		usuario.setPassword(passwordEncoder.encode("pruebapassword"));
		
		Usuario usuarioRespuesta = usuarioService.save(usuario);
		assertTrue(usuario.getNombre() == usuarioRespuesta.getNombre());
		assertTrue(usuario.getEmail() == usuarioRespuesta.getEmail());
		assertTrue(usuario.getPassword() == usuarioRespuesta.getPassword());
		
		usuarioDao.deleteById(usuarioRespuesta.getId());
	}
	
	@Test
	public void findByIdUsuario() {
		Usuario usuario = new Usuario();
		usuario.setNombre("nombre");
		usuario.setEmail("emailprueba2@gmail.com");
		usuario.setEnabled(true);
		usuario.setPassword(passwordEncoder.encode("pruebapassword"));
		
		Usuario usuarioGuardado = usuarioService.save(usuario);
		Usuario usuarioBuscado = usuarioService.findById(usuarioGuardado.getId());
		
		assertTrue(usuarioGuardado.getId() == usuarioBuscado.getId());
		assertTrue(usuarioGuardado.getNombre().contentEquals(usuarioBuscado.getNombre()));
		assertTrue(usuarioGuardado.getEmail().contentEquals(usuarioBuscado.getEmail()));
		assertTrue(usuarioGuardado.getPassword().contentEquals(usuarioBuscado.getPassword()));
		
		usuarioDao.deleteById(usuarioGuardado.getId());
	}
}
