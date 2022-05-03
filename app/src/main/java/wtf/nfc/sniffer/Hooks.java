package wtf.nfc.sniffer;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Hooks implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!"com.android.nfc".equals(lpparam.packageName))
            return;

        findAndHookMethod("com.android.nfc.NfcService", lpparam.classLoader, "onHostCardEmulationData", byte[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                byte[] data = (byte[]) param.args[0];

                String l = "Data in: " + bytesToHex(data);
                Log.i("NFCSNIFF", l);
                XposedBridge.log(l);
            }
        });

        findAndHookMethod("com.android.nfc.NfcService", lpparam.classLoader, "sendData", byte[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                byte[] data = (byte[]) param.args[0];

                String l = "Data out: " + bytesToHex(data);
                Log.i("NFCSNIFF", l);
                XposedBridge.log(l);
            }
        });

        Log.i("NFCSNIFF", "Hooked");
    }


    // source: http://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
