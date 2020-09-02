package ucam.edu.gestortareas.backend.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ucam.edu.gestortareas.backend.models.entity.Usuario;

@SpringBootTest
public class UsuarioRestControllerTests {
	
	@Autowired
	private UsuarioRestController controller;

	@Test
    public void findAll_UsuariosNotNull_ShouldReturnList() throws Exception {
        Usuario first = new Usuario();
        first.setId(1L);
		first.setNombre("nombre");
		first.setEmail("email@gmail.com");
		first.setEnabled(true);
		first.setPassword("pruebapassword");
        		
		Usuario second = new Usuario();
		second.setId(1L);
		second.setNombre("nombre11");
		second.setEmail("email11@gmail.com");
		second.setEnabled(true);
		second.setPassword("pruebapassword11");
 
		List<Usuario> usuarios = controller.index();
		assertNotNull(usuarios);
		
        //when(usuarioServiceMock.findAll()).thenReturn(Arrays.asList(first, second));
 
        //mockMvc.perform(get("http://localhost:8080/api/usuarios"))
               /* .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("nombre1")))
                .andExpect(jsonPath("$[0].apellido", is("apellido1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("nombre2")))
                .andExpect(jsonPath("$[1].apellido", is("apellido2")))*//*;
 
        verify(usuarioServiceMock, times(1)).findAll();
        verifyNoMoreInteractions(usuarioServiceMock);*/
    }
}
