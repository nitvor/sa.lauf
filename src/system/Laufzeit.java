package system;

import javax.persistence.*;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import swa.runningeasy.dtos.LaufzeitDTO;

@Entity
public class Laufzeit {
	@Transient
	private static Logger log = LogManager.getRootLogger();
	@Id
	@GeneratedValue
	private int id;
	
	private float distanz;
	@Temporal(TemporalType.DATE)
	private Date laufzeit;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Laufzeit> zwischenzeiten = new ArrayList<Laufzeit>();
	
	public Laufzeit(){
		
	}
	
	public Laufzeit(float distanz,Date laufzeit){
		this.distanz = distanz;
		this.laufzeit = laufzeit;
	}
	
	public Laufzeit(float distanz,Date laufzeit,List<Laufzeit> zwischenzeiten){
		this(distanz,laufzeit);
		for(Laufzeit l : zwischenzeiten){
			this.zwischenzeiten.add(l);
		}
	}
	
	public LaufzeitDTO generateDTO(int startNummer, String veranstaltung){
		return new LaufzeitDTO(
				startNummer,
				this.laufzeit,
				veranstaltung
				);
	}
	
	//Getter und Setter
	public float getDistanz() {
		return distanz;
	}
	public Date getLaufzeit() {
		return laufzeit;
	}
	
	public List<Laufzeit> getZwischenzeiten() {
		return zwischenzeiten;
	}
	public void setZwischenzeiten(List<Laufzeit> zwischenzeiten) {
		this.zwischenzeiten = zwischenzeiten;
	}
}
