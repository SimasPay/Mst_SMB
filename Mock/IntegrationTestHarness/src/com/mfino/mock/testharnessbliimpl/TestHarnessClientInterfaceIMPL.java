/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mock.testharnessbliimpl;

import com.mfino.mock.testharnessbli.TestHarnessBLI;
import com.mfino.mock.testharnessbli.TestHarnessClient;

/**
 *
 * @author sunil
 */
public class TestHarnessClientInterfaceIMPL implements TestHarnessClient {

    public void TestHarnessClient(TestHarnessValueObject input_vo, TestHarnessValueObject output_vo){
        TestHarnessBLI test_harness=TestHarnessBLIFactoryIMPL.createTestHarnessBLI(TestHarnessBLIFactoryIMPL.HarnessType.Gemalto);

        if((input_vo.getSMS_serviceName()).equalsIgnoreCase("subsricption_activation")){
                test_harness.subsricption_activation(input_vo);

        }else if((input_vo.getSMS_serviceName()).equalsIgnoreCase("change_mpin")){
                test_harness.change_pin(input_vo);

        }else if((input_vo.getSMS_serviceName()).equalsIgnoreCase("mpin_reset")){
                test_harness.reset_pin(input_vo);

        }else if((input_vo.getSMS_serviceName()).equalsIgnoreCase("last_3_transaction")){
                test_harness.get_transactions(input_vo);

        }else if((input_vo.getSMS_serviceName()).equalsIgnoreCase("topup")){
                test_harness.mobile_Agent_Recharge(input_vo);

        }else if((input_vo.getSMS_serviceName()).equalsIgnoreCase("topup_merchant")){
                test_harness.mcash_topup(input_vo);

        }else if((input_vo.getSMS_serviceName()).equalsIgnoreCase("chec_Inventory")){
                test_harness.check_balance(input_vo);

        }else if((input_vo.getSMS_serviceName()).equalsIgnoreCase("change_MerchantPin")){
                test_harness.change_mcash_Pin(input_vo);

        }else if((input_vo.getSMS_serviceName()).equalsIgnoreCase("merchant_mpin_reset")){
                test_harness.reset_pin(input_vo);

        }else {
            return;

        }




    }



}
