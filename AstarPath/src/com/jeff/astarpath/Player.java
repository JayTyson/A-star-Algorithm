package com.jeff.astarpath;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class Player {
	
	public ArrayList<Vector2> path;
	public Vector2 position;
	public Vector2 velocity;
	
	public int currentPointIndex = 0;
	public float distanceTraveled;
	public float distanceBetweenCurrentPoints;
	
	public Player(ArrayList<Vector2> path) {
		velocity = new Vector2();
		//this.path = path;
		position = new Vector2(20,20);
		//distanceTraveled = 0f;
		//distanceBetweenCurrentPoints = 0.02f;//distance(path.get(currentPointIndex), path.get(currentPointIndex+1));
	}
	
	public void setAPath(ArrayList<Vector2> path) {
		this.path = path;
	}
	
	public float distance(Vector2 point0, Vector2 point1) {
		  float dx = (float) (point1.x-point0.x);
		  float dy = (float) (point1.y-point0.y);
		  // Hello pythagoras :)
		  return (float) Math.sqrt(dx * dx + dy * dy);
	}
	
	public Vector2 interpolateWithWeights(Vector2 point0, Vector2 point1, float weight, Vector2 positionDst) {
	      // This is just a somehow slightly changed version of getting
	      // the average from two numbers. We include weights here.
	      positionDst.x = (point0.x * weight) + (point1.x * (1f - weight));
	      positionDst.y = (point0.y * weight) + (point1.y * (1f - weight));
	      return positionDst;
	}
	
	public Vector2 interpolate(Vector2 point0, Vector2 point1, float weight,Vector2 positionDst) {
	      // This is just a somehow slightly changed version of getting
	      // the average from two numbers. We include weights here.
	      positionDst.x = (point1.x - point0.x) * weight + point0.x;
	      positionDst.y = (point1.y - point0.y) * weight + point0.y;
	      return positionDst;
	}
	
	public float smoothStep(float x) {
		return x * x *(3 - 2 * 2);
	}
	
	public void updatePlayer() {
		if(currentPointIndex >= path.size()-1) {
			return;
		}
		Vector2 lastPoint = path.get(currentPointIndex);
		Vector2 nextPoint = path.get(currentPointIndex+1);
		distanceTraveled = distance(this.position, lastPoint);
		
		
		float weight = 0;
		float curvedTime = 0;
		weight += Gdx.graphics.getDeltaTime() * 0.05f;
		
		//this.position = interpolate(lastPoint, nextPoint, weight,position);
		curvedTime = smoothStep(weight);
		this.position.x = (nextPoint.x * curvedTime) + (lastPoint.x * (1 - curvedTime));
		this.position.y = (nextPoint.y * curvedTime) + (lastPoint.y * (1 - curvedTime));
		//System.out.println("Player position: " + this.position);
	   
		currentPointIndex++;
		if(weight > 1.0f) {
			weight = 0;
		}
	}
	
	public void renderPlayer(ShapeRenderer shape) {
		shape.setColor(Color.MAGENTA);
		shape.begin(ShapeType.Filled);
		shape.rect(this.position.x, this.position.y, 1, 1);
		shape.end();
	}

}
