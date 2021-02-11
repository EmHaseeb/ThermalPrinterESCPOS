[![Jitpack package repository - ThermalPrinter ESC/POS v1.0](https://jitpack.io/v/EmHaseeb/ThermalPrinterESCPOS.svg)](https://jitpack.io/#DantSu/ESCPOS-ThermalPrinter-Android/2.0.8)
[![License: Apache 2.0](https://img.shields.io/badge/License-APACHE-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
# ThermalPrinter ESC/POS Android library

Useful library to help Android developers to print with (Bluetooth, WIFI, Network, USB) thermal printers ESC/POS.

## 	❤️ Supporting

⭐ Star this repository to support this library. You can contribute to increase the visibility of this library 🙂

## Table of contents

- [Android version](#android-version)
- [Tested printers](#tested-printers)
- [Test It !](#test-it-)
- [Installation](#installation)
- [Bluetooth](#bluetooth)
  - [Bluetooth permission](#bluetooth-permission)
  - [Bluetooth code example](#bluetooth-code-example)
- [TCP](#tcp)
  - [TCP permission](#tcp-permission)
  - [TCP code example](#tcp-code-example)
- [USB](#usb)
  - [USB permission](#usb-permission)
  - [USB code example](#usb-code-example)
- [Charset encoding](#charset-encoding)
- [Formatted text : syntax guide](#formatted-text--syntax-guide)
- [Class list](#class-list)
  - [BluetoothPrintersConnections](#user-content-class--comdantsuescposprinterconnectionbluetoothbluetoothprintersconnections)
  - [UsbPrintersConnections](#user-content-class--comdantsuescposprinterconnectionusbusbprintersconnections)
  - [EscPosPrinter](#user-content-class--comdantsuescposprinterescposprinter)
  - [PrinterTextParserImg](#user-content-class--comdantsuescposprintertextparserprintertextparserimg)
  - [EscPosCharsetEncoding](#user-content-class--comdantsuescposprinterescposcharsetencoding)
- [Contributing](#contributing)


## Android version

Minimum SDK Version 16 Android 4.1 and above are supported.


## Tested printers

1. [EPSON TM-T88VI (121): SERIAL, USB, ETHERNET](https://www.epson.eu/viewcon/corporatesite/products/mainunits/overview/22187) (Tested over Network/Ethernet 📶)
2. [EPSON TM-T20 SERIES](https://www.epson.eu/products/sd/pos-printer/epson-tm-t20-series) (Tested over USB 🔌)
3. [Sewoo LK-P41](https://www.eutronix.nl/en/products/sewoo-lk-p41/) (Bluetooth Printer)
5. [OmniLink® TM-m50 POS Thermal Receipt Printer](https://epson.com/For-Work/Printers/POS/OmniLink%C2%AE-TM-m50-POS-Thermal-Receipt-Printer/p/C31CH94012) (Tested over Wifi)

## Test it !

To test this library, it's very simple.

- Create a directory and open a terminal inside
- Run `git clone https://github.com/EmHaseeb/ThermalPrinterESCPOS.git .`
- Open the directory with Android Studio
- Test it

## Installation

**Step 1.** Add the [JitPack](https://jitpack.io/#EmHaseeb/ThermalPrinterESCPOS/1.0) repository to your build file. Add it in your root `/build.gradle` at the end of repositories:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency in `/app/build.gradle` :

```
dependencies {
    ...
    implementation 'com.github.EmHaseeb:ThermalPrinterESCPOS:1.0'
}
```

## Bluetooth

### Bluetooth permission

Be sure to have `<uses-permission android:name="android.permission.BLUETOOTH" />` in your `AndroidMenifest.xml`.

Also, you have to check the bluetooth permission in your app like this :

```java
if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, MainActivity.PERMISSION_BLUETOOTH);
} else {
    // Your code HERE
}
```

### Bluetooth code example

The code below is an example to write in your activity :

```java
EscPosPrinter printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 65f, 42);
printer
    .printFormattedText(
        "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, getApplicationContext().getResources().getDrawableForDensity(R.drawable.testp, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                        "[L]\n" +
                        "[C]<u><font size='big'>ORDER N°0125</font></u>\n[L]\n" +
                        "[L] _________________________________________\n" +
                        "[L] Description [R]Amount\n[L]\n" +
                        "[L] <b>Beef Burger [R]10.00\n" +
                        "[L] Sprite-200ml [R]3.00\n" +
                        "[L] _________________________________________\n" +
                        "[L] TOTAL [R]13.00 BD\n" +
                        "[L] Total Vat Collected [R]1.00 BD\n" +
                        "[L]\n" +
                        "[L] _________________________________________\n" +
                        "[L]\n" +
                        "[C]<font size='tall'>Customer Info</font>\n" +
                        "[L] EM Haseeb\n" +
                        "[L] 14 Streets\n" +
                        "[L] Cantt, LHR\n" +
                        "[L] Tel : +923040017916\n" +
                        "[L]\n" +
                        "[L] <barcode type='ean13'>831254784551</barcode>\n [L]\n" +
                        "[L] <qrcode>http://github.com/EmHaseeb/</qrcode>\n"+
                        "[L]\n [L]\n"
    );
```

## TCP

### TCP permission

Be sure to have `<uses-permission android:name="android.permission.INTERNET"/>` in your `AndroidMenifest.xml`.

### TCP code example

The code below is an example to write in your activity :

```java
new Thread(new Runnable() {
    public void run() {
        try {
            EscPosPrinter printer = new EscPosPrinter(new TcpConnection("192.168.1.3", 9300), 203, 65f, 42);
            printer
                .printFormattedText(
                    "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, getApplicationContext().getResources().getDrawableForDensity(R.drawable.testp, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                        "[L]\n" +
                        "[C]<u><font size='big'>ORDER N°0125</font></u>\n[L]\n" +
                        "[L] _________________________________________\n" +
                        "[L] Description [R]Amount\n[L]\n" +
                        "[L] <b>Beef Burger [R]10.00\n" +
                        "[L] Sprite-200ml [R]3.00\n" +
                        "[L] _________________________________________\n" +
                        "[L] TOTAL [R]13.00 BD\n" +
                        "[L] Total Vat Collected [R]1.00 BD\n" +
                        "[L]\n" +
                        "[L] _________________________________________\n" +
                        "[L]\n" +
                        "[C]<font size='tall'>Customer Info</font>\n" +
                        "[L] EM Haseeb\n" +
                        "[L] 14 Streets\n" +
                        "[L] Cantt, LHR\n" +
                        "[L] Tel : +923040017916\n" +
                        "[L]\n" +
                        "[L] <barcode type='ean13'>831254784551</barcode>\n [L]\n" +
                        "[L] <qrcode>http://github.com/EmHaseeb/</qrcode>\n"+
                        "[L]\n [L]\n"
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}).start();
```

## USB

### USB permission

Be sure to have `<uses-feature android:name="android.hardware.usb.host" />` in your `AndroidMenifest.xml`.

You have to check the USB permission in your app like this :

```java
private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (MainActivity.ACTION_USB_PERMISSION.equals(action)) {
            synchronized (this) {
                UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if (usbManager != null && usbDevice != null) {
                        // YOUR PRINT CODE HERE
                    }
                }
            }
        }
    }
};

public void printUsb() {
    UsbConnection usbConnection = UsbPrintersConnections.selectFirstConnected(this);
    UsbManager usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
    if (usbConnection != null && usbManager != null) {
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(MainActivity.ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(MainActivity.ACTION_USB_PERMISSION);
        registerReceiver(this.usbReceiver, filter);
        usbManager.requestPermission(usbConnection.getDevice(), permissionIntent);
    }
}
```

### USB code example

The code below is an example to write in your activity :

```java
EscPosPrinter printer = new EscPosPrinter(new UsbConnection(usbManager, usbDevice), 203, 65f, 42);
printer
    .printFormattedText(
        "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, getApplicationContext().getResources().getDrawableForDensity(R.drawable.testp, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                        "[L]\n" +
                        "[C]<u><font size='big'>ORDER N°0125</font></u>\n[L]\n" +
                        "[L] _________________________________________\n" +
                        "[L] Description [R]Amount\n[L]\n" +
                        "[L] <b>Beef Burger [R]10.00\n" +
                        "[L] Sprite-200ml [R]3.00\n" +
                        "[L] _________________________________________\n" +
                        "[L] TOTAL [R]13.00 BD\n" +
                        "[L] Total Vat Collected [R]1.00 BD\n" +
                        "[L]\n" +
                        "[L] _________________________________________\n" +
                        "[L]\n" +
                        "[C]<font size='tall'>Customer Info</font>\n" +
                        "[L] EM Haseeb\n" +
                        "[L] 14 Streets\n" +
                        "[L] Cantt, LHR\n" +
                        "[L] Tel : +923040017916\n" +
                        "[L]\n" +
                        "[L] <barcode type='ean13'>831254784551</barcode>\n [L]\n" +
                        "[L] <qrcode>http://github.com/EmHaseeb/</qrcode>\n"+
                        "[L]\n [L]\n"
		
    );
```

Below a picture of the receipt printed with the code above :

![Example of a printed receipt](https://i.imgur.com/mIvX6iK.jpg)

## Characterset encoding

To change charset encoding of the printer, use `EscPosCharsetEncoding` class :

```java
EscPosPrinter printer = new EscPosPrinter(deviceConnection, 203, 48f, 32, new EscPosCharsetEncoding("windows-1252", 16));
```

`escPosCharsetId` may change with printer model.
[Follow this link to find `escPosCharsetId` that works with many printers](https://www.epson-biz.com/modules/ref_escpos/index.php?content_id=32)

## Formatted text : syntax guide

### New line

Use `\n` to create a new line of text.

### Text alignment and column separation

Add an alignment tag on a same line of text implicitly create a new column.

Column alignment tags :

- `[L]` : left side alignment
- `[C]` : center alignment
- `[R]` : right side alignment

Example :

- `[L]Some text` : One column aligned to left
- `[C]Some text` : One column aligned to center
- `[R]Some text` : One column aligned to right
- `[L]Some text[L]Some other text` : Two columns aligned to left. `Some other text` starts in the center of the paper.
- `[L]Some text[R]Some other text` : Two columns, first aligned to left, second aligned to right. `Some other text` is printed at the right of paper.
- `[L]Some[R]text[R]here` : Three columns.
- `[L][R]text[R]here` : Three columns. The first is empty but it takes a third of the available space.

### Font

#### Size

`<font></font>` tag allows you to change the font size and color. Default size is `normal` / `black`.

- `<font size='normal'>Some text</font>` : Normal size
- `<font size='wide'>Some text</font>` : Double width of medium size
- `<font size='tall'>Some text</font>` : Double height of medium size
- `<font size='big'>Some text</font>` : Double width and height of medium size

- `<font color='black'>Some text</font>` : black text - white background
- `<font color='bg-black'>Some text</font>` : white text - black background
- `<font color='red'>Some text</font>` : red text - white background (Not working on all printer)
- `<font color='bg-red'>Some text</font>` : white text - red background (Not working on all printer)

#### Bold

`<b></b>` tag allows you to change the font weight.

- `<b>Some text</b>`

#### Underline

`<u></u>` tag allows you to underline the text.

- `<u>Some text</u>` text underlined
- `<u type='double'>Some text</u>` text double-strike (Not working on all printer)

### Image

`<img></img>` tag allows you to print image. Inside the tag you need to write a hexadecimal string of an image.

Use `PrinterTextParserImg.bitmapToHexadecimalString` to convert `Drawable`, `BitmapDrawable` or `Bitmap` to hexadecimal string.

- `<img>`hexadecimal string of an image`</img>`

**⚠ WARNING ⚠** : This tag has several constraints :

- A line that contains `<img></img>` can have only one alignment tag and it must be at the beginning of the line.
- `<img>` must be directly preceded by nothing or an alignment tag (`[L][C][R]`).
- `</img>` must be directly followed by a new line `\n`.
- You can't write text on a line that contains `<img></img>`.

### Barcode

`<barcode></barcode>` tag allows you to print a barcode. Inside the tag you need to write the code number to print.

- `<barcode>451278452159</barcode>` : **(12 numbers)**  
Prints a EAN13 barcode (height: 10mm, width: ~70% printer width, text: displayed below).
- `<barcode type='ean8'>4512784</barcode>` : **(7 numbers)**  
Prints a EAN8 barcode (height: 10mm, width: ~70% printer width, text: displayed below).
- `<barcode type='upca' height='20'>4512784521</barcode>` : **(11 numbers)**  
Prints a UPC-A barcode (height: 20mm, width: ~70% printer width, text: displayed below).
- `<barcode type='upce' height='25' width='50' text='none'>512789</barcode>` : **(6 numbers)**  
Prints a UPC-E barcode (height: 25mm, width: ~50mm, text: hidden).
- `<barcode type='128' width='40' text='above'>DantSu</barcode>` : **(string)**  
Prints a barcode 128 (height: 10mm, width: ~40mm, text: displayed above).

**⚠ WARNING ⚠** : This tag has several constraints :

- A line that contains `<barcode></barcode>` can have only one alignment tag and it must be at the beginning of the line.
- `<barcode>` must be directly preceded by nothing or an alignment tag (`[L][C][R]`).
- `</barcode>` must be directly followed by a new line `\n`.
- You can't write text on a line that contains `<barcode></barcode>`.

### QR Code

`<qrcode></qrcode>` tag allows you to print a QR code. Inside the tag you need to write the QR code data.

- `<qrcode>http://www.developpeur-web.dantsu.com/</qrcode>` :
Prints a QR code with a width and height of 20 millimeters.
- `<qrcode size='25'>123456789</qrcode>` :
Prints a QR code with a width and height of 25 millimeters.

**⚠ WARNING ⚠** : This tag has several constraints :

- A line that contains `<qrcode></qrcode>` can have only one alignment tag and it must be at the beginning of the line.
- `<qrcode>` must be directly preceded by nothing or an alignment tag (`[L][C][R]`).
- `</qrcode>` must be directly followed by a new line `\n`.
- You can't write text on a line that contains `<qrcode></qrcode>`.

## Class list

### Class : `com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections`

#### **Static** Method : `selectFirstPaired()`
Easy way to get the first bluetooth printer paired / connected.
- **return** `BluetoothConnection`

#### Method : `getList()`
Get a list of bluetooth printers.
- **return** `BluetoothConnection[]`

### Class : `com.dantsu.escposprinter.connection.usb.UsbPrintersConnections`

#### **Static** Method : `selectFirstConnected()`
Easy way to get the first USB printer connected.
- **return** `UsbConnection`

#### Method : `getList()`
Get a list of USB printers.
- **return** `UsbConnection[]`

### Class : `com.dantsu.escposprinter.EscPosPrinter`

#### Constructor : `EscPosPrinter(DeviceConnection printer, int printerDpi, float printingWidthMM, int nbrCharactersPerLine [, EscPosCharsetEncoding charsetEncoding])`
- **param** `DeviceConnection printer` : Instance of a connected printer
- **param** `int printerDpi` : DPI of the connected printer
- **param** `float printerWidthMM` : Printing width in millimeters
- **param** `int printerNbrCharactersPerLine` : The maximum number of medium sized characters that can be printed on a line.
- **param** `EscPosCharsetEncoding charsetEncoding` *(optional)* : Set the charset encoding.

#### Method : `disconnectPrinter()`
Close the connection with the printer.
- **return** `Printer` : Fluent interface

#### Method : `getNbrCharactersPerLine()`
Get the maximum number of characters that can be printed on a line.
- **return** `int`

#### Method : `getPrinterWidthMM()`
Get the printing width in millimeters
- **return** `float`

#### Method : `getPrinterDpi()`
Get the printer DPI
- **return** `int`

#### Method : `getPrinterWidthPx()`
Get the printing width in dot
- **return** `int`

#### Method : `getPrinterCharSizeWidthPx()`
Get the number of dot that a printed character contain
- **return** `int`

#### Method : `mmToPx(float mmSize)`
Convert the mmSize variable from millimeters to dot.
- **param** `float mmSize` : Distance in millimeters to be converted
- **return** `int` : Dot size of mmSize.

#### Method : `printFormattedText(String text)`
Print a formatted text and feed paper (20 millimeters). Read the ["Formatted Text : Syntax guide" section](#formatted-text--syntax-guide) for more information about text formatting options.
- **param** `String text` : Formatted text to be printed.
- **return** `Printer` : Fluent interface

#### Method : `printFormattedTextAndCut(String text)`
Print a formatted text, feed paper (20 millimeters) and cut the paper. Read the ["Formatted Text : Syntax guide" section](#formatted-text--syntax-guide) for more information about text formatting options.
- **param** `String text` : Formatted text to be printed.
- **return** `Printer` : Fluent interface

#### Method : `printFormattedText(String text, float mmFeedPaper)`
Print a formatted text and feed paper (`mmFeedPaper` millimeters). Read the ["Formatted Text : Syntax guide" section](#formatted-text--syntax-guide) for more information about text formatting options.
- **param** `String text` : Formatted text to be printed.
- **param** `float mmFeedPaper` : Millimeter distance feed paper at the end.
- **return** `Printer` : Fluent interface

#### Method : `printFormattedTextAndCut(String text, float mmFeedPaper)`
Print a formatted text, feed paper (`mmFeedPaper` millimeters) and cut the paper. Read the ["Formatted Text : Syntax guide" section](#formatted-text--syntax-guide) for more information about text formatting options.
- **param** `String text` : Formatted text to be printed.
- **param** `float mmFeedPaper` : Millimeter distance feed paper at the end.
- **return** `Printer` : Fluent interface

#### Method : `printFormattedText(String text, int dotsFeedPaper)`
Print a formatted text and feed paper (`dotsFeedPaper` dots). Read the ["Formatted Text : Syntax guide" section](#formatted-text--syntax-guide) for more information about text formatting options.
- **param** `String text` : Formatted text to be printed.
- **param** `int dotsFeedPaper` : Distance feed paper at the end.
- **return** `Printer` : Fluent interface

#### Method : `printFormattedTextAndCut(String text, int dotsFeedPaper)`
Print a formatted text, feed paper (`dotsFeedPaper` dots) and cut the paper. Read the ["Formatted Text : Syntax guide" section](#formatted-text--syntax-guide) for more information about text formatting options.
- **param** `String text` : Formatted text to be printed.
- **param** `int dotsFeedPaper` : Distance feed paper at the end.
- **return** `Printer` : Fluent interface

#### Method : `bitmapToBytes(Bitmap bitmap)`
Convert Bitmap object to ESC/POS image.
- **param** `Bitmap bitmap` : Instance of Bitmap
- **return** `byte[]` : Bytes contain the image in ESC/POS command

### Class : `com.dantsu.escposprinter.textparser.PrinterTextParserImg`

#### **Static** Method : `bitmapToHexadecimalString(Printer printer, Drawable drawable)`
Convert Drawable instance to a hexadecimal string of the image data.
- **param** `Printer printer` : A Printer instance that will print the image.
- **param** `Drawable drawable` : Drawable instance to be converted.
- **return** `String` : A hexadecimal string of the image data. Empty string if Drawable cannot be cast to BitmapDrawable.

#### **Static** Method : `bitmapToHexadecimalString(Printer printer, BitmapDrawable bitmapDrawable)`
Convert BitmapDrawable instance to a hexadecimal string of the image data.
- **param** `Printer printer` : A Printer instance that will print the image.
- **param** `BitmapDrawable bitmapDrawable` : BitmapDrawable instance to be converted.
- **return** `String` : A hexadecimal string of the image data.

#### **Static** Method : `bitmapToHexadecimalString(Printer printer, Bitmap bitmap)`
Convert Bitmap instance to a hexadecimal string of the image data.
- **param** `Printer printer` : A Printer instance that will print the image.
- **param** `Bitmap bitmap` : Bitmap instance to be converted.
- **return** `String` : A hexadecimal string of the image data.

#### **Static** Method : `bytesToHexadecimalString(byte[] bytes)`
Convert byte array to a hexadecimal string of the image data.
- **param** `byte[] bytes` : Bytes contain the image in ESC/POS command.
- **return** `String` : A hexadecimal string of the image data.

#### **Static** Method : `hexadecimalStringToBytes(String hexString)`
Convert hexadecimal string of the image data to bytes ESC/POS command.
- **param** `String hexString` : Hexadecimal string of the image data.
- **return** `byte[]` : Bytes contain the image in ESC/POS command.

### Class : `com.dantsu.escposprinter.EscPosCharsetEncoding`

#### Constructor : `EscPosCharsetEncoding(String charsetName, int escPosCharsetId)`
- **param** `charsetName` Name of charset encoding (Ex: ISO-8859-1)
- **param** `escPosCharsetId` Id of charset encoding for your printer (Ex: 6)

## Contributing

Please fork this repository and contribute back using pull requests.

Any contributions, large or small, major features, bug fixes, are welcomed and appreciated but will be thoroughly reviewed
