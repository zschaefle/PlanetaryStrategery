package com.ue.ps;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.ue.ps.buildings.Building;
import com.ue.ps.buildings.Colony;
import com.ue.ps.buildings.Factory;
import com.ue.ps.ships.Line;
import com.ue.ps.ships.Ship;
import com.ue.ps.ships.ShipPointer;
import com.ue.ps.ships.ShipType;
import com.ue.ps.systems.GameServerClient;
import com.ue.ps.ui.Images;
import com.ue.ps.ui.ShipContainer;

public class Planet extends BaseActor {
	public boolean hasBranched;
	private int size;
	public int capacity;
	public Building[] buildings;
	private PlanetType type;
	public boolean isHomePlanet;
	public int FactoryQuant;
	public ArrayList<ShipContainer> BuildQueue;

	public int resourceCapacity;
	public int priority;

	public int id;

	// Display
	public static final float focusZoomAmount = 0.4f;
	public ArrayList<Ship> orbitingShips = new ArrayList<Ship>();

	// Storage info
	public int resource;
	public int people;

	public String name;

	public Player owner;

	private int rotateDirection = MathUtils.randomSign();

	public ArrayList<ShipPointer> pointers = new ArrayList<ShipPointer>();

	private boolean isCombat = false;

	private int builtBuildings = 0;
	
	private Texture pew = Images.getImg("pew");
	// planet where you can say what it is
	public Planet(PlanetType type, int size) {
		super();
		this.setType(type);
		this.size = size;
		this.finish();
	}

	// planet without specific given info
	public Planet() {
		super();
		this.type = PlanetType.values()[MathUtils.random(0, 4)];
		this.size = MathUtils.random(1, 3);

		this.finish();
	}

	// Does calculations that will be done on every planet, no matter what.
	private void finish() {
		this.capacity = size * 4;
		this.setSize(this.getWidth() * size, this.getHeight() * size);
		this.buildings = new Building[capacity];
		this.setTexture(this.type.tex); // TODO resize the images based on
										// planet size
		this.setRotation(MathUtils.random(0, 360));
		this.name = Utils.genName();
		this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);
		this.FactoryQuant = 0;
		this.BuildQueue = new ArrayList<ShipContainer>();
	}

	public void buildShip(ShipType s) {
		if (this.FactoryQuant > 0) {
			// use BuildQueue
			// for (Building b : this.buildings){
			// ((Factory) this.building).buildingShip = s;
			// }
		}
	}

	public void setType(PlanetType p) {
		this.type = p;
		this.setTexture(this.type.tex);
	}

	public void setSize(int size) {
		this.size = size;
		this.capacity = size * 4;
		this.setSize(this.getWidth() * size, this.getHeight() * size);
		this.buildings = new Building[capacity];
		this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);
	}

	public int getSize() {
		return this.size;
	}

	public PlanetType getPlanetType() {
		return this.type;
	}

	public void update() {

	}

	// TODO add new shipcontainer to planet when captured

	@Override
	public void act(float dt) {
		super.act(dt);
		for (Ship s : orbitingShips) {
			s.angle -= 0.5f;
			int orbitDist = 25 + 16;
			s.setRotation(s.angle);
			s.setCenter(this.getWidth() / 2, this.getHeight() / 2);
			Vector2 pos = Utils.polarToRect((int) (this.getWidth() / 2 + s.getWidth() / 2) + orbitDist, s.angle,
					new Vector2(this.getWidth() / 2 - 16, this.getHeight() / 2 - 16));
			s.setCenter(pos.x, pos.y);
			s.setRotation(s.angle - 90);
		}

		this.rotateBy(0.2f * rotateDirection);
		isCombat = false;
		for (Ship s1 : this.orbitingShips) {
			for (Ship s2 : this.orbitingShips) {
				if (!s1.getOwnerName().equals(s2.getOwnerName())) {
					isCombat = true;
				}
			}
		}
		
		if (isCombat) {
			
		}

	}

	public void performCombat(Player owner) {
		ArrayList<Ship> clientShips = splitShipsByOwner(owner);
		ArrayList<Building> buildingTargets = new ArrayList<Building>();
		ArrayList<Ship> shipTargets = new ArrayList<Ship>();
		if (this.owner != owner) {
			for (Building b : this.buildings) {
				if (b != null) {
					buildingTargets.add(b);
				}

			}
		}
		for (Ship s : this.orbitingShips) {
			shipTargets.add(s);

		}
		for (Ship s : clientShips) {
			s.attack(shipTargets, buildingTargets, this);
		}

	}

	private ArrayList<Ship> splitShipsByOwner(Player owner) {
		ArrayList<Ship> ownersShips = new ArrayList<Ship>();
		for (Ship s : this.orbitingShips) {
			if (s.getOwnerName().equals(owner.getUser())) {
				ownersShips.add(s);
			}
		

		}
		return ownersShips;

	}

	public void onTurnUpdate() {
		if (isCombat) {
			this.performCombat(GameServerClient.clientPlayer);
		}
		for (ShipContainer s : this.BuildQueue) {
			s.buildUpdate();
		}
	}

	public void addBuilding(Building b, int slot) {
		if (builtBuildings < capacity && slot < capacity) {
			this.addActor(b);
			buildings[slot] = b;
			b.owner = this.owner;
			int angle = 360 / capacity * slot;
			b.setRotation(angle);
			b.setCenter(this.getWidth() / 2, this.getHeight() / 2);
			Vector2 pos = Utils.polarToRect((int) (this.getWidth() / 2 + b.getWidth() / 2), 360 / capacity * slot,
					new Vector2(this.getWidth() / 2, this.getHeight() / 2));
			b.setCenter(pos.x, pos.y);
			b.setRotation(angle - 90);

			builtBuildings += 1;
			if (b instanceof Factory) {
				this.FactoryQuant += 1;
			}
		}
	}

	public void destroyBuilding(int slot) {
		this.removeActor(buildings[slot]);
		buildings[slot] = null;
		builtBuildings -= 1;
	}

	public int getBuildingSlot(Building b) {
		for (int i = 0; i < buildings.length; i++) {
			if (buildings[i].equals(b)) {
				return i;
			}
		}
		return 0;
	}

	public Vector2 getXbyY() {

		if (size == 1) {
			return new Vector2(64, 64);
		} else if (size == 2) {
			return new Vector2(128, 128);
		} else {
			return new Vector2(256, 256);
		}

	}

	public void colonizeFrom(Planet P, Player p, Stage m) {
		if (this.owner != p) {
			this.owner = p;
			this.addBuilding(new Colony(), 0);
			Line.genLine(new Line(P, this, p.faction), m);
		}

	}

	public Planet copy() {
		Planet newPlanet = new Planet(this.type, this.size);
		newPlanet.buildings = this.buildings;
		newPlanet.builtBuildings = this.builtBuildings;
		newPlanet.capacity = this.capacity;
		newPlanet.isHomePlanet = this.isHomePlanet;
		newPlanet.name = this.name;
		newPlanet.setColor(this.getColor());
		return newPlanet;

	}
	
	private void pew() {
		ArrayList<Ship> clientShips = splitShipsByOwner(owner);
		ArrayList<Ship> targetShips  = new ArrayList<Ship>();
		for (Ship s : this.orbitingShips) {
			if (!s.getOwnerName().equals(this.owner.getUser())) {
				targetShips.add(s);
			}
		}
		BaseActor shot = new BaseActor(pew);
		Vector2 v = clientShips.get(MathUtils.random(0, clientShips.size() -1)).center;

		
		
		
	
	}


}
