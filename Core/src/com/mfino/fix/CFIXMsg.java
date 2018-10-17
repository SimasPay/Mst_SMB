/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix;

/**
 *
 * @author moshiko
 */
abstract public class CFIXMsg extends CFIXGroup {

    private CMultiXBuffer m_pRawBuf;

    public abstract boolean Import(CMultiXBuffer Buf);
    private static IFIXMessageCreator m_MessageCreator;

    public static void SetFIXMsgCreator(IFIXMessageCreator Creator) {
        m_MessageCreator = Creator;
    }

    public CFIXMsg() {
        m_pRawBuf = null;
    }

    public void SetRawDataPtr(CMultiXBuffer pNewBuf) {
        m_pRawBuf = pNewBuf;
    }

    public static boolean ExpectedMessageLength(CMultiXBuffer Buf, int[] Length) {
        CFIXMsgParseParams Params = new CFIXMsgParseParams();

        if (ParseField(Buf, Params)) {
            //	FIX message starts with the BeginString Tag
            if (Params.Tag == 8 /*	FIX_TagID_BeginString	*/) {
                // now we look for the BodyLength tag
                if (ParseField(Buf, Params)) {
                    if (Params.Tag == 9 /*	FIX_TagID_BodyLength	*/) {
                        // since we return the entire message length, we calculate it as the body length provided
                        // plus the length of the first two fields	+	the length of the checksum with is always 10=XXX\001
                        Length[0] = Integer.parseInt(new String(Buf.DataPtr(), Params.ValueOffset, Params.ValueLength)) + Params.TagOffset + 7;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static CFIXMsg fromFIX(CMultiXBuffer Buf) {
        int CurrentTagOffset = 0;
        Integer MsgType = null;
        byte[] Ptr = Buf.DataPtr();
        CFIXMsgParseParams Params = new CFIXMsgParseParams();


        while (ParseField(Buf, Params)) {
            switch (Params.Tag) {
                case 10 /*	FIX_TagID_CheckSum	*/: {
                    int CheckSum = 0;
                    for (int I = 0; I < CurrentTagOffset; I++) {
                        CheckSum += Ptr[I];
                    }
                    if ((CheckSum & 0xff) == Integer.parseInt(new String(Ptr, Params.ValueOffset, Params.ValueLength))) {
                        CFIXMsg pMsg = m_MessageCreator.Create(MsgType);
                        if (pMsg != null) {
                            if (pMsg.Import(Buf)) {
                                pMsg.SetRawDataPtr(new CMultiXBuffer());
                                pMsg.m_pRawBuf.Append(Buf.DataPtr(), Params.TagOffset);
                            } else {
                                pMsg = null;
                            }
                        }
                        return pMsg;
                    } else {
                        return null;
                    }
                }
                //				break;
                case 35 /*	FIX_TagID_MsgType	*/: {
                    MsgType = CFIXGroup.ImportIntegerField(Buf, Params);
                }
                break;
            }
            CurrentTagOffset = Params.TagOffset;
        }

        return null;
    }

    public abstract boolean toFIX(CMultiXBuffer Buf);
    public abstract boolean toFIX(CMultiXBuffer Buf,boolean maskFields);

    public String DumpFields() {
    	CMultiXBuffer TempBuffer = new CMultiXBuffer();
        toFIX(TempBuffer,true);
        return DumpFIXBuffer(TempBuffer);
    }

    public void AdjustLengthAndChecksum(CMultiXBuffer Buf) {
        CFIXMsgParseParams Params = new CFIXMsgParseParams();

        if (ParseField(Buf, Params)) {
            //	FIX message starts with the BeginString Tag
            if (Params.Tag == 8 /*	FIX_TagID_BeginString	*/) {
                // now we look for the BodyLength tag
                if (ParseField(Buf, Params)) {
                    if (Params.Tag == 9 /*	FIX_TagID_BodyLength	*/) {
                        // we found the body length field. we need to update it to the real length of the message.
                        // this is because when we serialized the message, we did not know the actual length.
                        int BodyLength = Buf.Length() - Params.TagOffset - 7 /* the length of the cgecksum that already included */;
                        String B = String.format("%06d", BodyLength);	//	Body Length is always 6 bytes long
                        byte[] Bytes = B.getBytes();
                        System.arraycopy(Bytes, 0, Buf.DataPtr(), Params.ValueOffset, Params.ValueLength);
                        // now we want to calculate the checksum.
                        int Length = Buf.Length() - 1;
                        byte[] Ptr = Buf.DataPtr();
                        while (Ptr[Length - 1] != FIELD_SEPARATOR) //	we are looking for the beginning of the checksum tag, it is always the last tag.
                        {
                            Length--;
                        }
                        // now we calculate the checksum
                        int CheckSum = 0;
                        for (int I = 0; I < Length; I++) {
                            CheckSum += Ptr[I];
                        }
                        B = String.format("%d=%03d\001", 10, CheckSum & 0xff);
                        Buf.Store(Length, B);
                    }
                }
            }
        }

    }

    public CMultiXBuffer RawData() {
        return m_pRawBuf;
    }

    public void CloneRawData(CMultiXBuffer NewBuf) {
        m_pRawBuf = NewBuf;
    }
    
    public static void main(String[] args) {
    	String completePostString = "8=mFinoFIX.2.5.\n" +
"9=000107\n" +
"35=1049\n" +
"5126=0\n" +
"5127=read.\n" +
"5122=ASC.\n" +
"5123=20\n" +
"5124=FIX.\n" +
"8184=false\n" +
"5033=Intializing subscriber not allowed..\n" +
"10=029";
        CMultiXBuffer buf = new CMultiXBuffer();
        buf.Append(completePostString);

        CFIXMsg msg = CFIXMsg.fromFIX(buf);

        System.out.println("@kris: processFix completePostString: "+completePostString);
        System.out.println("@kris: processFix msg: "+msg);
        System.out.println("@kris: processFix df: "+msg.DumpFields());
    }
}
