package system;

import java.util.List;

import javax.persistence.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import swa.runningeasy.dtos.AnmeldungDTO;
import swa.runningeasy.dtos.LaeuferDTO;
import swa.runningeasy.dtos.LaufzeitDTO;
import swa.runningeasy.dtos.ListeneintragDTO;
import swa.runningeasy.dtos.VeranstaltungDTO;
import swa.runningeasy.dtos.VereinDTO;
import swa.runningeasy.services.Auswertung;
import swa.runningeasy.services.RunningServices;

import java.util.*;

@Entity
public class Service implements RunningServices {
	@Transient
	private EntityManager entityManager;
	@Transient
	private static Logger log = LogManager.getRootLogger();
	@Id
	@GeneratedValue
	private int id; 
	
	@OneToMany
	public List<Laeufer> laeuferListe = new ArrayList<Laeufer>();
	@OneToMany
	public List<Verein> vereineListe = new ArrayList<Verein>();
	@OneToMany
	public List<Veranstaltung> veranstaltungenListe = new ArrayList<Veranstaltung>();

	public Service(){
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("sa.lauf");
		this.entityManager = factory.createEntityManager();
		this.init();
	}

	@Override
	public void erzeugeVeranstaltung(VeranstaltungDTO v) {
		Veranstaltung veranstaltung = new Veranstaltung(v.getName(),v.getDistanz(),v.getStartgebuehr(), v.getDatum(),v.getAnmeldeschluss());
		this.veranstaltungenListe.add(veranstaltung);
		this.persist(veranstaltung);
	}

	@Override
	public void erzeugeVerein(VereinDTO v) {
		log.debug("Verein "+v.getName()+" wird erzeugt");
		Verein verein = new Verein(v.getName());
		this.vereineListe.add(verein);
		this.persist(verein);
	}

	@Override
	public void erzeugeAnmeldung(AnmeldungDTO a) throws IllegalArgumentException {
		Laeufer l = this.searchLaeferByName(a.getLaeufer().getVorname(),a.getLaeufer().getName());
		Veranstaltung v = this.searchVeranstaltungByName(a.getVeranstaltung());
		Verein ver = null;
		if(a.getVerein() != null && !a.getVerein().isEmpty()){
			ver = this.searchVereinByName(a.getVerein());
		}
		Anmeldung anmeldung = new Anmeldung(v,l,ver);
		if(a.getStartnummer() > 0){
			anmeldung.setStartNummer(a.getStartnummer());
		}
		if(!v.contains(anmeldung) && !l.contains(anmeldung)){
			this.persist(anmeldung);
			v.anmeldungHinzufuegen(anmeldung);
			l.anmeldungHinzufuegen(anmeldung);
		}else{
			throw new IllegalArgumentException("Anmeldung bereits vorhanden");
		}
	}

	@Override
	public void erzeugeLaeufer(LaeuferDTO a) {
		log.debug("Laufer "+a.getName()+" wird erzeugt");
		Laeufer laeufer = new Laeufer(
				a.getName(),
				a.getVorname(),
				a.getGeburtsjahr(),
				a.getGeschlecht(),
				a.getEmail(),
				a.getSms(),
				a.getStrasse(),
				a.getPlz(),
				a.getOrt(),
				a.getLand(),
				null
			);
		this.laeuferListe.add(laeufer);
		this.persist(laeufer);
	}

	@Override
	public void erzeugeLaufzeit(LaufzeitDTO l) throws IllegalArgumentException {
		Veranstaltung veranstaltung = this.searchVeranstaltungByName(l.getVeranstaltung());
		Anmeldung anmeldung = veranstaltung.searchAnmeldungByStartNummer(l.getStartnummer());
		Laufzeit laufzeit= new Laufzeit(veranstaltung.getDistanz(),l.getLaufzeit());
		anmeldung.setLaufzeit(laufzeit);
		this.persist(laufzeit);
	}

	@Override
	public List<VeranstaltungDTO> getVeranstaltungen() {
		log.debug("GetVeranstaltungen aufegerufen");
		List<VeranstaltungDTO> result = new ArrayList<VeranstaltungDTO>();
		for(Veranstaltung v : this.veranstaltungenListe){
			result.add(v.generateDTO());
		}
		return result;
	}

	@Override
	public List<VereinDTO> getVereine() {
		log.debug("GetVereine aufegerufen");
		ArrayList<VereinDTO> tmp = new ArrayList<VereinDTO>();
		for(Verein v: this.vereineListe){
			tmp.add(v.generateDTO());
		}
		return tmp;
	}

	@Override
	public List<LaeuferDTO> getLaeufer() {
		log.debug("GetLaeufer aufegerufen");
		ArrayList<LaeuferDTO> tmp = new ArrayList<LaeuferDTO>();
		for(Laeufer l: this.laeuferListe){
			tmp.add(l.generateDTO());
		}
		return tmp;
	}

