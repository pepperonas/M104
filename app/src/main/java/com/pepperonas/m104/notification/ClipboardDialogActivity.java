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

package com.pepperonas.m104.notification;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.animation.FadeAnimation;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.andbasx.concurrency.ThreadUtils;
import com.pepperonas.andbasx.system.DeviceUtils;
import com.pepperonas.m104.MainService;
import com.pepperonas.m104.R;
import com.pepperonas.m104.adapter.ClipDataAdvancedAdapter;
import com.pepperonas.m104.config.Const;
import com.pepperonas.m104.custom.SwipeableRecyclerViewTouchListener;
import com.pepperonas.m104.dialogs.DialogEnterPassword;
import com.pepperonas.m104.model.ClipDataAdvanced;
import com.pepperonas.m104.model.Database;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class ClipboardDialogActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = "ClipboardDialogActivity";

    private Database mDb;
    private TextView mTvHeader;
    private ImageView mIvEasterEgg;
    private List<ClipDataAdvanced> mClips;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
          init dialog
         */
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * Const.RELATIVE_DIALOG_WIDTH);

        setContentView(R.layout.activity_clipboard_dialog);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = Const.DIALOG_DIM_AMOUNT;
        getWindow().setAttributes(layoutParams);
        getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        mDb = new Database(this);
        mClips = mDb.getClipData(AesPrefs.getInt(getString(R.string.MAX_CLIPS_IN_RECYCLER), Const.DEFAULT_MAX_CLIPS_IN_RECYCLER));
        if (mClips.isEmpty()) {
            ToastUtils.toastShort(R.string.no_clip_data_set);
            finish();
            return;
        }

        ensureInitLockButton();

        mIvEasterEgg = findViewById(R.id.iv_easter_egg);
        mIvEasterEgg.setVisibility(View.INVISIBLE);

        mTvHeader = findViewById(R.id.tv_header);
        onUpdateTvHeader();

        mRecyclerView = findViewById(R.id.recycler_view_clip_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        setData();

        SwipeableRecyclerViewTouchListener swipeTouchListener = new SwipeableRecyclerViewTouchListener(
                mRecyclerView, new SwipeableRecyclerViewTouchListener.SwipeListener() {
            @Override
            public boolean canSwipe(int position) {
                return true;
            }

            @Override
            public void onDismissedBySwipeLeft(RecyclerView rv, int[] reverseSortedPositions) {
                // TODO: make favorite...
                removeClip(rv, reverseSortedPositions);
            }

            @Override
            public void onDismissedBySwipeRight(RecyclerView rv, int[] reverseSortedPositions) {
                removeClip(rv, reverseSortedPositions);
            }

            private void removeClip(RecyclerView rv, int[] reverseSortedPositions) {
                for (int pos : reverseSortedPositions) {
                    mDb.deleteClipData(mClips.get(pos).getClipText());
                    mClips.remove(pos);
                    rv.getAdapter().notifyDataSetChanged();
                    onUpdateTvHeader();

                    sendBroadcast(new Intent(MainService.BROADCAST_CLIP_DELETED));

                    if (mClips.size() == 0) {

                        Drawable d = new IconicsDrawable(ClipboardDialogActivity.this, CommunityMaterial.Icon.cmd_check)
                                .colorRes(R.color.sa_teal).sizeDp(64);
                        mIvEasterEgg.setVisibility(View.VISIBLE);
                        mIvEasterEgg.setImageDrawable(d);
                        FadeAnimation anim = new FadeAnimation(mIvEasterEgg, 1.0f, 0.0f, 600, 0);
                        anim.fadeIn();

                        ThreadUtils.runDelayed(new Callable<Void>() {
                            @Override
                            public Void call() {
                                finish();
                                return null;
                            }
                        }, 1500);
                    }
                }
            }
        });

        mRecyclerView.addOnItemTouchListener(swipeTouchListener);

        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent arg1) {
                finish();
                return true;
            }
        });
    }

    public void ensureInitLockButton() {
        final ImageButton ibtnLock = findViewById(R.id.ibtn_lock);
        final View parent = (View) ibtnLock.getParent();
        parent.post(new Runnable() {
            public void run() {
                final Rect r = new Rect();
                ibtnLock.getHitRect(r);
                r.top -= DeviceUtils.dp2px(60 - 24);
                r.bottom += DeviceUtils.dp2px(60 - 24);
                parent.setTouchDelegate(new TouchDelegate(r, ibtnLock));
            }
        });

        boolean isEncryptionActive = !AesPrefs.getRes(R.string.ENCRYPTION_PASSWORD, "").equals("");
        if (isEncryptionActive) {

            ibtnLock.setVisibility(View.VISIBLE);
            // TODO: lock when unlocked
            if (AesPrefs.getLongRes(R.string.LOGOUT_TIME, 0) < System.currentTimeMillis()) {

                Drawable d = new IconicsDrawable(ClipboardDialogActivity.this,
                        CommunityMaterial.Icon.cmd_lock_open_outline).colorRes(R.color.stock_android_white).sizeDp(24);
                ibtnLock.setImageDrawable(d);
                ibtnLock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DialogEnterPassword(ClipboardDialogActivity.this);
                    }
                });

            } else {

                Drawable d = new IconicsDrawable(ClipboardDialogActivity.this,
                        CommunityMaterial.Icon.cmd_lock_outline).colorRes(R.color.stock_android_white).sizeDp(24);
                ibtnLock.setImageDrawable(d);
                ibtnLock.setOnClickListener(new View.OnClickListener() {
                    boolean mCalledEnsureLockButton = false;

                    @Override
                    public void onClick(View v) {
                        AesPrefs.putLongRes(R.string.LOGOUT_TIME, 0);
                        ToastUtils.toastShort(getString(R.string.clipboard_locked));
                        setData();
                        mRecyclerView.getAdapter().notifyDataSetChanged();

                        if (!mCalledEnsureLockButton) {
                            mCalledEnsureLockButton = true;
                            ensureInitLockButton();
                        }
                    }
                });
            }

        } else {
            ibtnLock.setVisibility(View.INVISIBLE);
        }

    } // ensureInitLockButton

    public void setData() {
        mClips = mDb.getClipData(AesPrefs.getInt(getString(R.string.MAX_CLIPS_IN_RECYCLER), Const.DEFAULT_MAX_CLIPS_IN_RECYCLER));
        ClipDataAdvancedAdapter adapter = new ClipDataAdvancedAdapter(this, mDb, mClips);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mIvEasterEgg.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        mDb.close();

        super.onDestroy();
    }

    private void onUpdateTvHeader() {
        int clipDataCount = mDb.getClipDataCount();
        if (clipDataCount == 0) {
            mTvHeader.setText(MessageFormat.format("{0} - {1}", getString(R.string.dialog_clipboard_header), getString(R.string.no_entries)));
            return;
        }
        if (clipDataCount == 1) {
            mTvHeader.setText(MessageFormat.format("{0} - {1}", getString(R.string.dialog_clipboard_header), getString(R.string.one_entry)));
            return;
        }

        mTvHeader.setText(MessageFormat.format("{0} - {1} {2}", getString(R.string.dialog_clipboard_header), clipDataCount, getString(R.string.entries)));
    }

}
