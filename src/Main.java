import java.util.Date;
import java.util.List;

import swa.runningeasy.dtos.AnmeldungDTO;
import swa.runningeasy.dtos.LaeuferDTO;
import swa.runningeasy.dtos.LaufzeitDTO;
import swa.runningeasy.dtos.VeranstaltungDTO;
import system.Laufzeit;
import system.Service;

public class Main {
	static LaeuferDTO laeuferEins = new LaeuferDTO("Schmidt","Hans",1980,'M',"hans.schmidt@mail.com","0123456789","Kapele 2","98755","Berlin","Deutschland");
	static VeranstaltungDTO veranstaltung = new VeranstaltungDTO("Freiburg Marathon",new Date() ,new Date() ,20,42.5f);
	static AnmeldungDTO anmeldnung = new AnmeldungDTO(laeuferEins,true,veranstaltung.getName(),"",1);
	static LaufzeitDTO laufzeit = new LaufzeitDTO(1, new Date(0,0,0,0,57),veranstaltung.getName());
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Service service = new Service(true);
		service.erzeugeLaeufer(laeuferEins);
		service.erzeugeVeranstaltung(veranstaltung);
		service.erzeugeAnmeldung(anmeldnung);
		service.erzeugeLaufzeit(laufzeit);
		
		
		//System.out.println("Test: "+service.getAnmeldungen(veranstaltung.getName()).get(0).toString());
		
		System.out.println("Anzahl Starter: "+service.getAnzahlStarter(veranstaltung.getName()));
		List<Laufzeit> test = service.ausgabeLaufzeit(veranstaltung.getName(), new Date(0,0,0,0,56) , new Date(0,0,0,0,58));
		
		for(Laufzeit l: test) {
			System.out.println("Laufzeit: "+l.generateDTO(0, veranstaltung.getName()).toString());
		}
		
		service.startNumberAdd();
		service.ta.commit();
	}

}
