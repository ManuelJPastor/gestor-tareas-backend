package ucam.edu.gestortareas.backend.models.entity;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CollectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import ucam.edu.gestortareas.backend.emails.EmailService;


@Entity
@Table(name="tareas")
public class Tarea implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public enum Estado {enProceso, Pendiente, Finalizada, Disponible}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty(message = "no puede estar vacío")
	@Size(max=50, message = "no puede ser superior a 50 caracteres")
	@Column(nullable = false)
	private String titulo;
	
	private String descripcion;
	
	private String espacio;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private Date fechaMax;
	
	@Column(nullable = false)
	private int dias=-1;
	
	@Column(nullable = false)
	private int nivel = 1;
	
	@Enumerated(EnumType.STRING)
	private Estado estado = Estado.Pendiente;
	
	@Column(nullable = false)
	private int diasAviso = 0;
	
	@Column(nullable = false)
	private int diasAviso2 = 0;
	
	@ManyToOne
    @JoinColumn(name="sector_id", nullable = false)
	private Sector sector;
	
	@ManyToOne
    @JoinColumn(name="tareaPadre_id")
	private Tarea tareaPadre;
	
	@JsonIgnore
	@OneToMany(mappedBy = "tareaPadre")
	private List<Tarea> subTareas;
	
	@ManyToMany
    @JoinTable(
       name="tareas_precedentes",
       joinColumns={@JoinColumn(name="tarea_id", referencedColumnName="id")},
       inverseJoinColumns={@JoinColumn(name="tarea_precedente_id", referencedColumnName="id")})
    private List<Tarea> tareasPrecedentes;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "tareasPrecedentes")
    private List<Tarea> tareasSiguientes;
	
	
	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
       name="tarea_user",
       joinColumns={@JoinColumn(name="tarea_id", referencedColumnName="id")},
       inverseJoinColumns={@JoinColumn(name="user_id", referencedColumnName="id")})
    private List<Usuario> usuarios;
	
	@ManyToMany
    @JoinTable(
       name="tarea_actor",
       joinColumns={@JoinColumn(name="tarea_id", referencedColumnName="id")},
       inverseJoinColumns={@JoinColumn(name="actor_id", referencedColumnName="id")})
    private List<Actor> actores;
	
	@JsonIgnore
	@OneToMany(mappedBy = "tarea")
	private List<Comentario> comentarios;
	
	@JsonIgnore
	@OneToMany(mappedBy = "tarea")
	private List<Presupuesto> presupuestos;
	
	@Column(nullable = true)
	private long presupuestoEscogidoId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getEspacio() {
		return espacio;
	}

	public void setEspacio(String espacio) {
		this.espacio = espacio;
	}

	public Date getFechaMax() {
		return fechaMax;
	}

	public void setFechaMax(Date fechaMax) {
		this.fechaMax = fechaMax;
	}

	public int getDias() {
		return dias;
	}

	public void setDias(int dias) {
		this.dias = dias;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public int getDiasAviso() {
		return diasAviso;
	}

	public void setDiasAviso(int diasAviso) {
		this.diasAviso = diasAviso;
	}

	public int getDiasAviso2() {
		return diasAviso2;
	}

	public void setDiasAviso2(int diasAviso2) {
		this.diasAviso2 = diasAviso2;
	}

	public Sector getSector() {
		return sector;
	}

	public void setSector(Sector sector) {
		this.sector = sector;
	}

	public Tarea getTareaPadre() {
		return tareaPadre;
	}

	public void setTareaPadre(Tarea tareaPadre) {
		this.tareaPadre = tareaPadre;
	}

	public List<Tarea> getSubTareas() {
		return subTareas;
	}

	public void setSubTareas(List<Tarea> subTareas) {
		this.subTareas = subTareas;
	}

	public List<Tarea> getTareasPrecedentes() {
		return tareasPrecedentes;
	}

	public void setTareasPrecedentes(List<Tarea> tareasPrecedentes) {
		this.tareasPrecedentes = tareasPrecedentes;
	}

	public List<Tarea> getTareasSiguientes() {
		return tareasSiguientes;
	}

	public void setTareasSiguientes(List<Tarea> tareasSiguientes) {
		this.tareasSiguientes = tareasSiguientes;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public List<Actor> getActores() {
		return actores;
	}

	public void setActores(List<Actor> actores) {
		this.actores = actores;
	}

	public List<Comentario> getComentarios() {
		return comentarios;
	}

	public void setComentarios(List<Comentario> comentarios) {
		this.comentarios = comentarios;
	}

	public List<Presupuesto> getPresupuestos() {
		return presupuestos;
	}

	public void setPresupuestos(List<Presupuesto> presupuestos) {
		this.presupuestos = presupuestos;
	}

	public long getPresupuestoEscogidoId() {
		return presupuestoEscogidoId;
	}

	public void setPresupuestoEscogidoId(long presupuestoEscogidoId) {
		this.presupuestoEscogidoId = presupuestoEscogidoId;
	}

}
