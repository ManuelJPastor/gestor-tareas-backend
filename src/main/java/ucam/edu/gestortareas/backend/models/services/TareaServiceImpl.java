package ucam.edu.gestortareas.backend.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ucam.edu.gestortareas.backend.models.dao.ITareaDao;
import ucam.edu.gestortareas.backend.models.entity.Tarea;

@Service
public class TareaServiceImpl implements ITareaService{

	@Autowired
	private ITareaDao tareaDao;
	
	@Override
	public List<Tarea> findAll() {
		return (List<Tarea>) tareaDao.findAll();
	}

	@Override
	public Tarea findById(Long id) {
		return tareaDao.findById(id).orElse(null);
	}

	@Override
	public Tarea save(Tarea tarea) {
		return tareaDao.save(tarea);
	}

	@Override
	public void delete(Long id) {
		tareaDao.deleteById(id);
		
	}
	
	@Override
	public List<Tarea> findTareasPadre() {
		return tareaDao.findTareasPadre();
	}
	
	@Override
	public List<Tarea> findSubTareasById(Long id) {
		// TODO Auto-generated method stub
		return tareaDao.findSubTareasById(id);
	}

	@Override
	public List<Tarea> findTareasPrecedentesById(Long id) {
		return tareaDao.findTareasPrecedentesById(id);
	}

	@Override
	public List<Tarea> findRamaTareasById(Long id) {
		return tareaDao.findRamaTareasById(id);
	}

	@Override
	public List<Tarea> findByEmailUsuario(String email) {
		// TODO Auto-generated method stub
		return tareaDao.findByEmailUsuario(email);
	}

}
