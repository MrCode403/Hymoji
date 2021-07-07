package com.nerbly.bemoji.Fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nerbly.bemoji.R;

import java.util.Objects;

import static com.nerbly.bemoji.Activities.HomeActivity.userIsAskingForActivityToReload;
import static com.nerbly.bemoji.Functions.MainFunctions.initializeCacheScan;
import static com.nerbly.bemoji.Functions.MainFunctions.setFragmentLocale;
import static com.nerbly.bemoji.Functions.MainFunctions.trimCache;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;
import static com.nerbly.bemoji.UI.UserInteractions.showMessageDialog;

public class SettingsFragment extends BottomSheetDialogFragment {

    private final Intent intent = new Intent();

    private LinearLayout slider;
    private RelativeLayout setting1;
    private RelativeLayout setting3;
    private RelativeLayout setting2;
    private RelativeLayout setting4;
    private RelativeLayout setting5;
    private RelativeLayout setting8;
    private RelativeLayout setting6;
    private RelativeLayout setting7;
    private RelativeLayout setting10;
    private RelativeLayout setting11;
    private RelativeLayout setting12;
    private RelativeLayout setting13;
    private TextView textview8;
    private SharedPreferences sharedPref;
    private boolean isAskingForReload = false;

    @NonNull
    @Override

