package com.ue.ps.ui;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.ue.ps.BaseActor;
import com.ue.ps.PS;
import com.ue.ps.Planet;
import com.ue.ps.ships.Ship;
import com.ue.ps.ships.ShipPointer;
import com.ue.ps.systems.GameServerClient;

public class SidePanel extends BaseActor {

	private Planet dispPlanet = new Planet();
	private Planet planet = new Planet();
	private Label planetName = new Label("", PS.font);
	private Label planetType = new Label("", PS.font);

	public Tab tabBuildings = new Tab(65, 637);
	public Tab tabShips = new Tab(65+100, 637);
	
	private Label planetCap = new Label("5", PS.font);
	private Label planetPrioirity = new Label("9", PS.font);

	private Button incrementCapButton = new Button(Images.Increment);
	private Button incrementPriorityButton = new Button(Images.Increment);
	private Button deincrementCapButton = new Button(Images.Deincrement);
	private Button deincrementPriorityButton = new Button(Images.Deincrement);

	private BaseActor capContainer = new BaseActor("assets/numContainer.png");
	private BaseActor priorityContainer = new BaseActor("assets/numContainer.png");

	private BaseActor prioritySymbol = new BaseActor("assets/priority.png");
	private BaseActor capacitySymbol = new BaseActor("assets/capacity.png");

	public static BaseActor uiMouseBlot = new BaseActor("assets/stageMouseBlot.png");
	private Vector2 localMousePos = new Vector2();

	private Vector2 copiedMousePos = new Vector2();

	private ArrayList<BuildingContainer> buildingContainers = new ArrayList<BuildingContainer>(); // the
																									// blank
																									// buildings
	private ArrayList<Label> buildingCost = new ArrayList<Label>();

	public static ArrayList<ShipContainer> shipContainers = new ArrayList<ShipContainer>();

	public static ArrayList<Ship> selectedShips = new ArrayList<Ship>();

	private boolean buildBoxesShowing; // TODO change to tab showing
	private boolean shipBuildBoxesShowing;
	private int selectedBuildingSlot = -1;
	
	private static Tab activeTab;

	private Label planetResource = new Label("", PS.font);

	public SidePanel() {
		super("assets/sidePanel.png");
		planetName.setPosition(98, this.getHeight() - 47);
		planetName.setFontScale(2);
		this.addActor(planetName);

		planetType.setPosition(70, this.getHeight() - 75);
		this.addActor(planetType);

		planetResource.setPosition(70 + 21 + 21 + 21 + 21, this.getHeight() - 75 - 16 - 10);
		this.addActor(planetResource);

		capacitySymbol.setPosition(70, this.getHeight() - 75 - 16 - 10);
		this.addActor(capacitySymbol);

		prioritySymbol.setPosition(70, this.getHeight() - 75 - 16 - 10 - 16 - 10);
		this.addActor(prioritySymbol);

		capContainer.setPosition(70 + 21, this.getHeight() - 75 - 16 - 10);
		this.addActor(capContainer);
		planetCap.setPosition(74 + 21, this.getHeight() - 75 - 16 - 11);
		this.addActor(planetCap);
		incrementCapButton.setPosition(70 + 21 + 21, this.getHeight() - 75 - 16 - 10);
		this.addActor(incrementCapButton);
		deincrementCapButton.setPosition(70 + 21 + 21 + 21, this.getHeight() - 75 - 16 - 10);
		this.addActor(deincrementCapButton);

		priorityContainer.setPosition(70 + 21, this.getHeight() - 75 - 16 - 10 - 16 - 10);
		this.addActor(priorityContainer);
		planetPrioirity.setPosition(74 + 21, this.getHeight() - 75 - 16 - 10 - 16 - 11);
		this.addActor(planetPrioirity);
		incrementPriorityButton.setPosition(70 + 21 + 21, this.getHeight() - 75 - 16 - 10 - 16 - 10);
		this.addActor(incrementPriorityButton);
		deincrementPriorityButton.setPosition(70 + 21 + 21 + 21, this.getHeight() - 75 - 16 - 10 - 16 - 10);
		this.addActor(deincrementPriorityButton);

		this.addActor(tabBuildings);
		this.addActor(tabShips);
		tabShips.setName("tab Ships");
		tabBuildings.setName("tab buildings");
		
		this.addActor(uiMouseBlot);

	}

	/**
	 * Sets the side panel to display a specific planet's stats.
	 * 
	 * @param p the planet to be displayed
	 */