	@Override
	public List<AnmeldungDTO> getAnmeldungen(String Veranstaltung) {
		log.debug("GetAnmeldungen aufegerufen");
		ArrayList<AnmeldungDTO> tmp = new ArrayList<AnmeldungDTO>();
		Veranstaltung v = this.searchVeranstaltungByName(Veranstaltung);
		for(Anmeldung a: v.getAnmeldungen()){
			tmp.add(a.generateDTO());
		}
		return tmp;
	}

	@Override
	public List<LaufzeitDTO> getLaufzeiten(String Veranstaltung) {
		log.debug("GetAnmeldungen aufegerufen");
		ArrayList<LaufzeitDTO> tmp = new ArrayList<LaufzeitDTO>();
		Veranstaltung v = this.searchVeranstaltungByName(Veranstaltung);
		for(Anmeldung a: v.getAnmeldungen()){
			if(a.getLaufzeit() != null){
				tmp.add(a.getLaufzeit().generateDTO(a.getStartNummer(), a.getVeranstaltung().getName()));
			}
		}
		return tmp;
	}

	@Override
	public List<ListeneintragDTO> getAuswertung(Auswertung a, String Veranstaltung) {
		Veranstaltung v = this.searchVeranstaltungByName(Veranstaltung);
		List<ListeneintragDTO> result = null;
		switch(a){
		case ABBRECHER:
			result =
				this.generateList(v.getAbbrecherListe(), false);
			break;
		case GESAMTERGEBNISLISTE:
			result =
			this.generateList(v.getGesamtErgebnissListe(), true);
			break;
		case NICHTSTARTER:
			result =
			this.generateList(v.getNichtstarterListe(), false);
			break;
		case STARTLISTE:
			result =
			this.generateList(v.getStarterListe(), false);
			break;
		}
		return result;
	}
	
	private List<ListeneintragDTO> generateList(ArrayList<Anmeldung> anmeldungen, boolean addPlatz){
		List<ListeneintragDTO> result = new ArrayList<ListeneintragDTO>();
		for(int i = 0; i<anmeldungen.size(); i++){
			int platz = 0;
			if(addPlatz){
				platz = i+1;
			}
			result.add(this.anmeldungToListeneintragDTO(anmeldungen.get(i), platz));
		}
		return result;
	}
	
	private ListeneintragDTO anmeldungToListeneintragDTO(Anmeldung a, int platzierung){
		return new ListeneintragDTO(
				a.getLaeufer().getName(),
				a.getLaeufer().getVorname(),
				a.getLaeufer().getGeburtsjahr(),
				a.getLaeufer().getGeschlecht(),
				a.getVerein().getName(),
				a.getStartNummer(),
				platzierung,
				a.getLaufzeit() != null ? a.getLaufzeit().getLaufzeit() : null
				);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		laeuferListe = new ArrayList<Laeufer>();
		vereineListe = new ArrayList<Verein>();
		veranstaltungenListe = new ArrayList<Veranstaltung>();
		/*
		this.persist(this.laeuferListe);
		this.persist(vereineListe);
		this.persist(veranstaltungenListe);
		*/
	}
	
	private void persist(Object o){
		EntityTransaction tx = this.entityManager.getTransaction();
		tx.begin();
		this.entityManager.persist(o);
		tx.commit();
	}
	
	private Veranstaltung searchVeranstaltungByName(String name) 
			throws IllegalArgumentException{
		Veranstaltung result = null;
		for(Veranstaltung v : this.veranstaltungenListe){
			if(v.getName().equalsIgnoreCase(name)){
				result = v;
				break;
			}
		}
		if(result == null){
			throw new IllegalArgumentException("Veranstaltung "+name+" nicht gefunden.");
		}
		return result;
	}
	
	private Laeufer searchLaeferByName(String vorname, String nachname) 
			throws IllegalArgumentException{
		Laeufer result = null;
		for(Laeufer l : this.laeuferListe){
			if(l.getName().equalsIgnoreCase(nachname) 
					&& l.getVorname().equalsIgnoreCase(vorname)){
				result = l;
				break;
			}
		}
		if(result == null){
			throw new IllegalArgumentException("Laeufer "+vorname+" "+nachname+" nicht gefunden.");
		}
		return result;
	}
	
	private Verein searchVereinByName(String name) 
			throws IllegalArgumentException{
		Verein result = null;
		for(Verein v : this.vereineListe){
			if(v.getName().equalsIgnoreCase(name)){
				result = v;
				break;
			}
		}
		if(result == null){
			throw new IllegalArgumentException("Verein "+name+" nicht gefunden.");
		}
		return result;
		
	}

	//Getter und Setter
	public List<Laeufer> getLaeuferListe() {
		return laeuferListe;
	}

	public void setLaeuferListe(List<Laeufer> laeuferListe) {
		this.laeuferListe = laeuferListe;
	}
	
	public List<Verein> getVereineListe() {
		return vereineListe;
	}

	public void setVereineListe(List<Verein> vereineListe) {
		this.vereineListe = vereineListe;
	}
	
	public List<Veranstaltung> getVeranstaltungenListe() {
		return veranstaltungenListe;
	}

	public void setVeranstaltungenListe(List<Veranstaltung> veranstaltungenListe) {
		this.veranstaltungenListe = veranstaltungenListe;
	}
}
