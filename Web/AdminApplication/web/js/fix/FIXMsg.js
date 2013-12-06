/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("FIX");

FIX.version = "1.0.0";

FIX.CFIXMsgParseParams = function() {
    this.Tag = 0;
    this.ValueOffset = 0;
    this.ValueLength = 0;
    this.TagOffset = 0;
    this.BinaryTagIDs = [];
    this.BinaryTagIDsLen = 0;
};

FIX.MessageCreator = null;


FIX.FIELD_SEPARATOR = '\x01';

FIX.Numout = function(N, Size) {
    var S = N.toString();
    while (S.length < Size)
    {    
        S = "0" + S;
    }
    return S;
};
FIX.ToFIXField = function(Tag, Value) {
    if (Value === null)
    {    
        return "";
    }
    return Tag + "=" + Value + FIX.FIELD_SEPARATOR;

};

FIX.CFIXGroup = function() {
    FIX.CFIXGroup.prototype.Export = function(Buf) {
    };

    FIX.CFIXGroup.prototype.CheckRequiredFields = function() {
        return true;
    };

    FIX.CFIXGroup.prototype.Import = function(Buf, Params) {
        return true;
    };

    FIX.CFIXGroup.prototype.GetActualElementsCount = function(Elements) {
        var RetVal = 0;
        if (Elements === null) {
            return 0;
        }
        if (Elements.length === 0) {
            return 0;
        }
        for (var I = 0; I < Elements.length; I++) {
            if (Elements[I] !== null) {
                if (I > RetVal) {
                    Elements[RetVal] = Elements[I];
                    Elements[I] = null;
                }
                RetVal++;
            }
        }
        return RetVal;
    };

    FIX.CFIXGroup.prototype.ParseField = function(Buf, Params) {
        if (Params.TagOffset > Buf.Length()) {
            return false;
        }
        var bTagFound = false;
        var Ptr = Buf.DataPtr();
        for (var I = Params.TagOffset; I < Ptr.length; I++) {
            if (!bTagFound) {
                if (Ptr.charAt(I) == '=') {
                    //	At this point we have the TAG
                    Params.Tag = parseInt(Ptr.substr(Params.TagOffset, I - Params.TagOffset), 10);
                    bTagFound = true;
                    Params.ValueOffset = I + 1;
                    if (Params.BinaryTagIDs !== null) {
                        for (var J = 0; J < Params.BinaryTagIDs.length; J++) {
                            if (Params.Tag == Params.BinaryTagIDs[J]) {
                                //	We found Binary Data Tag, we should look in the prev field for the length
                                var K = I;
                                var EndLength = I;
                                while (Ptr.charAt(K - 1) != '=') {
                                    K--;
                                    if (Ptr.charAt(K) == FIX.FIELD_SEPARATOR) {
                                        EndLength = K;
                                    }
                                }
                                Params.ValueLength = parseInt(Ptr.substr(K, EndLength - K), 10); //	the length of the binary data
                                Params.TagOffset = Params.ValueOffset + Params.ValueLength + 1;
                                return true;
                            }
                        }
                    }
                }
            } else if (Ptr.charAt(I) == FIX.FIELD_SEPARATOR) {
                Params.ValueLength = I - Params.ValueOffset;
                /*
				if(ValueLength	==	0)	//	this means that the field separator came right after the '=' sign, which means empty value
				return	false;						//	which is not permitted under FIX
				*/
                if (Params.ValueLength > 0) {
                    Params.TagOffset = Params.ValueOffset + Params.ValueLength + 1;
                    return true;
                } else {
                    if (Params.Tag == 20 && Ptr.charAt(I) == FIX.FIELD_SEPARATOR) {
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
    };


    FIX.CFIXGroup.prototype.ImportIntegerField = function(Buf, Params) {

        try {
            return parseInt(Buf.DataPtr().substr(Params.ValueOffset, Params.ValueLength), 10);
        } catch (Err) {
        }
        return null;
    };

    FIX.CFIXGroup.prototype.ImportIntegerFieldFromString = function(S) {
        try {
            return parseInt(S, 10);
        } catch (Err) {
        }
        return null;
    };

    FIX.CFIXGroup.prototype.ImportLongFieldFromString = function(S) {
        try {
            return parseInt(S, 10);
        } catch (Err) {
        }
        return null;
    };



    FIX.CFIXGroup.prototype.ImportBooleanFieldFromString = function(S) {
        if(S ==  "true")
        {
            return  true;
        } else  if(S ==  "false")
        {
            return  false;
        }
        return null;
    };

    FIX.CFIXGroup.prototype.ImportCharacterField = function(Buf, Params) {
        try {
            return Buf.DataPtr().substr(Params.ValueOffset, Params.ValueLength).charAt(0);
        } catch (Err) {
        }
        return null;
    };

    FIX.CFIXGroup.prototype.ImportLongField = function(Buf, Params) {
        try {
            return parseInt(Buf.DataPtr().substr(Params.ValueOffset, Params.ValueLength), 10);
        } catch (Err) {
        }
        return null;
    };

    FIX.CFIXGroup.prototype.ImportDoubleField = function(Buf, Params) {
        try {
            return parseFloat(Buf.DataPtr().substr(Params.ValueOffset, Params.ValueLength));
        }
        catch (Err) {
        }
        return null;

    };

    FIX.CFIXGroup.prototype.ImportBigDecimalField = function(Buf, Params) {
        try {
            return parseFloat(Buf.DataPtr().substr(Params.ValueOffset, Params.ValueLength));
        }
        catch (Err) {
        }
        return null;

    };    
    
    FIX.CFIXGroup.prototype.ImportStringField = function(Buf, Params) {
        try {
            if(Params.ValueLength > 1)
            {
                return Buf.DataPtr().substr(Params.ValueOffset, Params.ValueLength-1);
            }else
            {
                return "";
            }

        } catch (Err) {
        }
        return null;
    };
    FIX.CFIXGroup.prototype.ImportBooleanField = function(Buf, Params) {
        if(Buf.DataPtr().substr(Params.ValueOffset, Params.ValueLength) ==  "true")
        {
            return  true;
        }
        if(Buf.DataPtr().substr(Params.ValueOffset, Params.ValueLength) ==  "false")
        {
            return  false;
        }
        return null;
    };


    FIX.CFIXGroup.prototype.ImportStringFieldFromString = function(S) {
        return S;
    };

    FIX.CFIXGroup.prototype.ImportCFIXBinaryField = function(Buf, Params) {
        try {
            var S = Buf.DataPtr().substr(Params.ValueOffset, Params.ValueLength);
            return new CFIXBinary(S);
        } catch (Err) {
        }
        return null;
    };

    FIX.CFIXGroup.prototype.ImportCUTCTimeStampField = function(Buf, Params) {
        try {
            return FIX.CUTCTimeStamp.FromString(Buf.DataPtr().substr(Params.ValueOffset, Params.ValueLength));
        } catch (Err) {
        }
        return null;
    };

    FIX.CFIXGroup.prototype.ImportDateField = FIX.CFIXGroup.prototype.ImportCUTCTimeStampField;
    FIX.CFIXGroup.prototype.ImportTimestampField = FIX.CFIXGroup.prototype.ImportCUTCTimeStampField;

    FIX.CFIXGroup.prototype.ImportCMonthYearField = function(Buf, Params) {
        var Field = new FIX.CMonthYear();
        var MY = parseInt(Buf.DataPtr().substr(Params.ValueOffset, Params.ValueLength), 10);
        Field.m_Year = MY / 100;
        Field.m_Month = MY % 100;
        if (Field.m_Year < 0 || Field.m_Month < 1 || Field.m_Month > 12) {
            return null;
        }
        return Field;
    };

    FIX.CFIXGroup.prototype.ImportCUTCDateField = function(Buf, Params) {
        try {
            return FIX.CUTCDate.FromString(Buf.DataPtr().substr(Params.ValueOffset, Params.ValueLength));
        } catch (Err) {
        }
        return null;
    };

    FIX.CFIXGroup.prototype.ImportCUTCTimeField = function(Buf, Params) {
        try {
            return FIX.CUTCTime.FromString(Buf.DataPtr().substr(Params.ValueOffset, Params.ValueLength));
        } catch (Err) {
        }
        return null;
    };
};



FIX.CFIXGroup.prototype.ExportStringField = function(Buf, Field, TagID) {
    Buf.Append(FIX.ToFIXField(TagID, Field + "\t"));
};

FIX.CFIXGroup.prototype.ExportBooleanField = function(Buf, Field, TagID) {

    if(Field==="")
    {
        return;
    }
    if(typeof(Field) == 'boolean')
    {
        Buf.Append(FIX.ToFIXField(TagID, Field ? "true" : "false"));
    }
    else    if(typeof(Field)=='number')
    {
        Buf.Append(FIX.ToFIXField(TagID, Field ? "true" : "false"));
    } else  if(typeof(Field)=='string' && (Field=="true" || Field=="false"))
    {
        Buf.Append(FIX.ToFIXField(TagID, Field));
    } else
    {
        throw "FIX Export error: Invalid Boolean Value with TagID " + TagID;
    }
};

FIX.CFIXGroup.prototype.ExportLongField = function(Buf, Field, TagID) {
    if(Field==="")
    {
        return;
    }
    if (isNaN(Number(Field)))
    {
        throw "FIX export error: Invalid Numeric Value with TagID " + TagID;
    }
    if (TagID == 9) //	BodyLength - we will always use at least 6 digits - assumin no single FIX message will be more than one million bytes
    {
        Buf.Append(FIX.ToFIXField(TagID, FIX.Numout(Field, 6)));
    } else {
        Buf.Append(FIX.ToFIXField(TagID, Field));
    }
};

FIX.CFIXGroup.prototype.ExportIntegerField = function(Buf, Field, TagID) {
    if(Field==="")
    {
        return;
    }
    if (isNaN(Number(Field)))
    {
        throw "FIX export error: Invalid Numeric Value with TagID " + TagID;
    }
    if (TagID == 9) //	BodyLength - we will always use at least 6 digits - assumin no single FIX message will be more than one million bytes
    {
        Buf.Append(FIX.ToFIXField(TagID, FIX.Numout(Field, 6)));
    } else {
        Buf.Append(FIX.ToFIXField(TagID, Field));
    }
};

FIX.CFIXGroup.prototype.ExportCharacterField = function(Buf, Field, TagID) {
    if(Field==="")
    {
        return;
    }
    if (Field.length != 1)
    {
        throw "FIX export error: Invalid Character Field with TagID" + TagID;
    }
    Buf.Append(FIX.ToFIXField(TagID, Field));
};

FIX.CFIXGroup.prototype.ExportDoubleField = function(Buf, Field, TagID) {
    if(Field==="")
    {
        return;
    }
    if (isNaN(Number(Field)))
    {
        throw "FIX export error: Invalid Numeric Value with TagID " + TagID;
    }
    Buf.Append(FIX.ToFIXField(TagID, Field));
};

FIX.CFIXGroup.prototype.ExportBigDecimalField = function(Buf, Field, TagID) {
    if(Field==="")
    {
        return;
    }
    if (isNaN(Number(Field)))
    {
        throw "FIX export error: Invalid Numeric Value with TagID " + TagID;
    }
    Buf.Append(FIX.ToFIXField(TagID, Field));
};

FIX.CFIXGroup.prototype.ExportCFIXBinaryField = function(Buf, Field, TagID) {
    if(Field==="")
    {
        return;
    }
    var S = Field.ToString();
    if (S.length === 0)
    {
        return;
    }
    Buf.Append(FIX.ToFIXField(TagID, S));
};

FIX.CFIXGroup.prototype.ExportCUTCTimeStampField = function(Buf, Field, TagID) {
    if(Field===""){
        return;
    }
    try {
        //work around the fact that date range picker returns a string type
        //instead of a date type
        if(Ext.isString(Field)){
            var time = FIX.CUTCTimeStamp.FromWebString(Field);
            Buf.Append(FIX.ToFIXField(TagID, time.ToString()));
        }else{
            Buf.Append(FIX.ToFIXField(TagID, Field.ToString()));
        }

    } catch (Err) {
        var T = new FIX.CUTCTimeStamp();
        T.m_Date = Field;
        Buf.Append(FIX.ToFIXField(TagID, T.ToString()));
    }
};

FIX.CFIXGroup.prototype.ExportDateField = FIX.CFIXGroup.prototype.ExportCUTCTimeStampField;
FIX.CFIXGroup.prototype.ExportTimestampField = FIX.CFIXGroup.prototype.ExportCUTCTimeStampField;

FIX.CFIXGroup.prototype.ExportCMonthYearField = function(Buf, Field, TagID) {
    if(Field==="")
    {
        return;
    }
    Buf.Append(FIX.ToFIXField(TagID, Field.m_Year * 100 + Field.m_Month));
};

FIX.CFIXGroup.prototype.ExportCUTCTimeField = function(Buf, Field, TagID) {
    if(Field==="")
    {
        return;
    }
    try {
        Buf.Append(FIX.ToFIXField(TagID, Field.ToString()));
    } catch (Err) {
        var T = new FIX.CUTCTime();
        T.m_Date = Field;
        Buf.Append(FIX.ToFIXField(TagID, T.ToString()));
    }
};

FIX.CFIXGroup.prototype.ExportCUTCDateField = function(Buf, Field, TagID) {
    if(Field==="")
    {
        return;
    }
    try {
        Buf.Append(FIX.ToFIXField(TagID, Field.ToString()));
    } catch (Err) {
        var T = new FIX.CUTCDate();
        T.m_Date = Field;
        Buf.Append(FIX.ToFIXField(TagID, T.ToString()));
    }
};


FIX.CFIXMsg = function() {

    FIX.CFIXMsg.superclass.constructor.call(this);
    this.m_pRawBuf = null;

    FIX.CFIXMsg.prototype.FromFIX = function(Buf) {
        var CurrentTagOffset = 0;
        var MsgType = null;
        var Ptr = Buf.DataPtr();
        var Params = new FIX.CFIXMsgParseParams();
        try {
            while (this.ParseField(Buf, Params)) {
                switch (Params.Tag) {
                    case 10 /*	FIX_TagID_CheckSum	*/:
                        var CheckSum = 0;
                        for (var I = 0; I < CurrentTagOffset; I++) {
                            CheckSum += Ptr.charCodeAt(I);
                        }
                        if ((CheckSum & 0xff) == parseInt(Ptr.substr(Params.ValueOffset, Params.ValueLength), 10)) {
                            var pMsg = FIX.MessageCreator.Create(MsgType);
                            if (pMsg !== null) {
                                if (pMsg.ImportMsg(Buf)) {
                                    pMsg.CloneRawData(new FIX.CMultiXBuffer());
                                    pMsg.m_pRawBuf.Append(Buf.DataPtr().substr(0, Params.TagOffset));
                                } else {
                                    pMsg = null;
                                }
                            }
                            return pMsg;
                        } else {
                            return null;
                        }
                        break;
                    case 35 /*	FIX_TagID_MsgType	*/:
                        MsgType =	this.ImportIntegerField(Buf, Params);
                        break;
                }
                CurrentTagOffset = Params.TagOffset;
            }
        } catch (Err) {
        }
        return null;
    };

    this.AdjustLengthAndChecksum = function(Buf) {
        var Params = new FIX.CFIXMsgParseParams();
        try {
            if (this.ParseField(Buf, Params)) {
                //	FIX message starts with the BeginString Tag
                if (Params.Tag == 8 /*	FIX_TagID_BeginString	*/) {
                    // now we look for the BodyLength tag
                    if (this.ParseField(Buf, Params)) {
                        if (Params.Tag == 9 /*	FIX_TagID_BodyLength	*/) {
                            // we found the body length field. we need to update it to the real length of the message.
                            // this is because when we serialized the message, we did not know the actual length.
                            var BodyLength = Buf.Length() - Params.TagOffset - 7 /* the length of the cgecksum that already included */;
                            var B = FIX.Numout(BodyLength, 6); //	Body Length is always 6 bytes long
                            Buf.Store(Params.ValueOffset, FIX.Numout(BodyLength, 6));
                            // now we want to calculate the checksum.
                            var Length = Buf.Length() - 1;
                            var Ptr = Buf.DataPtr();
                            while (Ptr.charAt(Length - 1) != FIX.FIELD_SEPARATOR) //	we are looking for the beginning of the checksum tag, it is always the last tag.
                            {
                                Length--;
                            }
                            // now we calculate the checksum
                            var CheckSum = 0;
                            for (var I = 0; I < Length; I++) {
                                CheckSum += Ptr.charCodeAt(I);
                            }
                            Buf.Store(Length, FIX.ToFIXField(10, FIX.Numout(CheckSum & 0xff, 3)));
                        }
                    }
                }
            }
        } catch (Err) {
        }
    };

    this.RawData = function() {
        return this.m_pRawBuf;
    };

    this.CloneRawData = function(NewBuf) {
        this.m_pRawBuf = NewBuf;
    };
};

FIX.CFIXMsg.prototype = new FIX.CFIXGroup();
FIX.CFIXMsg.prototype.constructor = FIX.CFIXMsg;
FIX.CFIXMsg.superclass = FIX.CFIXGroup.prototype;



FIX.CMultiXBuffer = function() {
    this.m_pBuf = '';
    this.Append = function(Value) {
        this.m_pBuf += Value;
    };
    this.Length = function() {
        return this.m_pBuf.length;
    };
    this.DataPtr = function() {
        return this.m_pBuf;
    };
    this.Store = function(Offset, Value) {
        var S = Value.toString();
        var First = this.m_pBuf.substring(0, Offset);
        var Last = this.m_pBuf.substring(Offset + S.length);
        this.m_pBuf = First + S + Last;
    };
};

FIX.CMonthYear = function() {
    this.m_Year = 0;
    this.m_Month = 0;
};


FIX.CUTCDate = function() {

    this.m_Date = new Date();
    this.valueOf = function() {
        return this.m_Date;
    };

    this.EQ = function(Other) {
        return this.m_Date == Other.m_Date;
    };

    this.NE = function(Other) {
        return this.m_Date != Other.m_Date;
    };

    this.ToString = function() {
        return (this.m_Date.getUTCFullYear() * 10000 + (this.m_Date.getUTCMonth() + 1) * 100 + this.m_Date.getUTCDate()).toString();
    };
};

FIX.CUTCDate.FromString = function(S) {
    var RetVal = new FIX.CUTCDate();
    var D = parseInt(S, 10);
    RetVal.m_Date.setFullYear(D / 10000, (D / 100) % 100, D % 100);
    return RetVal;
};


FIX.CUTCTimeStamp = function() {

    this.m_Date = new Date();

    this.valueOf = function() {
        return this.m_Date;
    };

    this.Clear = function() {
        this.m_Date = null;
    };

    this.SetTimeNow = function() {
        this.m_Date = new Date();
    };

    this.ToString = function() {
        var T = this.m_Date;
        var S = T.getUTCFullYear().toString();
        S += FIX.Numout(T.getUTCMonth() + 1, 2);
        S += FIX.Numout(T.getUTCDate(), 2);
        S += "-" + FIX.Numout(T.getUTCHours(), 2);
        S += ":" + FIX.Numout(T.getUTCMinutes(), 2);
        S += ":" + FIX.Numout(T.getUTCSeconds(), 2);
        S += "." + FIX.Numout(T.getUTCMilliseconds(), 3);
        return S;
    };


    this.EQ = function(Other) {
        return this.m_Date == Other.m_Date;
    };

    this.NE = function(Other) {
        return this.m_Date != Other.m_Date;
    };

    this.LT = function(Other) {
        return this.m_Date < Other.m_Date;
    };

    this.LE = function(Other) {
        return this.m_Date <= Other.m_Date;
    };

    this.Sub = function(Other) {
        return this.m_Date - Other.m_Date;
    };
};

FIX.CUTCTimeStamp.Now = function() {
    return new FIX.CUTCTimeStamp();
};

FIX.CUTCTimeStamp.FromString = function(S) {
    var RetVal = new FIX.CUTCTimeStamp();
    var year = parseInt(S.substr(0, 4), 10);
    var month = parseInt(S.substr(4, 2), 10) - 1;
    var day = parseInt(S.substr(6, 2), 10);
    RetVal.m_Date.setUTCFullYear(year, month, day);
    var hour = parseInt(S.substr(9, 2), 10);
    var min = parseInt(S.substr(12, 2), 10);
    var sec = parseInt(S.substr(15, 2), 10);
    var milli = parseInt(S.substr(18, 3), 10);
    RetVal.m_Date.setUTCHours(hour, min, sec, milli);
    return RetVal;
};

FIX.CUTCTimeStamp.FromWebString = function(S) {
    var RetVal = new FIX.CUTCTimeStamp();
    var year = parseInt(S.substr(0, 4), 10);
    var month = parseInt(S.substr(4, 2), 10) - 1;
    var day = parseInt(S.substr(6, 2), 10);
    var hour = parseInt(S.substr(9, 2), 10);
    var min = parseInt(S.substr(12, 2), 10);
    var sec = parseInt(S.substr(15, 2), 10);
    var milli = parseInt(S.substr(18, 2), 10);
    RetVal.m_Date = new Date(year, month, day, hour, min, sec, milli);
    return RetVal;
};

FIX.CUTCTime = function() {

    this.m_Date = null;

    this.valueOf = function() {
        return this.m_Date;
    };

    this.ToString = function() {
        var T = this.m_Date;
        var S = FIX.Numout(T.getUTCHours(), 2).toString();
        S += ":" + FIX.Numout(T.getUTCMinutes(), 2);
        S += ":" + FIX.Numout(T.getUTCSeconds(), 2);
        S += "." + FIX.Numout(T.getUTCMilliseconds(), 3);
        return S;
    };
};

FIX.CUTCTime.FromString = function(S) {
    var RetVal = new FIX.CUTCTime();

    RetVal.m_Date.setUTCHours(parseInt(S.substr(0, 2), 10));
    RetVal.m_Date.setUTCMinutes(parseInt(S.substr(3, 2), 10));
    RetVal.m_Date.setUTCSeconds(parseInt(S.substr(6, 2), 10));
    RetVal.m_Date.setUTCMilliseconds(parseInt(S.substr(9, 3), 10));
    return RetVal;
};

FIX.CBase64 = function() {
    this.Base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/0=";
    this.Base64Inverted = "";
    this.Ascii = "";
    FIX.CBase64.ToBase64 = function(Data) {
        var I = 0;
        var J = 0;
        var Str = "";
        var Length = Data.length;
        var I32 = 0;
        for (I = 0; I < Length-2; I += 3) {
            I32 = (Data.charCodeAt(I) << 16) + (Data.charCodeAt(I + 1) << 8) + Data.charCodeAt(I + 2);
            Str += this.Base64Chars.charAt((I32 >> 18) & 0x3f);
            Str += this.Base64Chars.charAt((I32 >> 12) & 0x3f);
            Str += this.Base64Chars.charAt((I32 >> 6) & 0x3f);
            Str += this.Base64Chars.charAt((I32) & 0x3f);
        }
        if (Length - I == 1) {
            I32 = (Data.charCodeAt(I) << 16);
            Str += this.Base64Chars.charAt((I32 >> 18) & 0x3f);
            Str += this.Base64Chars.charAt((I32 >> 12) & 0x3f);
            Str += '=';
            Str += '=';
        } else if (Length - I == 2) {
            I32 = (Data.charCodeAt(I) << 16) + (Data.charCodeAt(I + 1) << 8);
            Str += this.Base64Chars.charAt((I32 >> 18) & 0x3f);
            Str += this.Base64Chars.charAt((I32 >> 12) & 0x3f);
            Str += this.Base64Chars.charAt((I32 >> 6) & 0x3f);
            Str += '=';
        }
        return Str;
    };

    FIX.CBase64.FromBase64 = function(Data) {
        if (this.Base64Inverted.length === 0) {
            var I;
            var Temp = [];
            for (I = 0; I < 64; I++) {
                Temp[this.Base64Chars.charCodeAt(I)] = I;
            }
            for (I = 0; I < 64; I++) {
                this.Base64Inverted += Temp.charCodeAt(I);
            }
            for (I = 0; I < 255; I++) {
                this.Ascii += String.fromCharCode(I);
            }
        }
        var RetVal = "";
        var I32 = 0;
        for (I = 0; I < this.Base64String.length(); I += 4) {
            if (this.Base64String.charAt(I + 3) != '=') {
                I32 = (this.Base64Inverted[this.Base64String.charCodeAt(I)] << 18) +
                (this.Base64Inverted[this.Base64String.charCodeAt(I + 1)] << 12) +
                (this.Base64Inverted[this.Base64String.charCodeAt(I + 2)] << 6) +
                this.Base64Inverted[this.Base64String.charCodeAt(I + 3)];
                RetVal += this.Ascii.charCodeAt((I32 >> 16) & 0xff);
                RetVal += this.Ascii.charCodeAt((I32 >> 8) & 0xff);
                RetVal += this.Ascii.charCodeAt((I32) & 0xff);
            } else if (Base64String[I + 2] != '=') {
                I32 = (this.Base64Inverted[this.Base64String.charCodeAt(I)] << 18) +
                (this.Base64Inverted[this.Base64String.charCodeAt(I + 1)] << 12) +
                (this.Base64Inverted[this.Base64String.charCodeAt(I + 2)] << 6);
                RetVal += this.Ascii.charCodeAt((I32 >> 16) & 0xff);
                RetVal += this.Ascii.charCodeAt((I32 >> 8) & 0xff);
            } else {
                I32 = (this.Base64Inverted[this.Base64String.charCodeAt(I)] << 18) +
                (this.Base64Inverted[this.Base64String.charCodeAt(I + 1)] << 12);
                RetVal += this.Ascii.charCodeAt((I32 >> 16) & 0xff);
            }
        }
        return RetVal;
    };
};

FIX.CFIXBinary = function(Value) {
    this.m_pData = FIX.CBase64.FromBase64(Value);
    this.ToString = function() {
        if (m_pData === null || m_pData.length === 0)
        {
            return "";
        }
        return FIX.CBase64.ToBase64(m_pData);
    };
    this.valueOf = function() {
        return this.ToString();
    };
};

