/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mock.testharnessbli;

import com.mfino.mock.testharnessbliimpl.TestHarnessValueObject;

/**
 *
 * @author sunil
 */
public interface TestHarnessBLI {

        boolean subsricption_activation(TestHarnessValueObject input);

	boolean change_pin (TestHarnessValueObject input);

	boolean reset_pin (TestHarnessValueObject input);

	boolean get_transactions (TestHarnessValueObject input);

        boolean check_balance (TestHarnessValueObject input);

	boolean change_mcash_Pin (TestHarnessValueObject input);

	boolean mobile_Agent_Recharge (TestHarnessValueObject input);

	boolean mcash_topup (TestHarnessValueObject input);
	
	boolean get_mcash_transactions (TestHarnessValueObject input);

        boolean mobile_agent_distribute (TestHarnessValueObject input);

        boolean share_load (TestHarnessValueObject input);

        boolean mcash_to_mcash (TestHarnessValueObject input);

        boolean mcash_balance_inquiry (TestHarnessValueObject input);

        boolean frequency_test  (int selectionList, int totalNumberOfRequests);

        boolean merchant_mpin_reset ( TestHarnessValueObject input);

}
