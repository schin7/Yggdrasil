package com.yggdrasil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.upgma.cluster.Cluster;
import com.upgma.cluster.DNASequence;
import com.upgma.cluster.DisMatrix;

/**
 * Servlet implementation class FastaReader
 */
@WebServlet("/FastaReader")
public class FastaReader extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FastaReader() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		// Store DNA Sequences in fasta file 
		StringBuilder sequence = new StringBuilder();
		ArrayList<DNASequence> sequenceList = new ArrayList<DNASequence>();
		int seqNum = 0;
		
		// Parse FASTA format file for headers staring with ">" using Regexp
		String header = "";
		String headerStart = ">";
		Pattern p = Pattern.compile(headerStart);
		Matcher m;
		
		 try {
			 	// Get uploaded file 
		        FileItemFactory itemFactory = new DiskFileItemFactory();
		        ServletFileUpload upload = new ServletFileUpload(itemFactory);
		        
		        List<FileItem> items = upload.parseRequest(request);
		        
		        for (FileItem item : items) {		            		    
		        	
		                // Read text from file line by line                
		                InputStream fileContent = item.getInputStream();		                
		                BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent));		                		           
		                String line;
		                
		                while ((line = reader.readLine()) != null) {
		                    
		                	m = p.matcher(line);	//Check if line is a header
		                    
		                	// If it's not a header, it's a sequence
		                    if(m.lookingAt() == false){
		                    	sequence.append(line);
		                    }
		                    else{
		                    	// Create a new DNASequence object using the header and sequence
		                    	if(seqNum != 0){
		                    		sequenceList.add((seqNum-1), new DNASequence(header, sequence.toString())); 
		                    		sequence.setLength(0);	// empty the sequence string for next sequence
		                    	}
		                    	header = line;	// read in next header
		                    	seqNum++;		                    	
		                    }		                	  		                    		                   
		                }
		                sequenceList.add((seqNum-1), new DNASequence(header, sequence.toString()));	// capture the final sequence	
	            		            
		        }
		        
		    } catch (FileUploadException e) {
		        throw new ServletException("Cannot parse multipart request.", e);
		    }
		
		 
		 out.println("Clustering your sequences...");
		 
		 DisMatrix dmatrix = new DisMatrix(sequenceList);	// Generate a dissimilarity Matrix
		 dmatrix.debug();
		 double[][] scores = dmatrix.calcScores();
			
		 Cluster cluster = new Cluster(sequenceList); // Cluster nearest neighbors in matrix
		 cluster.joinNearest(scores);
			
		 
	}

}