package system;

import javax.persistence.*;

import org.apache.logging.log4j.*;

import swa.runningeasy.dtos.VereinDTO;

import java.util.*;


@Entity
@NamedQueries({
	 @NamedQuery(name="findAllVerein", query="select c from Verein c"),
	 @NamedQuery(name="deleteVerein", query="DELETE  from Laeufer "),
	 @NamedQuery(name="findByNameVerein",
	 query="select c from Verein c where c.name=:name")
	})
public class Verein {
	@Transient
	private static Logger log = LogManager.getRootLogger();
	@Id
	@GeneratedValue
	private int id;
	
	private String name;
	@OneToMany
	private List<Laeufer> mitglider = new ArrayList<Laeufer>();
	

	public int getId() {
		return id;
	}

	public Verein(){
		
	}
	
	public Verein(String name){
		log.debug("Verein "+name+" erzeugt.");
		this.name = name;
	}
	
	public void mitgliedHinzufuegen(Laeufer laeufer){
		laeufer.setVereinszugehoerigkeit(this);
		if(laeufer.getVereinszugehoerigkeit() == this){
			log.debug("Laeufer "+laeufer.getName()+" wurde dem Verein "+this.getName()+" hinzugefuegt.");
			this.mitglider.add(laeufer);
		}
	}
	
	public void mitgliedEntfernen(Laeufer laeufer){
		int index = this.mitglider.indexOf(laeufer);
		if(index != -1){
			log.debug("Laeufer "+laeufer.getName()+" wurde aus dem Verein "+this.getName()+" entfernt");
			this.mitglider.remove(index);
		}else{
			log.error("Laeufer "+laeufer.getName()+" ist nicht im Verein "+this.getName());
		}
	}
	
	public VereinDTO generateDTO() {
		return new VereinDTO(this.name);
	}
	
	//Getter
	public String getName() {
		return name;
	}
	
	public List<Laeufer> getMitglider() {
		return mitglider;
	}

	public void setMitglider(List<Laeufer> mitglider) {
		this.mitglider = mitglider;
	}
}