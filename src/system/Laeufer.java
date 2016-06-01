<<<<<<< HEAD
package system;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.apache.logging.log4j.*;

import swa.runningeasy.dtos.LaeuferDTO;

@Entity
@NamedQueries({
	 @NamedQuery(name="findAllLaeufer", query="select c from Laeufer c"),
	 @NamedQuery(name="deleteLaeufer", query="DELETE from Laeufer "),
	 @NamedQuery(name="findByNameLaeufer",
	 query="select c from Laeufer c where c.name=:name and c.vorname=:vorname")
	})
public class Laeufer {
	@Transient
	private static Logger log = LogManager.getRootLogger();
	@Id
	@GeneratedValue
	private int id;
	
	private String name;
	private String vorname;
	private int geburtsjahr;
	private char geschlecht;
	private String email;
	private String telefonnummer;
	private String strasse;
	private String plz;
	private String ort;
	private String land;
	
	
	private Verein vereinszugehoerigkeit;
	
	@OneToMany
	private List<Anmeldung> anmeldungen = new ArrayList<Anmeldung>();
	
	public Laeufer(){
		
	}
	
	public Laeufer(String name, String vorname, int geburtsjahr, char geschlecht, String email, String telefonnummer,
			String strasse, String plz, String ort, String land, Verein vereinszugehoerigkeit) {
		this.name = name;
		this.vorname = vorname;
		this.geburtsjahr = geburtsjahr;
		this.geschlecht = geschlecht;
		this.email = email;
		this.telefonnummer = telefonnummer;
		this.strasse = strasse;
		this.plz = plz;
		this.ort = ort;
		this.land = land;
		this.vereinszugehoerigkeit = vereinszugehoerigkeit;
	}
	
	
	public LaeuferDTO generateDTO() {
		return new LaeuferDTO(this.getName(), this.getVorname(), this.getGeburtsjahr(), this.getGeschlecht(),
				this.getEmail(), this.getTelefonnummer(), this.getStrasse(), this.getPlz(), this.getOrt(),
				this.getLand());
	}
	
	public void anmeldungHinzufuegen(Anmeldung a){
		if(!this.contains(a)){
			this.anmeldungen.add(a);
		}
	}
	
	public boolean contains(Anmeldung a){
		boolean result = false;
		for(Anmeldung anm : this.anmeldungen){
			if(anm.getVeranstaltung() == a.getVeranstaltung()){
				result = true;
				break;
			}
		}
		return result;
	}
	
	//Getter
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getVorname() {
		return vorname;
	}
	public int getGeburtsjahr() {
		return geburtsjahr;
	}
	public char getGeschlecht() {
		return geschlecht;
	}
	public String getEmail() {
		return email;
	}
	public String getTelefonnummer() {
		return telefonnummer;
	}
	public String getStrasse() {
		return strasse;
	}
	public String getPlz() {
		return plz;
	}
	public String getOrt() {
		return ort;
	}
	public String getLand() {
		return land;
	}
	public Verein getVereinszugehoerigkeit() {
		return vereinszugehoerigkeit;
	}
	
	public void setVereinszugehoerigkeit(Verein vereinszugehoerigkeit) {
		if(this.vereinszugehoerigkeit == null || vereinszugehoerigkeit == null){
			this.vereinszugehoerigkeit = vereinszugehoerigkeit;
		}
	}

	
	public List<Anmeldung> getAnmeldungen() {
		return anmeldungen;
	}

	public void setAnmeldungen(List<Anmeldung> anmeldungen) {
		this.anmeldungen = anmeldungen;
	}

}
=======
package system;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.apache.logging.log4j.*;

import swa.runningeasy.dtos.LaeuferDTO;

@Entity
public class Laeufer {
	@Transient
	private static Logger log = LogManager.getRootLogger();
	@Id
	@GeneratedValue
	private int id;
	
	private String name;
	private String vorname;
	private int geburtsjahr;
	private char geschlecht;
	private String email;
	private String telefonnummer;
	private String strasse;
	private String plz;
	private String ort;
	private String land;
	
	@ManyToOne
	private Verein vereinszugehoerigkeit;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Anmeldung> anmeldungen = new ArrayList<Anmeldung>();
	
	public Laeufer(){
		
	}
	
	public Laeufer(String name, String vorname, int geburtsjahr, char geschlecht, String email, String telefonnummer,
			String strasse, String plz, String ort, String land, Verein vereinszugehoerigkeit) {
		this.name = name;
		this.vorname = vorname;
		this.geburtsjahr = geburtsjahr;
		this.geschlecht = geschlecht;
		this.email = email;
		this.telefonnummer = telefonnummer;
		this.strasse = strasse;
		this.plz = plz;
		this.ort = ort;
		this.land = land;
		this.vereinszugehoerigkeit = vereinszugehoerigkeit;
	}
	
	
	public LaeuferDTO generateDTO() {
		return new LaeuferDTO(this.getName(), this.getVorname(), this.getGeburtsjahr(), this.getGeschlecht(),
				this.getEmail(), this.getTelefonnummer(), this.getStrasse(), this.getPlz(), this.getOrt(),
				this.getLand());
	}
	
	public void anmeldungHinzufuegen(Anmeldung a){
		if(!this.contains(a)){
			this.anmeldungen.add(a);
		}
	}
	
	public boolean contains(Anmeldung a){
		boolean result = false;
		for(Anmeldung anm : this.anmeldungen){
			if(anm.getVeranstaltung() == a.getVeranstaltung()){
				result = true;
				break;
			}
		}
		return result;
	}
	
	//Getter
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getVorname() {
		return vorname;
	}
	public int getGeburtsjahr() {
		return geburtsjahr;
	}
	public char getGeschlecht() {
		return geschlecht;
	}
	public String getEmail() {
		return email;
	}
	public String getTelefonnummer() {
		return telefonnummer;
	}
	public String getStrasse() {
		return strasse;
	}
	public String getPlz() {
		return plz;
	}
	public String getOrt() {
		return ort;
	}
	public String getLand() {
		return land;
	}
	public Verein getVereinszugehoerigkeit() {
		return vereinszugehoerigkeit;
	}
	
	public void setVereinszugehoerigkeit(Verein vereinszugehoerigkeit) {
		if(this.vereinszugehoerigkeit == null || vereinszugehoerigkeit == null){
			this.vereinszugehoerigkeit = vereinszugehoerigkeit;
		}
	}

	
	public List<Anmeldung> getAnmeldungen() {
		return anmeldungen;
	}

	public void setAnmeldungen(List<Anmeldung> anmeldungen) {
		this.anmeldungen = anmeldungen;
	}

}
>>>>>>> refs/remotes/origin/master
