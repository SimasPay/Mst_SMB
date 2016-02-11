package com.mfino.mock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * 
 */

/**
 * @author Admin
 *
 */
public class AddressDataJSONFileReading.java {

	private static final String filePath = "I:\\all_new.json";

	public static void main(String[] args) throws Exception {

		StringBuffer sbProvince = new StringBuffer();
		StringBuffer sbRegion = new StringBuffer();
		StringBuffer sbDistrict = new StringBuffer();
		StringBuffer sbVillage = new StringBuffer();

		try {

			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();

			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
			JSONObject obj = (JSONObject) jsonObject.get("indonesia");

			JSONArray prov = (JSONArray) obj.get("province");
			// start of province

			for (int p = 0; p < prov.size(); p++) {

				JSONObject id_province = (JSONObject) prov.get(p);
				// StringBuffer sbProvince = new StringBuffer();

				sbProvince
						.append("INSERT INTO PROVINCE (ID,VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, DISPLAYTEXT, PROVINCEID) VALUES (PROVINCE_ID_SEQ.NEXTVAL,1,SYSDATE,'system',SYSDATE,'system'"
								+ ","
								+ "'"
								+ id_province.get("province_name").toString()
								+ "'"
								+ ","
								+ "'"
								+ id_province.get("id_province").toString()
								+ "');\n");
				sbProvince.append(System.getProperty("line.separator"));

				JSONArray json = (JSONArray) id_province.get("region");

				// region starts here
				for (int reg = 0; reg < json.size(); reg++) {

					JSONObject region = (JSONObject) json.get(reg);
					// System.out.println("size of region :" + region.size());
					// StringBuffer sbRegion = new StringBuffer();

					sbRegion.append("INSERT INTO PROVINCE_REGION (ID,VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, DISPLAYTEXT, REGIONID,BIREGIONID,IDPROVINCE) VALUES (REGION_ID_SEQ.NEXTVAL,1,SYSDATE,'system',SYSDATE,'system'"
							+ ","
							+ "'"
							+ region.get("region_name").toString()
							+ "'"
							+ ","
							+ "'"
							+ region.get("id_region").toString()
							+ "'"
							+ ","
							+ "'"
							+ region.get("id_region_bi").toString()
							+ "'"
							+ ","
							+ "( SELECT ID FROM PROVINCE WHERE DISPLAYTEXT ='"
							+ id_province.get("province_name").toString()
							+ "' AND PROVINCEID ='"
							+ id_province.get("id_province").toString()
							+ "'));\n");

					sbRegion.append(System.getProperty("line.separator"));

					JSONArray json1 = (JSONArray) region.get("district");
					// String districtName;

					for (int d = 0; d < json1.size(); d++) {
						JSONObject dist = (JSONObject) json1.get(d);

						String districtName;
						districtName = dist.get("district_name").toString();						

						if(districtName.indexOf("'") > 0) {

							districtName = districtName.replaceAll("'", "''");

						}

						sbDistrict
								.append("INSERT INTO DISTRICT (ID,VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, DISPLAYTEXT, DISTRICTID,IDREGION) VALUES (DISTRICT_ID_SEQ.NEXTVAL,1,SYSDATE,'system',SYSDATE,'system'"
										+ ","
										+ "'"
										+ districtName
										+ "'"
										+ ","
										+ "'"
										+ dist.get("id_district").toString()
										+ "'"
										+ ","
										+ " (SELECT ID FROM PROVINCE_REGION WHERE DISPLAYTEXT ='"
										+ region.get("region_name").toString()
										+ "' AND REGIONID = '"
										+ dist.get("id_region").toString()
										+ "'));\n");

						sbDistrict.append(System.getProperty("line.separator"));

						try {

							JSONArray village = (JSONArray) dist.get("village");

							String villageName;
							
							for (int j = 0; j < village.size(); j++) {
								
								JSONObject v0 = (JSONObject) village.get(j);

								villageName = v0.get("village_name").toString();						

								if(villageName.indexOf("'") > 0) {

									villageName = villageName.replaceAll("'", "''");
								}
								
								sbVillage
										.append("INSERT INTO VILLAGE (ID,VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, DISPLAYTEXT, VILLAGEID,IDDISTRICT) VALUES (VILLAGE_ID_SEQ.NEXTVAL,1,SYSDATE,'system',SYSDATE,'system'"
												+ ","
												+ "'"
												+ villageName
												+ "'"
												+ ","
												+ "'"
												+ v0.get("id_village")
														.toString()
												+ "'"
												+ ","
												+ "( SELECT ID FROM DISTRICT WHERE DISPLAYTEXT ='"
												+ districtName
												+ "' AND IDREGION = (SELECT ID FROM PROVINCE_REGION WHERE REGIONID = '"
												+ dist.get("id_region")
														.toString() + "')));\n");

								sbVillage.append(System
										.getProperty("line.separator"));

							}// end of village

						} catch (Exception ex) {

							JSONObject village = (JSONObject) dist.get("village");
							// village size starts
							String villageName;
							villageName = village.get("village_name").toString();						

							if(villageName.indexOf("'") > 0) {

								villageName = villageName.replaceAll("'", "''");

							}

							sbVillage
									.append("INSERT INTO VILLAGE (ID,VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, DISPLAYTEXT, VILLAGEID,IDDISTRICT) VALUES (VILLAGE_ID_SEQ.NEXTVAL,1,SYSDATE,'system',SYSDATE,'system'"
											+ ","
											+ "'"
											+ villageName
											+ "'"
											+ ","
											+ "'"
											+ village.get("id_village")
													.toString()
											+ "'"
											+ ","
											+ "( SELECT ID FROM DISTRICT WHERE DISPLAYTEXT ='"
											+ districtName
											+ "' AND IDREGION = (SELECT ID FROM PROVINCE_REGION WHERE REGIONID = '"
											+ dist.get("id_region").toString()
											+ "')));\n");

						}// end of village
					}
				}// end of district
			}// ends region

			// from string buffer to province.txt file

			// String s[]=sbProvince.toString().split(";");
			// File file = new File("I:\\province.txt");
			File file = new File("I:\\province.sql");
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bwr = new BufferedWriter(fw);
			bwr.write(sbProvince.toString());
			bwr.flush();
			bwr.close();

			// end of province.txt file
			// File file1 = new File("I:\\region.txt");
			File file1 = new File("I:\\region.sql");
			FileWriter fw1 = new FileWriter(file1, true);
			BufferedWriter bwr1 = new BufferedWriter(fw1);
			bwr1.write(sbRegion.toString());
			bwr1.flush();
			bwr1.close();
			// end of region.txt file

			// File file2 = new File("I:\\district.txt");
			File file2 = new File("I:\\district.sql");
			FileWriter fw2 = new FileWriter(file2, true);
			BufferedWriter bwr2 = new BufferedWriter(fw2);
			bwr2.write(sbDistrict.toString());
			bwr2.flush();
			bwr2.close();

			// end of district.txt file
			// File file3 = new File("I:\\village.txt");
			File file3 = new File("I:\\village.sql");
			FileWriter fw3 = new FileWriter(file3, true);
			BufferedWriter bwr3 = new BufferedWriter(fw3);
			bwr3.write(sbVillage.toString());
			bwr3.flush();
			bwr3.close();

		} catch (Exception ex) {
			System.out.print(ex);
		}

	}

}