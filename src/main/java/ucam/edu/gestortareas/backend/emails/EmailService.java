package ucam.edu.gestortareas.backend.emails;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import ucam.edu.gestortareas.backend.models.entity.Tarea;
import ucam.edu.gestortareas.backend.models.entity.Usuario;

@Service
public class EmailService {

	@Autowired
    private Scheduler scheduler;
	
	
	@Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailProperties mailProperties;
    
    public void creacionTarea(Tarea tarea) {
    	JobDataMap jobDataMap = new JobDataMap();
    	jobDataMap.put("id", tarea.getId());
        jobDataMap.put("opcion", "creacion");
        
    	this.crearJob(jobDataMap, ZonedDateTime.now());
    	
    	this.actualizacionTarea(tarea);
	}
    
    public void actualizacionTarea(Tarea tarea) {
    	JobDataMap jobDataMap = new JobDataMap();
    	jobDataMap.put("id", tarea.getId());
        jobDataMap.put("opcion", "avisoFecha");
        
        Date date = new Date(tarea.getFechaMax().getTime());
        
    	ZonedDateTime fechaAviso = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    	if(tarea.getDiasAviso()!=0) {
    		jobDataMap.put("dias", tarea.getDiasAviso());
    		fechaAviso = fechaAviso.minusDays(tarea.getDiasAviso());
    		if(ZonedDateTime.now().compareTo(fechaAviso)<0) {
        		this.crearJob(jobDataMap, fechaAviso);
        	}
    		if(tarea.getDiasAviso2()!=0) {
        		jobDataMap.put("opcion", "avisoFecha2");
        		jobDataMap.put("dias", tarea.getDiasAviso2());
            	fechaAviso = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            	fechaAviso = fechaAviso.minusDays(tarea.getDiasAviso2());
            	if(ZonedDateTime.now().compareTo(fechaAviso)<0) {
            		this.crearJob(jobDataMap, fechaAviso);
            	}
        	}
    	}
    	
    	
    	
    }
    
    public void eliminacionTarea(Tarea tarea) {
    	JobDataMap jobDataMap = new JobDataMap();
    	jobDataMap.put("id", tarea.getId());
        jobDataMap.put("opcion", "eliminacion");
    	this.crearJob(jobDataMap, ZonedDateTime.now());
    	
    	try {
 			if(scheduler.checkExists(new JobKey(String.valueOf(tarea.getId()), "avisoFecha"))){
 				scheduler.deleteJob(new JobKey(String.valueOf(tarea.getId()), "avisoFecha"));
 			}
 			if(scheduler.checkExists(new JobKey(String.valueOf(tarea.getId()), "avisoFecha2"))){
 				scheduler.deleteJob(new JobKey(String.valueOf(tarea.getId()), "avisoFecha2"));
 			}
 		} catch (SchedulerException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
    }
    
    public void disponibilidadTarea(Tarea tarea) {
    	JobDataMap jobDataMap = new JobDataMap();
    	jobDataMap.put("id", tarea.getId());
        jobDataMap.put("opcion", "disponibilidad");
    	this.crearJob(jobDataMap, ZonedDateTime.now());
    }
    
    public void finalizacionTarea(Tarea tarea) {
    	JobDataMap jobDataMap = new JobDataMap();
    	jobDataMap.put("id", tarea.getId());
        jobDataMap.put("opcion", "finalizacion");
    	this.crearJob(jobDataMap, ZonedDateTime.now());
    	
    	try {
 			if(scheduler.checkExists(new JobKey(String.valueOf(tarea.getId()), "avisoFecha"))){
 				scheduler.deleteJob(new JobKey(String.valueOf(tarea.getId()), "avisoFecha"));
 			}
 			if(scheduler.checkExists(new JobKey(String.valueOf(tarea.getId()), "avisoFecha2"))){
 				scheduler.deleteJob(new JobKey(String.valueOf(tarea.getId()), "avisoFecha2"));
 			}
 		} catch (SchedulerException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
    }
    
    public void addUsuariosTarea(Tarea tarea, ArrayList<String> toEmails) {
    	JobDataMap jobDataMap = new JobDataMap();
    	jobDataMap.put("id", tarea.getId());
        jobDataMap.put("opcion", "addUsuarios");
        jobDataMap.put("toEmails", toEmails);
    	this.crearJob(jobDataMap, ZonedDateTime.now());
    }
    
    /*public void creacionTarea(Tarea tarea) {
        //ZonedDateTime dateTime = ZonedDateTime.ofInstant(tarea.getFechaMax().toInstant(), ZoneId.systemDefault());
		LocalDateTime localDateTime = LocalDateTime.parse("2020-04-26T09:10:00");
		ZonedDateTime dateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
		System.out.println(dateTime.getDayOfMonth()+" "+dateTime.getHour()+" "+dateTime.getMinute());

        crearJob(tarea.getId(), "crear", dateTime);
	        
	    
	}*/
    
    private void crearJob(JobDataMap jobDataMap, ZonedDateTime startAt) {
    	long id = jobDataMap.getLong("id");
    	String opcion = jobDataMap.getString("opcion");
    	
    	JobDetail jobDetail = buildJobDetail(jobDataMap);
        Trigger trigger = buildJobTrigger(jobDetail, startAt);
        try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private JobDetail buildJobDetail(JobDataMap jobDataMap) {
    	long id = jobDataMap.getLong("id");
    	String opcion = jobDataMap.getString("opcion");
        
        try {
			if(scheduler.checkExists(new JobKey(String.valueOf(id), opcion))){
				scheduler.deleteJob(new JobKey(String.valueOf(id), opcion));
			}
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(String.valueOf(id), opcion)
                .withDescription("Envío de email por "+opcion+" en la tarea con id "+id)
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), jobDetail.getKey().getGroup())
                .withDescription("Trigger del jobDetail: "+jobDetail.getKey())
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
	
	public void sendMail(ArrayList<String> toEmails, String subject, String body) {
		
		String[] emails = new String[toEmails.size()];              
		for(int i=0;i<toEmails.size();i++){
		  emails[i] = toEmails.get(i);
		}
		
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            messageHelper.setFrom(mailProperties.getUsername());
            messageHelper.setTo(emails);
            mailSender.send(message);
        } catch (MessagingException ex) {
            System.out.println("Failed to send email to " + emails);
        }
    }
}
