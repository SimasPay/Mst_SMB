package com.mfino.mce.iso.jpos.test.server;
import java.io.File;
import java.io.IOException;
import org.jpos.q2.Q2;


public class ISOJPOSServer 
{
	public static void main(String[] args) throws IOException 
	{

		final Q2 q2 = new Q2("config/");
		q2.start();
		Configuration obj = Configuration.getConfigObject();

		//setting the user defined path, balance and no of transactions to be stored
		//stop execution if input parameters is not like a key value pair i.e is not an even no
		if(args.length==1||args.length==3||args.length==5||args.length==7){
			if(!args[0].contains("-")){
				System.err.println("invalid argument without - .running in default mode.");
			}else{
			System.err.println("invalid number of arguments");
			System.exit(0);
			}
		}else if(args.length>=2){//setting the 1st parameter is exists
			if(args[0].equals("-trxnNo")){
				try{
					obj.setTrxnNo(Integer.parseInt(args[1]));
				}catch(NumberFormatException e){
					System.err.println("error="+e);
					System.exit(0);
				}
			}else if(args[0].equals("-path")){
				File file = new File(args[1]);
				boolean exists = file.exists();
				if(!exists){
					System.err.println("entered directory doesnt exist.");
					System.exit(0);
					
				}else{
					obj.setPath(args[1]);
				}
			}else if(args[0].equals("-balance")){
				try{
					obj.setInitialBalance(Integer.parseInt(args[1]));
				}catch(NumberFormatException e){
					System.err.println("error="+e);
					System.exit(0);
				}
			}else if(args[0].equals("-mode")){
				if(args[1].equals("on")||args[1].equals("off")){
				obj.setMockMode(args[1]);
				}else{
					System.err.println("mode parameter invalid.set it to either on or off");
					System.exit(0);
				}
			}else{
				System.err.println("input parameter type not defined");
				System.exit(0);
			}
			
		}
		if(args.length>=4){//setting 2nd parameter if exists
			if(args[2].equals("-trxnNo")){
				try{
					obj.setTrxnNo(Integer.parseInt(args[3]));
				}catch(NumberFormatException e){
					System.err.println("error="+e);
					System.exit(0);
				}
			}else if(args[2].equals("-path")){
				File file = new File(args[3]);
				boolean exists = file.exists();
				if(!exists){
					System.err.println("entered directory doesnt exist.");
					System.exit(0);
					
				}else{
					obj.setPath(args[3]);
				}
			}else if(args[2].equals("-balance")){
				try{
					obj.setInitialBalance(Integer.parseInt(args[3]));
				}catch(NumberFormatException e){
					System.err.println("error="+e);
					System.exit(0);
				}
			}else if(args[2].equals("-mode")){
				if(args[3].equals("on")||args[1].equals("off")){
				obj.setMockMode(args[3]);
				}else{
					System.err.println("mode parameter invalid.set it to either on or off");
					System.exit(0);
				}
			}else{
				System.err.println("input parameter type not defined");
				System.exit(0);
			}

		}
		if(args.length>=6){//setting 3rd parameter if exists
			if(args[4].equals("-trxnNo")){
				try{
					obj.setTrxnNo(Integer.parseInt(args[5]));
				}catch(NumberFormatException e){
					System.err.println("error="+e);
					System.exit(0);
				}
			}else if(args[4].equals("-path")){
				File file = new File(args[5]);
				boolean exists = file.exists();
				if(!exists){
					System.err.println("entered directory doesnt exist.");
					System.exit(0);
					
				}else{
					obj.setPath(args[5]);
				}
			}else if(args[4].equals("-balance")){
				try{
					obj.setInitialBalance(Integer.parseInt(args[5]));
				}catch(NumberFormatException e){
					System.err.println("error="+e);
					System.exit(0);
				}
			}else if(args[4].equals("-mode")){
				if(args[5].equals("on")||args[1].equals("off")){
				obj.setMockMode(args[5]);
				}else{
					System.err.println("mode invalid.set it to either on or off");
					System.exit(0);
				}
			}else{
				System.err.println("input parameter type not defined");
				System.exit(0);
			}
			

		}
		if(args.length>=8){//setting 4rd parameter if exists
			if(args[6].equals("-trxnNo")){
				try{
					obj.setTrxnNo(Integer.parseInt(args[7]));
				}catch(NumberFormatException e){
					System.err.println("error="+e);
					System.exit(0);
				}
			}else if(args[6].equals("-path")){
				File file = new File(args[7]);
				boolean exists = file.exists();
				if(!exists){
					System.err.println("entered directory doesnt exist.");
					System.exit(0);
					
				}else{
					obj.setPath(args[7]);
				}
			}else if(args[6].equals("-balance")){
				try{
					obj.setInitialBalance(Integer.parseInt(args[7]));
				}catch(NumberFormatException e){
					System.err.println("error="+e);
					System.exit(0);
				}
			}else if(args[6].equals("-mode")){
				if(args[7].equals("on")||args[1].equals("off")){
				obj.setMockMode(args[7]);
				}else{
					System.err.println("mode parameter invalid.set it to either on or off");
					System.exit(0);
				}
			}else{
				System.err.println("input parameter type not defined");
				System.exit(0);
			}
			

		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                q2.stop();
            }
        });
	}
}
