package com.mfino.fix;

import java.io.Serializable;

/**
 * Summary description for CMultiXBuffer.
 */
public class CMultiXBuffer implements Serializable {

	byte[] m_pBuf;
	int m_Length;

	public CMultiXBuffer() {
		m_pBuf = new byte[4096];
		m_Length = 0;
	}
	public CMultiXBuffer(byte[] pInit) {
		m_pBuf = pInit;
		m_Length = pInit.length;
	}

	public int Length() {
		return m_Length;
	}

	public byte[] DataPtr() {
		return m_pBuf;
	}

	public void Append(String S) {
		if (S.length() == 0) {
			return;
		}
		if (m_Length + S.length() > m_pBuf.length) {
			int NewSize = (((m_Length + S.length()) / 4096) + 1) * 4096;
			byte[] pBuf = new byte[NewSize];
			System.arraycopy(m_pBuf, 0, pBuf, 0, m_Length);
			m_pBuf = pBuf;
		}
		byte[] Bytes = S.getBytes();
		System.arraycopy(Bytes, 0, m_pBuf, m_Length, Bytes.length);
		m_Length += Bytes.length;
	}

	public void Store(int Offset, String S) {
		if (S.length() == 0) {
			return;
		}
		if (Offset + S.length() > m_pBuf.length) {
			int NewSize = (((Offset + S.length()) / 4096) + 1) * 4096;
			byte[] pBuf = new byte[NewSize];
			System.arraycopy(m_pBuf, 0, pBuf, 0, m_Length);
			m_pBuf = pBuf;
		}
		byte[] Bytes = S.getBytes();
		System.arraycopy(Bytes, 0, m_pBuf, Offset, Bytes.length);
		if (Offset + Bytes.length > m_Length) {
			m_Length = Bytes.length + Offset;
		}
	}

	public void Append(byte[] Bytes) {
		if (Bytes == null || Bytes.length == 0) {
			return;
		}
		if (m_Length + Bytes.length > m_pBuf.length) {
			int NewSize = (((m_Length + Bytes.length) / 4096) + 1) * 4096;
			byte[] pBuf = new byte[NewSize];
			System.arraycopy(m_pBuf, 0, pBuf, 0, m_Length);
			m_pBuf = pBuf;
		}
		System.arraycopy(Bytes, 0, m_pBuf, m_Length, Bytes.length);
		m_Length += Bytes.length;
	}

	public void Append(byte[] Bytes, int Length) {
		if (Bytes.length == 0) {
			return;
		}
		if (Length > Bytes.length) {
			Length = Bytes.length;
		}
		if (m_Length + Length > m_pBuf.length) {
			int NewSize = (((m_Length + Length) / 4096) + 1) * 4096;
			byte[] pBuf = new byte[NewSize];
			System.arraycopy(m_pBuf, 0, pBuf, 0, m_Length);
			m_pBuf = pBuf;
		}
		System.arraycopy(Bytes, 0, m_pBuf, m_Length, Length);
		m_Length += Length;
	}

	public void Append(CMultiXBuffer Buf) {
		Append(Buf.DataPtr(), Buf.Length());
	}

	public void Empty() {
		m_Length = 0;
	}

	public void ShiftLeft(int Count) {
		if (m_Length == 0) {
			return;
		}
		if (Count >= m_Length) {
			m_Length = 0;
		}
		m_Length -= Count;
		System.arraycopy(m_pBuf, Count, m_pBuf, 0, m_Length);
	}
}
