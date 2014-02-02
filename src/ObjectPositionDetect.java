import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetSpatialMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetCentralMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMoments;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import javax.imageio.ImageIO;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

public class ObjectPositionDetect {
    static int hueLowerR = 160;
    static int hueUpperR = 180;
	static int[][] maze;
	static ArrayList<Node> allthenodes = null;
	static int divider = 5;
	
    public static void main(String[] args) throws IOException {


    	maze = readMaze("maze2.jpg");
    	maze = increaseBlackSpace(8);
    	constructImage(maze,"testing.png");
    	resize(2,"testing.png");
        maze = readMaze("resized.png");
        devide(maze);
        createModifiedGraph(maze);
        int x = 0;
   
        for(int i = 16; i < 28; i++){
        	for(int j = 20; j < 30; j++){
        		maze[i][j] = 2;
        	}
        }
        for(int i = 153; i < 159; i++){
        	for(int j = 102; j < 107; j++){
        		maze[i][j] = 3;
        	}
        }
        constructImage(maze,"justtesting.png");
        breadthfirstsearch(); System.out.println("BFS ended");
        constructImage(maze,"ended.png");
        int[][] temp = maze.clone();
        maze = readMaze("maze2.jpg");
        enlargeMaze(2,maze,temp);
        constructImage(maze,"fina.png");
    }
    
    public static void enlargeMaze(int factor,int[][] large, int[][] small){
    	for(int i = 0; i < small.length; i++){
    		for(int j = 0; j < small[0].length; j++){
    			if(small[i][j] == 5){
    				large[i*factor][j*factor] = 5;
    			}
    		}
    	}
    }
    
    public static int[][] extractMazeOnly(String filename) throws IOException{
    	IplImage orgImg = cvLoadImage(filename);
        IplImage thresholdImage = hsvThreshold(orgImg);
        cvSaveImage("robot.png", thresholdImage);
        int temp[][] = readMaze(filename);
        int temp2[][] = readMaze("robot.png");
        for(int i = 0; i < temp.length; i++){
        	for(int j = 0; j < temp[0].length; j++){
        		if(temp2[i][j] == 1){
        			temp[i][j] = 1;
        		}
        	}
        }
        return temp;
    }
    
    public static void extractRobot(String filename){
    	IplImage orgImg = cvLoadImage(filename);
        IplImage thresholdImage = hsvThreshold(orgImg);
        cvSaveImage("robot.png", thresholdImage);
    }
    
    public static int[][] increaseBlackSpace(int t){
    	int[][] maze2 = new int[maze.length][maze[0].length];
		for(int i = 0; i < maze2.length; i++){
			for(int j = 0; j < maze2[0].length; j++){
				maze2[i][j] = 1;
			}
		} 
    	for(int i = 0; i < maze2.length; i++){
			for(int j = 0; j < maze2[0].length; j++){
				if(maze[i][j] == 0){
					if(i - t >= 0){
						for(int k = 0; k < t; k++){maze2[i - k][j] = 0;}
					}
					if(i + 20 <= maze.length - 1){
						for(int k = 0; k < t; k++){maze2[i + k][j] = 0;}
					}
					if(j - 20 >= 0){
						for(int k = 0; k < t; k++){maze2[i][j - k] = 0;}
					}
					if(j + 20 <= maze[0].length - 1){
						for(int k = 0; k < t; k++){maze2[i][j+k] = 0;}
					}
					
				}
			}
		}
		return maze2;
    }
    
