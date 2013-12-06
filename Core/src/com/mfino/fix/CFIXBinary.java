package com.mfino.fix;

/**
 * Summary description for CFIXBinary.
 */
public class CFIXBinary {

	public CFIXBinary(String Value) {
		m_pData = CBase64.FromBase64(Value);
	}

	public CFIXBinary(CFIXBinary Src) {
		if (Src.m_pData != null && Src.m_pData.length > 0) {
			m_pData = new byte[Src.m_pData.length];
			System.arraycopy(Src.m_pData, 0, m_pData, 0, Src.m_pData.length);
		} else {
			m_pData = null;
		}
	}

	public String ToString() {
		if (m_pData != null && m_pData.length > 0) {
			return CBase64.ToBase64(m_pData);
		}
		return "";
	}

	public byte[] Data() {
		return m_pData;
	}
	private byte[] m_pData;
}
