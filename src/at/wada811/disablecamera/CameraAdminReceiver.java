/*
 * Copyright 2013 wada811<at.wada811@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.wada811.disablecamera;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CameraAdminReceiver extends DeviceAdminReceiver {

    private static final String TAG = CameraAdminReceiver.class.getSimpleName();

    @Override
    public void onEnabled(Context context, Intent intent){
        super.onEnabled(context, intent);
        Log.d(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context, Intent intent){
        super.onDisabled(context, intent);
        Log.d(TAG, "onDisabled");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent){
        Log.d(TAG, "onDisableRequested");
        return super.onDisableRequested(context, intent);
    }
}
