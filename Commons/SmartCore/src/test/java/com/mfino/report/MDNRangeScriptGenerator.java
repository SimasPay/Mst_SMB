/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import com.mfino.dao.BrandDAO;
import com.mfino.dao.MerchantDAO;
import com.mfino.domain.Brand;
import com.mfino.domain.Merchant;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author Raju
 */
public class MDNRangeScriptGenerator {

    public static void main(String args[]) throws Throwable {
        //before u run make sure u have a record in company
        HibernateUtil.getCurrentSession().beginTransaction();
        MerchantDAO dao = new MerchantDAO();
        //MerchantQuery query = new MerchantQuery();
        int i = 100;
        List<Merchant> results = dao.getAll();
        String filePath = "C:\\Script" + ".sql";
        File MerchantFile = new File(filePath);
        PrintWriter out = new PrintWriter(new FileWriter(MerchantFile));
        System.out.println("results.size()" + results.size());
        long brandid = 0;
        out.println("insert into brand values (10,1,now(),'user',now(),'user',1,62,881,'SMART',1);");
        BrandDAO bdao = new BrandDAO();
        for (Merchant merchant : results) {
            Merchant parent = merchant.getMerchantByParentID();
            if (parent != null) {
                //String mdn = MerchantService.getMDNFromMerchant(merchant);
            	String mdn = null;
                List<Brand> brandresults = bdao.getAll();
                if (brandresults.size() > 0) {
                     brandid = 0;
                    for (int k = 0; k < brandresults.size(); k++) {
                        if (mdn.startsWith("62" + brandresults.get(k).getPrefixCode())) {
                            brandid = brandresults.get(k).getID();
                            break;
                        } 
                    }
                    if(brandid == 0 && mdn.length() > 5){
                        out.println("insert into brand values (" + i + ",1,now(),'user',now(),'user',1,62,"+mdn.substring(2, 5)+",'SMART',1);");
                        brandid = i;
                    }
                }
                if (mdn.contains("R") || mdn.length() < 5) {
                    continue;
                }
                String maximum = mdn.substring(5);
                if (merchant != null) {
                    i++;
                    if (maximum.length() > 0) {
                        out.println("Insert into mdn_range values( " + i + ", 1,now(),'user',now(),'user'," + parent.getID() + ",0," + maximum + "," + brandid + ");");
                    }
                }
            }
        }
        HibernateUtil.getCurrentTransaction().commit();
        out.close();
    }
}
