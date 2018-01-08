package com.ue.ps;

public class Factory extends Building{
	
	private int resourceInput = 3;
	private int shipProgressOutput = 10;


	
	
	public Factory() {
		super("assets/factory.png");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void update(Player p){
	
		if (p.resource > resourceInput){
			p.resource -= resourceInput;
			//increment ship progress here;
		}
		
		
		
	}

}
