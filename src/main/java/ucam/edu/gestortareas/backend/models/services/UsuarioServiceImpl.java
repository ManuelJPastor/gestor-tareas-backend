package ucam.edu.gestortareas.backend.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ucam.edu.gestortareas.backend.models.dao.IUsuarioDao;
import ucam.edu.gestortareas.backend.models.entity.Role;
import ucam.edu.gestortareas.backend.models.entity.Usuario;

@Service
public class UsuarioServiceImpl implements IUsuarioService{

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private IUsuarioDao usuarioDao;
	
	@Transactional(readOnly = true)
	public List<Usuario> findAll() {
		return (List<Usuario>) usuarioDao.findAll();
	}

	@Transactional(readOnly = true)
	public Usuario findById(Long id) {
		return usuarioDao.findById(id).orElse(null);
	}

	@Transactional
	public Usuario save(Usuario usuario) {
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		return usuarioDao.save(usuario);
	}
	
	@Transactional
	public Usuario update(Usuario usuario) {
		return usuarioDao.save(usuario);
	}

	@Transactional
	public void delete(Long id) {
		usuarioDao.deleteById(id);
	}
	
	@Transactional(readOnly = true)
	public Usuario findByEmail(String email){
		return usuarioDao.findByEmail(email);
		
	}

	@Override
	public List<Usuario> findBySector(long id) {
		// TODO Auto-generated method stub
		return usuarioDao.findBySector(id);
	}
	
}
