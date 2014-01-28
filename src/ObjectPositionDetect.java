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
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

public class ObjectPositionDetect {
    static int hueLowerR = 160;
    static int hueUpperR = 180;
    static BufferedImage input;
	static BufferedImage blackWhite;
	static int[][] maze;
	static ArrayList<Node> allthenodes = null;

    public static void main(String[] args) throws IOException {
    	int[][] pixels = readMaze("maze2.jpg");

    	IplImage orgImg = cvLoadImage("maze.jpg");
        IplImage thresholdImage = hsvThreshold(orgImg);
        cvSaveImage("hsvthreshold.png", thresholdImage);
       int[][] pixels2 = readMaze("hsvthreshold.png");
        
    /*  for(int y = 0; y < pixels.length; y++){
       
        	for(int x = 0; x < pixels[0].length; x++){
        		if(pixels2[y][x] == 1){
        			pixels[y][x] = 2;
        		}
        	}
        }*/
        pixels[2][4] = 2;
        pixels[20][12] = 3;
        maze = pixels;
        createGraph();
		breadthfirstsearch();
        
        BufferedImage input2 = ImageIO.read(new File("bigsize.jpg"));
        int pixel3[][] = readMaze("bigsize.jpg");
        for(int i = 0; i < maze.length; i++){
        	for(int j = 0; j < maze[0].length; j++){
        		if(maze[i][j] == 4){
        			pixel3[i*16][j*16] = 4;
        		}
        		
        	}
        }
        
        BufferedImage muhaha = new BufferedImage(input2.getWidth(),input2.getHeight(),BufferedImage.TYPE_INT_RGB);
        
        for(int x = 0; x < muhaha.getWidth(); x++){
        	for(int y = 0; y < muhaha.getHeight(); y++){
        		if(pixel3[x][y] == 0){
        			muhaha.setRGB(x, y, 0x000000);
        		}
        		else if(pixel3[x][y] == 1){
        			muhaha.setRGB(x, y, 0xFFFFFF);
        		}
        		else if(pixel3[x][y] == 2){
        			muhaha.setRGB(x, y, 0xFF0000);
        		}
        		else if(pixel3[x][y] == 3){
        			muhaha.setRGB(x, y, 0xFF0000);
        		}
        		else if(pixel3[x][y] == 4){
        			muhaha.setRGB(x, y, 0x0000FF);
        		}
        	}
        }
        
        File outputfile = new File("final.png");
        ImageIO.write(muhaha, "png", outputfile); 
        
        printArray(pixel3);
    }
    
    static void findRed(){
    	System.out.println("enter findRed");
    	ArrayList<Integer> x = new ArrayList<Integer>();
    	ArrayList<Integer> y = new ArrayList<Integer>();
    	BufferedImage input;
		try {
			input = ImageIO.read(new File("hsvthreshold.png"));
			for(int i = 0; i < input.getWidth(); i++){
	    		for(int j = 0; j < input.getHeight(); j++){
	    			if(input.getRGB(i, j) == 0xFFFFFFFF){
	    				x.add(i); y.add(j);
	    			}
	    		}
	    	}
	    	for(int i: x){
	    		System.out.print(" " + i);
	    	}
	    	System.out.println();
	    	for(int i: y){
	    		System.out.print(" " + i);
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    public static void breadthfirstsearch(){
    	System.out.println("Breadth first search started");
		Node start = null,end = null;
		for(Node n: allthenodes){
			if(n.getValue() == 2){start = n;}
			if(n.getValue() == 3){end = n;}
		}
		Queue<Node> BFSQueue = new LinkedList<Node>();
		BFSQueue.add(start);
		boolean found = false;
		while(!found){
			Node current = BFSQueue.remove();
			current.setVisited(true);
			if(current.value == 3){
				found = true;
			}
			ArrayList<Node> temp = current.getNeighbours();
			for(Node n:temp){
				if(n.isVisited() != true){
					n.setParent(current);
					BFSQueue.add(n);
				}
			}
		}
		Node current = end;
		while(current.parent != null){
			maze[current.getCoordinates().getY()][current.getCoordinates().getX()] = 4;
			current = current.parent;
		}
		maze[current.getCoordinates().getY()][current.getCoordinates().getX()] = 4;
	}
	
	public static void createGraph(){
		System.out.println("Create graph started");
		ArrayList<Node> nodes = new ArrayList<Node>();
		
		for(int i = 0; i < maze.length;i++){
			for(int j = 0; j < maze[0].length; j++){
				if(maze[i][j] != 0){
					nodes.add(new Node(new Coordinates(i,j),maze[i][j]));
				}
			}
		}
		
		for(Node n: nodes){
			Coordinates up = new Coordinates(n.getCoordinates().getY() - 1, n.getCoordinates().getX());
			Coordinates down = new Coordinates(n.getCoordinates().getY() + 1, n.getCoordinates().getX());
			Coordinates left = new Coordinates(n.getCoordinates().getY(), n.getCoordinates().getX() - 1);
			Coordinates right = new Coordinates(n.getCoordinates().getY(), n.getCoordinates().getX() + 1);
			
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
		System.out.println("Ended at: " + allthenodes.size());
	}
    
    
    
    static int[][] readMaze(String z) throws IOException{
    	
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

