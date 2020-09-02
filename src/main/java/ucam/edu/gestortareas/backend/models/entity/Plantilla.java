package ucam.edu.gestortareas.backend.models.entity;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity
@Table(name="plantillas")
public class Plantilla implements Serializable{

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
    @JoinColumn(name="plantillaPadre_id")
	private Plantilla plantillaPadre;
	
	@JsonIgnore
	@OneToMany(mappedBy = "plantillaPadre")
	private List<Plantilla> subPlantillas;
	
	@ManyToMany
    @JoinTable(
       name="plantillas_precedentes",
       joinColumns={@JoinColumn(name="plantilla_id", referencedColumnName="id")},
       inverseJoinColumns={@JoinColumn(name="plantilla_precedente_id", referencedColumnName="id")})
    private List<Plantilla> plantillasPrecedentes;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "plantillasPrecedentes")
    private List<Plantilla> plantillasSiguientes;
	
	private boolean plantilla;
	
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

	public Plantilla getPlantillaPadre() {
		return plantillaPadre;
	}

	public void setPlantillaPadre(Plantilla plantillaPadre) {
		this.plantillaPadre = plantillaPadre;
	}

	public List<Plantilla> getSubPlantillas() {
		return subPlantillas;
	}

	public void setSubPlantillas(List<Plantilla> subPlantillas) {
		this.subPlantillas = subPlantillas;
	}

	public List<Plantilla> getPlantillasPrecedentes() {
		return plantillasPrecedentes;
	}

	public void setPlantillasPrecedentes(List<Plantilla> plantillasPrecedentes) {
		this.plantillasPrecedentes = plantillasPrecedentes;
	}

	public List<Plantilla> getPlantillasSiguientes() {
		return plantillasSiguientes;
	}

	public void setPlantillasSiguientes(List<Plantilla> plantillasSiguientes) {
		this.plantillasSiguientes = plantillasSiguientes;
	}

	public boolean isPlantilla() {
		return plantilla;
	}

	public void setPlantilla(boolean plantilla) {
		this.plantilla = plantilla;
	}

}
