package com.emh.thermalprinter.connection.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;

import com.emh.thermalprinter.connection.DeviceConnection;
import com.emh.thermalprinter.exceptions.EscPosConnectionException;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnection extends DeviceConnection {

    private BluetoothDevice device;
    private BluetoothSocket socket = null;

    /**
     * Create un instance of BluetoothConnection.
     *
     * @param device an instance of BluetoothDevice
     */
    public BluetoothConnection(BluetoothDevice device) {
        super();
        this.device = device;
    }

    /**
     * Get the instance BluetoothDevice connected.
     *
     * @return an instance of BluetoothDevice
     */
    public BluetoothDevice getDevice() {
        return this.device;
    }

    /**
     * Check if OutputStream is open.
     *
     * @return true if is connected
     */
    @Override
    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected() && super.isConnected();
    }

    /**
     * Start socket connection with the bluetooth device.
     */
    @SuppressLint("MissingPermission")
    public BluetoothConnection connect() throws EscPosConnectionException {
        if (this.isConnected()) {
            return this;
        }

        if (this.device == null) {
            throw new EscPosConnectionException("Bluetooth device is not connected.");
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        UUID uuid = getDeviceUUID();

        try {
            this.socket = this.device.createRfcommSocketToServiceRecord(uuid);
            bluetoothAdapter.cancelDiscovery();
            this.socket.connect();
            this.outputStream = this.socket.getOutputStream();
            this.data = new byte[0];
        } catch (IOException e) {
            e.printStackTrace();
            this.disconnect();
            throw new EscPosConnectionException("Unable to connect to bluetooth device.");
        }
        return this;
    }

    /**
     * Get bluetooth device UUID
     */
    @SuppressLint("MissingPermission")
    protected UUID getDeviceUUID() {
        ParcelUuid[] uuids = device.getUuids();
        // https://developer.android.com/reference/android/bluetooth/BluetoothDevice
        UUID sppUid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        return (uuids != null && uuids.length > 0) ? uuids[0].getUuid() : sppUid;
    }

    /**
     * Close the socket connection with the bluetooth device.
     */
    public BluetoothConnection disconnect() {
        this.data = new byte[0];
        if (this.outputStream != null) {
            try {
                this.outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.outputStream = null;
        }
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.socket = null;
        }
        return this;
    }

}
