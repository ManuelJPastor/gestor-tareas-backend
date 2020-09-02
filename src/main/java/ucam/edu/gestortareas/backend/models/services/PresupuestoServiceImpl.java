package ucam.edu.gestortareas.backend.models.services;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import ucam.edu.gestortareas.backend.models.dao.IPresupuestoDao;
import ucam.edu.gestortareas.backend.models.entity.Presupuesto;
import ucam.edu.gestortareas.backend.models.entity.Tarea;

@Service
public class PresupuestoServiceImpl implements IPresupuestoService{

	@Autowired
	private IPresupuestoDao presupuestoDao;
	
	@Override
	public List<Presupuesto> findAll() {
		return (List<Presupuesto>) presupuestoDao.findAll();
	}

	@Override
	public Presupuesto findById(Long id) {
		return presupuestoDao.findById(id).orElse(null);
	}

	@Override
	public Presupuesto save(MultipartFile file, Tarea tarea) {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
            	return null;
            }

            Presupuesto presupuesto = new Presupuesto(fileName, file.getContentType(), file.getBytes(), tarea);

            return presupuestoDao.save(presupuesto);
        } catch (IOException ex) {
        	return null;
        }
	}

	@Override
	public void delete(Long id) {
		presupuestoDao.deleteById(id);
		
	}

	@Override
	public List<Presupuesto> findByIdTarea(Long id) {
		// TODO Auto-generated method stub
		return presupuestoDao.findByIdTarea(id);
	}

}
