package ucam.edu.gestortareas.backend.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ucam.edu.gestortareas.backend.models.dao.IActorDao;
import ucam.edu.gestortareas.backend.models.entity.Actor;

@Service
public class ActorServiceImpl implements IActorService{

	@Autowired
	private IActorDao actorDao;
	
	@Override
	public List<Actor> findAll() {
		return (List<Actor>) actorDao.findAll();
	}

	@Override
	public Actor findById(Long id) {
		return actorDao.findById(id).orElse(null);
	}

	@Override
	public Actor save(Actor actor) {
		return actorDao.save(actor);
	}

	@Override
	public void delete(Long id) {
		actorDao.deleteById(id);
		
	}

	@Override
	public List<Actor> findByIdSector(Long id) {
		// TODO Auto-generated method stub
		return actorDao.findByIdSector(id);
	}

	@Override
	public List<Actor> findByIdTarea(Long id) {
		// TODO Auto-generated method stub
		return actorDao.findByIdTarea(id);
	}

}
