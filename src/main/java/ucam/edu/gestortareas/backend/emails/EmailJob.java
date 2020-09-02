package ucam.edu.gestortareas.backend.emails;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import ucam.edu.gestortareas.backend.models.entity.Actor;
import ucam.edu.gestortareas.backend.models.entity.Tarea;
import ucam.edu.gestortareas.backend.models.entity.Usuario;
import ucam.edu.gestortareas.backend.models.services.IActorService;
import ucam.edu.gestortareas.backend.models.services.ISectorService;
import ucam.edu.gestortareas.backend.models.services.ITareaService;

@Component
public class EmailJob extends QuartzJobBean{

	@Autowired
    private EmailService emailService;
	
	@Autowired
	private ITareaService tareaService;
	
	@Autowired
	private IActorService actorService;
	
   
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        long id = (long) jobDataMap.get("id");
        Tarea tarea = this.tareaService.findById(id);
        String opcion = jobDataMap.getString("opcion");
        ArrayList<String> toEmails = new ArrayList<String>();
        List<Actor> actores;
        String subject = "";
        String body = "";
        Tarea tareaAux = tarea;
        int dias;
        
        switch(opcion) {
        case "creacion": 
        	actores = this.actorService.findByIdSector(tareaAux.getSector().getId());
        	for(Actor actor : actores) {
        		if(actor.isEncargado()) {
        			toEmails.add(actor.getEmail());
        		}
        	}
        	actores = this.actorService.findByIdTarea(tareaAux.getId());
        	for(Actor actor : actores) {
        		toEmails.add(actor.getEmail());
        	}
        	do{
        		for (Usuario usuario : tareaAux.getUsuarios()) {
        			toEmails.add(usuario.getEmail());
        		}
        		tareaAux = tareaAux.getTareaPadre();
        	}while(tareaAux!=null);
        	
        	
        	subject = "Gestor de Tareas - Creación tarea: "+tarea.getTitulo();
        	body = "Se ha creado la tarea "+tarea.getTitulo()+", en la que eres partícipe."
        			+ "<br>-Descripción: "+tarea.getDescripcion()
        			+ "<br>-Fecha Máxima: "+tarea.getFechaMax()
        			+ "<br>-Espacio: "+tarea.getEspacio()
        			+ "<br>-Estado: "+tarea.getEstado();
        	if(!toEmails.isEmpty()) {
        		this.emailService.sendMail(toEmails, subject, body);
        	}
        	break;
        	
        case "avisoFecha":
        	dias = jobDataMap.getInt("dias");
        	actores = this.actorService.findByIdSector(tareaAux.getSector().getId());
        	for(Actor actor : actores) {
        		if(actor.isEncargado()) {
        			toEmails.add(actor.getEmail());
        		}
        	}
        	actores = this.actorService.findByIdTarea(tareaAux.getId());
        	for(Actor actor : actores) {
        		toEmails.add(actor.getEmail());
        	}
        	do{
        		for (Usuario usuario : tareaAux.getUsuarios()) {
        			toEmails.add(usuario.getEmail());
        		}
        		tareaAux = tareaAux.getTareaPadre();
        	}while(tareaAux!=null);
        	
        	subject = "Gestor de Tareas - Quedan "+dias+" dias para la tarea: "+tarea.getTitulo();
        	body = "Solo quedan "+dias+" dias para que la tarea "+tarea.getTitulo()+" llegue a su fecha máxima."
        			+ "<br>-Descripción: "+tarea.getDescripcion()
        			+ "<br>-Fecha Máxima: "+tarea.getFechaMax()
        			+ "<br>-Espacio: "+tarea.getEspacio()
        			+ "<br>-Estado: "+tarea.getEstado();
        	if(!toEmails.isEmpty()) {
        		this.emailService.sendMail(toEmails, subject, body);
        	}
        	break;
        	
        case "avisoFecha2":
        	dias = jobDataMap.getInt("dias");
        	actores = this.actorService.findByIdSector(tareaAux.getSector().getId());
        	for(Actor actor : actores) {
        		if(actor.isEncargado()) {
        			toEmails.add(actor.getEmail());
        		}
        	}
        	actores = this.actorService.findByIdTarea(tareaAux.getId());
        	for(Actor actor : actores) {
        		toEmails.add(actor.getEmail());
        	}
        	do{
        		for (Usuario usuario : tareaAux.getUsuarios()) {
        			toEmails.add(usuario.getEmail());
        		}
        		tareaAux = tareaAux.getTareaPadre();
        	}while(tareaAux!=null);
        	
        	subject = "Gestor de Tareas - Quedan "+dias+" dias para la tarea: "+tarea.getTitulo();
        	body = "Solo quedan "+dias+" dias para que la tarea "+tarea.getTitulo()+" llegue a su fecha máxima."
        			+ "<br>-Descripción: "+tarea.getDescripcion()
        			+ "<br>-Fecha Máxima: "+tarea.getFechaMax()
        			+ "<br>-Espacio: "+tarea.getEspacio()
        			+ "<br>-Estado: "+tarea.getEstado();
        	if(!toEmails.isEmpty()) {
        		this.emailService.sendMail(toEmails, subject, body);
        	}
        	break;
        	
        case "eliminacion":
        	actores = this.actorService.findByIdSector(tareaAux.getSector().getId());
        	for(Actor actor : actores) {
        		if(actor.isEncargado()) {
        			toEmails.add(actor.getEmail());
        		}
        	}
        	actores = this.actorService.findByIdTarea(tareaAux.getId());
        	for(Actor actor : actores) {
        		toEmails.add(actor.getEmail());
        	}
        	do{
        		for (Usuario usuario : tareaAux.getUsuarios()) {
        			toEmails.add(usuario.getEmail());
        		}
        		tareaAux = tareaAux.getTareaPadre();
        	}while(tareaAux!=null);
        	
        	subject = "Gestor de Tareas - Eliminación de la tarea: "+tarea.getTitulo();
        	body = "Se ha eliminado la tarea "+tarea.getTitulo()
		        	+ "<br>-Descripción: "+tarea.getDescripcion()
					+ "<br>-Fecha Máxima: "+tarea.getFechaMax()
					+ "<br>-Espacio: "+tarea.getEspacio()
					+ "<br>-Estado: "+tarea.getEstado();
        	if(!toEmails.isEmpty()) {
        		this.emailService.sendMail(toEmails, subject, body);
        	}
        	break;
        	
        case "disponibilidad":
        	actores = this.actorService.findByIdSector(tareaAux.getSector().getId());
        	for(Actor actor : actores) {
        		if(actor.isEncargado()) {
        			toEmails.add(actor.getEmail());
        		}
        	}
        	actores = this.actorService.findByIdTarea(tareaAux.getId());
        	for(Actor actor : actores) {
        		toEmails.add(actor.getEmail());
        	}
        	do{
        		for (Usuario usuario : tareaAux.getUsuarios()) {
        			toEmails.add(usuario.getEmail());
        		}
        		tareaAux = tareaAux.getTareaPadre();
        	}while(tareaAux!=null);
        	
        	
        	subject = "Gestor de Tareas - Disponibilidad de la tarea: "+tarea.getTitulo();
        	body = "Ya se encuentra disponible la tarea "+tarea.getTitulo()
		        	+ "<br>-Descripción: "+tarea.getDescripcion()
					+ "<br>-Fecha Máxima: "+tarea.getFechaMax()
					+ "<br>-Espacio: "+tarea.getEspacio()
					+ "<br>-Estado: "+tarea.getEstado();
        	if(!toEmails.isEmpty()) {
        		this.emailService.sendMail(toEmails, subject, body);
        	}
        	break;
        	
        case "finalizacion":
        	actores = this.actorService.findByIdSector(tareaAux.getSector().getId());
        	for(Actor actor : actores) {
        		if(actor.isEncargado()) {
        			toEmails.add(actor.getEmail());
        		}
        	}
        	actores = this.actorService.findByIdTarea(tareaAux.getId());
        	for(Actor actor : actores) {
        		toEmails.add(actor.getEmail());
        	}
        	do{
        		for (Usuario usuario : tareaAux.getUsuarios()) {
        			toEmails.add(usuario.getEmail());
        		}
        		tareaAux = tareaAux.getTareaPadre();
        	}while(tareaAux!=null);
        	
        	subject = "Gestor de Tareas - Finalización de la tarea: "+tarea.getTitulo();
        	body = "Ya se ha completado la tarea "+tarea.getTitulo()
		        	+ "<br>-Descripción: "+tarea.getDescripcion()
					+ "<br>-Fecha Máxima: "+tarea.getFechaMax()
					+ "<br>-Espacio: "+tarea.getEspacio()
					+ "<br>-Estado: "+tarea.getEstado();
        	if(!toEmails.isEmpty()) {
        		this.emailService.sendMail(toEmails, subject, body);
        	}
        	break;
        	
        case "addUsuarios":
        	toEmails = (ArrayList<String>) jobDataMap.get("toEmails");
        	
        	subject = "Gestor de Tareas - Añadido a la tarea: "+tarea.getTitulo();
        	body = "Se te ha añadido a la tarea "+tarea.getTitulo()
		        	+ "<br>-Descripción: "+tarea.getDescripcion()
					+ "<br>-Fecha Máxima: "+tarea.getFechaMax()
					+ "<br>-Espacio: "+tarea.getEspacio()
					+ "<br>-Estado: "+tarea.getEstado();
        	if(!toEmails.isEmpty()) {
        		this.emailService.sendMail(toEmails, subject, body);
        	}
        	break;
        }
        
    }

}
