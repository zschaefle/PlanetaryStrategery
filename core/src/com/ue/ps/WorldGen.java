package com.ue.ps;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class WorldGen {
	private static boolean canPlacePlanets = true;
	private static boolean canGenerate = true;
	private static Vector2 pos = new Vector2();
	private static int nextDist;
	private static int angle;
	private static int numConnects = 2;
	public static Rectangle planetBorder;
	public static ArrayList<Planet> allPlanets = new ArrayList<Planet>();
	private static int failedSteps = 0;
	private static boolean done = false;

	public static void setup(Stage m) {

		planetBorder = new Rectangle(-64 * 50 * 8, -64 * 50 * 8, 64 * 50 * 8 + 64 * 50 * 8, 64 * 50 * 8 + 64 * 50 * 8);
		pos = planetBorder.getCenter(pos);
		Planet p = new Planet();
		m.addActor(p);
		p.setCenter(pos.x, pos.y);
		p.setColor(Color.PURPLE);
		p.setZIndex(10000);

	}

	public static void generate(Stage m, int sizeX, int sizeY) {

		if (!done) {
			canGenerate = true;

			nextDist = MathUtils.random(50 * 8 * 8, 100 * 8 * 8);
			angle += MathUtils.random(0, 360);
			pos = Utils.polarToRect(nextDist, angle, pos);

			for (Planet p : allPlanets) {
				if (p.distanceTo(pos.x, pos.y) < 50 * 8 * 8) {
					canGenerate = false;
				}
			}

			if (canGenerate) {
				Planet p = new Planet();
				allPlanets.add(p);
				m.addActor(p);
				p.setCenter(pos.x, pos.y);
				p.setColor(Color.GREEN);
				p.id = 1;
				failedSteps = 0;

			} else {
				Planet p = new Planet();
				// allPlanets.add(p);
				p.setColor(Color.RED);
				m.addActor(p);
				p.setCenter(pos.x, pos.y);
				p.addAction(Actions.fadeOut(1));
				failedSteps += 1;

			}

			if (!planetBorder.contains(pos)) {

				pos = allPlanets.get(0).center;
			}

			if (failedSteps > 100) {
				for (Planet p : allPlanets) {
					p.setColor(Colors.getColors().values().toArray().get(MathUtils.random(0, Colors.getColors().values().toArray().size - 1)));
				}
				done = true;

				genHomePlanets(2, m);

			}

		} else {

		}

	}

	private static Planet getClosestPlanetTo(float x, float y) {

		Planet[] planetMap = new Planet[allPlanets.size()];
		planetMap = allPlanets.toArray(planetMap);
		for (int i = 1; i < planetMap.length; i++) {
			Planet index = planetMap[i];
			int j = i;
			while (j > 0 && planetMap[j - 1].distanceTo(x, y) > index.distanceTo(x, y)) {
				planetMap[j] = planetMap[j - 1];
				j--;
			}
			planetMap[j] = index;
		}

		return planetMap[0];
	}

	private static Polygon genHomePlanets(int numHomePlanets, Stage m) {
		// numHomePlanets *=2;
		Polygon ring = new Polygon();

		int dist = (int) (planetBorder.width * 2);

		for (int i = 0; i < numHomePlanets; i++) {
			Vector2 vert = Utils.polarToRect(dist, (360 / numHomePlanets * i) + 45, planetBorder.getCenter(new Vector2()));

			Planet home = getClosestPlanetTo(vert.x, vert.y);
			home.setColor(Color.BLUE);
			home.isHomePlanet = true;
			home.setType(PlanetType.goldilocks);
			Planet p = new Planet();
			// allPlanets.add(p);
			p.setColor(Color.TEAL);
			m.addActor(p);
			p.setCenter(vert.x, vert.y);

		}
		
		allPlanets.get(1).addBuilding(new Factory(), 1);
		allPlanets.get(2).addBuilding(new Factory(), 1);
		for (Planet p : allPlanets) {
			for (int i = 0; i < p.capacity; i++) {
				
				Ship.spawnShip(Faction.Xin, p, ShipType.drone, 10 * i);
			}
		}
		
		Line.genLine(new Line(allPlanets.get(1), allPlanets.get(2), Faction.Xin), m);
		Line.genLine(new Line(allPlanets.get(2), allPlanets.get(1), Faction.Xin), m);

		return ring;

	}

}