    public View onCreateView(@NonNull LayoutInflater _inflater, @Nullable ViewGroup _container, @Nullable final Bundle _savedInstanceState) {
        Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View _view = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert _view != null;
            BottomSheetBehavior.from(_view);
            initialize(_view);
            com.google.firebase.FirebaseApp.initializeApp(requireContext());
            initializeLogic();
        });
        return _inflater.inflate(R.layout.settings, _container, false);

    }

    private void initialize(View view) {
        slider = view.findViewById(R.id.slider);
        setting1 = view.findViewById(R.id.setting1);
        setting3 = view.findViewById(R.id.setting3);
        setting2 = view.findViewById(R.id.setting2);
        setting4 = view.findViewById(R.id.setting4);
        setting5 = view.findViewById(R.id.setting5);
        setting8 = view.findViewById(R.id.setting8);
        setting6 = view.findViewById(R.id.setting6);
        setting7 = view.findViewById(R.id.setting7);
        setting10 = view.findViewById(R.id.setting10);
        setting11 = view.findViewById(R.id.setting11);
        setting12 = view.findViewById(R.id.setting12);
        setting13 = view.findViewById(R.id.setting13);
        textview8 = view.findViewById(R.id.textview8);
        sharedPref = requireActivity().getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        setting1.setOnClickListener(_view -> {
            sharedPref.edit().putString("emojisData", "").apply();

            sharedPref.edit().putString("categoriesData", "").apply();

            sharedPref.edit().putString("packsData", "").apply();

            sharedPref.edit().putString("isAskingForReload", "true").apply();
            showCustomSnackBar(getString(R.string.emojis_reloaded_success), requireActivity());
        });

        setting3.setOnClickListener(_view -> {
            try {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://emoji.gg/submit"));
            startActivity(intent);
            } catch (Exception e) {
                showMessageDialog(getString(R.string.error_msg), getString(R.string.webview_device_not_supported), getString(R.string.copy_text), getString(R.string.dialog_negative_text), getActivity(),
                        (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Bemoji", "https://emoji.gg/submit");
                            clipboard.setPrimaryClip(clip);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });

        setting2.setOnClickListener(_view -> {
            trimCache(getActivity());
            textview8.setText(getString(R.string.settings_option_3_title).concat(" (" + initializeCacheScan(getActivity()) + ")"));
        });

        setting4.setOnClickListener(_view -> {
            intent.setAction(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"nerblyteam@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Bemoji App - Contact Us");
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                showMessageDialog(getString(R.string.error_msg), getString(R.string.mailto_device_not_supported), getString(R.string.dialog_positive_text), getString(R.string.dialog_negative_text), getActivity(),
                        (dialog, which) -> {
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.nerbly.bemoji"));
                            startActivity(intent);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });

        setting5.setOnClickListener(_view -> {
            try {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.nerbly.bemoji"));
                startActivity(intent);
            } catch (Exception e) {
                showMessageDialog(getString(R.string.error_msg), getString(R.string.webview_device_not_supported), getString(R.string.copy_text), getString(R.string.dialog_negative_text), getActivity(),
                        (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Bemoji", "https://play.google.com/store/apps/details?id=com.nerbly.bemoji");
                            clipboard.setPrimaryClip(clip);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });

        setting6.setOnClickListener(_view -> {
            try {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://emoji.gg/copyright"));
                startActivity(intent);
            } catch (Exception e) {
                showMessageDialog(getString(R.string.error_msg), getString(R.string.webview_device_not_supported), getString(R.string.copy_text), getString(R.string.dialog_negative_text), getActivity(),
                        (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Bemoji", "https://emoji.gg/copyright");
                            clipboard.setPrimaryClip(clip);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });

        setting7.setOnClickListener(_view -> {
            try {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://emoji.gg/"));
                startActivity(intent);
            } catch (Exception e) {
                showMessageDialog(getString(R.string.error_msg), getString(R.string.webview_device_not_supported), getString(R.string.copy_text), getString(R.string.dialog_negative_text), getActivity(),
                        (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Bemoji", "https://emoji.gg/");
                            clipboard.setPrimaryClip(clip);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });
        setting10.setOnClickListener(_view -> {
            try {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/ilyassesalama/bemoji"));
                startActivity(intent);
            } catch (Exception e) {
                showMessageDialog(getString(R.string.error_msg), getString(R.string.webview_device_not_supported), getString(R.string.copy_text), getString(R.string.dialog_negative_text), getActivity(),
                        (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Bemoji", "https://github.com/ilyassesalama/bemoji");
                            clipboard.setPrimaryClip(clip);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });
        setting11.setOnClickListener(_view -> showLanguagesSheet());

        setting12.setOnClickListener(_view -> {
            intent.setAction(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"nerblyteam@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Bemoji Translation Contribution");
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                showMessageDialog(getString(R.string.error_msg), getString(R.string.mailto_device_not_supported), getString(R.string.dialog_positive_text), getString(R.string.dialog_negative_text), getActivity(),
                        (dialog, which) -> {
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.nerbly.bemoji"));
                            startActivity(intent);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });

        setting13.setOnClickListener(_view -> {
            try {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://discord.gg/nxy2Qq4YP4"));
                startActivity(intent);
            } catch (Exception e) {
                showMessageDialog(getString(R.string.error_msg), getString(R.string.webview_device_not_supported), getString(R.string.copy_text), getString(R.string.dialog_negative_text), getActivity(),
                        (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Bemoji", "https://discord.gg/nxy2Qq4YP4");
                            clipboard.setPrimaryClip(clip);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });

    }


    private void initializeLogic() {
        LOGIC_FRONTEND();
        LOGIC_BACKEND();
    }

    public void LOGIC_BACKEND() {
        textview8.setText(getString(R.string.settings_option_3_title).concat(" (" + initializeCacheScan(getActivity()) + ")"));
    }

    public void LOGIC_FRONTEND() {
        setViewRadius(slider, 90, "#E0E0E0");
        rippleRoundStroke(setting1, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting2, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting3, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting4, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting5, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting6, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting7, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting8, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting10, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting11, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting12, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
        rippleRoundStroke(setting13, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
    }

    private void showLanguagesSheet() {
        final String[] languages = {"English", "Português", "Français", "Deutsche", "Türkçe", "русский"};
        MaterialAlertDialogBuilder languagesDialog = new MaterialAlertDialogBuilder(requireActivity(), R.style.RoundShapeTheme);
        int languagePosition = -1;
        if (sharedPref.getString("language_position", "") != null) {
            if (!sharedPref.getString("language_position", "").equals("")) {
                languagePosition = Integer.parseInt(sharedPref.getString("language_position", ""));
            }
        }
        languagesDialog.setTitle("Choose your language")
                .setSingleChoiceItems(languages, languagePosition, (dialog, i) -> {
                    isAskingForReload = true;
                    if (i == 0) {
                        setFragmentLocale("en", Integer.toString(i), requireView());
                    } else if (i == 1) {
                        setFragmentLocale("pt", Integer.toString(i), requireView());
                    } else if (i == 2) {
                        setFragmentLocale("fr", Integer.toString(i), requireView());
                    } else if (i == 3) {
                        setFragmentLocale("de", Integer.toString(i), requireView());
                    } else if (i == 4) {
                        setFragmentLocale("tr", Integer.toString(i), requireView());
                    } else if (i == 5) {
                        setFragmentLocale("ru", Integer.toString(i), requireView());
                    }
                    dialog.dismiss();
                    sharedPref.edit().putString("isAskingForReload", "true").apply();
                    dismiss();
                })
                .show();
    }


    @Override
    public void onStart() {
        super.onStart();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.2f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (isAskingForReload) {
            userIsAskingForActivityToReload(requireActivity());
        }
    }
}