package ucam.edu.gestortareas.backend.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ucam.edu.gestortareas.backend.models.dao.IPlantillaDao;
import ucam.edu.gestortareas.backend.models.entity.Plantilla;

@Service
public class PlantillaServiceImpl implements IPlantillaService{

	@Autowired
	private IPlantillaDao plantillaDao;
	
	@Override
	public List<Plantilla> findAll() {
		return (List<Plantilla>) plantillaDao.findAll();
	}
	
	@Override
	public List<Plantilla> findPlantillas() {
		return (List<Plantilla>) plantillaDao.findPlantillas();
	}

	@Override
	public Plantilla findById(Long id) {
		return plantillaDao.findById(id).orElse(null);
	}

	@Override
	public Plantilla save(Plantilla plantilla) {
		return plantillaDao.save(plantilla);
	}

	@Override
	public void delete(Long id) {
		plantillaDao.deleteById(id);
		
	}
	
	@Override
	public List<Plantilla> findPlantillasPadre() {
		return plantillaDao.findPlantillasPadre();
	}
	
	@Override
	public List<Plantilla> findSubPlantillasById(Long id) {
		// TODO Auto-generated method stub
		return plantillaDao.findSubPlantillasById(id);
	}

	@Override
	public List<Plantilla> findPlantillasPrecedentesById(Long id) {
		return plantillaDao.findPlantillasPrecedentesById(id);
	}

	@Override
	public List<Plantilla> findRamaPlantillasById(Long id) {
		// TODO Auto-generated method stub
		return plantillaDao.findRamaPlantillasById(id);
	}

}
