package swa.runningeasy.services.impl;

import system.Service;
import swa.runningeasy.services.RunningServices;

public class RunningServicesFactory {
	private static Service service;
	public static RunningServices getInstance(){
		if(service == null){
			service = new Service(true);
		}
		return service;
	}
}
