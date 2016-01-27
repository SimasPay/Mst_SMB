/*package com.mfino.rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mfino.transaction.TransactionBo;

@Component
@Path("/payment")
public class PaymentService {

	@Autowired
	TransactionBo transactionBo;

	@GET
	@Path("/testGet")
	public Response testGet() {
		//http://localhost:8080/RESTfulExample/rest/payment/testGet
		System.out.println("somebody invoked testGet method");
		String result = "Test for GET method on Restfull Webservice through Browser is successful";
		return Response.status(200).entity(result).build();
	}
	
	  @POST
	  @Path("/inoutJsonPost")
	  @Produces(MediaType.APPLICATION_JSON)
	  @Consumes(MediaType.APPLICATION_JSON)
	  public JSONObject inoutJsonPost(JSONObject inputJsonObj) throws Exception {
		  //https://localhost:8443/RESTfulExample/rest/payment/inoutJsonPost
		System.out.println("somebody invoked inoutJsonPost method");
	    String tutorials = (String) inputJsonObj.get("input");
	    System.out.println("json request received is: "+tutorials);
	    String output = "The input data you sent in request is: " + tutorials;
	    JSONObject outputJsonObj = new JSONObject();
	    outputJsonObj.put("output", output);

	    return outputJsonObj;
	  }
	  
		@POST
		@Path("/inStreamOutJsonPost")
		@Produces(MediaType.APPLICATION_JSON)
		//@Consumes(MediaType.APPLICATION_JSON)
		public Response inStreamOutJsonPost(InputStream incomingData) throws Exception {
			//https://localhost:8443/RESTfulExample/rest/payment/inStreamOutJsonPost
			System.out.println("somebody has invoked inStreamOutJsonPost service");
			StringBuilder sBuilder = new StringBuilder();
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
				String str = null;
				while ((str = in.readLine()) != null) 
				{
					sBuilder.append(str);
					JSONObject jObj = new JSONObject(str);
					String mdn = jObj.getString("mdn").trim();
					System.out.println("mdn received in ws is: "+mdn);
				}
			} catch (Exception e) {
				System.out.println("Error in Parsing");
			}
			System.out.println("Data Received in hPay: " + sBuilder.toString());
			
			JSONObject outputJsonObj = new JSONObject();
			outputJsonObj.put("requestId", "1234567890000000000");
			outputJsonObj.put("status", "Success");
			outputJsonObj.put("walletId", "44444444444444444");
			
			System.out.println("Response sent is: "+outputJsonObj.toString());
			// return HTTP response 200 in case of success
			return Response.status(200).entity(outputJsonObj.toString()).build();
		}
}*/

package com.mfino.rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.springframework.stereotype.Component;



@Component
@Path("/")
public class KTPValidationService {

	

	//@POST
	@GET
	@Path("/testGet")
	public Response testGet() {
		//for testing Restful webservices use below URL:-
		//http://localhost:8078/KTPMock/testGet
		System.out.println("somebody invoked testGet method");
		String result = "Test for GET method on Restfull Webservice through Browser is successful";
		return Response.status(200).entity(result).build();
	}
	
	  	  
		@POST
		@Path("/validate")
		@Produces(MediaType.APPLICATION_JSON)		
		public Response Validate(InputStream incomingData) throws Exception {
			//https://localhost:8444/KTPMock/validate
			System.out.println("somebody has invoked KTPValidation service");
			StringBuilder sBuilder = new StringBuilder();
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
				String str = null;
				while ((str = in.readLine()) != null) 
				{
					sBuilder.append(str);
				}
			} catch (Exception e) {
				System.out.println("Error in Parsing");
			}
						
			JSONObject outputJsonObj = new JSONObject();			
			outputJsonObj.put("tempatlahir","JAKARTHA");
			outputJsonObj.put("namaibukandung", "Jasodha");
			outputJsonObj.put("alamat", "Jakartha-7,Indonesia");
			outputJsonObj.put("rt", "444");
			outputJsonObj.put("rw","786");
			outputJsonObj.put("kelurahan","JAKARTHA-Subdistrict");
			outputJsonObj.put("kecamatan", "JAKARTHA-district");
			outputJsonObj.put("kota", "city-Indonesia");
			outputJsonObj.put("provinsi", "Indonesia-province");
			outputJsonObj.put("kodepos","postalcode-786");
			outputJsonObj.put("responsemessage", "success");
			outputJsonObj.put("responsecode","00");
			
			
			System.out.println("Response sent is: "+outputJsonObj.toString());
			// return HTTP response 200 in case of success
			return Response.status(200).entity(outputJsonObj.toString()).build();
		}
}