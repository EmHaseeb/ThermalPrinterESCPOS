package com.emh.thermalprinter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.emh.thermalprinter.EscPosPrinter;
import com.emh.thermalprinter.connection.DeviceConnection;
import com.emh.thermalprinter.connection.bluetooth.BluetoothConnection;
import com.emh.thermalprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.emh.thermalprinter.connection.tcp.TcpConnection;
import com.emh.thermalprinter.connection.usb.UsbConnection;
import com.emh.thermalprinter.connection.usb.UsbPrintersConnections;
import com.emh.thermalprinter.exceptions.EscPosBarcodeException;
import com.emh.thermalprinter.exceptions.EscPosConnectionException;
import com.emh.thermalprinter.exceptions.EscPosEncodingException;
import com.emh.thermalprinter.exceptions.EscPosParserException;
import com.emh.thermalprinter.textparser.PrinterTextParserImg;
import com.emh.thermalprinter.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Activity activity = MainActivity.this;

    Button bt, tcp, usb, cashBox, btBrowse;
    EditText ip, port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btBrowse = findViewById(R.id.browse_bt);
        bt = findViewById(R.id.button_bluetooth);
        tcp = findViewById(R.id.button_tcp);
        usb = findViewById(R.id.button_usb);
        ip = findViewById(R.id.edittext_tcp_ip);
        port = findViewById(R.id.edittext_tcp_port);
        cashBox = findViewById(R.id.openCashBox);

        cashBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ip.getText().toString().length() > 0 && port.getText().toString().length() > 0) {

                    openCashBox(ip.getText().toString(), Integer.parseInt(port.getText().toString()));

                } else {
                    Toast.makeText(activity, "IP or Port is In-Correct!", Toast.LENGTH_LONG).show();
                }

            }
        });

        btBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseBluetoothDevice();
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printBluetooth();
            }
        });

        usb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printUsb();
            }
        });

        tcp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ip.getText().toString().length() > 0 && port.getText().toString().length() > 0) {

                    printTcp(ip.getText().toString(), Integer.parseInt(port.getText().toString()));

                } else {
                    Toast.makeText(activity, "IP or Port is In-Correct!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * Open Cash Box Only Part
     */


    public void openCashBox(String ip, int port) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    EscPosPrinter printer = new EscPosPrinter(new TcpConnection(ip, port), 203, 65f, 42);
                    printer.openCashBox();
                    printer.disconnectPrinter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * BLUETOOTH PART
     */

    public static final int PERMISSION_BLUETOOTH = 1;
    public static final int PERMISSION_BLUETOOTH_ADMIN = 2;
    public static final int PERMISSION_BLUETOOTH_CONNECT = 3;
    public static final int PERMISSION_BLUETOOTH_SCAN = 4;

    public interface OnBluetoothPermissionsGranted {
        void onPermissionsGranted();
    }
    public OnBluetoothPermissionsGranted onBluetoothPermissionsGranted;

    private BluetoothConnection selectedDevice;
    public void browseBluetoothDevice() {
        this.checkBluetoothPermissions(() -> {
            final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();

            if (bluetoothDevicesList != null) {
                final String[] items = new String[bluetoothDevicesList.length + 1];
                items[0] = "Default printer";
                int i = 0;
                for (BluetoothConnection device : bluetoothDevicesList) {
                    items[++i] = device.getDevice().getName();
                }

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Bluetooth printer selection");
                alertDialog.setItems(
                        items,
                        (dialogInterface, i1) -> {
                            int index = i1 - 1;
                            if (index == -1) {
                                selectedDevice = null;
                            } else {
                                selectedDevice = bluetoothDevicesList[index];
                            }
                            btBrowse.setText(items[i1]);
                        }
                );

                AlertDialog alert = alertDialog.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();
            }
        });

    }
    public void printBluetooth() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, MainActivity.PERMISSION_BLUETOOTH);
        } else {
            EscPosPrinter printer = null;
            try {
                printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32);
                printer.printFormattedTextAndCut(
                        "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, getApplicationContext().getResources().getDrawableForDensity(R.drawable.testp, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                                "[L]\n" +
                                "[C]<u><font size='big'>ORDER N°1125</font></u>\n[L]\n" +
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
                                "[L] <barcode type='ean13' height='10'>831254784551</barcode>\n[L]\n" +
                                "[L] <qrcode>http://github.com/EmHaseeb/</qrcode>\n[L]\n[L]\n[L]\n"
                );
                printer.disconnectPrinter();
            } catch (EscPosConnectionException e) {
                e.printStackTrace();
                new AlertDialog.Builder(this)
                        .setTitle("Broken connection")
                        .setMessage(e.getMessage())
                        .show();
            } catch (EscPosParserException e) {
                e.printStackTrace();
                new AlertDialog.Builder(this)
                        .setTitle("Invalid formatted text")
                        .setMessage(e.getMessage())
                        .show();
            } catch (EscPosEncodingException e) {
                e.printStackTrace();
                new AlertDialog.Builder(this)
                        .setTitle("Bad selected encoding")
                        .setMessage(e.getMessage())
                        .show();
            } catch (EscPosBarcodeException e) {
                e.printStackTrace();
                new AlertDialog.Builder(this)
                        .setTitle("Invalid barcode")
                        .setMessage(e.getMessage())
                        .show();
            }
        }
    }

    public void checkBluetoothPermissions(OnBluetoothPermissionsGranted onBluetoothPermissionsGranted) {
        this.onBluetoothPermissionsGranted = onBluetoothPermissionsGranted;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, MainActivity.PERMISSION_BLUETOOTH);
        } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, MainActivity.PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, MainActivity.PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, MainActivity.PERMISSION_BLUETOOTH_SCAN);
        } else {
            this.onBluetoothPermissionsGranted.onPermissionsGranted();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case MainActivity.PERMISSION_BLUETOOTH:
                case MainActivity.PERMISSION_BLUETOOTH_ADMIN:
                case MainActivity.PERMISSION_BLUETOOTH_CONNECT:
                case MainActivity.PERMISSION_BLUETOOTH_SCAN:
                    this.checkBluetoothPermissions(this.onBluetoothPermissionsGranted);
                    break;
            }
        }
    }


    /**
     * USB PART
     */

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
                            printIt(usbManager, usbDevice);
                        }
                    }
                }
            }
        }
    };

    public void printUsb() {
        UsbConnection usbConnection = UsbPrintersConnections.selectFirstConnected(this);
        UsbManager usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);

        if (usbConnection == null || usbManager == null) {
            new AlertDialog.Builder(this)
                    .setTitle("USB Connection")
                    .setMessage("No USB printer found.")
                    .show();
            return;
        }

        PendingIntent permissionIntent = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(MainActivity.ACTION_USB_PERMISSION),
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0
        );
        IntentFilter filter = new IntentFilter(MainActivity.ACTION_USB_PERMISSION);
        registerReceiver(this.usbReceiver, filter);
        usbManager.requestPermission(usbConnection.getDevice(), permissionIntent);
    }

    public void printIt(UsbManager usbManager, UsbDevice usbDevice) {

        try {
            EscPosPrinter printer = new EscPosPrinter(new UsbConnection(usbManager, usbDevice), 203, 65f, 42);
            printer.printFormattedTextAndCut(
                    "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, getApplicationContext().getResources().getDrawableForDensity(R.drawable.testp, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                            "[L]\n" +
                            "[C]<u><font size='big'>ORDER N°1125</font></u>\n[L]\n" +
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
                            "[L] <barcode type='ean13' height='10'>831254784551</barcode>\n[L]\n" +
                            "[L] <qrcode>http://github.com/EmHaseeb/</qrcode>\n[L]\n[L]\n[L]\n"
            );
            printer.disconnectPrinter();
        } catch (EscPosConnectionException e) {
            e.printStackTrace();
            new AlertDialog.Builder(activity)
                    .setTitle("Broken connection")
                    .setMessage(e.getMessage())
                    .show();
        } catch (EscPosParserException e) {
            e.printStackTrace();
            new AlertDialog.Builder(activity)
                    .setTitle("Invalid formatted text")
                    .setMessage(e.getMessage())
                    .show();
        } catch (EscPosEncodingException e) {
            e.printStackTrace();
            new AlertDialog.Builder(activity)
                    .setTitle("Bad selected encoding")
                    .setMessage(e.getMessage())
                    .show();
        } catch (EscPosBarcodeException e) {
            e.printStackTrace();
            new AlertDialog.Builder(activity)
                    .setTitle("Invalid barcode")
                    .setMessage(e.getMessage())
                    .show();
        }

    }


    /**
     * ESC/POS - Wifi/Ethernet/Network PRINTER PART
     */


    public void printTcp(String ip, int port) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    EscPosPrinter printer = new EscPosPrinter(new TcpConnection(ip, port), 203, 65f, 42);
                    printer.printFormattedTextAndCut(
                            "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, getApplicationContext().getResources().getDrawableForDensity(R.drawable.testp, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                                    "[L]\n" +
                                    "[C]<u><font size='big'>ORDER N°1125</font></u>\n[L]\n" +
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
                                    "[L] <barcode type='ean13' height='10'>831254784551</barcode>\n[L]\n" +
                                    "[L] <qrcode>http://github.com/EmHaseeb/</qrcode>\n[L]\n[L]\n[L]\n"
                    );
                    printer.disconnectPrinter();
                } catch (EscPosConnectionException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(activity)
                            .setTitle("Broken connection")
                            .setMessage(e.getMessage())
                            .show();
                } catch (EscPosParserException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(activity)
                            .setTitle("Invalid formatted text")
                            .setMessage(e.getMessage())
                            .show();
                } catch (EscPosEncodingException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(activity)
                            .setTitle("Bad selected encoding")
                            .setMessage(e.getMessage())
                            .show();
                } catch (EscPosBarcodeException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(activity)
                            .setTitle("Invalid barcode")
                            .setMessage(e.getMessage())
                            .show();
                }
            }
        }).start();

    }


}
