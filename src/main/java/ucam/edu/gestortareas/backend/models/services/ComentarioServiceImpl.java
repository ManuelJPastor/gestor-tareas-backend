package ucam.edu.gestortareas.backend.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ucam.edu.gestortareas.backend.models.dao.IComentarioDao;
import ucam.edu.gestortareas.backend.models.entity.Comentario;

@Service
public class ComentarioServiceImpl implements IComentarioService{

	@Autowired
	private IComentarioDao comentarioDao;
	
	@Override
	public List<Comentario> findAll() {
		// TODO Auto-generated method stub
		return (List<Comentario>) comentarioDao.findAll();
	}

	@Override
	public Comentario findById(Long id) {
		// TODO Auto-generated method stub
		return comentarioDao.findById(id).orElse(null);
	}

	@Override
	public Comentario save(Comentario comentario) {
		// TODO Auto-generated method stub
		return comentarioDao.save(comentario);
	}

	@Override
	public void delete(Long id) {
		comentarioDao.deleteById(id);// TODO Auto-generated method stub
		
	}

	@Override
	public List<Comentario> findByIdTarea(Long id) {
		// TODO Auto-generated method stub
		return comentarioDao.findByIdTarea(id);
	}

}
