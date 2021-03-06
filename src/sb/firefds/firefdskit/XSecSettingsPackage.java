/*
 * Copyright (C) 2016 Mohamed Karami for XTouchWiz Project (Wanam@xda)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sb.firefds.firefdskit;

import android.content.Context;
import android.os.Bundle;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import sb.firefds.firefdskit.utils.Packages;

public class XSecSettingsPackage {

	private static ClassLoader classLoader;

	public static void doHook(XSharedPreferences prefs, ClassLoader classLoader) {

		XSecSettingsPackage.classLoader = classLoader;
		
		if (prefs.getBoolean("disableBluetoothScanDialog", false)) {
			try {
				disableBluetoothScanDialog();
			} catch (Throwable e) {
				XposedBridge.log(e.toString());

			}
		}

		if (prefs.getBoolean("disableTetherProvisioning", false)) {
			try {
				disableTetherProvisioning();
			} catch (Throwable e) {
				XposedBridge.log(e.toString());

			}
		}
	}

	private static void disableTetherProvisioning() {
		try {
			XposedHelpers.findAndHookMethod("com.android.settingslib.TetherUtil", classLoader, "isProvisioningNeeded",
					Context.class, XC_MethodReplacement.returnConstant(Boolean.FALSE));

		} catch (Throwable e) {
			XposedBridge.log(e);
		}

		try {
			XposedHelpers.findAndHookMethod(Packages.SETTINGS + ".wifi.mobileap.WifiApBroadcastReceiver", classLoader,
					"isProvisioningNeeded", Context.class, XC_MethodReplacement.returnConstant(Boolean.FALSE));
		} catch (Throwable e) {
			XposedBridge.log(e);
		}

		try {
			XposedHelpers.findAndHookMethod(Packages.SETTINGS + ".wifi.mobileap.WifiApSwitchEnabler", classLoader,
					"isProvisioningNeeded", XC_MethodReplacement.returnConstant(Boolean.FALSE));
		} catch (Throwable e) {
			XposedBridge.log(e);
		}

		try {

			XposedHelpers.findAndHookMethod(Packages.SETTINGS + ".wifi.mobileap.WifiApWarning", classLoader,
					"isProvisioningNeeded", XC_MethodReplacement.returnConstant(Boolean.FALSE));
		} catch (Throwable e) {
			XposedBridge.log(e);
		}

		try {
			XSystemProp.set("net.tethering.noprovisioning", "false");
			XSystemProp.set("Provisioning.disable", "0");
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
	}

	private static void disableBluetoothScanDialog() {
		try {
			XposedHelpers.findAndHookMethod(Packages.SETTINGS + ".bluetooth.BluetoothScanDialog", classLoader,
					"onCreate", Bundle.class, new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					((android.app.Activity) param.thisObject).finish();
				}
			});
		} catch (Throwable e) {
			XposedBridge.log(e.toString());

		}
	}

}
