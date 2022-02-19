package com.nerbly.bemoji.Fragments;

import static com.nerbly.bemoji.Configurations.DISCORD_INVITE_LINK;
import static com.nerbly.bemoji.Functions.MainFunctions.initializeCacheScan;
import static com.nerbly.bemoji.Functions.MainFunctions.loadFragmentLocale;
import static com.nerbly.bemoji.Functions.MainFunctions.setFragmentLocale;
import static com.nerbly.bemoji.Functions.MainFunctions.trimCache;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.UserInteractions.showMessageDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nerbly.bemoji.Activities.HomeActivity;
import com.nerbly.bemoji.R;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsFragment extends BottomSheetDialogFragment {

    public static boolean isFragmentAttached = false;
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
    private RelativeLayout setting14;
    private TextView textview8;
    private SharedPreferences sharedPref;
    private String cacheSize;
    private boolean isAskingForReload = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        if (isAdded() && getActivity() != null) {
            Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View view = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(Objects.requireNonNull(view));
                initialize(view);
                initializeLogic();
            });
            return inflater.inflate(R.layout.settings, container, false);
        } else {
            dismiss();
            return null;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        loadFragmentLocale(context);
    }

    @SuppressLint("SetTextI18n")
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
        setting14 = view.findViewById(R.id.setting14);
        textview8 = view.findViewById(R.id.textview8);
        sharedPref = requireActivity().getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        setting1.setOnClickListener(_view -> {
            ((HomeActivity) requireActivity()).startManualRefresh();
            dismiss();
        });

        setting3.setOnClickListener(_view -> {
            try {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://emoji.gg/submit"));
                startActivity(intent);
            } catch (Exception e) {
                showMessageDialog(true, getString(R.string.error_msg), getString(R.string.webview_device_not_supported), getString(R.string.copy_text), getString(R.string.dialog_negative_text), requireActivity(),
                        (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(requireActivity().getString(R.string.app_name), "https://emoji.gg/submit");
                            clipboard.setPrimaryClip(clip);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });

        setting2.setOnClickListener(_view -> {
            trimCache(getActivity());
            textview8.setText(getString(R.string.settings_option_3_title) + " (" + initializeCacheScan(getActivity()) + ")");
        });

        setting4.setOnClickListener(_view -> {
            try {
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"nerblyteam@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, requireActivity().getString(R.string.app_name) + " App - Contact Us");
                startActivity(intent);
            } catch (Exception e) {
                showMessageDialog(true, getString(R.string.error_msg), getString(R.string.mailto_device_not_supported), getString(R.string.dialog_positive_text), getString(R.string.dialog_negative_text), requireActivity(),
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
                showMessageDialog(true, getString(R.string.error_msg), getString(R.string.webview_device_not_supported), getString(R.string.copy_text), getString(R.string.dialog_negative_text), requireActivity(),
                        (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(requireActivity().getString(R.string.app_name), "https://play.google.com/store/apps/details?id=com.nerbly.bemoji");
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
                showMessageDialog(true, getString(R.string.error_msg), getString(R.string.webview_device_not_supported), getString(R.string.copy_text), getString(R.string.dialog_negative_text), requireActivity(),
                        (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(requireActivity().getString(R.string.app_name), "https://emoji.gg/copyright");
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
                showMessageDialog(true, getString(R.string.error_msg), getString(R.string.webview_device_not_supported), getString(R.string.copy_text), getString(R.string.dialog_negative_text), requireActivity(),
                        (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(requireActivity().getString(R.string.app_name), "https://emoji.gg/");
                            clipboard.setPrimaryClip(clip);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });
        setting10.setOnClickListener(_view -> {
            try {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/ilyassesalama/hymoji"));
                startActivity(intent);
            } catch (Exception e) {
                showMessageDialog(true, getString(R.string.error_msg), getString(R.string.webview_device_not_supported), getString(R.string.copy_text), getString(R.string.dialog_negative_text), requireActivity(),
                        (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Hymoji", "https://github.com/ilyassesalama/hymoji");
                            clipboard.setPrimaryClip(clip);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });
        setting11.setOnClickListener(_view -> showLanguagesDialog());

        setting12.setOnClickListener(_view -> {


            TranslationContributorsFragment bottomSheet = new TranslationContributorsFragment();
            if (!isFragmentAttached) {
                isFragmentAttached = true;
                bottomSheet.show(requireActivity().getSupportFragmentManager(), "Contributors");
            }
        });

        setting13.setOnClickListener(_view -> {
            try {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(DISCORD_INVITE_LINK));
                startActivity(intent);
            } catch (Exception e) {
                showMessageDialog(true, getString(R.string.error_msg), getString(R.string.webview_device_not_supported), getString(R.string.copy_text), getString(R.string.dialog_negative_text), requireActivity(),
                        (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText(requireActivity().getString(R.string.app_name), DISCORD_INVITE_LINK);
                            clipboard.setPrimaryClip(clip);
                        },
                        (dialog, which) -> dialog.dismiss());
            }
        });
        setting14.setOnClickListener(_view -> showThemeDialog());

    }

    private void initializeLogic() {
        LOGIC_FRONTEND();
        LOGIC_BACKEND();
    }

    @SuppressLint("SetTextI18n")
    public void LOGIC_BACKEND() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.3f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            cacheSize = initializeCacheScan(getActivity());
            handler.post(() -> {
                if (isAdded()) {
                    textview8.setText(getString(R.string.settings_option_3_title) + " (" + cacheSize + ")");
                }
            });
        });


        if (Build.VERSION.SDK_INT >= 29) {
            //setting14.setVisibility(View.VISIBLE);
        }
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
        rippleRoundStroke(setting14, "#FFFFFF", "#E0E0E0", 25, 1, "#BDBDBD");
    }

    private void showLanguagesDialog() {
        final String[] languages = {"English", "Português", "Français", "Deutsch", "Türkçe", "Pусский", "Polskie"};
        MaterialAlertDialogBuilder languagesDialog = new MaterialAlertDialogBuilder(requireActivity(), R.style.RoundShapeTheme);
        int languagePosition;
        languagePosition = sharedPref.getInt("lang_pos", -1);
        languagesDialog.setTitle("Choose your language")
                .setSingleChoiceItems(languages, languagePosition, (dialog, i) -> {
                    if (languagePosition != i) {
                        if (getView() != null) {
                            isAskingForReload = true;
                            switch (i) {
                                case 0:
                                    setFragmentLocale("en", i, requireContext());
                                    break;
                                case 1:
                                    setFragmentLocale("pt", i, requireContext());
                                    break;
                                case 2:
                                    setFragmentLocale("fr", i, requireContext());
                                    break;
                                case 3:
                                    setFragmentLocale("de", i, requireContext());
                                    break;
                                case 4:
                                    setFragmentLocale("tr", i, requireContext());
                                    break;
                                case 5:
                                    setFragmentLocale("ru", i, requireContext());
                                    break;
                                case 6:
                                    setFragmentLocale("pl", i, requireContext());
                                    break;
                            }
                            dismiss();
                        }
                    }

                    dialog.dismiss();
                })
                .show();
    }

    @SuppressLint("SwitchIntDef")
    private void showThemeDialog() {
        final String[] themes = {getString(R.string.theme_auto_title), getString(R.string.theme_light_title), getString(R.string.theme_dark_title)};
        int currentTheme = -1;

        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                currentTheme = 0;
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                currentTheme = 1;
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                currentTheme = 2;
                break;
        }

        MaterialAlertDialogBuilder themeDialog = new MaterialAlertDialogBuilder(requireActivity(), R.style.RoundShapeTheme);
        themeDialog.setTitle(getString(R.string.settings_option_14_title))
                .setSingleChoiceItems(themes, sharedPref.getInt("currentTheme", currentTheme), (dialog, i) -> {
                    isAskingForReload = true;
                    switch (i) {
                        case 0:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            break;
                        case 1:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            break;
                        case 2:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            break;
                        default:
                    }
                    sharedPref.edit().putInt("currentTheme", i).apply();
                    dialog.dismiss();
                    dismiss();
                })
                .show();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        ((HomeActivity) requireActivity()).isFragmentAttached = false;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        ((HomeActivity) requireActivity()).isFragmentAttached = false;
        if (isAskingForReload) {
            ((HomeActivity) requireActivity()).userIsAskingForActivityToReload(requireActivity());
        }
    }

}