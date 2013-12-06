package com.mfino.domain;

import java.math.BigDecimal;

public class LedgerBalance {
	private BigDecimal balance;

	private String balanceType;

	private Long pocketId;

	public LedgerBalance(BigDecimal balance, Long pocketId, String balanceType) {
		this.balance = balance;
		this.pocketId = pocketId;
		this.balanceType = balanceType;
	}

	@Override
	public String toString() {
		return "LedgerBalance [balance=" + balance + ", balanceType="
				+ balanceType + ", pocketId=" + pocketId + "]";
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((balance == null) ? 0 : balance.hashCode());
		result = prime * result
				+ ((balanceType == null) ? 0 : balanceType.hashCode());
		result = prime * result
				+ ((pocketId == null) ? 0 : pocketId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LedgerBalance other = (LedgerBalance) obj;
		if (balance == null) {
			if (other.balance != null)
				return false;
		} else if (!balance.equals(other.balance))
			return false;
		if (balanceType == null) {
			if (other.balanceType != null)
				return false;
		} else if (!balanceType.equals(other.balanceType))
			return false;
		if (pocketId == null) {
			if (other.pocketId != null)
				return false;
		} else if (!pocketId.equals(other.pocketId))
			return false;
		return true;
	}

	public String getBalanceType() {
		return balanceType;
	}

	public void setBalanceType(String balanceType) {
		this.balanceType = balanceType;
	}

	public Long getPocketId() {
		return pocketId;
	}

	public void setPocketId(Long pocketId) {
		this.pocketId = pocketId;
	}

}
