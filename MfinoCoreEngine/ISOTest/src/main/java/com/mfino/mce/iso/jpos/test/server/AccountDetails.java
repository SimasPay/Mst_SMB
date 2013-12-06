package com.mfino.mce.iso.jpos.test.server;
import java.io.*;
/**
 * 
 * @author Sreenath
 *
 */
public class AccountDetails {



  //send the latest balance after the last transaction to calling class
  public synchronized String getBalance(String id) throws Exception{
	    File customerDataFile;
		String customerFileDirectory = Configuration.getConfigObject().getPath();
		String customerFileName = id+".txt";
		File dir = new File(customerFileDirectory);
		customerDataFile= new File(dir,customerFileName);
		BufferedReader in = new BufferedReader(new FileReader(customerDataFile));
		String fileLineIterator;
		String[] fileLineReader = null;
		while((fileLineIterator=in.readLine())!=null){
			fileLineReader = fileLineIterator.split(",");
		}
        return fileLineReader[4];//4 since balance is stored in  the fourth place in the file
  }
  
  //getting the transaction history and formatting in the required output format
  public synchronized String getHistory(String id) throws IOException{
	    File customerDataFile;
		String customerFileDirectory = Configuration.getConfigObject().getPath();//"D:\\Project\\Bank";
		String customerFileName = id+".txt";
		File dir = new File(customerFileDirectory);
		customerDataFile= new File(dir,customerFileName);
	    BufferedReader in = new BufferedReader(new FileReader(customerDataFile));
	    //size for different output fields form isomsg filed 48
	    //accNo=28,date=14,amount=12, stan=6;
	    int accNo=28;
		String fileLineIterator;
		String[] fileLineReader = null;
		String miniStatementHolder = "" ;
		String dataSizeChanger = null;
		
		while((fileLineIterator=in.readLine())!=null){
			fileLineReader = fileLineIterator.split(",");
			dataSizeChanger = fileLineReader[0];
			while(dataSizeChanger.length()<accNo){
				dataSizeChanger = "0"+dataSizeChanger;
			}
			fileLineReader[0]=dataSizeChanger;
			//Selecting the required data fields from the text file
			miniStatementHolder = miniStatementHolder+fileLineReader[0]+"|"+fileLineReader[1]+"|"+(fileLineReader[2].equals(Constants.DEFAULT_AMOUNT)?fileLineReader[3]:fileLineReader[2])+"|"+fileLineReader[5];
			miniStatementHolder=miniStatementHolder+"~";
		}
		in.close();
	return miniStatementHolder;
	  
  }
  //access the file in thread safe way
  public synchronized void createFile(TrxnDetails trxn) throws Exception{
	  	BufferedWriter output;
		String id = trxn.getId();
		File customerDataFile;
		String customerFileDirectory = Configuration.getConfigObject().getPath();//"D:\\Project\\Bank";
		String customerFileName = id+".txt";
		File dir = new File(customerFileDirectory);
		customerDataFile= new File(dir,customerFileName);
		//Writing to a file
		if(!customerDataFile.exists()){                 //creating a non-existent file
			customerDataFile.createNewFile();
			//----------------------------------------------------
			//Enter 1st transaction record to new customer file
			//String[] inData = {id,date,credit,debit,ibalance.toString(),stan};
			output = new BufferedWriter(new FileWriter(customerDataFile));
			String dataToBeWritten =trxn.getId()+","+trxn.getDate()+","+Constants.DEFAULT_AMOUNT+","+Constants.DEFAULT_AMOUNT+","+
						trxn.getInitBalance()+","+trxn.getStan();
			output.write(dataToBeWritten);
			output.close();
		}
		// 31 -- check balance
		// 38 -- history 
		else if(trxn.getTrxnType().equals(Constants.CHECK_BALANCE) || trxn.getTrxnType().equals(Constants.HISTORY)){
			//for check balance and history no need to add new data
			return;
		}
		else{       //appending data to already existing file
			//Reading no of transaction records present
			int numberOfTransactionsInFile=1;
			BufferedReader in = new BufferedReader(new FileReader(customerDataFile));
			String fileLineIterator;
			while((fileLineIterator=in.readLine())!=null){
					numberOfTransactionsInFile++;
			}
			//-------------------------------------------------
			//when transaction limit is reached delete first line and move rest up
			if(trxn.getTrxnNo()<numberOfTransactionsInFile){
				RandomAccessFile randomAccessorToCustomerFile = new RandomAccessFile(customerDataFile,"rw");
				long writePosition = randomAccessorToCustomerFile.getFilePointer();
				randomAccessorToCustomerFile.readLine();
				long readPosition = randomAccessorToCustomerFile.getFilePointer();
				byte[] dataBuffer =new byte[1024];
				int endOfFileChecker;
				while(-1!=(endOfFileChecker=randomAccessorToCustomerFile.read(dataBuffer))){
					randomAccessorToCustomerFile.seek(writePosition);
					randomAccessorToCustomerFile.write(dataBuffer,0,endOfFileChecker);
					readPosition+=endOfFileChecker;
					writePosition+=endOfFileChecker;
					randomAccessorToCustomerFile.seek(readPosition);
				}
				randomAccessorToCustomerFile.setLength(writePosition);
				randomAccessorToCustomerFile.close();
			}
			//--------------------------------------------------------------
			//Input new transaction data into file
				output = new BufferedWriter(new FileWriter(customerDataFile,true));
				output.newLine();
				Integer balance = Integer.parseInt(getBalance(id));
				balance=balance + Integer.parseInt(trxn.getCredit())-Integer.parseInt(trxn.getDebit());
				//making the balance of length 12
				String bal = balance.toString();
				while(bal.length()<12){
					bal = "0"+bal;
				}

				String dataToBeWritten =trxn.getId()+","+trxn.getDate()+","+trxn.getCredit()+","+trxn.getDebit()+","+
							bal+","+trxn.getStan();
				output.write(dataToBeWritten);
				output.close();
		}
}

}
	