	public void setPlanet(Planet p) {
		// remove old planet
		this.planet = p;
		this.removeActor(this.dispPlanet);
		// get displayable planet copy
		this.dispPlanet = p.copy();
		this.addActor(dispPlanet);
		dispPlanet.setSize(dispPlanet.getXbyY().x / 5, dispPlanet.getXbyY().y / 5);
		dispPlanet.setCenter(32, this.getHeight() - 98);

		// update text fields
		planetName.setText(this.planet.name);

		planetType.setText(this.planet.getPlanetType().name);
		// remove old building boxes
		for (BuildingContainer bbox : this.buildingContainers) {
			this.removeActor(bbox);
		}

		for (BaseActor sbox : shipContainers) {
			this.removeActor(sbox);
		}

		this.buildingContainers.clear();
		this.buildingCost.clear();
		shipContainers.clear();
		tabBuildings.reset();
		tabShips.reset();

		//Ship displaying (move)
		int localrand = 0;
		
		int f = 0;
		for (int i = 0; i < this.planet.getAllOrbitingShips().size(); i++) {
			
		
				ShipContainer sbox = new ShipContainer(this.planet.getAllOrbitingShips().get(i));
				sbox.setShip(this.planet.getAllOrbitingShips().get(i));
				//sbox.setPosition(100, PS.viewHeight - 150 - i * 20);
				sbox.setPosition(10, Tab.maxHeight - (f) * 50); //TODO change? YES
				shipContainers.add(sbox);
				tabShips.addActor(sbox);
			if (this.planet.getAllOrbitingShips().get(i).getOwnerName().equals(GameServerClient.user)) {
				for (ShipPointer pointer : this.planet.pointers) { // makes the
																	// container
																	// show where
																	// the ship is
																	// going
					
					for (Ship s : pointer.ships) {
						System.out.println(s);
						if (s != null && getShipContainer(s) != null) {
							getShipContainer(s).setDestinationInfo(pointer.destination);
						} else {
							System.out.println("Missing ship container!");
						}

					}
				}
				localrand = i;
				f++;
			}
		}

		for (ShipContainer s : this.planet.BuildQueue) { // add ships being
															// built
			localrand++;
			s.setPosition(10, Tab.maxHeight - 10 - localrand * 20);
			shipContainers.add(s);
			tabShips.addActor(s);
			tabShips.internalHeight += 20; //add the height of this object (and buffer beneath) to the net height of the tab
		}

		// add final "next build" box
		//ShipContainer sbox = new ShipContainer();
		//shipContainers.add(sbox);
		//tabShips.addActor(sbox); 

		//BUILDINGS TAB
		// setup building boxes
		for (int i = 0; i < this.planet.landBuildings.length; i++) {
		
				BuildingContainer bc = new BuildingContainer(i, 10, Tab.maxHeight - (i) * 50, p, false); //from bottom left
				//BuildingContainer bc = new BuildingContainer(i, 10, -10 - (i+1) * 50); //from top left
				this.buildingContainers.add(bc);
				tabBuildings.addActor(bc);
				tabBuildings.internalHeight += 50; //add the height of this object (and buffer beneath) to the net height of the tab
	
				if (this.planet.landBuildings[i] != null) {
					bc.setBuilding(this.planet.landBuildings[i]);
				} else {
					bc.setBuilding(null);
				}
		}
		
		for (int i = 0; i < this.planet.spaceBuildings.length; i++) {
			
		
				BuildingContainer bc = new BuildingContainer(i, 10, Tab.maxHeight - (i + this.planet.landBuildings.length) * 50, p, true); //from bottom left
				//BuildingContainer bc = new BuildingContainer(i, 10, -10 - (i+1) * 50); //from top left
				this.buildingContainers.add(bc);
				tabBuildings.addActor(bc);
				tabBuildings.internalHeight += 50; //add the height of this object (and buffer beneath) to the net height of the tab
	
				if (this.planet.spaceBuildings[i] != null) {
					bc.setBuilding(this.planet.spaceBuildings[i]);
				} else {
					bc.setBuilding(null);
				}
				
		}
		
	
		
		//UI must be final thing here
		setActiveTab(tabBuildings);
	}

