import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class QuoteCollectionEditor {
	
	public void theAdder() throws IOException{
		
		Queue<String> queue = new LinkedList<String>();
		
		
		Scanner reader = new Scanner(System.in);
		System.out.println("Automatic [1] or Manual [2]?");
		String mode = reader.nextLine();
		System.out.println("You have selected " + mode);
		
		//Automatic
		if (mode.equals("1")){
			System.out.println("getting mail...");
			//email info
			System.out.println("email address?");
			String userName = reader.nextLine();
			System.out.println("email password?");
			String password = reader.nextLine();
			
			//String userName = "joreyjorey@live.com";
	        //String password = ":)";
			
			//the are for windows live mail
	        String protocol = "pop3";
	        String host = "pop-mail.outlook.com";
	        String port = "995";
			
			ReceiveMail x = new ReceiveMail();
			queue = x.downloadEmails(protocol, host, port, userName, password);
		}
		
		//Manual
		else {
			
		//Scanner reader = new Scanner(System.in);
		
			while (true){
				System.out.print("quote: ");
				String quote = reader.nextLine();
				System.out.print("author: ");
				//String author = "\t" + "~ " + reader.nextLine();
				String author = reader.nextLine();
				System.out.println("more? ");
				System.out.print("y/n");
				String x = reader.nextLine();
				queue.add(quote);
				queue.add(author);
				if (x.equals("n")){
					reader.close();
					break;
				}
			}
		}	
		
		//File to edit.
		String fileOld = "C:/Users/Jorvon/Documents/quotes.txt";
		String fileNew = "C:/Users/Jorvon/Documents/quotes2.txt";
		
		String line = "";
		
		try{
			
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileNew));
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileOld));
			
			System.out.println("gg");
		
			while((line = bufferedReader.readLine()) != null){
				
				
				if (line.equals("@")){
					line = line.replace("@", "");
					
					while (!queue.isEmpty()){
						line = line + queue.remove() + "\n" + "\t" + "~ " + queue.remove() + "\n" + "\n";
					}
					
					line = line + "@";
						
						bufferedWriter.write(line);
						break;
				}
				
				bufferedWriter.write(line + "\n");
				
			}
			bufferedReader.close();
			bufferedWriter.close();
		}
		
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileOld + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileOld + "'");                  
        }	
		
		return;
	}
}
