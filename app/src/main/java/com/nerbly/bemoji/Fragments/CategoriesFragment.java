package com.nerbly.bemoji.Fragments;

import static com.nerbly.bemoji.Configurations.CATEGORIES_API_LINK;
import static com.nerbly.bemoji.UI.MainUIMethods.advancedCorners;
import static com.nerbly.bemoji.UI.MainUIMethods.rippleRoundStroke;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Activities.CategoriesPreviewActivity;
import com.nerbly.bemoji.Activities.HomeActivity;
import com.nerbly.bemoji.Adapters.LoadingPacksAdapter;
import com.nerbly.bemoji.Functions.RequestNetwork;
import com.nerbly.bemoji.Functions.RequestNetworkController;
import com.nerbly.bemoji.Functions.Utils;
import com.nerbly.bemoji.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class CategoriesFragment extends BottomSheetDialogFragment {
    private final ArrayList<HashMap<String, Object>> shimmerList = new ArrayList<>();
    private HashMap<String, Object> categoriesMap = new HashMap<>();
    private ArrayList<HashMap<String, Object>> categoriesList = new ArrayList<>();
    private LinearLayout background;
    private LinearLayout slider;
    private RecyclerView categoriesRecycler;
    private RecyclerView loadingRecycler;
    private BottomSheetBehavior sheetBehavior;
    private RequestNetwork RequestCategories;
    private RequestNetwork.RequestListener CategoriesRequestListener;
    private SharedPreferences sharedPref;
    private BottomSheetDialog d;

    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        if (isAdded() && getActivity() != null) {
            Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
                d = (BottomSheetDialog) dialog;
                View view = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                assert view != null;
                sheetBehavior = BottomSheetBehavior.from(view);

                initialize(view);
                com.google.firebase.FirebaseApp.initializeApp(requireContext());
                initializeLogic();
            });
            return inflater.inflate(R.layout.categories, container, false);
        } else {
            showCustomSnackBar(getString(R.string.error_msg), getActivity());
            dismiss();
            return null;
        }

    }

    private void initialize(View view) {
        background = view.findViewById(R.id.background);
        slider = view.findViewById(R.id.slider);
        categoriesRecycler = view.findViewById(R.id.categoriesRecycler);
        loadingRecycler = view.findViewById(R.id.loadingRecycler);
        RequestCategories = new RequestNetwork(getActivity());
        sharedPref = requireActivity().getSharedPreferences("AppData", Activity.MODE_PRIVATE);


        CategoriesRequestListener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                try {
                    JSONObject obj = new JSONObject(response);
                    Iterator<String> keys = obj.keys();

                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = String.valueOf(obj.get(key));
                        if (!value.equals("NSFW")) {
                            categoriesMap = new HashMap<>();
                            categoriesMap.put("category_id", key);
                            categoriesMap.put("category_name", value);
                            categoriesList.add(categoriesMap);
                        }
                    }

                } catch (JSONException e) {
                    System.err.println(e.toString());
                }

                sharedPref.edit().putString("categoriesData", new Gson().toJson(categoriesList)).apply();
                Utils.sortListMap(categoriesList, "category_name", false, true);
                categoriesRecycler.setAdapter(new CategoriesRecyclerAdapter(categoriesList));

                new Handler().postDelayed(() -> sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), 1000);

                new Handler().postDelayed(() -> loadingRecycler.setVisibility(View.GONE), 2000);
            }

            @Override
            public void onErrorResponse(String tag, String message) {

            }
        };
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        ((HomeActivity) requireActivity()).isFragmentAttached = false;
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        ((HomeActivity) requireActivity()).isFragmentAttached = false;
        super.onDismiss(dialog);
    }

    private void initializeLogic() {
        LOGIC_BACKEND();
        LOGIC_FRONTEND();
    }

    public void LOGIC_BACKEND() {
        loadingRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        categoriesRecycler.setHasFixedSize(true);
        loadingRecycler.setHasFixedSize(true);

        for (int i = 0; i < 7; i++) {
            HashMap<String, Object> shimmerMap = new HashMap<>();
            shimmerMap.put("key", "value");
            shimmerList.add(shimmerMap);
        }
        loadingRecycler.setAdapter(new LoadingPacksAdapter.LoadingRecyclerAdapter(shimmerList));

        if (sharedPref.getString("categoriesData", "").isEmpty()) {
            RequestCategories.startRequestNetwork(RequestNetworkController.GET, CATEGORIES_API_LINK, "", CategoriesRequestListener);
        } else {
            categoriesList = new Gson().fromJson(sharedPref.getString("categoriesData", ""), new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            Utils.sortListMap(categoriesList, "category_name", false, true);
            categoriesRecycler.setAdapter(new CategoriesRecyclerAdapter(categoriesList));

            new Handler().postDelayed(() -> sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), 1000);

            new Handler().postDelayed(() -> loadingRecycler.setVisibility(View.GONE), 2000);
        }

        background.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }


    public void LOGIC_FRONTEND() {
        setViewRadius(slider, 90, "#E0E0E0");
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.3f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }

    public class CategoriesRecyclerAdapter extends RecyclerView.Adapter<CategoriesRecyclerAdapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> data;

        public CategoriesRecyclerAdapter(ArrayList<HashMap<String, Object>> _arr) {
            data = _arr;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View _v = _inflater.inflate(R.layout.categoriesview, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            View view = holder.itemView;
            TextView textview1 = view.findViewById(R.id.emptyTitle);

            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(_lp);
            textview1.setText(Objects.requireNonNull(data.get(position).get("category_name")).toString());
            textview1.setOnClickListener(_view -> {
                if (Objects.requireNonNull(data.get(position).get("category_name")).toString().equals("Animated")) {
                    showWarningSheet(Objects.requireNonNull(data.get(position).get("category_id")).toString());
                } else {
                    int category_id = Integer.parseInt(Objects.requireNonNull(data.get(position).get("category_id")).toString());
                    Intent toEmojis = new Intent();
                    toEmojis.putExtra("category_id", category_id);
                    toEmojis.setClass(getContext(), CategoriesPreviewActivity.class);
                    startActivity(toEmojis);
                }
            });
            rippleRoundStroke(textview1, "#F5F5F5", "#EEEEEE", 25, 1, "#EEEEEE");
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View v) {
                super(v);
            }
        }

    }

    private void showWarningSheet(String category_id) {

        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext(), R.style.materialsheet);

        View bottomSheetView;
        bottomSheetView = getLayoutInflater().inflate(R.layout.infosheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);

        MaterialButton infook = bottomSheetView.findViewById(R.id.infosheet_ok);
        MaterialButton infocancel = bottomSheetView.findViewById(R.id.infosheet_cancel);
        LinearLayout infoback = bottomSheetView.findViewById(R.id.infosheet_back);
        LinearLayout slider = bottomSheetView.findViewById(R.id.slider);

        advancedCorners(infoback, "#ffffff", 38, 38, 0, 0);

        setViewRadius(slider, 180, "#BDBDBD");
        infook.setOnClickListener(v -> {
            Intent toEmojis = new Intent();
            toEmojis.putExtra("category_id", Integer.valueOf(category_id));
            toEmojis.setClass(getContext(), CategoriesPreviewActivity.class);
            startActivity(toEmojis);
            bottomSheetDialog.dismiss();
        });
        infocancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();

    }

}
