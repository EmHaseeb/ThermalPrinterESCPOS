package com.emh.thermalprinter.textparser;

import com.emh.thermalprinter.EscPosPrinterCommands;
import com.emh.thermalprinter.exceptions.EscPosEncodingException;

public interface IPrinterTextParserElement {
    int length() throws EscPosEncodingException;
    IPrinterTextParserElement print(EscPosPrinterCommands printerSocket) throws EscPosEncodingException;
}
