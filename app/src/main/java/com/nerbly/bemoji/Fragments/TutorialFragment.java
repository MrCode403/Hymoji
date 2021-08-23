package com.nerbly.bemoji.Fragments;

import static com.nerbly.bemoji.Configurations.TUTORIAL_SOURCE;
import static com.nerbly.bemoji.UI.MainUIMethods.setViewRadius;
import static com.nerbly.bemoji.UI.UserInteractions.showCustomSnackBar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerbly.bemoji.Activities.HomeActivity;
import com.nerbly.bemoji.Adapters.LoadingPacksAdapter;
import com.nerbly.bemoji.Adapters.TutorialAdapter;
import com.nerbly.bemoji.Functions.RequestNetwork;
import com.nerbly.bemoji.Functions.RequestNetworkController;
import com.nerbly.bemoji.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TutorialFragment extends BottomSheetDialogFragment {
    private final ArrayList<HashMap<String, Object>> shimmerList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> tutorialList = new ArrayList<>();
    private LinearLayout slider;
    private RecyclerView recyclerview1;
    private RecyclerView loadingRecycler;
    private BottomSheetBehavior sheetBehavior;
    private RequestNetwork requestTutorial;
    private RequestNetwork.RequestListener TutorialRequestListener;

    @NonNull
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle _savedInstanceState) {
        Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View view = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert view != null;
            sheetBehavior = BottomSheetBehavior.from(view);
            initialize(view);
            com.google.firebase.FirebaseApp.initializeApp(requireContext());
            initializeLogic();
        });
        return inflater.inflate(R.layout.tutorial, container, false);

    }

    private void initialize(View view) {
        slider = view.findViewById(R.id.slider);
        recyclerview1 = view.findViewById(R.id.recyclerview1);
        loadingRecycler = view.findViewById(R.id.loadingRecycler);
        requestTutorial = new RequestNetwork(getActivity());


        TutorialRequestListener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                try {
                    tutorialList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                    }.getType());
                    recyclerview1.setAdapter(new TutorialAdapter.Recyclerview1Adapter(tutorialList));

                    new Handler().postDelayed(() -> sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), 1000);

                    new Handler().postDelayed(() -> {
                        recyclerview1.setVisibility(View.VISIBLE);
                        loadingRecycler.setVisibility(View.GONE);
                    }, 2000);
                } catch (Exception ignored) {
                    if(isAdded()) {
                        showCustomSnackBar(getString(R.string.error_msg_2), getActivity());
                        dismiss();
                    }
                }
            }

            @Override
            public void onErrorResponse(String tag, String message) {

            }
        };
    }

    private void initializeLogic() {
        LOGIC_FRONTEND();
        LOGIC_BACKEND();
    }

    public void LOGIC_BACKEND() {

        recyclerview1.setLayoutManager(new LinearLayoutManager(getContext()));
        loadingRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview1.setHasFixedSize(true);
        loadingRecycler.setHasFixedSize(true);

        for (int i = 0; i < 7; i++) {
            HashMap<String, Object> shimmerMap = new HashMap<>();
            shimmerMap.put("key", "value");
            shimmerList.add(shimmerMap);
        }
        loadingRecycler.setAdapter(new LoadingPacksAdapter.LoadingRecyclerAdapter(shimmerList));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            requestTutorial.startRequestNetwork(RequestNetworkController.GET, TUTORIAL_SOURCE, "", TutorialRequestListener);

            handler.post(() -> {

            });
        });
    }

    public void LOGIC_FRONTEND() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.3f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
        setViewRadius(slider, 90, "#E0E0E0");
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

}
