/*
 * Copyright (c) 2016 Martin Pfeffer
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
 * @author Martin Pfeffer (pepperonas)
 */
public class DialogChangelog {

    public DialogChangelog(Context context) {
        new MaterialDialog.Builder(context)
                .title("Changelog")
                .changelogDialog(getChangelogs(), context.getString(R.string.bullet_release_info))
                .positiveText(R.string.ok)
                .show();
    }


    public List<Changelog> getChangelogs() {
        List<Changelog> changelogs = new ArrayList<>();

        changelogs.add(new Changelog("0.2.3", "2016-02-12", new ReleaseInfo(
                "Removed unnecessary callbacks"
        )));

        changelogs.add(new Changelog("0.2.2", "2016-02-11", new ReleaseInfo(
                "Fixed error (fast network speed)"
        )));

        changelogs.add(new Changelog("0.2.1", "2016-02-11", new ReleaseInfo(
                "Minor bugfixes"
        )));

        changelogs.add(new Changelog("0.2.0", "2016-02-10", new ReleaseInfo(
                "Improved performance",
                "Stability update"
        )));

        changelogs.add(new Changelog("0.1.9", "2016-02-09", new ReleaseInfo(
                "Improved database access"
        )));

        changelogs.add(new Changelog("0.1.8", "2016-02-05", new ReleaseInfo(
                "Fixed premium key"
        )));

        changelogs.add(new Changelog("0.1.7", "2016-01-25", new ReleaseInfo(
                "Fixed animation when clipboard is wiped",
                "Fixed animation in network fragment",
                "Performance tweaks"
        )));

        changelogs.add(new Changelog("0.1.6", "2016-01-17", new ReleaseInfo(
                "Fixed null-pointer in network fragment",
                "Root mode (experimental)",
                "Performance improvements",
                "Larger pictures in tutorial",
                "Smaller .apk size",
                "Improved layout alignment"
        )));

        changelogs.add(new Changelog("0.1.5", "2016-01-16", new ReleaseInfo(
                "Added units",
                "Removed typos"
        )));

        changelogs.add(new Changelog("0.1.4", "2016-01-14", new ReleaseInfo(
                "Added new animation for network chart",
                "Fixed verification process"
        )));

        changelogs.add(new Changelog("0.1.3", "2016-01-13", new ReleaseInfo(
                "Bugfix in autostart"
        )));

        changelogs.add(new Changelog("0.1.2", "2016-01-12", new ReleaseInfo(
                "Reduced power consumption (experimental)",
                "Prevent showing dialog if clipboard is empty",
                "Fixed null-pointer in Android 4.4"
        )));

        changelogs.add(new Changelog("0.1.1", "2016-01-11", new ReleaseInfo(
                "Improved network chart",
                "Little performance improvements"
        )));

        changelogs.add(new Changelog("0.1.0", "2016-01-10", new ReleaseInfo(
                "Added Google Analytics",
                "Minified build (approx. 2MB smaller)"
        )));

        changelogs.add(new Changelog("0.0.9", "2016-01-09", new ReleaseInfo(
                "Improved performance in network fragment",
                "Added scroll animation"
        )));

        changelogs.add(new Changelog("0.0.8", "2016-01-09", new ReleaseInfo(
                "Added image in navigation drawer",
                "Revised margin in notifications",
                "Fixed FC in network notification"
        )));

        changelogs.add(new Changelog("0.0.7", "2016-01-07", new ReleaseInfo(
                "Bugfix in battery notification"
        )));

        changelogs.add(new Changelog("0.0.6", "2016-01-06", new ReleaseInfo(
                "Added tutorial",
                "Added API 16 support (4.1 Jelly Bean)",
                "Open app when round shape in notification is tapped",
                "Revised layout alignment",
                "Clipboard decryption and encryption of older (unencrypted) entries",
                "Design fixed in 'enter password'-dialog",
                "Much faster loading of network stats",
                "Various small performance tweaks",
                "New animations (still targeting older APIs)",
                "Bugfix in battery notification"
        )));

        changelogs.add(new Changelog("0.0.5", "2016-01-04", new ReleaseInfo(
                "Performance improvements",
                "Cosmetics"
        )));

        changelogs.add(new Changelog("0.0.4", "2016-01-03", new ReleaseInfo(
                "Added 'License'-dialog",
                "Added 'Changelog'-dialog"
        )));

        changelogs.add(new Changelog("0.0.3", "2016-01-02", new ReleaseInfo(
                "Improved notification ordering"
        )));

        changelogs.add(new Changelog("0.0.2", "2015-12-31", new ReleaseInfo(
                "Bugfixes in network fragment"
        )));

        changelogs.add(new Changelog("0.0.1", "2015-12-30", new ReleaseInfo(
                "Initial release"
        )));

        return changelogs;
    }

}