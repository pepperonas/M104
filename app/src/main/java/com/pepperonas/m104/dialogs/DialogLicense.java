/*
 * Copyright (c) 2019 Martin Pfeffer
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
import android.support.annotation.NonNull;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.pepperonas.m104.R;
import com.pepperonas.m104.config.Const;
import com.pepperonas.materialdialog.MaterialDialog;
import com.pepperonas.materialdialog.model.LicenseInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class DialogLicense {

    public DialogLicense(Context context) {
        List<LicenseInfo> licenseInfos = getLicenseInfos();

        new MaterialDialog.Builder(context)
                .icon(new IconicsDrawable(context, CommunityMaterial.Icon.cmd_git)
                        .colorRes(R.color.dialog_icon)
                        .sizeDp(Const.NAV_DRAWER_ICON_SIZE))
                .title(R.string.dialog_license_title)
                .licenseDialog(licenseInfos)
                .positiveText(R.string.ok)
                .show();
    }

    @NonNull
    private List<LicenseInfo> getLicenseInfos() {
        List<LicenseInfo> licenseInfos = new ArrayList<>();

        licenseInfos.add(new LicenseInfo("Android-Iconics", "Copyright 2016 Mike Penz",
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n"
                        + "you may not use this file except in compliance with the License.\n"
                        + "You may obtain a copy of the License at\n" + "\n"
                        + "   http://www.apache.org/licenses/LICENSE-2.0\n" + "\n"
                        + "Unless required by applicable law or agreed to in writing, software\n"
                        + "distributed under the License is distributed on an \"AS IS\" BASIS,\n"
                        + "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
                        + "See the License for the specific language governing permissions and\n"
                        + "limitations under the License."));

        licenseInfos.add(new LicenseInfo("MPAndroidChart", "Copyright 2015 Philipp Jahoda",
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n"
                        + "you may not use this file except in compliance with the License.\n"
                        + "You may obtain a copy of the License at\n" + "\n"
                        + "   http://www.apache.org/licenses/LICENSE-2.0\n" + "\n"
                        + "Unless required by applicable law or agreed to in writing, software\n"
                        + "distributed under the License is distributed on an \"AS IS\" BASIS,\n"
                        + "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
                        + "See the License for the specific language governing permissions and\n"
                        + "limitations under the License."));

        licenseInfos.add(new LicenseInfo("AppIntro", "Copyright 2015 Paolo Rotolo",
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n"
                        + "you may not use this file except in compliance with the License.\n"
                        + "You may obtain a copy of the License at\n" + "\n"
                        + "   http://www.apache.org/licenses/LICENSE-2.0\n" + "\n"
                        + "Unless required by applicable law or agreed to in writing, software\n"
                        + "distributed under the License is distributed on an \"AS IS\" BASIS,\n"
                        + "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
                        + "See the License for the specific language governing permissions and\n"
                        + "limitations under the License."));

        return licenseInfos;
    }
}
