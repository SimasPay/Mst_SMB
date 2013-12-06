package com.mfino.fix;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;

/**
 * Summary description for CFIXGroup.
 */
abstract public class CFIXGroup implements Serializable {

	public Dictionary<String, Integer>	m_RemoteModifiedFields	= new Hashtable<String, Integer>();

	public void setRemoteModifiedField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		String S = new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength - 1);
		m_RemoteModifiedFields.put(S, 1);
	}

	public Boolean isRemoteModifiedField(String FieldName) {
		return m_RemoteModifiedFields.get(FieldName) != null;
	}

	public boolean checkRequiredFields() {
		return true;
	}

	public CFIXGroup() {
	}

	public static final int	FIELD_SEPARATOR	= 1;

	public void Export(CMultiXBuffer Buf) {
	}
	public void ExportWithMasking(CMultiXBuffer Buf) {
	}

	public boolean Import(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		return true;
	}

	public String DumpFields() {
		CMultiXBuffer TempBuffer = new CMultiXBuffer();
		Export(TempBuffer);
		return DumpFIXBuffer(TempBuffer);
	}

	public int GetActualElementsCount(Object[] Array) {
		int RetVal = 0;
		if (Array == null) {
			return 0;
		}
		if (Array.length == 0) {
			return 0;
		}
		for (int I = 0; I < Array.length; I++) {
			if (Array[I] != null) {
				if (I > RetVal) {
					Array[RetVal] = Array[I];
					Array[I] = null;
				}
				RetVal++;
			}
		}
		return RetVal;
	}

	public static String DumpFIXBuffer(CMultiXBuffer SourceBuf) {
		byte[] Bytes = new byte[SourceBuf.Length()];
		System.arraycopy(SourceBuf.DataPtr(), 0, Bytes, 0, Bytes.length);
		for (int I = 0; I < Bytes.length; I++) {
			if (Bytes[I] == FIELD_SEPARATOR) {
				Bytes[I] = '\n';
			}
			else if (Bytes[I] < 32) {
				Bytes[I] = '.';
			}
		}
		return new String(Bytes);
	}

	public static boolean ParseField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		if (Params.TagOffset > Buf.Length()) {
			return false;
		}
		boolean bTagFound = false;
		byte[] Ptr = Buf.DataPtr();

		for (int I = Params.TagOffset; I < Buf.Length(); I++) {
			if (!bTagFound) {

				if (Ptr[I] == '=') {
					//	At this point we have the TAG
					Params.Tag = Integer.parseInt(new String(Ptr, Params.TagOffset, I - Params.TagOffset));
					bTagFound = true;
					Params.ValueOffset = I + 1;
					if (Params.BinaryTagIDs != null) {
						for (int J = 0; J < Params.BinaryTagIDs.length; J++) {
							if (Params.Tag == Params.BinaryTagIDs[J]) {
								//	We found Binary Data Tag, we should look in the prev field for the length
								int K = I;
								int EndLength = I;
								while (Ptr[K - 1] != '=') {
									K--;
									if (Ptr[K] == FIELD_SEPARATOR) {
										EndLength = K;
									}
								}
								Params.ValueLength = Integer.parseInt(new String(Ptr, K, EndLength - K));
								Params.TagOffset = Params.ValueOffset + Params.ValueLength + 1;
								return true;
							}
						}
					}
				}
			}
			else if (Ptr[I] == FIELD_SEPARATOR) {
				Params.ValueLength = I - Params.ValueOffset;
				/*
				 * if(ValueLength == 0) // this means that the field separator
				 * came right after the '=' sign, which means empty value return
				 * false; // which is not permitted under FIX
				 */
				if (Params.ValueLength > 0) {
					Params.TagOffset = Params.ValueOffset + Params.ValueLength + 1;
					return true;
				}
				else {
					if (Params.Tag == 20 && Ptr[I] == FIELD_SEPARATOR) {
						Params.ValueLength = 1;
						Params.TagOffset = Params.ValueOffset + Params.ValueLength + 1;
						return true;
					}
					return false;
				}
			}
		}
		//	we fall here if we have an invalid FIX message
		return false;
	}

	public static Integer ImportIntegerField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		Integer RetVal = null;
		RetVal = Integer.parseInt(new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength));
		return RetVal;
	}

	public static Integer ImportIntegerField(String S) {
		Integer RetVal = null;
		RetVal = Integer.parseInt(S);
		return RetVal;
	}

	public static BigDecimal ImportBigDecimalField(String S) {
		BigDecimal RetVal = null;
		RetVal = new BigDecimal(S);
		return RetVal;
	}

	public static Long ImportLongField(String S) {
		Long RetVal = null;
		RetVal = Long.parseLong(S);
		return RetVal;
	}

	public static Character ImportCharacterField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		String S = new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength);
		return S.charAt(0);
	}

	public static Long ImportLongField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		Long RetVal = null;
		RetVal = Long.parseLong(new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength));
		return RetVal;
	}

	public static Double ImportDoubleField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		Double RetVal = null;
		RetVal = Double.parseDouble(new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength));
		return RetVal;
	}

	public static BigDecimal ImportBigDecimalField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		BigDecimal RetVal = null;
		RetVal = new BigDecimal(new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength));
		return RetVal;
	}

	public static String ImportStringField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		if (Params.ValueLength > 1) {
			return new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength - 1);
		}
		else {
			return new String();
		}
	}

	public static Boolean ImportBooleanField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		String S = new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength);
		if (S.contentEquals("true")) {
			return true;
		}
		if (S.contentEquals("false")) {
			return false;
		}
		return null;
	}

	public static Boolean ImportBooleanField(String S) {
		if (S.contentEquals("true")) {
			return true;
		}
		if (S.contentEquals("false")) {
			return false;
		}
		return null;
	}

	public static String ImportStringField(String S) {
		return new String(S);
	}

	public static CFIXBinary ImportCFIXBinaryField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {

		String S = new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength);
		return new CFIXBinary(S);
	}

	//    public static Date ImportDateField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
	//        CUTCTimeStamp RetVal = null;
	//        RetVal = CUTCTimeStamp.fromString(new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength));
	//        return RetVal.getDate();
	//    }
	public static Timestamp ImportTimestampField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		CUTCTimeStamp RetVal = null;
		RetVal = CUTCTimeStamp.fromString(new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength));
		return RetVal.getDate();
	}

	public static CUTCTimeStamp ImportCUTCTimeStampField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		CUTCTimeStamp RetVal = null;
		RetVal = CUTCTimeStamp.fromString(new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength));
		return RetVal;
	}

	public static CMonthYear ImportCMonthYearField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		CMonthYear RetVal = new CMonthYear();
		int MY = Integer.parseInt(new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength));
		RetVal.m_Year = MY / 100;
		RetVal.m_Month = MY % 100;
		if (RetVal.m_Year < 0 || RetVal.m_Month < 1 || RetVal.m_Month > 12) {
			throw new RuntimeException(MessageText._("Bad Data"));
		}
		return RetVal;
	}

	public static CUTCDate ImportCUTCDateField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		CUTCDate RetVal = null;
		RetVal = CUTCDate.FromString(new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength));
		return RetVal;
	}

	public static CUTCTime ImportCUTCTimeField(CMultiXBuffer Buf, CFIXMsgParseParams Params) {
		CUTCTime RetVal = null;
		RetVal = CUTCTime.FromString(new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength));
		return RetVal;
	}

	public static void ExportField(CMultiXBuffer Buf, String Field, int TagID, boolean isSecure) {
		if (!isSecure)
			Buf.Append(String.format("%d=%s\t\001", TagID, Field));
		else
			Buf.Append(String.format("%d=%s\t\001", TagID, replaceSecureString(Field, replacementCharacter)));
	}

	public static void ExportField(CMultiXBuffer Buf, Long Field, int TagID, boolean isSecure) {
		if (TagID == 9) //	BodyLength - we will always use at least 6 digits - assumin no single FIX message will be more than one million bytes
			Buf.Append(String.format("%d=%06d\001", TagID, Field));
		else {
			if (!isSecure)
				Buf.Append(String.format("%d=%d\001", TagID, Field));
			else
				Buf.Append(String.format("%d=%s\001", TagID, replaceSecureString(Field, replacementCharacter)));
		}
	}

	private static String replaceSecureString(Object Field, char c) {
		String field = Field.toString();
		int len = field.length();
		field = "*";
		for (int i = 1; i < len - 1; i++)
			field = field + "*";
		return field;
	}

	public static void ExportField(CMultiXBuffer Buf, Integer Field, int TagID, boolean isSecure) {
		if (TagID == 9) //	BodyLength - we will always use at least 6 digits - assumin no single FIX message will be more than one million bytes
			Buf.Append(String.format("%d=%06d\001", TagID, Field));
		else {
			if (!isSecure)
				Buf.Append(String.format("%d=%d\001", TagID, Field));
			else
				Buf.Append(String.format("%d=%s\001", TagID, replaceSecureString(Field, replacementCharacter)));
		}
	}

	public static void ExportField(CMultiXBuffer Buf, Boolean Field, int TagID, boolean isSecure) {
		if (!isSecure) 
			Buf.Append(String.format("%d=%s\001", TagID, Field ? "true" : "false"));
		else 
			Buf.Append(String.format("%d=%s\001", TagID, "*****"));
	}

	public static void ExportField(CMultiXBuffer Buf, Character Field, int TagID, boolean isSecure) {
		if (!isSecure)
			Buf.Append(String.format("%d=%c\001", TagID, Field));
		else
			Buf.Append(String.format("%d=%c\001", TagID, replacementCharacter));
	}

	public static void ExportField(CMultiXBuffer Buf, Double Field, int TagID, boolean isSecure) {
		if (!isSecure)
			Buf.Append(String.format("%d=%f\001", TagID, Field));
		else
			Buf.Append(String.format("%d=%s\001", TagID, replaceSecureString(Field, replacementCharacter)));
	}

	public static void ExportField(CMultiXBuffer Buf, BigDecimal Field, int TagID, boolean isSecure) {
		if (!isSecure)
			Buf.Append(String.format("%d=%s\001", TagID, Field));
		else
			Buf.Append(String.format("%d=%s\001", TagID, replaceSecureString(Field, replacementCharacter)));
	}

	public static void ExportField(CMultiXBuffer Buf, CFIXBinary Field, int TagID, boolean isSecure) {
		String Data = Field.ToString();
		if (Data.length() == 0) {
			return;
		}
		
		String S = Integer.toString(TagID);
		//Before Correcting errors reported by Findbugs:
			//S.concat("=");
	
	    //After Correcting the errors reported by Findbugs:reassigned to S
		S = S.concat("=");
		if (!isSecure) {
			Buf.Append(S);
			Buf.Append(Data);
			Buf.Append(new byte[] { 1 });
		}
		else {
			Buf.Append(replaceSecureString(S, replacementCharacter));
			Buf.Append(replaceSecureString(Data, replacementCharacter));
			Buf.Append(replaceSecureString("1", replacementCharacter));
		}
	}

	public static void ExportField(CMultiXBuffer Buf, Date Field, int TagID, boolean isSecure) {
		CUTCTimeStamp Val = new CUTCTimeStamp(Field);
		if (!isSecure)
			Buf.Append(String.format("%d=%s\001", TagID, Val.toString()));
		else
			Buf.Append(String.format("%d=%s\001", TagID, replaceSecureString(Val.toString(), replacementCharacter)));
	}

	public static void ExportField(CMultiXBuffer Buf, CUTCTimeStamp Field, int TagID, boolean isSecure) {
		if (!isSecure)
			Buf.Append(String.format("%d=%s\001", TagID, Field.toString()));
		else
			Buf.Append(String.format("%d=%s\001", TagID, replaceSecureString(Field, replacementCharacter)));
	}

	public static void ExportField(CMultiXBuffer Buf, CMonthYear Field, int TagID, boolean isSecure) {
		if (!isSecure)
			Buf.Append(String.format("%d=%d\001", TagID, Field.m_Year * 100 + Field.m_Month));
		else
			Buf.Append(String.format("%d=%s\001", TagID, replaceSecureString(Field.m_Year * 100 + Field.m_Month, replacementCharacter)));
	}

	public static void ExportField(CMultiXBuffer Buf, CUTCDate Field, int TagID, boolean isSecure) {
		if (!isSecure)
			Buf.Append(String.format("%d=%s\001", TagID, Field.toString()));
		else
			Buf.Append(String.format("%d=%s\001", TagID, replaceSecureString(Field, replacementCharacter)));
	}

	public static void ExportField(CMultiXBuffer Buf, CUTCTime Field, int TagID, boolean isSecure) {
		if (!isSecure)
			Buf.Append(String.format("%d=%s\001", TagID, Field.toString()));
		else
			Buf.Append(String.format("%d=%s\001", TagID, replaceSecureString(Field, replacementCharacter)));
	}

	private static final char	replacementCharacter	= '*';
}