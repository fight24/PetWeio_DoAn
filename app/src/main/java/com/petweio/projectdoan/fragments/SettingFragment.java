package com.petweio.projectdoan.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.ybq.android.spinkit.style.FoldingCube;
import com.petweio.projectdoan.Introduction.IntroductionActivity;
import com.petweio.projectdoan.Model.ApiResponse;
import com.petweio.projectdoan.Model.UserSearch;
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.api.ApiManager;
import com.petweio.projectdoan.service.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingFragment extends Fragment {
    private static final String TAG = "SettingFragment";
    private LinearLayout logout,templateSettings,loading;
    private static final String ARG_PARAM_USER_NAME = "username";
    private String userName;
    private TextView txtUserNameSetting,txtEmailSetting;
    ApiService apiService;
    ProgressBar loadingProgressBar;
    public SettingFragment() {
        // Required empty public constructor
    }


    public static SettingFragment newInstance(String name) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_USER_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        init(rootView);
        return rootView;
    }
    private void init(@NonNull View v){
        logout = v.findViewById(R.id.logoutLinearLayout);
        txtEmailSetting = v.findViewById(R.id.txtEmailSettings);
        txtUserNameSetting = v.findViewById(R.id.txtUserNameSetting);
        templateSettings = v.findViewById(R.id.templateSettings);
        loading = v.findViewById(R.id.loadingsettings);
        loadingProgressBar = v.findViewById(R.id.loadingProgressBarSettings);
        loadingProgressBar.setIndeterminateDrawable(new FoldingCube());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = ApiManager.getInstance().getMyApiService();
        Bundle args = this.getArguments();
        if (args != null) {
            userName = args.getString(ARG_PARAM_USER_NAME);

        }
        Call<List<UserSearch>> userCall = apiService.searchUser(userName);
        userCall.enqueue(new Callback<List<UserSearch>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserSearch>> call, @NonNull Response<List<UserSearch>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        List<UserSearch> users = response.body();
                        Log.d(TAG, "Ok ");
                        Log.d(TAG, "user: " + users.get(0).toString());
                        txtEmailSetting.setText(users.get(0).getEmail());
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserSearch>> call, @NonNull Throwable t) {
                Log.d(TAG, "failure: "+t);
            }
        });
        txtUserNameSetting.setText(upCaseFirstWord(userName));
        new Handler().postDelayed(() -> {
            loading.setVisibility(View.GONE);
            templateSettings.setVisibility(View.VISIBLE);
        }, 5000);
        logout.setOnClickListener(v -> {
            Call<ApiResponse> delete = apiService.deleteTokenByName(userName);
            delete.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    if(response.isSuccessful()){
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(requireContext(), IntroductionActivity.class);
                            Toast.makeText(requireContext(),"You logged out",Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            requireActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                            requireActivity().finish();
                        }, 2000);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {

                }
            });
        });
    }
    String upCaseFirstWord(@NonNull String name){
        return name.substring(0,1).toUpperCase() + name.substring(1);
    }
}