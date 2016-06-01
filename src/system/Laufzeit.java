package system;

import javax.persistence.*;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import swa.runningeasy.dtos.LaufzeitDTO;

@NamedQueries(
@NamedQuery(name="ausgabeLaufzeit",
query="select l from Laufzeit l, Anmeldung a, Veranstaltung v where a.laufzeit = l and a.veranstaltung = v and v.name = :name and l.laufzeit > :laufzeitMin and l.laufzeit < :laufzeitMax"))
@Entity
public class Laufzeit {
	@Transient
	private static Logger log = LogManager.getRootLogger();
	@Id
	@GeneratedValue
	private int id;
	
	private float distanz;
	@Temporal(TemporalType.TIMESTAMP)
	private Date laufzeit;
	
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval=true)
	private List<Laufzeit> zwischenzeiten = new ArrayList<Laufzeit>();
	
	public Laufzeit(){
		
	}
	
	public Laufzeit(float distanz,Date laufzeit){
		log.debug("Laufzeit mit der Distanz "+distanz+" Datum "+laufzeit);
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