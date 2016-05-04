package system;

import java.util.List;

import javax.persistence.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
	private EntityManager entityManager2;
	@Transient
	private EntityTransaction ta;
	@Transient
	private static Logger log = LogManager.getRootLogger();
	
	@Id
	@GeneratedValue
	private int id; 
	
	@OneToMany(cascade = CascadeType.REMOVE)
	public List<Laeufer> laeuferListe = new ArrayList<Laeufer>();
	@OneToMany(cascade = CascadeType.REMOVE)
	public List<Verein> vereineListe = new ArrayList<Verein>();
	@OneToMany(cascade = CascadeType.REMOVE)
	public List<Veranstaltung> veranstaltungenListe = new ArrayList<Veranstaltung>();

	public Service(){

	}
	
	public Service(boolean neu) {
		log.debug("Erzeuge EntityManagerFactory.");
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("sa.lauf");
		this.entityManager = factory.createEntityManager();
		this.init();
		this.ta = this.entityManager.getTransaction();
		this.ta.begin();
		this.persist(this);
	}

	@Override
	public void erzeugeVeranstaltung(VeranstaltungDTO v) {
		log.debug("Veranstaltung "+v.getName()+" wird erzeugt.");
		Veranstaltung veranstaltung = new Veranstaltung(v.getName(),v.getDistanz(),v.getStartgebuehr(), v.getDatum(),v.getAnmeldeschluss());
		this.veranstaltungenListe.add(veranstaltung);
		this.persist(veranstaltung);
	}

	@Override
	public void erzeugeVerein(VereinDTO v) {
		log.debug("Verein "+v.getName()+" wird erzeugt.");
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
		log.debug("Laeufer "+a.getName()+" wird erzeugt.");
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
		this.persist(laeufer);
		this.laeuferListe.add(laeufer);
		
	}

	@Override
	public void erzeugeLaufzeit(LaufzeitDTO l) throws IllegalArgumentException {
		log.debug("Laufzeit "+l.getLaufzeit()+"wird erzeugt.");
		Veranstaltung veranstaltung = this.searchVeranstaltungByName(l.getVeranstaltung());
		Anmeldung anmeldung = veranstaltung.searchAnmeldungByStartNummer(l.getStartnummer());
		Laufzeit laufzeit= new Laufzeit(veranstaltung.getDistanz(),l.getLaufzeit());
		anmeldung.setLaufzeit(laufzeit);
		this.persist(laufzeit);
	}

	@Override
	public List<VeranstaltungDTO> getVeranstaltungen() {
		log.debug("GetVeranstaltungen aufgerufen.");
		List<VeranstaltungDTO> result = new ArrayList<VeranstaltungDTO>();
		Query query = entityManager.createQuery("SELECT v FROM Veranstaltung v");
		List<?> veranstaltungList = query.getResultList();
		for(Object v : veranstaltungList){
			Veranstaltung veranstaltung = (Veranstaltung) v;
			result.add(veranstaltung.generateDTO());
		}
		return result;
	}

	@Override
	public List<VereinDTO> getVereine() {
		log.debug("GetVereine aufgerufen.");
		ArrayList<VereinDTO> tmp = new ArrayList<VereinDTO>();
		Query query = entityManager.createQuery("Select v FROM Verein v");
		List<?> vereinList = query.getResultList();
		for(Object v: vereinList){
			Verein verein = (Verein) v;
			tmp.add(verein.generateDTO());
		}
		return tmp;
	}

	@Override
	public List<LaeuferDTO> getLaeufer() {
		log.debug("GetLaeufer aufgerufen.");
		ArrayList<LaeuferDTO> tmp = new ArrayList<LaeuferDTO>();
		Query query = entityManager.createQuery("SELECT l FROM Laeufer l");
		List<?> laeuferListe = query.getResultList();
		for(Object l: laeuferListe){
			Laeufer laeufer = (Laeufer) l;
			tmp.add(laeufer.generateDTO());
		}
		return tmp;
	}

	@Override
	public List<AnmeldungDTO> getAnmeldungen(String Veranstaltung) {
		log.debug("GetAnmeldungen aufgerufen.");
		ArrayList<AnmeldungDTO> tmp = new ArrayList<AnmeldungDTO>();
		Veranstaltung v = this.searchVeranstaltungByName(Veranstaltung);
		for(Anmeldung a: v.getAnmeldungen()){
			tmp.add(a.generateDTO());
		}
		return tmp;
	}

	@Override
	public List<LaufzeitDTO> getLaufzeiten(String Veranstaltung) {
		log.debug("GetAnmeldungen aufgerufen.");
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
		log.debug("GetAuswertung aufgerufen.");
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

        if(this.ta != null && this.ta.isActive()){
        	this.ta.rollback();
        	this.ta.begin();
        }
		
		log.debug("Erzeuge LaeuferListe.");

		laeuferListe = new ArrayList<Laeufer>();
		log.debug("Erzeuge VereineListe.");

		vereineListe = new ArrayList<Verein>();
		log.debug("Erzeuge VeranstaltungenListe.");

		veranstaltungenListe = new ArrayList<Veranstaltung>();
		
		
		
	}
	

	
	private void persist(Object o){
		//EntityTransaction tx = this.entityManager.getTransaction();
		//tx.begin();
		this.entityManager.persist(o);
		//tx.commit();
		this.entityManager.flush();
	}
	
	private Veranstaltung searchVeranstaltungByName(String name) 
			throws IllegalArgumentException{
		Veranstaltung result = null;
		
		try{
		Query q  = this.entityManager.createNamedQuery("findByNameVeranstaltung");
		q.setParameter("name", name);
		result = (Veranstaltung)q.getResultList().get(0);
		}catch(Exception e){}
		/*
		Query query = entityManager.createQuery("SELECT v FROM Veranstaltung v");
		List<?> veranstaltungenListe = query.getResultList();*/
		for(Object v : veranstaltungenListe){
			if(((Veranstaltung)v).getName().equalsIgnoreCase(name)){
				result = (Veranstaltung)v;
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
		
		try{
		Query q  = this.entityManager.createNamedQuery("findByNameLaeufer");
		q.setParameter("name", nachname);
		q.setParameter("vorname", vorname);
		result = (Laeufer)q.getResultList().get(0);
		}catch(Exception e){}
		/*
		Query query = entityManager.createQuery("SELECT l FROM Laeufer l");
		List<?> laeuferListe = query.getResultList();*/
		for(Object l : laeuferListe){
			if(((Laeufer)l).getName().equalsIgnoreCase(nachname) 
					&& ((Laeufer)l).getVorname().equalsIgnoreCase(vorname)){
				result = (Laeufer)l;
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
		
		try{
		Query q  = this.entityManager.createNamedQuery("findByNameVerein");
		q.setParameter("name", name);
		result = (Verein)q.getResultList().get(0);
		}catch(Exception e){}
		/*
		Query query = entityManager.createQuery("SELECT v FROM Verein v");
		List<?> vereinsListe = query.getResultList();*/
		for(Object v : this.vereineListe){
			if(((Verein)v).getName().equals(name)){
				result = (Verein)v;
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