	/**
	 * updates the sidepanel
	 * 
	 * @param uiStage the uiStage
	 */
	public void update(Stage uiStage) {
		// update local mouse position
		copiedMousePos.x = GameplayScreen.mouseBlot.getX();
		copiedMousePos.y = PS.viewHeight - GameplayScreen.mouseBlot.getY();

		localMousePos = this.stageToLocalCoordinates(uiStage.screenToStageCoordinates(copiedMousePos));
		
		uiMouseBlot.setPosition(localMousePos.x, localMousePos.y);
		//System.out.println(localMousePos);

		this.planetResource.setText("Resource: " + this.planet.resource); // TODO
																			// only
		tabBuildings.update(uiMouseBlot.center);			
		tabShips.update(uiMouseBlot.center);// update
																			// when
		//this works even though it shouldn't																	// resource
		if (activeTab != null && activeTab.selected) {														// updates
			activeTab.updateChildren(uiMouseBlot.center);
		}
		
	

		// check for clicking on increment/deincrement priority/capacity and
		// increment/deincrement them
		
		//TODO put in an if for the tab they will be in
		/*if (Gdx.input.justTouched()) {
			Rectangle mouse = uiMouseBlot.getBoundingRectangle(); // make this
																	// global
																	// and the
																	// buttons
																	// will be
																	// easier

			if (this.incrementCapButton.Pressed(mouse)) {
				this.planet.resourceCapacity += 1;
			} else if (this.incrementPriorityButton.Pressed(mouse)) {
				this.planet.priority += 1;
			} else if (this.deincrementCapButton.Pressed(mouse)) {
				this.planet.resourceCapacity -= 1;
			} else if (this.deincrementPriorityButton.Pressed(mouse)) {
				this.planet.priority -= 1;
			}

			// check for destroying building
			 * if (this.destroyBuildingBox.getBoundingRectangle().contains(
			 * uiMouseBlot.center) && Gdx.input.justTouched()) { //destroy
			 * building this.planet.destroyBuilding(selectedBuildingSlot);
			 * //update building boxes
			 * buildingContainers.get(selectedBuildingSlot).setBuilding(null);
			 * hideDestroy(); }
			 
		}*/

		
		for (int i = 0; i < shipContainers.size(); i++) {
			//update Ship containers
		
			if (shipContainers.get(i).done) {
				if (shipContainers.get(i).isSelected()) {
					//add ships to selectedShips, unless the ship is already in selectedShips
					if (!selectedShips.contains(shipContainers.get(i).getShip())) {
						selectedShips.add(shipContainers.get(i).getShip());
						System.out.println("adding SHIP");
					}

				} else {
					selectedShips.remove(shipContainers.get(i).getShip());
					
					
					
				}
				//remove old pointers
				ArrayList<ShipPointer> deleteThisPointer = new ArrayList<ShipPointer>();
				//if ship doesn't have a destination
				if (!shipContainers.get(i).isDestinationSet) {
					for (ShipPointer sp : this.planet.pointers) {
						//remove it from it's pointer
						sp.ships.remove(shipContainers.get(i).getShip());
						
						//if the pointer is empty, flag the pointer
						if (sp.ships.isEmpty()) {
							sp.delete();
							deleteThisPointer.add(sp);
							System.out.println(sp);
						}
					}
				}
				//deleting flagged pointers
				for (ShipPointer sp : deleteThisPointer) {
					this.planet.pointers.remove(sp);
				}
				//destroy dead ships
				if (shipContainers.get(i).getShip() != null && shipContainers.get(i).getShip().health <= 0) {
					shipContainers.get(i).remove();
					shipContainers.remove(i);
					//i--;
					
				}
			}
			
		}
		
			
		// update text fields
		this.planetCap.setText(Integer.toString(this.planet.resourceCapacity));
		this.planetPrioirity.setText(Integer.toString(this.planet.priority));
	}

	private ShipContainer getShipContainer(Ship s) {
		for (ShipContainer sc : shipContainers) {
			if (sc.getShip() == s) {
				return sc;
			}
		}
		return null;
	}

	public void onDestinationSet(Planet destination) {
		for (Ship s : selectedShips) {
			getShipContainer(s).setDestinationInfo(destination);
		}
	}

	public void unset() {
		// remove old planet
		this.setPlanet(this.planet);

	}
	
	public static void setActiveTab(Tab t) {
		activeTab = t;
		t.BringUp();
	}
	
	public Tab getActiveTab() {
		return this.activeTab;
	}
	
	public Planet getSelectedPlanet() {
		return this.planet;
	}
}
