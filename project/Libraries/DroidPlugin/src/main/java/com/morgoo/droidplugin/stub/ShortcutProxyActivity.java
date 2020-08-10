/*
**        DroidPlugin Project
**
** Copyright(c) 2015 Andy Zhang <zhangyong232@gmail.com>
**
** This file is part of DroidPlugin.
**
** DroidPlugin is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** DroidPlugin is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with DroidPlugin.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/

package com.morgoo.droidplugin.stub;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.morgoo.droidplugin.core.Env;
import com.morgoo.droidplugin.pm.PluginManager;

import java.net.URISyntaxException;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/5/27.
 */
public class ShortcutProxyActivity /*extends Activity*/ {

    private Activity mObj;

//    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            //super.onCreate(savedInstanceState);
            Intent intent = mObj.getIntent();

            if (intent != null) {
                Intent forwordIntent = getForwarIntent();
                if (forwordIntent != null) {
                    forwordIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    forwordIntent.putExtras(intent);
                    //安全审核问题
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        forwordIntent.setSelector(null);
                    }
                    if (PluginManager.getInstance().isConnected()) {
                        if (isPlugin(forwordIntent)) {
                            execStartForwordIntent(forwordIntent);
                        }
                        mObj.finish();
                    } else {
                        waitAndStart(forwordIntent);
                    }
                } else {
                    mObj.finish();
                }
            } else {
                mObj.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mObj.finish();
        }
    }

    public void onCreate(Activity obj, @Nullable Bundle savedInstanceState) {
        mObj = obj;
        onCreate(savedInstanceState);
    }

    protected void execStartForwordIntent(Intent forwordIntent) {
        mObj.startActivity(forwordIntent);
    }

    private boolean isPlugin(Intent intent) {
        try {
            String pkg = null;
            if (intent.getComponent() != null && intent.getComponent().getPackageName() != null) {
                pkg = intent.getComponent().getPackageName();
            } else {
                ResolveInfo info = PluginManager.getInstance().resolveIntent(intent, null, 0);
                pkg = info.resolvePackageName;
            }
            return pkg != null && PluginManager.getInstance().isPluginPackage(pkg);
        } catch (Exception e) {
            return false;
        }
    }

    private void waitAndStart(final Intent forwordIntent) {
        new Thread() {
            @Override
            public void run() {
                try {
                    PluginManager.getInstance().waitForConnected();
                    if (isPlugin(forwordIntent)) {
                        execStartForwordIntent(forwordIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mObj.finish();
                }

            }
        }.start();
    }

    private Intent getForwarIntent() {
        Intent intent = mObj.getIntent();
        try {
            if (intent != null) {
                Intent forwordIntent = intent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
                String intentUri = intent.getStringExtra(Env.EXTRA_TARGET_INTENT_URI);
                if (intentUri != null) {
                    try {
                        Intent res = Intent.parseUri(intentUri, 0);
                        return res;
                    } catch (URISyntaxException e) {
                    }
                } else if (forwordIntent != null) {
                    return forwordIntent;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
}
