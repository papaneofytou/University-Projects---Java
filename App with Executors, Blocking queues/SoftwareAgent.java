//package myProject;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.lang.NumberFormatException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.Thread;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SoftwareAgent {
	
	public SoftwareAgent() {
		/*number of one time jobs. Set initially at 1, set later from properties file*/
		int threadsNo = 1;
		/*requester interval in seconds. Set initially at 20, read later from file*/
		int requesterInterval = 30;
		/*sender interval in seconds. Set initially at 20, read later from file*/
		int senderInterval = 20;
		/*properties read from file*/
		Properties prop = new Properties();
		/*nmap jobs read from file*/
		List<List<String>> nmapJobs = new ArrayList<List<String>>();
		/*thread-safe queue used for all the jobs sent from AM*/
		BlockingQueue<List<String>> receivedJobs = new LinkedBlockingQueue<List<String>>();
		/*thread-safe queue used for one time jobs*/
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		/*thread-safe queue used for the results*/
		BlockingQueue<String> results = new LinkedBlockingQueue<String>();
		/*list with executors for periodic jobs*/
		List<ScheduledExecutorService> repeaters = new ArrayList<ScheduledExecutorService>();
		
		/*reading properties*/
		try {
			prop.load(new FileInputStream("config.properties"));
			try {
				threadsNo = Integer.parseInt(prop.getProperty("threadsNo"));
				requesterInterval = Integer.parseInt(prop.getProperty("requesterInterval"));
				senderInterval = Integer.parseInt(prop.getProperty("senderInterval"));
			} catch (NumberFormatException nfe) {
				System.out.println("This is not a number");
				System.out.println(nfe.getMessage());
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*reading nmap jobs*/
		nmapJobs = readFile("nmap.txt");
		
		/*creating one time job threads*/
		ExecutorService threadPool = Executors.newFixedThreadPool(threadsNo);
		
		/*creating job requesting thread*/
		RequestJobs reqJobs = new RequestJobs(nmapJobs, receivedJobs);
		ScheduledExecutorService requester = Executors.newSingleThreadScheduledExecutor();
		/*we use scheduleWithFixedDelay so that time starts after the previous round is done (not when it begins)*/
		requester.scheduleWithFixedDelay(reqJobs, 0, requesterInterval, TimeUnit.SECONDS);
		
		/*creating sender thread*/
		SenderThread send = new SenderThread(results);
		ScheduledExecutorService sender = Executors.newSingleThreadScheduledExecutor();
		/*initial delay: 10 secs */
		sender.scheduleWithFixedDelay(send, 10, senderInterval, TimeUnit.SECONDS);
		
		/*handle ctrl+c*/
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				try {
					/*how long to wait for threads to end before forcing them to shut down*/
					int exitWaitTime = 5;
					System.out.println("Shutting down, please wait...");
					//System.out.println("Final queue size = " + queue.size());
					threadPool.shutdown();
					sender.shutdown();
					requester.shutdown();
					for (int i=0; i<repeaters.size(); i++) {
						repeaters.get(i).shutdown();
					}
					threadPool.awaitTermination(exitWaitTime,TimeUnit.SECONDS);
					sender.awaitTermination(exitWaitTime,TimeUnit.SECONDS);
					requester.awaitTermination(exitWaitTime,TimeUnit.SECONDS);
					for (int i=0; i<repeaters.size(); i++) {
						repeaters.get(i).awaitTermination(exitWaitTime,TimeUnit.SECONDS);
					}
				} catch (InterruptedException e) {
					System.out.println("Tasks interrupted");
				} finally {
					if (!threadPool.isTerminated()) {
						System.out.println("Forcing thread pool to stop...");
						threadPool.shutdownNow();
					}
					if (!sender.isTerminated()) {
						System.out.println("Forcing sender to stop...");
						sender.shutdownNow();
					}
					if (!requester.isTerminated()) {
						System.out.println("Forcing requester to stop...");
						requester.shutdownNow();
					}
					for (int i=0; i<repeaters.size(); i++) {
						if (!repeaters.get(i).isTerminated()) {
							System.out.println("Forcing repeater " + i + " to stop...");
							repeaters.get(i).shutdownNow();
						}
					}
					System.out.println("Done");
				}
			}
		}));
		
		while (true) {
			try {
				List<String> command = receivedJobs.take();
				/*adding xml output format option, if not exists*/
				if (!command.get(1).contains("-oX - ")) {
					command.set(1, "-oX - " + command.get(1));
				}
				System.out.println("received job = " + command);
				if (command.get(2).trim().toLowerCase().equals("true")) {
					try {
						int sec = Integer.parseInt(command.get(3).trim());
						ScheduledExecutorService r = Executors.newSingleThreadScheduledExecutor();
						r.scheduleWithFixedDelay(new RunNmap("nmap " + command.get(1), results), 0, sec, TimeUnit.SECONDS);
						System.out.println("Repeater " + repeaters.size() + "\t" + command);
						repeaters.add(r);
					} catch (NumberFormatException nfe) {
						System.out.println("Command not processed. Fourth field must be integer:\n" + command);
					}	
				}
				else if (command.get(2).trim().toLowerCase().equals("false")) {
					queue.put(command.get(1));
					threadPool.execute(new RunNmap("nmap " + queue.take().toString(), results));				
				}
				else {
					System.out.println("Command not processed. Third field must be true or false:\n" + command);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
	}

	public List<List<String>> readFile(String fileName) {
		/* grammi pou diavazoume */
		String line = null;
		/* Lista me listes me ola ta dedomena */
		List<List<String>> data = new ArrayList<List<String>>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
	
			int cur = 0;
			/* diavazoume to arxeio. morfi: id, parameters, flag periodic, time periodic.
				ipo8etoume pos den iparxei kefalida */ 	 			
			while ((line = reader.readLine()) != null) {
				if (line.length() > 0){ //ean mi keni grammi
					data.add(new ArrayList<String>());
					/* diaxoristiko pedion arxeiou: komma */
					String[] dataArray = line.split(","); 		
					/* prosthetoume ola ta pedia sti lista */
					for (String item:dataArray) {	
						data.get(cur).add(item.trim());
					}	
					cur++;					
				}
			}
			reader.close();
		} catch (IOException ioe){
			System.out.println("Couldn't read file " + fileName + 	" (cause: " + ioe.getMessage() + ")");
		}		
		return data;
	}
	
	/*klasi gia tin apostoli requests ston AM. Pros to paron diavazoume tixaio arithmo apo ti lista*/
	private class RequestJobs implements Runnable {
		private List<List<String>> nmapJobs;
		private BlockingQueue<List<String>> receivedJobs;
		
		public RequestJobs(List<List<String>> nmapJobs, BlockingQueue<List<String>> receivedJobs) {
			this.nmapJobs = nmapJobs;
			this.receivedJobs = receivedJobs;
		}
		
		public void run() {
			Random rand = new Random();
			int max = 3;
			int min = 1;
			int rn = rand.nextInt((max - min) + 1) + min;
			
			if (nmapJobs.size() == 0) {
				rn = 0;
				System.out.println("There are no more jobs left..");
			}
			else if (nmapJobs.size() < rn) {
				rn = nmapJobs.size();
				System.out.println("Only " + rn + " job(s) left.. ");
			}
		
			for (int i=0; i<rn; i++) {
				try {
					receivedJobs.put(nmapJobs.get(0));
					nmapJobs.remove(0);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
	}
	
	private class SenderThread implements Runnable {
		private BlockingQueue<String> results;
		
		public SenderThread(BlockingQueue<String> results) {
			this.results = results;
		}
		
		public void run() {
			while (results.size() > 0) {
				System.out.println(results.remove());
			}
		}
	}
	
	private class RunNmap implements Runnable {
		String command;
		BlockingQueue<String> results;
		
		public RunNmap(String command, BlockingQueue<String> results) {
			this.command = command;
			this.results = results;
		}
		
		public void run() {
			try {
				Process proc = Runtime.getRuntime().exec(command);
				
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				
				String line = "";
				String res = "";
				while ((line = stdInput.readLine()) != null) {
					res += line + "\n";
				}					
				
				stdInput.close();
				
				try {
					int exitstatus = proc.waitFor();
					System.out.println("exit = " + exitstatus + "\t" + command);
					if (exitstatus == 0) {
						results.put(res);
					}
				} catch (InterruptedException e) {
					e.getMessage();
				}
				
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}