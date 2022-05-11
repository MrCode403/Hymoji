package com.nerbly.bemoji.fragments;

import static com.nerbly.bemoji.ui.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.ui.UserInteractions.showMessageDialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.adapters.TranslationContributorsAdapter;
import com.nerbly.bemoji.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TranslationContributorsFragment extends BottomSheetDialogFragment {
    private LinearLayout slider;
    private LinearLayout view_leftline;
    private RecyclerView recyclerview;
    private BottomSheetDialog d;

    @NonNull
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
            d = (BottomSheetDialog) dialog;
            View view = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert view != null;
            BottomSheetBehavior.from(view);
            initialize(view);
            initializeLogic();
        });
        return inflater.inflate(R.layout.translation_contributors_fragment, container, false);

    }

    private void initialize(View view) {
        MaterialCardView contribute_go = view.findViewById(R.id.contribute_go);
        slider = view.findViewById(R.id.slider);
        view_leftline = view.findViewById(R.id.view_leftline);
        recyclerview = view.findViewById(R.id.recyclerview);
        requireActivity().getSharedPreferences("AppData", Activity.MODE_PRIVATE);


        contribute_go.setOnClickListener(_view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"nerblyteam@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, requireActivity().getString(R.string.app_name) + " Translation Contribution");
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                showMessageDialog(true, getString(R.string.error_msg), getString(R.string.mailto_device_not_supported), getString(R.string.dialog_positive_text), getString(R.string.dialog_negative_text), requireActivity(),
                        (dialog, which) -> {
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.nerbly.bemoji"));
                            startActivity(intent);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });

    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        SettingsFragment.isFragmentAttached = false;
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        SettingsFragment.isFragmentAttached = false;
        super.onDismiss(dialog);
    }

    private void initializeLogic() {
        LOGIC_BACKEND();
        LOGIC_FRONTEND();
    }

    public void LOGIC_BACKEND() {
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.setHasFixedSize(true);

        ArrayList<HashMap<String, Object>> contributorsList = new Gson().fromJson(getContributorsFromAsset(), new TypeToken<ArrayList<HashMap<String, Object>>>() {
        }.getType());

        recyclerview.setAdapter(new TranslationContributorsAdapter(contributorsList));
    }

    public void LOGIC_FRONTEND() {
        setViewRadius(slider, 90, "#E0E0E0");
        setViewRadius(view_leftline, 90, "#009688");
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.3f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }

    public String getContributorsFromAsset() {
        String json;
        try {
            InputStream is = requireActivity().getAssets().open("translation_contributors.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
