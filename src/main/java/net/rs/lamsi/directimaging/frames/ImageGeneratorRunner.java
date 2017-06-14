package net.rs.lamsi.directimaging.frames;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.utils.FileAndPathUtil;
import net.rs.lamsi.utils.mywriterreader.TxtWriter;

import org.apache.commons.io.FileUtils;

public class ImageGeneratorRunner implements Runnable {

	private boolean isRunning = false, inFolder = true;
	private Random rand;
	private long sleep = 20;
	private Vector<Image2D[]> taskList;
	private WindowDirectImaging window;
	private int task = 0, id = 0; 
	
	public ImageGeneratorRunner(WindowDirectImaging window) {
		rand = new Random(System.currentTimeMillis());
		this.window = window; 
	}
	
	@Override
	public void run() { 
		String dir = FileAndPathUtil.getPathOfJar().getParent()+"/data/direct/"; 
		try {
			FileUtils.cleanDirectory(new File(dir));
		} catch (IOException e) { 
			e.printStackTrace();
		}
		
		TxtWriter writer = new TxtWriter();
		id = (rand.nextInt(1000));
		startNewFile(writer, dir, "task"+id, "/file1");
		int cl = 0; 
		int cdp = 0;
		while(isRunning) {
			Image2D[] image = taskList.get(task);
			//write next element
			String line = ""+image[0].getX(true,cl,cdp);
			for(int i=0; i<image.length; i++) {
				line += ","+image[i].getI(true,cl, cdp);
			}
			writer.writeLine(line);
			cdp++;
			if(cdp>=image[0].getLineLength(cl)) {
				cdp=0;
				cl++;
				if(cl>=image[0].getData().getLinesCount()) {
					// next task or stop?
					cl = 0;
					id = (rand.nextInt(1000));
					task++;
					if(task>=taskList.size())
						isRunning = false;
				}
				if(isRunning) {
					// create new file if still running
					writer.closeDatOutput();
					startNewFile(writer, dir,"task"+id, "/file"+(cl+1));
				}
			}
			window.getLbLabel().setText("10Images #  task="+(task+1)+" #  line="+cl+" #  dp="+cdp);
			//
			
			try {
				if(cdp%4==0)
				Thread.sleep(1);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private void startNewFile(TxtWriter writer,String dir, String subdir,  String file) {
		// annoying files
		if(inFolder)  
			writer.openNewFileOutput(dir+subdir+file+"/"+file+".txt");
		else writer.openNewFileOutput(dir+subdir+"/"+file+".txt");
		writer.writeLine("Header Stuff,dont try to read this,really!");
		writer.writeLine("Header Stuff,dont try to read this,really!");
		writer.writeLine("More Header Stuff"); 
		// real file
		if(inFolder)  
			writer.openNewFileOutput(dir+subdir+file+"/"+file+".csv");
		else writer.openNewFileOutput(dir+subdir+"/"+file+".csv"); 
		writer.writeLine("Header Stuff,dont try to read this,really!");
		writer.writeLine("Header Stuff,dont try to read this,really!");
		writer.writeLine("More Header Stuff"); 
		// write title
		writer.writeLine("x,PO4,Ba,Ca,Fe,Co,Cu,Zn,C13,Na,K");
	} 
	
	/**
	 * create images
	 * @return
	 */
	/*
	public Image2D createImage() {
		ScanLine2D[] lines = new ScanLine2D[100];
		for(int l=0; l<lines.length; l++) {
			DataPoint2D[] dp = new DataPoint2D[1000];
			for(int d=0; d<dp.length; d++) {
				// middle the highest
				double in = (50-Math.pow(Math.abs(l+1-lines.length/2),2))*10+ 500-Math.pow(Math.abs(d+1-dp.length/2),2)*1;
				in += Math.abs(l+1-lines.length/2)*1 + Math.abs(d+1-dp.length/2)*400;
				in += (lines.length-l)*5000;
				in += Math.abs(rand.nextInt(6000)/100.0); 
				in = in/20;
				// create dp
				dp[d] = new DataPoint2D(d*0.1, in);
			}
			lines[l] = new ScanLine2D(dp);
		}
		return new Image2D(new Dataset2D(lines));
	}
	/**
	 * creating standards
	 * 0 - 800
	 */
	/*
	public Image2D createStandards() {
		ScanLine2D[] lines = new ScanLine2D[24];
		for(int l=0; l<lines.length; l++) { 
			DataPoint2D[] dp = new DataPoint2D[1000];
			for(int d=0; d<dp.length; d++) {
				// middle the highest
				double in = (int)(l/4)*200.0;
				in += Math.abs(rand.nextInt(6000)/100.0);
				// create dp
				dp[d] = new DataPoint2D(d*0.1, in);
			}
			lines[l] = new ScanLine2D(dp);
		}
		return new Image2D(new Dataset2D(lines));
	}
	*/
	public void stopImage() {
		isRunning = false;
	}
	public Image2D[] startImage(boolean inFolder) {
		this.inFolder = inFolder;
		taskList = new Vector<Image2D[]>();
		/*
		Image2D[] std = new Image2D[10];
		for(int i=0; i<std.length; i++)
			std[i] = createStandards();
		taskList.add(std);
		
		Image2D[] image = new Image2D[10];
		for(int i=0; i<image.length; i++)
			image[i] = createImage();
		taskList.add(image);

		taskList.add(std);
		
		taskList.add(image);

		taskList.add(std);
		*/
		isRunning = true;
		Thread t = new Thread(this);
		t.start();
		//return image;
		return null;
	}
	public void setSleep(long sl) {
		sleep = sl;
	}
}
