//package net.codejava.mail;
 
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

public class ReceiveMail {
	
    /**
     * Returns a Properties object which is configured for a POP3/IMAP server
     *
     * @param protocol either "imap" or "pop3"
     * @param host
     * @param port
     * @return a Properties object
     */

	private Properties getServerProperties(String protocol, String host,
            String port) {
        Properties properties = new Properties();
 
        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);
 
        // SSL setting
        properties.setProperty(
                String.format("mail.%s.socketFactory.class", protocol),
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(
                String.format("mail.%s.socketFactory.fallback", protocol),
                "false");
        properties.setProperty(
                String.format("mail.%s.socketFactory.port", protocol),
                String.valueOf(port));
 
        return properties;
    }
 
    /**
     * Downloads new messages and fetches details for each message.
     * @param protocol
     * @param host
     * @param port
     * @param userName
     * @param password
     * @throws IOException 
     */
    @SuppressWarnings("unused")
	public Queue<String> downloadEmails(String protocol, String host, String port, String userName, String password) throws IOException {
        Properties properties = getServerProperties(protocol, host, port);
        Session session = Session.getDefaultInstance(properties);
        
        //session.setDebug(true);
        
        Queue<String> queue = new LinkedList<String>();
        
 
        try {
            // connects to the message store
            Store store = session.getStore(protocol);
            store.connect(userName, password);
 
            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            //folderInbox.open(Folder.READ_ONLY);
            folderInbox.open(Folder.READ_WRITE);
 
            // fetches new messages from server
            Message[] messages = folderInbox.getMessages();
 
            for (int i = 0; i < messages.length; i++) {
                Message msg = messages[i];
                Address[] fromAddress = msg.getFrom();
                String from = fromAddress[0].toString();
                
                //
                //if it's not a quote message, skip to the next message
                if (!from.equals("\"Worldofquotes.com - Quote of the Day\" <dailyquote@worldofquotes.com>")){
                	continue;
                }
                else{
                	//msg.setFlag(Flags.Flag.DELETED, true);
                }
                //
                
                String subject = msg.getSubject();
                String toList = parseAddresses(msg
                        .getRecipients(RecipientType.TO));
                String ccList = parseAddresses(msg
                        .getRecipients(RecipientType.CC));
                String sentDate = msg.getSentDate().toString();
 
                String contentType = msg.getContentType();
                String messageContent = "";
 
                
                if (contentType.contains("text/plain") || contentType.contains("text/html")) {
                    try {
                        Object content = msg.getContent();
                        if (content != null) {                        	                                           	
                            messageContent = content.toString();
                        }
                    } catch (Exception ex) {
                        messageContent = "[Error downloading content]";
                        ex.printStackTrace();
                    }
                }
                
                //
                else if (contentType.contains("multipart")) {
                	

                	//System.out.println("XXXX");
                	
                    Multipart mp = (Multipart)msg.getContent();
                    //int numParts = mp.getCount();
                    
                    //
                    MimeBodyPart part = (MimeBodyPart)mp.getBodyPart(0);
                    messageContent = part.getContent().toString();
                    
                    //System.out.println(messageContent);
                   // System.out.println(sentDate);
                    
                    //quote
                    int Qstart = messageContent.indexOf("8220;")+5;
                    int Qstop = messageContent.indexOf("&#8221");
                    queue.add(messageContent.substring(Qstart,Qstop).trim());
                    
                    //author
                    int Astart = messageContent.indexOf("index.html\">")+12;
                    int Astop = messageContent.indexOf("</a>",Astart);
                    queue.add(messageContent.substring(Astart,Astop).trim());
                    
                    //
                    
                    /*
                    for(int count = 0; count < numParts; count++) {
                        MimeBodyPart part = (MimeBodyPart)mp.getBodyPart(count);
                        messageContent = messageContent + part.getContent().toString();
                        
                    }
                    */
                }
                //
                               
 
                // print out details of each message
                /*
                System.out.println("Message #" + (i + 1) + ":");
                System.out.println("\t From: " + from);
                System.out.println("\t To: " + toList);
                System.out.println("\t CC: " + ccList);
                System.out.println("\t Subject: " + subject);
                System.out.println("\t Sent Date: " + sentDate);
                System.out.println("\t Message: " + messageContent);
                */
                
            }
            
            /*
            while (!queue.isEmpty()){
            	System.out.println(queue.remove());
            	System.out.println("===");
            }
            */
            
 
            // disconnect
            folderInbox.close(true);
            store.close();
            
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for protocol: " + protocol);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        }
        
        return queue;
        
    }
 
    /**
     * Returns a list of addresses in String format separated by comma
     *
     * @param address an array of Address objects
     * @return a string represents a list of addresses
     */
    private String parseAddresses(Address[] address) {
        String listAddress = "";
 
        if (address != null) {
            for (int i = 0; i < address.length; i++) {
                listAddress += address[i].toString() + ", ";
            }
        }
        if (listAddress.length() > 1) {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }
 
        return listAddress;
    }
 
    /**
     * Test downloading e-mail messages
     * @throws IOException 
     */

    
    public static void main(String[] args) throws IOException {
        // for POP3
        String protocol = "pop3";
        String host = "pop-mail.outlook.com";
        String port = "995";
 
        String userName = "joreyjorey@live.com";
        String password = ":)";
 
        //ReceiveMail receiver = new ReceiveMail();
        //receiver.downloadEmails(protocol, host, port, userName, password);
    }
    

	
	
}
