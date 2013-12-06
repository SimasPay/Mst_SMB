package com.mfino.vah.converters;

import org.jpos.iso.ISOMsg;

public interface IsoToNativeTransformer {
	
	public String transform(ISOMsg msg) throws TransformationException;

}
