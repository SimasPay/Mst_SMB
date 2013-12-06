
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

public class RemoveCommentsFromSource {
	private String dir; 
	private File changedFilesList;
	private PrintWriter changedFileWriter;
	private static int changeFileCount = 0;
	private Stack<File> directoryList = new Stack<File>();
	private static char ch ='"';
	      
    public RemoveCommentsFromSource(String dir) {
		super();
		this.dir = dir;
	}
    
    public void processComments() throws Exception {
    		
    	changedFilesList = new File("changedFilesList");
    	changedFileWriter = new PrintWriter(new FileWriter(changedFilesList));
    	changedFileWriter.println("List of changed files ");
    	changedFileWriter.println("........................ ");
    	changedFileWriter.println();
    	String dr=dir;
    	directoryList.push(changedFilesList);//to avoid directoryList.isEmpty()is true before processing last directory
    	do
    	{
    	processDir(dr);
    	dr=directoryList.pop().getAbsolutePath();
    	}while(!directoryList.isEmpty());
    	    	
    	changedFileWriter.println("........................ ");
    	changedFileWriter.println();
    	changedFileWriter.println("Total changed Files : "+changeFileCount);    	
    	changedFileWriter.close();
    	}

	private void processDir(String dirctory) throws Exception {
		
			int j=0;
			try{
			File folder = new File(dirctory);
    	    File[] fileList = folder.listFiles();
    	    for (int i = 0; i < fileList.length; i++) {
    	    	 if (fileList[i].isFile()) {
    	          String file= fileList[i].getAbsolutePath();
    	          if(file.endsWith(".java")){
    	        	  if(j==0){
      	        		String path = folder.getAbsolutePath();
      	        		int index = path.indexOf("com");
      	        		if(index != -1){
      	        		changedFileWriter.println();	
      	        		String packageName = path.substring(index);
      	        		packageName = packageName.replace('\\', '.');
      	        		packageName = packageName.replace('/', '.');
      	        		String direc = path.substring(0,index-1);
      	        		direc = direc.substring(path.indexOf(dir)+dir.length()+1);
      	        		changedFileWriter.println("Directory :"+ direc);
      	        		changedFileWriter.println("Package: "+ packageName);
      	        		changedFileWriter.println("Files : ");
      	        		j++;
      	        		}
      	        	  }
    	        	  changeFileCount++;
    	        	  removeCommentsFromFile(file);
    	        	  continue;
    	        	 }
    	          else if(file.endsWith(".cpp")){
    	        	  if(j==0){
    	        		 String direc = folder.getAbsolutePath();
    	        		 changedFileWriter.println();
    	        		 changedFileWriter.println("Directory :"+ direc.substring(direc.indexOf(dir)+dir.length()+1));
    	        		 changedFileWriter.println("Files : ");
    	        		 j++;
    	        		 }
    	        	  changeFileCount++;
    	        	  removeCommentsFromFile(file);
    	        	  continue;
    	        	  }
    	          } 
    	    	 else if (fileList[i].isDirectory()){
    	    		 directoryList.push(fileList[i]);
    	    		 }
    	    	 }
    	    }catch (NullPointerException e)
    	    {
			System.out.println("Directory not found in current directory or provide absolute path of directory");
			}
    	    }
	
    public void removeCommentsFromFile(String file) {
    	
        try
        {
        	File inFile = new File(file);
        	changedFileWriter.println(changeFileCount+". "+inFile.getName());
      	  	System.out.println(changeFileCount+inFile.getName());
          
      	  	File tempFile = new File(inFile.getAbsolutePath() + ".tmp");
      	  	BufferedReader br = new BufferedReader(new FileReader(inFile));
      	  	PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
          
      	  	String line = null;
      	  	while ((line = br.readLine()) != null){
      	  		if(line.indexOf("#pragma")!=-1){
      	  			if(line.indexOf(ch)!=-1){
      	  				pw.println(line);
      	  				pw.flush();
      	  				}
      	  			continue;
      	  			}
      	  		//Removing single line comment
      	  		if(line.indexOf("//")!=-1){
      	  			line = line.trim();
      	  			int index=line.indexOf("//");
      	  			if(line.indexOf(ch)==-1 || index<line.indexOf(ch)){
      	  				if(index!=0){
      	  					line=line.substring(0, index);
      	  					pw.println(line);
      	  					pw.flush();
      	  					}
      	  				continue;
      	  				}
      	  			else{
      	  				int count = 0;
      	  				int index3=index;
      	  				while(line.lastIndexOf(ch, index3)!=-1){
      	  					index3 = line.lastIndexOf(ch, index3)-1;
      	  					count++;
      	  					}
      	  				if(count%2==0){
      	  					line=line.substring(0, index);
      	  					pw.println(line);
      	  					pw.flush();
      	  					continue;
      	  					}
      	  				else{
      	  					pw.println(line);
      	  					pw.flush();
      	  					continue;
      	  					}
      	  				}
      	  			}
      	  		//Removing multiline comments 
      	  		if (line.indexOf("/*")!=-1) {
      	  			int index2=line.indexOf("/*");
      	  			if(line.indexOf(ch)==-1 || index2<line.indexOf(ch)){
      	  				processLine(index2, line, br, pw, inFile);
      	  				continue;
      	  				}
      	  			else{
      	  				int count = 0;
      	  				int index3=index2;
      	  				while(line.lastIndexOf(ch, index3)!=-1){
      	  					index3 = line.lastIndexOf(ch, index3)-1;
      	  					count++;
      	  					}
      	  				if(count%2==0){
      	  					processLine(index2, line, br, pw,inFile);
      	  					continue;
      	  					}
      	  				else{
      	  					pw.println(line);
      	  					pw.flush();
      	  					continue;
      	  					}
      	  				}
      	  			}
      	  		pw.println(line);
      	  		pw.flush();
      	  		}
      	  	pw.close();
      	  	br.close();
      	  	
      	  	if (!inFile.delete()) {
      	  		System.out.println("Could not delete file :"+inFile.getName());            
      	  		return;
      	  		}
      	  	
      	  	if (!tempFile.renameTo(inFile)){
      	  		System.out.println("Could rename tem file to:"+inFile.getName());
      	  		tempFile.delete();
      	  		}
      	  	
      	  	}catch (FileNotFoundException ex)
      	  	{
      	  		ex.printStackTrace();
      	  		}
      	  	catch (IOException ex) {
      	  		ex.printStackTrace();
      	  		}
      	  	catch (Exception ex) {
      	  		ex.printStackTrace();
      	  		}
      	  	}
    
    private void processLine(int index,String line,BufferedReader br,PrintWriter pw,File file)throws IOException{
    	
    	if(index != 0)
    	{
    		String noncomment=line.substring(0,index);
    		noncomment=noncomment.trim();
    		if(!noncomment.isEmpty()){
    			pw.println(noncomment);
    			pw.flush();
    			}
    		}
    	while(line.indexOf("*/")==-1)
    	{
    		line=br.readLine();
    		}
    	index = line.indexOf("*/")+2;
    	if(index != line.length())
    	{
    		line = line.substring(index);
    		pw.println(line);
    		pw.flush();
    		}
    	}
    
    public static void main(String[] args) throws Exception {
    	try {
    		if(args.length < 1){
    			throw new RuntimeException("Please provide directory path");
    			}  		
            RemoveCommentsFromSource remauth = new RemoveCommentsFromSource(args[0]);
            remauth.processComments();
            System.exit(0);
            } catch (Exception ex)
            {
            	System.out.print(ex.toString());
            	ex.printStackTrace(System.out);
            	System.exit(1);
            	}
            }
    }
