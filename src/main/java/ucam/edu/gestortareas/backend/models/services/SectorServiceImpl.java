package ucam.edu.gestortareas.backend.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ucam.edu.gestortareas.backend.models.dao.ISectorDao;
import ucam.edu.gestortareas.backend.models.entity.Sector;

@Service
public class SectorServiceImpl implements ISectorService{

	@Autowired
	private ISectorDao sectorDao;
	
	@Override
	public List<Sector> findAll() {
		return (List<Sector>) sectorDao.findAll();
	}

	@Override
	public Sector findById(Long id) {
		return sectorDao.findById(id).orElse(null);
	}

	@Override
	public Sector save(Sector sector) {
		return sectorDao.save(sector);
	}

	@Override
	public void delete(Long id) {
		sectorDao.deleteById(id);
	}

	@Override
	public Sector findByIdTarea(Long id) {
		sectorDao.findByIdTarea(id);
		return null;
	}

}
