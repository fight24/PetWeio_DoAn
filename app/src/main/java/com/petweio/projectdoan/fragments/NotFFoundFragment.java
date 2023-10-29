package com.petweio.projectdoan.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.ybq.android.spinkit.style.FoldingCube;
import com.petweio.projectdoan.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotFFoundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotFFoundFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout loading;
    ProgressBar loadingProgressBar;
    private LinearLayout containerLinearLayout;

    public NotFFoundFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotFFoundFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotFFoundFragment newInstance(String param1, String param2) {
        NotFFoundFragment fragment = new NotFFoundFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_not_f_found, container, false);
        initView(v);
        return v;
    }
    private void initView(View rootView) {
        loading = rootView.findViewById(R.id.loading);
        loadingProgressBar = rootView.findViewById(R.id.loadingProgressBar);
        containerLinearLayout = rootView.findViewById(R.id.containerLinearLayout);
        loadingProgressBar.setIndeterminateDrawable(new FoldingCube());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(() -> {
            loading.setVisibility(View.GONE);
            containerLinearLayout.setVisibility(View.VISIBLE);
        }, 5000);
    }
}