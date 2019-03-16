package com.jeff.astarpath;

import java.util.ArrayList;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class AstarGame extends InputAdapter implements ApplicationListener {
	
	private OrthographicCamera camera;
	private ShapeRenderer shape;
	
	int cols = 50;
	int rows = 40;
	
	Node[][] grid;
	
	Array<Node> openSet;
	Array<Node> closedSet;
	
	Node start;
	Node end;
	int w,h;
	ArrayList<Node> path;
	ArrayList<Vector2> pathPos;
	private boolean pathFound;
	
	Player player;
	private boolean keyTouched;
	
	class Node {
		public int f,g,h;
		//public int x,y;
		
		public Array<Node> neighbors;
		public Node previous;
		public Vector2 position;
		
		public boolean wall;
		
		
		public Node(int x,int y) {
			//this.x = x;
			//this.y = y;
			neighbors = new Array<Node>();
			previous = null;
			position = new Vector2(x,y);
			wall = false;
			float rand = MathUtils.random(1.0f);
			if(rand < 0.3f) {
				wall = true;
			}
		}
		
		public void render(Color color,ShapeType type) {
			shape.begin(type);
			shape.setColor(color);
			//shape.rect(this.x, this.y, 1, 1);
			shape.rect(position.x, position.y, 1, 1);
			shape.end();
		}
		
		public void addNeighbors(Node[][] grid) {
			if(this.position.x > 0) {
				//neighbors.add(grid[this.x - 1][this.y]);
				neighbors.add(grid[(int) (this.position.x - 1)][(int) this.position.y]);
			}
			if(this.position.x < cols-1) {
				//neighbors.add(grid[this.x + 1][this.y]);
				neighbors.add(grid[(int) (this.position.x + 1)][(int) this.position.y]);
			}
			if(this.position.y > 0) {
				//neighbors.add(grid[this.x][this.y - 1]);
				neighbors.add(grid[(int) this.position.x][(int) (this.position.y - 1)]);
			}
			
			if(this.position.y < rows-1) {
				neighbors.add(grid[(int) this.position.x][(int) (this.position.y + 1)]);
			}
			
		}
		
		
		public int getX() {
			return (int) position.x;
		}
		
		public int getY() {
			return (int) position.y;
		}
	}
	
	
	@Override
	public void create() {		
		
		
		camera = new OrthographicCamera(50,40);
		camera.position.x = 50/2;
		camera.position.y = 40/2;
		camera.update();
		shape = new ShapeRenderer();
		
		w = (int) (camera.viewportWidth/cols);
		h = (int) (camera.viewportWidth/rows);
		
		//System.out.println(w + " " + h);
		
		
		grid = new Node[cols][rows];
		openSet = new Array<Node>();
		closedSet = new Array<Node>();
		path = new ArrayList<Node>();
		
		pathPos = new ArrayList<Vector2>();
		pathFound = false;
		
		player = new Player(pathPos);
		
		for(int i = 0;i < cols;i++) {
			for(int j = 0;j < rows;j++) {
				grid[i][j] = new Node(i,j);
			}
		}
		
		for(int i = 0;i < cols;i++) {
			for(int j = 0;j < rows;j++) {
				grid[i][j].addNeighbors(grid);
			}
		}
		
		start = grid[MathUtils.random(cols-1)][MathUtils.random(rows-1)];
		end = grid[MathUtils.random(cols-1)][MathUtils.random(rows-1)];
		
		openSet.add(start);
		
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void dispose() {
		
	}
	
	public int heuristic(Node a,Node b) {
		//int abx = b.x - a.x;
		//int aby = b.y - a.y;
		int abx = (int)(b.position.x - a.position.x);
		int aby = (int)(b.position.y - a.position.y);
		int d = (int) Math.sqrt(abx * abx + aby * aby);
		//int d = Math.abs(abx) + Math.abs(aby);
		return d;
	}

	@Override
	public void render() {	
		//player.updatePlayer();
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		shape.setProjectionMatrix(camera.combined);
		
		if(openSet.size > 0) {
			int winner = 0;
			for(int i = 0;i < openSet.size;i++) {
				if(openSet.get(i).f < openSet.get(winner).f) {
					winner = i;
				}
			}
			
			Node current = openSet.get(winner);
			
			if(current.equals(end)) {
				path = new ArrayList<Node>();
				Node temp = current;
				path.add(temp);
				while(temp.previous != null) {
					path.add(temp.previous);
					temp = temp.previous;
				}
				
				pathFound = true;
			}
			
			if(!pathFound) {
				if(openSet.contains(current, false)) {
					openSet.removeValue(current, false);
				}
				closedSet.add(current);
				
				Array<Node> neighbors = current.neighbors;
				for(int i = 0;i <neighbors.size;i++) {
					Node neighbor = neighbors.get(i);
					
					if(!closedSet.contains(neighbor, false) && !neighbor.wall) {
						int tempG = current.g + 1;
						if(openSet.contains(neighbor, false)) {
							if(tempG < neighbor.g) {
								neighbor.g = tempG;
							}
						} else {
							neighbor.g = tempG;
							openSet.add(neighbor);
						}
						
						neighbor.h = heuristic(neighbor,end);
						neighbor.f = neighbor.g + neighbor.h;
						neighbor.previous = current;
					}
					
				}
			}
			
			path = new ArrayList<Node>();
			Node temp = current;
			path.add(temp);
			while(temp.previous != null) {
				path.add(temp.previous);
				temp = temp.previous;
			}
			
			
		} else {
			System.out.println("No solution");
		}
		
		
		
		for(int i = 0;i < openSet.size;i++) {
			openSet.get(i).render(Color.GREEN,ShapeType.Filled);
		}
		
		for(int i = 0;i < closedSet.size;i++) {
			closedSet.get(i).render(Color.RED,ShapeType.Filled);
		}
		
		for(int i = 0;i < path.size();i++) {
			path.get(i).render(Color.BLUE,ShapeType.Filled);
		}
		
		for(int i = 0;i < cols;i++) {
			for(int j = 0;j < rows;j++) {
				grid[i][j].render(Color.BLACK,ShapeType.Line);
			}
		}
		
		for(int i = 0;i < cols;i++) {
			for(int j = 0;j < rows;j++) {
				if(grid[i][j].wall) {
					grid[i][j].render(Color.BLACK,ShapeType.Filled);
				}
			}
		}
		
		for(int i = 0;i < cols;i++) {
			for(int j = 0;j < rows;j++) {
				if(grid[i][j].equals(start) || grid[i][j].equals(end)) {
					grid[i][j].render(Color.YELLOW,ShapeType.Filled);
				}
			}
		}
		
		if(pathFound) {
			//player.renderPlayer(shape);
			//System.out.println(path.size());
		}
		
		if(keyTouched) {
			//player = new Player(pathPos);
			player.setAPath(pathPos);
			player.updatePlayer();
			player.renderPlayer(shape);
		}
		
		
		start.wall = false;
		end.wall = false;
		
	}
	
	@Override
	public boolean keyDown(int key) {
		if(Keys.S == key) {
			if(pathFound) {
				int pathSize = path.size()-1;
				for(int i = pathSize;i > 0;i--) {
					//System.out.println(path.get(i).getX() + " " + path.get(i).getY());
					//player.position.set(path.get(i).getX(), path.get(i).getY());
				}
				//System.out.println("NodeList Path Size: " + path.size());
				System.out.println("Start node pos: " + start.getX() + " " + start.getY());
				System.out.println("End node pos: " + end.getX() + " " + end.getY());
				//pathFound = false;
			}
		}
		
		if(Keys.F == key) {
			if(pathFound) {
				
				int pathSize = path.size()-1;
				for(int i = 0;i < pathSize;i++) {
					pathPos.add(path.get(i).position);	
				}
				
				int pathVecSize = pathPos.size()-1;
//				for(int i = pathVecSize;i > 0;i--) {
//					System.out.println(pathPos.get(i).x + " " + pathPos.get(i).y);
//				}
				for(int i = 0;i < pathVecSize;i++) {
					System.out.println(pathPos.get(i).x + " " + pathPos.get(i).y);
				}
				pathPos.trimToSize();
				
				System.out.println("Vector2 list size: " + pathPos.size());
				//System.out.println("First Position: " + pathPos.get(0));
				
			}
		}
		if(Keys.ENTER == key) {
			if(pathFound) {
				keyTouched = true;
			}
		}
		
		if(Keys.RIGHT == key) {
			if(pathFound) {
//				ListIterator<Vector2> iter = pathPos.listIterator(pathPos.size());
//				int currentIndex = iter.previousIndex();
//				while(iter.hasPrevious()) {
//					Vector2 currentPos = iter.previous();
//					System.out.println(iter.previousIndex()-1);
//					System.out.println(currentPos.x + " " + currentPos.y);
//					break;
//				}
				int pathSize = path.size()-1;
				for(int i = pathSize;i > 0;) {
					getNextPosition(i);
				}
			}
		}
		return false;
	}
	
	private void getNextPosition(int index) {
		int i = 0;
		
		i = index;
		//for(int i = pathSize;i > 0;) {
		System.out.println(pathPos.get(i) + " " + pathPos.get(i));
		//break;
		//}
	}
	
	@Override
	public boolean keyUp(int key) {
		if(Keys.S == key) {
			pathFound = false;
			return true;
		}
		return false;
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
