package com.emh.thermalprinter.barcode;

import com.emh.thermalprinter.EscPosPrinterCommands;
import com.emh.thermalprinter.EscPosPrinterSize;
import com.emh.thermalprinter.exceptions.EscPosBarcodeException;

public class BarcodeEAN8 extends BarcodeNumber {
    public BarcodeEAN8(EscPosPrinterSize printerSize, String code, float widthMM, float heightMM, int textPosition) throws EscPosBarcodeException {
        super(printerSize, EscPosPrinterCommands.BARCODE_TYPE_EAN8, code, widthMM, heightMM, textPosition);
    }

    @Override
    public int getCodeLength() {
        return 8;
    }
}
