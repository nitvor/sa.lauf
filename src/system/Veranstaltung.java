package system;

import java.util.*;

import javax.persistence.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import swa.runningeasy.dtos.VeranstaltungDTO;

@Entity
@NamedQueries({
	 @NamedQuery(name="findAllVeranstaltung", query="select c from Veranstaltung c"),
	 @NamedQuery(name="findByNameVeranstaltung",
	 query="select c from Veranstaltung c where c.name=:name"),
	 @NamedQuery(name="getAnzahlGemeldeteStarter",
	 query="select count(a) from Veranstaltung v, Anmeldung a where v = a.veranstaltung and v.name = :name")
	})

public class Veranstaltung {
	@Transient
	private static Logger log = LogManager.getRootLogger();
	@Id
	@GeneratedValue
	private int id;
	
	public int getId() {
		return id;
	}
	/*
	 * Name der Veranstaltung
	 */
	private String name;	
	private float distanz;
	/*
	 * Termin von der Veranstaltung
	 */
	@Temporal(TemporalType.DATE)
	private Date termin;
	/*
	 * Anmeldeschluss zur Veranstaltung
	 */
	@Temporal(TemporalType.DATE)
	private Date anmeldeschluss;
	/*
	 * Startgebuehr fuer die Veranstaltung
	 */
	private int startgebuehr;
	/*
	 * Zahlungsinformation fuer die Startgebuehr
	 */
	private String zahlungsinformationen;
	
	@OneToMany
	private List<Anmeldung> anmeldungen = new ArrayList<Anmeldung>();
	
	public Veranstaltung(){
		
	}
	
	public Veranstaltung(String name, float distanz,int startGebuer, Date termin, Date anmeldeschluss) {
		log.debug("Veranstaltung "+name);
		this.name = name;
		this.distanz = distanz;
		this.termin = termin;
		this.anmeldeschluss = anmeldeschluss;
		this.startgebuehr = startGebuer;
	}
	
	public void anmeldungHinzufuegen(Anmeldung a){
		if(!this.contains(a)){
			this.anmeldungen.add(a);
		}
	}
	
	public boolean contains(Anmeldung a){
		boolean result = false;
		for(Anmeldung anm : this.anmeldungen){
			if(anm.getLaeufer() == a.getLaeufer()){
				result = true;
				break;
			}
		}
		return result;
	}
	
	public Anmeldung searchAnmeldungByStartNummer(int nummer) throws IllegalArgumentException{
		Anmeldung result = null;
		for(Anmeldung a: this.anmeldungen){
			if(a.getStartNummer() == nummer){
				result = a;
				break;
			}
		}
		if(result == null){
			throw new IllegalArgumentException(
					"Laeufer mit Nummer "+nummer+" in der Veranstaltung "
							+this.name+" wurde nicht gefunden.");
		}
		return result;
	}
	
	public VeranstaltungDTO generateDTO(){
		VeranstaltungDTO res = new VeranstaltungDTO(
				this.name,
				this.termin,
				this.anmeldeschluss,
				this.startgebuehr,
				this.distanz
				);
		return res;
	}
	
	public ArrayList<Anmeldung> getAbbrecherListe(){
		ArrayList<Anmeldung> result = new ArrayList<Anmeldung>();
		for(Anmeldung a: this.anmeldungen){
			if(a.getStatus() == AnmeldungStatus.BEZAHLT
					&& a.getLaufzeit() == null){
				result.add(a);
			}
		}
		return result;
	}
	
	public ArrayList<Anmeldung> getGesamtErgebnissListe(){
		//TODO
		ArrayList<Anmeldung> result = new ArrayList<Anmeldung>();
		for(Anmeldung a: this.anmeldungen){
			if(a.getStatus() == AnmeldungStatus.BEENDET){
				result.add(a);
			}
		}
		Collections.sort(result, new Comparator<Anmeldung>() {
		    @Override
		    public int compare(Anmeldung o1, Anmeldung o2) {
		        return o1.getLaufzeit().getLaufzeit().compareTo(
		        		o2.getLaufzeit().getLaufzeit());
		    }
		});
		return result;
	}
	
	public ArrayList<Anmeldung> getNichtstarterListe(){
		ArrayList<Anmeldung> result = new ArrayList<Anmeldung>();
		for(Anmeldung a: this.anmeldungen){
			if(a.getStatus() == AnmeldungStatus.NEU){
				result.add(a);
			}
		}
		return result;
	}
	
	public ArrayList<Anmeldung> getStarterListe(){
		ArrayList<Anmeldung> result = new ArrayList<Anmeldung>();
		for(Anmeldung a: this.anmeldungen){
			if(a.getStatus() != AnmeldungStatus.NEU){
				result.add(a);
			}
		}
		return result;
	}
	
	// Getter und Setter
	public String getName() {
		return name;
	}
	public float getDistanz() {
		return distanz;
	}
	public Date getTermin() {
		return termin;
	}
	public Date getAnmeldeschluss() {
		return anmeldeschluss;
	}
	public int getStartgebuehr() {
		return startgebuehr;
	}
	public String getZahlungsinformationen() {
		return zahlungsinformationen;
	}
	public List<Anmeldung> getAnmeldungen() {
		return anmeldungen;
	}
	public void setAnmeldungen(List<Anmeldung> anmeldungen) {
		this.anmeldungen = anmeldungen;
	}
}