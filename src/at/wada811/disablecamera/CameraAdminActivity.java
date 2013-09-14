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

import java.io.IOException;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class CameraAdminActivity extends Activity implements OnClickListener {

    // DEBUG
    private static final String TAG                      = CameraAdminActivity.class.getSimpleName();

    // DeviceAdmin
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName       mCameraAdminReceiver;
    private static final int    REQUEST_ADD_DEVICE_ADMIN = 1;

    // UI
    private Button              mAdminActivateButton;
    private Button              mChangeStatusButton;
    private Button              mCheckCameraStatusButton;
    private Button              mLaunchCameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DevicePolicyManagerインスタンス生成
        mDevicePolicyManager = (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        mCameraAdminReceiver = new ComponentName(this, CameraAdminReceiver.class);

        // 管理者権限の有効/無効を変更する
        mAdminActivateButton = (Button)findViewById(R.id.adminActivateButton);
        mAdminActivateButton.setOnClickListener(this);
        // カメラの有効/無効を変更する
        mChangeStatusButton = (Button)findViewById(R.id.changeStatusButton);
        mChangeStatusButton.setOnClickListener(this);
        // カメラの有効/無効の状態をToastで表示する
        mCheckCameraStatusButton = (Button)findViewById(R.id.checkCameraStatusButton);
        mCheckCameraStatusButton.setOnClickListener(this);
        mLaunchCameraButton = (Button)findViewById(R.id.launchCameraButton);
        mLaunchCameraButton.setOnClickListener(this);

        // 初期状態の表示への反映
        setAdminStatus(mDevicePolicyManager.isAdminActive(mCameraAdminReceiver));
        setCameraStatus(mDevicePolicyManager.getCameraDisabled(null));
    }

    /**
     * 管理者権限の状態を表示に反映
     */
    private void setAdminStatus(boolean isAdminActive){
        if(isAdminActive){
            mAdminActivateButton.setText(R.string.deactivate_admin);
        }else{
            mAdminActivateButton.setText(R.string.activate_admin);
        }
        mChangeStatusButton.setEnabled(isAdminActive);
    }

    /**
     * カメラの有効/無効の状態を表示に反映
     */
    private void setCameraStatus(boolean isCameraDisabled){
        if(isCameraDisabled){
            mChangeStatusButton.setText(R.string.enable_camera);
        }else{
            mChangeStatusButton.setText(R.string.disable_camera);
        }
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.adminActivateButton:
                changeAdminActivated();
                break;
            case R.id.changeStatusButton:
                changeCameraStatus();
                break;
            case R.id.checkCameraStatusButton:
                boolean isCameraDisabled = mDevicePolicyManager.getCameraDisabled(null);
                int resId = isCameraDisabled ? R.string.is_disable_camera : R.string.is_enable_camera;
                Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
                break;
            case R.id.launchCameraButton:
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 管理者権限の有効/無効を変更する
     */
    private void changeAdminActivated(){
        if(mDevicePolicyManager.isAdminActive(mCameraAdminReceiver)){
            deactivateAdmin();
        }else{
            activateAdmin();
        }
    }

    /**
     * デバイス管理者権限を有効にする画面を呼び出す
     */
    private void activateAdmin(){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mCameraAdminReceiver);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.admin_explanation));
        startActivityForResult(intent, REQUEST_ADD_DEVICE_ADMIN);
    }

    /**
     * デバイス管理者権限の変更結果を受け取る
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case REQUEST_ADD_DEVICE_ADMIN:
                if(resultCode == Activity.RESULT_OK){
                    Log.i(TAG, "Administration enabled!");
                    setAdminStatus(true);
                }else{
                    Log.i(TAG, "Administration enable canceled!");
                }
                break;
        }
    }

    /**
     * デバイス管理者権限を無効にする
     */
    private void deactivateAdmin(){
        mDevicePolicyManager.removeActiveAdmin(mCameraAdminReceiver);
        setAdminStatus(false);
    }

    /**
     * デバイスの管理者権限がアクティブの場合はカメラの有効/無効を切り替える
     * 
     * @return 変更後のカメラの有効/無効フラグ
     */
    private boolean changeCameraStatus(){
        if(mDevicePolicyManager.isAdminActive(mCameraAdminReceiver) && isEnabledToUsePolicy(DeviceAdminInfo.USES_POLICY_DISABLE_CAMERA)){
            boolean isCameraDisabled = mDevicePolicyManager.getCameraDisabled(mCameraAdminReceiver);
            // 設定を変更するためフラグ反転
            isCameraDisabled = !isCameraDisabled;
            try{
                mDevicePolicyManager.setCameraDisabled(mCameraAdminReceiver, isCameraDisabled);
            }catch(SecurityException e){
                e.printStackTrace();
                Log.e(TAG, e.getMessage(), e);
            }
            setCameraStatus(isCameraDisabled);
            return isCameraDisabled;
        }
        return false;
    }

    /**
     * XMLリソースに宣言してるかのチェック(自分で作っているアプリなのでチェックは不要だけどサンプルとして実装)
     */
    private boolean isEnabledToUsePolicy(int usePolicy){
        List<ResolveInfo> resolveInfos = getPackageManager().queryBroadcastReceivers(new Intent(DeviceAdminReceiver.ACTION_DEVICE_ADMIN_ENABLED), PackageManager.GET_META_DATA);
        int count = resolveInfos == null ? 0 : resolveInfos.size();
        for(int i = 0; i < count; i++){
            ResolveInfo resolveInfo = resolveInfos.get(i);
            try{
                DeviceAdminInfo deviceAdminInfo = new DeviceAdminInfo(this, resolveInfo);
                if(deviceAdminInfo.isVisible() && deviceAdminInfo.getPackageName().equals(getPackageName())){
                    return deviceAdminInfo.usesPolicy(usePolicy);
                }
            }catch(XmlPullParserException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
