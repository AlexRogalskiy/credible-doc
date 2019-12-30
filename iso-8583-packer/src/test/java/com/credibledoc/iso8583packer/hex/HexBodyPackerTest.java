package com.credibledoc.iso8583packer.hex;

import com.credibledoc.iso8583packer.FieldBuilder;
import com.credibledoc.iso8583packer.ValueHolder;
import com.credibledoc.iso8583packer.message.MsgFieldType;
import com.credibledoc.iso8583packer.message.MsgValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HexBodyPackerTest {

    private FieldBuilder beforeTest() {
        return FieldBuilder.builder(MsgFieldType.VAL)
            .defineLen(2)
            .defineBodyPacker(HexBodyPacker.INSTANCE);
    }

    @Test
    public void pack() {
        FieldBuilder fieldBuilder = beforeTest();
        String value = "0123";

        fieldBuilder.validateStructure();
        
        ValueHolder valueHolder = ValueHolder.newInstance(fieldBuilder.getCurrentField())
            .setValue(value);
        
        valueHolder.validateData();

        byte[] valueBytes = valueHolder.pack();
        assertEquals("0123", HexService.bytesToHex(valueBytes));
    }

    @Test
    public void unpack() {
        FieldBuilder fieldBuilder = beforeTest();
        String packedValue = "0123";

        fieldBuilder.validateStructure();
        
        MsgValue msgValue = ValueHolder.newInstance(fieldBuilder.getCurrentField())
            .unpack(HexService.hex2byte(packedValue));

        String expectedValue = "0123";
        String unpackedValue = (String) msgValue.getBodyValue();
        assertEquals(expectedValue, unpackedValue);
    }
}
