/*
 * Copyright (c) 2018 Martin Pfeffer
 *
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

package com.pepperonas.m104.dialogs;

import android.content.Context;

import com.pepperonas.m104.R;
import com.pepperonas.materialdialog.MaterialDialog;
import com.pepperonas.materialdialog.model.Changelog;
import com.pepperonas.materialdialog.model.ReleaseInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class DialogChangelog {

    @SuppressWarnings("unused")
    private static final String TAG = "DialogChangelog";

    public DialogChangelog(Context context) {
        new MaterialDialog.Builder(context).title("Changelog")
                .changelogDialog(getChangelogs(), context.getString(R.string.bullet_release_info))
                .positiveText(R.string.ok)
                .show();
    }

    private List<Changelog> getChangelogs() {
        List<Changelog> changes = new ArrayList<>();

        changes.add(new Changelog("0.3.6-beta", "2018-02-08", new ReleaseInfo(
                "Improved notifications (Android O)",
                "Removed notification sounds")));

        changes.add(new Changelog("0.3.5-beta", "2018-02-08", new ReleaseInfo(
                "Fixed notifications (Android O)",
                "Fixed automatic startup")));

        changes.add(new Changelog("0.3.4", "2018-02-06", new ReleaseInfo(
                "Improved notifications",
                "Fixed permissions",
                "Added dialogs")));

        changes.add(new Changelog("0.3.3-beta", "2018-02-05", new ReleaseInfo(
                "Android-O support",
                "Removable clipboard encryption",
                "Fixed network usage info",
                "Improved charts' readability",
                "Updated SDK tools (27.0.2)",
                "Updated support libraries (27.0.2)",
                "Updated Play Services (11.8.0)",
                "Fixed possible null pointer",
                "Removed unused code")));

        changes.add(new Changelog("0.3.2-beta", "2017-04-20", new ReleaseInfo(
                "Back to Nougat (25.0.2)",
                "Fixed network notification",
                "Removed unused code",
                "Minified build",
                "Proguard enabled")));

        changes.add(new Changelog("0.3.1-beta", "2017-03-23", new ReleaseInfo(
                "Migrate to Android O (26.0.0 rc1)",
                "Optimized images",
                "Removed LRU cache")));

        changes.add(new Changelog("0.3.0", "2017-03-17", new ReleaseInfo(
                "Updated SDK tools (25.0.2)",
                "Fixed missing battery indicator")));

        changes.add(new Changelog("0.2.9", "2017-03-17", new ReleaseInfo(
                "Enabled shrink resources (-300 kB)",
                "Improved stability and performance",
                "Fixed array access in battery notification",
                "Removed ordered update")));

        changes.add(new Changelog("0.2.8", "2017-02-19", new ReleaseInfo(
                "Updated SDK tools (25.0.1)",
                "Updated support libraries (25.1.1)",
                "Updated andbasx (0.2.6)",
                "Updated material dialog libraries (0.2.5)",
                "Updated app-registry (0.1.0)",
                "Updated aes-preferences (0.0.6)")));

        changes.add(new Changelog("0.2.7", "2017-01-26", new ReleaseInfo(
                "Updated support libraries (25.1.0)",
                "Updated Play Services (10.0.1)")));

        changes.add(new Changelog("0.2.6", "2016-07-24", new ReleaseInfo(
                "Supported Android Nougat",
                "Updated support libraries (23.4.0)",
                "Updated Play Services (9.2.1)",
                "Fixed icons")));

        changes.add(new Changelog("0.2.5", "2016-05-03", new ReleaseInfo(
                "Minor fixes")));

        changes.add(new Changelog("0.2.4", "2016-04-10", new ReleaseInfo(
                "Minor fixes")));

        changes.add(new Changelog("0.2.3", "2016-02-20", new ReleaseInfo(
                "Removed unnecessary callbacks")));

        changes.add(new Changelog("0.2.2", "2016-02-11", new ReleaseInfo(
                "Fixed error (fast network speed)")));

        changes.add(new Changelog("0.2.1", "2016-02-11", new ReleaseInfo(
                "Minor fixes")));

        changes.add(new Changelog("0.2.0", "2016-02-10", new ReleaseInfo(
                "Improved performance",
                "Stability update")));

        changes.add(new Changelog("0.1.9", "2016-02-09", new ReleaseInfo(
                "Improved database access")));

        changes.add(new Changelog("0.1.8", "2016-02-05", new ReleaseInfo(
                "Fixed premium key")));

        changes.add(new Changelog("0.1.7", "2016-01-25", new ReleaseInfo(
                "Fixed animation when clipboard is wiped",
                "Fixed animation in network fragment",
                "Performance tweaks")));

        changes.add(new Changelog("0.1.6", "2016-01-17", new ReleaseInfo(
                "Fixed null-pointer in network fragment",
                "Root mode (experimental)",
                "Performance improvements",
                "Larger pictures in tutorial",
                "Smaller .apk size",
                "Improved layout alignment")));

        changes.add(new Changelog("0.1.5", "2016-01-16", new ReleaseInfo(
                "Added units",
                "Removed typos")));

        changes.add(new Changelog("0.1.4", "2016-01-14", new ReleaseInfo(
                "Added new animation for network chart",
                "Fixed verification process")));

        changes.add(new Changelog("0.1.3", "2016-01-13", new ReleaseInfo(
                "Fixed autos-tart")));

        changes.add(new Changelog("0.1.2", "2016-01-12", new ReleaseInfo(
                "Reduced power consumption (experimental)",
                "Prevent showing dialog if clipboard is empty",
                "Fixed null-pointer in Android 4.4")));

        changes.add(new Changelog("0.1.1", "2016-01-11", new ReleaseInfo(
                "Improved network chart",
                "Little performance improvements")));

        changes.add(new Changelog("0.1.0", "2016-01-10", new ReleaseInfo(
                "Added Google Analytics",
                "Minified build (approx. 2MB smaller)")));

        changes.add(new Changelog("0.0.9", "2016-01-09", new ReleaseInfo(
                "Improved performance in network fragment",
                "Added scroll animation")));

        changes.add(new Changelog("0.0.8", "2016-01-09", new ReleaseInfo(
                "Added image in navigation drawer",
                "Revised margin in notifications",
                "Fixed FC in network notification")));

        changes.add(new Changelog("0.0.7", "2016-01-07", new ReleaseInfo(
                "Fixed battery notification")));

        changes.add(new Changelog("0.0.6", "2016-01-06", new ReleaseInfo(
                "Added tutorial",
                "Added API 16 support (4.1 Jelly Bean)",
                "Open app when round shape in notification is tapped",
                "Revised layout alignment",
                "Clipboard decryption and encryption of older (unencrypted) entries",
                "Design fixed in 'enter password'-dialog",
                "Much faster loading of network stats",
                "Various small performance tweaks",
                "New animations (still targeting older APIs)",
                "Fixed battery notification")));

        changes.add(new Changelog("0.0.5", "2016-01-04", new ReleaseInfo(
                "Performance improvements",
                "Cosmetics")));

        changes.add(new Changelog("0.0.4", "2016-01-03", new ReleaseInfo(
                "Added 'License'-dialog",
                "Added 'Changelog'-dialog")));

        changes.add(new Changelog("0.0.3", "2016-01-02", new ReleaseInfo(
                "Improved notification ordering")));

        changes.add(new Changelog("0.0.2", "2015-12-31", new ReleaseInfo(
                "Fixed network fragment")));

        changes.add(new Changelog("0.0.1", "2015-12-30", new ReleaseInfo(
                "Initial release")));

        return changes;
    }

}
