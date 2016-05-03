package system;

import javax.persistence.*;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import swa.runningeasy.dtos.AnmeldungDTO;

@Entity
public class Anmeldung {
	@Transient
	private static Logger log = LogManager.getRootLogger();
	@Id
	@GeneratedValue
	private int id;
	
	private AnmeldungStatus status = AnmeldungStatus.NEU;
	
	private int startNummer = 0;
	@OneToOne
	private Laufzeit laufzeit = null;

	private Laeufer laeufer;

	private Veranstaltung veranstaltung;
	
	private Verein verein = null;

	public Anmeldung(){
		
	}
	
	public Anmeldung(Veranstaltung veranstaltung,Laeufer laeufer,Verein verein){
		this.veranstaltung = veranstaltung;
		this.laeufer = laeufer;
		this.verein = verein;
	}
	
	public Anmeldung(Veranstaltung veranstaltung,Laeufer laeufer,Verein verein,int startNummer){
		this(veranstaltung,laeufer,verein);
		this.setStartNummer(startNummer);
	}
	
	public Anmeldung(Veranstaltung veranstaltung,Laeufer laeufer,Verein verein,int startNummer,Laufzeit laufzeit){
		this(veranstaltung,laeufer,verein,startNummer);
		this.setLaufzeit(laufzeit);
	}
	
	public AnmeldungDTO generateDTO(){
		AnmeldungDTO result = new AnmeldungDTO(
				this.laeufer.generateDTO(),
				this.status == AnmeldungStatus.BEZAHLT || this.status == AnmeldungStatus.BEENDET ? true : false,
				this.veranstaltung.getName(),
				this.verein != null ? this.verein.getName() : "",
				this.startNummer
				); 
		return result;
	}
	
	// Getter und Setter
	public int getStartNummer() {
		return startNummer;
	}
	
	public void setStartNummer(int startNummer) {
		if(this.status == AnmeldungStatus.NEU){
			this.status = AnmeldungStatus.BEZAHLT;
		}
		this.startNummer = startNummer;
	}
	
	public Laufzeit getLaufzeit() {
		return laufzeit;
	}
	public void setLaufzeit(Laufzeit laufzeit) {
		this.status = AnmeldungStatus.BEENDET;
		this.laufzeit = laufzeit;
	}
	public Laeufer getLaeufer() {
		return laeufer;
	}
	public void setLaeufer(Laeufer laeufer) {
		this.laeufer = laeufer;
	}
	public Veranstaltung getVeranstaltung() {
		return veranstaltung;
	}
	public void setVeranstaltung(Veranstaltung veranstaltung) {
		this.veranstaltung = veranstaltung;
	}
	public AnmeldungStatus getStatus() {
		return status;
	}
	
	public Verein getVerein() {
		return verein;
	}

	public void setVerein(Verein verein) {
		this.verein = verein;
	}
}