    public static void devide(int[][] maze2){
    	for(int i = 0; i < maze2.length; i++){
    		for(int j = 0; j < maze2[0].length; j++){
    			if(maze2[i][j] != 0){
    				if(i % divider == 0){
        				if(j % divider == 0){maze2[i][j] = 4;}
        			}
    			}
    		}
    	}
    }
    
    
    public static void constructImage(int[][] pixel3, String pikachu) throws IOException{
    	BufferedImage muhaha = new BufferedImage(pixel3.length,pixel3[0].length,BufferedImage.TYPE_INT_RGB);
    	for(int x = 0; x < muhaha.getWidth(); x++){
        	for(int y = 0; y < muhaha.getHeight(); y++){
        		if(pixel3[x][y] == 0){muhaha.setRGB(x, y, 0x000000);}
        		else if(pixel3[x][y] == 1){muhaha.setRGB(x, y, 0xFFFFFF);}
        		else if(pixel3[x][y] == 2){muhaha.setRGB(x, y, 0xFF0000);}
        		else if(pixel3[x][y] == 3){muhaha.setRGB(x, y, 0xFF0000);}
        		else if(pixel3[x][y] == 4){muhaha.setRGB(x, y, 0x0000FF);}
        		else if(pixel3[x][y] == 5){muhaha.setRGB(x, y, 0x00FF00);}
        	}
        }
    	File outputfile = new File(pikachu);
        ImageIO.write(muhaha, "png", outputfile);
    }
    
    static void findRed(){
    	System.out.println("enter findRed");
    	ArrayList<Integer> x = new ArrayList<Integer>();
    	ArrayList<Integer> y = new ArrayList<Integer>();
    	BufferedImage input;
		try {
			input = ImageIO.read(new File("robot.png"));
			for(int i = 0; i < input.getWidth(); i++){
	    		for(int j = 0; j < input.getHeight(); j++){
	    			if(input.getRGB(i, j) == 0xFFFFFFFF){
	    				x.add(i); y.add(j);
	    			}
	    		}
	    	}
	    	for(int i: x){System.out.print(" " + i);}
	    	System.out.println();
	    	for(int i: y){System.out.print(" " + i);}
		} catch (IOException e) { System.out.println("Line 102");}
    	
    }
    
    public static void breadthfirstsearch(){
    	System.out.println("Breadth first search started");
		Node start = null,end = null;

		for(Node n: allthenodes){
			if(maze[n.getCoordinates().getY()][n.getCoordinates().getX()] == 2){
					start = n;
			}
			if(maze[n.getCoordinates().getY()][n.getCoordinates().getX()] == 3){
				end = n;
			}
		}

		Queue<Node> BFSQueue = new LinkedList<Node>();
		BFSQueue.add(start);
		
		boolean found = false;
		while(!found){
			Node current = BFSQueue.remove();
			current.setVisited(true);
			if(current.equals(end)){
				found = true;
			} 
			ArrayList<Node> temp = current.getNeighbours();
			for(Node n:temp){
				if(n.isVisited() == false){
					n.setParent(current);
					BFSQueue.add(n);
				}
			}
		}
		Node current = end;
		while(current.parent != null){
			maze[current.getCoordinates().getY()][current.getCoordinates().getX()] = 5;
			current = current.parent;
		}
		maze[current.getCoordinates().getY()][current.getCoordinates().getX()] = 5;
	}
	
    public static void createModifiedGraph(int[][] maze2){
    	System.out.println("Created modified graph started");
    	ArrayList<Node> nodes = new ArrayList<Node>();
    	
    	for(int i = 0; i < maze2.length; i++){
    		for(int j = 0; j < maze2[0].length; j++){
    			if(maze2[i][j] == 4){
    				nodes.add(new Node(new Coordinates(i,j),maze2[i][j]));
    			}
    		}
    	}
    	for(Node n: nodes){
			Coordinates up = new Coordinates(n.getCoordinates().getY() - 5, n.getCoordinates().getX());
			Coordinates down = new Coordinates(n.getCoordinates().getY() + 5, n.getCoordinates().getX());
			Coordinates left = new Coordinates(n.getCoordinates().getY(), n.getCoordinates().getX() - 5);
			Coordinates right = new Coordinates(n.getCoordinates().getY(), n.getCoordinates().getX() + 5);
			
			ArrayList<Node> neighbour = new ArrayList<Node>();
			for(Node m: nodes){
				if(m.getCoordinates().toString().equals(up.toString()) || m.getCoordinates().toString().equals(down.toString())
						|| m.getCoordinates().toString().equals(left.toString()) || m.getCoordinates().toString().equals(right.toString())){
					neighbour.add(m);
				}
			}
			n.setNeighbours(neighbour);
		}
		allthenodes = nodes;
    }
   
    
	public static void createGraph(){
		System.out.println("Create graph started");
		ArrayList<Node> nodes = new ArrayList<Node>();
		
		for(int i = 0; i < maze.length;i++){ /* creates new node for white cell */
			for(int j = 0; j < maze[0].length; j++){
				if(maze[i][j] != 0){nodes.add(new Node(new Coordinates(i,j),maze[i][j]));}
			}
		}
		int x = 1;
		for(Node n: nodes){
			System.out.println(x + "Current Node: " + n.getCoordinates());
			Coordinates up = new Coordinates(n.getCoordinates().getY() - 1, n.getCoordinates().getX());
			Coordinates down = new Coordinates(n.getCoordinates().getY() + 1, n.getCoordinates().getX());
			Coordinates left = new Coordinates(n.getCoordinates().getY(), n.getCoordinates().getX() - 1);
			Coordinates right = new Coordinates(n.getCoordinates().getY(), n.getCoordinates().getX() + 1);
			ArrayList<Node> neighbour = new ArrayList<Node>();
			System.out.println("	Neighbour:");
			for(Node m: nodes){
				if(m.getCoordinates().toString().equals(up.toString()) || m.getCoordinates().toString().equals(down.toString())|| m.getCoordinates().toString().equals(left.toString()) || m.getCoordinates().toString().equals(right.toString())){
					neighbour.add(m);
					System.out.println(" " + m.getCoordinates() + ":");
				}
			}
			n.setNeighbours(neighbour);
			x++;
		}
		allthenodes = nodes;
		System.out.println("Total number of nodes: " + allthenodes.size());
	}
    
    static int[][] readMaze(String z) throws IOException{
    	BufferedImage input, blackWhite;
		input = ImageIO.read(new File(z));
        blackWhite = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g2d = blackWhite.createGraphics();
        g2d.drawImage(input, null, 0, 0);
        g2d.dispose();
        
        File outputfile = new File("saved.png");
        ImageIO.write(blackWhite, "png", outputfile);
        
        input = ImageIO.read(new File("saved.png"));
        int[][] pixels = new int[input.getWidth()][input.getHeight()];

        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {
                pixels[x][y] = (input.getRGB(x, y) == 0xFFFFFFFF ? 1 : 0);
            }
        }
        
        return pixels;
    }
    
    static void printArray(int[][] pixels){
    	for(int x = 0; x < pixels[x].length; x++){
        	for(int y = 0; y < pixels.length; y++){
        		System.out.print(pixels[y][x]);
        	}
        	System.out.println("");
        }
    }
    
    public static void resize(int x,String y) throws IOException{
		BufferedImage binarized = ImageIO.read(new File(y));
		BufferedImage resize = new BufferedImage(binarized.getWidth()/x,binarized.getHeight()/x,BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D g = resize.createGraphics();
		g.drawImage(binarized,0,0,binarized.getWidth()/x,binarized.getHeight()/x,null);
		g.dispose();
		ImageIO.write(resize,"png", new File("resized.png"));	
	}
	
	public static String binarize(String z) throws IOException{
	    	BufferedImage input, blackWhite;
			input = ImageIO.read(new File(z));
	        blackWhite = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
	        Graphics2D g2d = blackWhite.createGraphics();
	        g2d.drawImage(input, null, 0, 0);
	        g2d.dispose();
	        ImageIO.write(blackWhite, "png", new File("binarize.png"));
	        return "binarize.png";
	}

    static IplImage hsvThreshold(IplImage orgImg) {
        IplImage imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);
        cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
        IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        cvInRangeS(imgHSV, cvScalar(hueLowerR, 100, 100, 0), cvScalar(hueUpperR, 255, 255, 0), imgThreshold);
        cvReleaseImage(imgHSV);
        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 13);
        return imgThreshold;
    }
}

