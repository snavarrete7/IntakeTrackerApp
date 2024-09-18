package com.example.intaketraackerapp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.intaketraackerapp.HomePageActivity;
import com.example.intaketraackerapp.IntakeClasses.Food;
import com.example.intaketraackerapp.IntakeClasses.User;
import com.example.intaketraackerapp.R;
import com.example.intaketraackerapp.ui.login.LoginActivity;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView emailTextView;
    private TextView weightTextView;
    private TextView passwordTextView;
    private TextView usernameTextView;
    private TextView heightTextView;



    public FragmentProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentProfile newInstance(String param1, String param2) {
        FragmentProfile fragment = new FragmentProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getActivity(), HomePageActivity.class);
                startActivity(intent);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_2:
                // Acción para el segundo item del menú
                ConfirmLogoutDialog confirmLogoutDialog = new ConfirmLogoutDialog(new ConfirmLogoutDialog.ConfirmLogoutDialogListener() {
                    @Override
                    public void onConfirmLogout() {
                        //confirmLogout();
                    }
                });
                confirmLogoutDialog.show(getParentFragmentManager(), "confirmLogoutDialog");
                return true;
            default:
                return false;
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        emailTextView = view.findViewById(R.id.emailProfile);
        weightTextView = view.findViewById(R.id.weightProfile);
        passwordTextView = view.findViewById(R.id.passwordProfile);
        usernameTextView = view.findViewById(R.id.usernameProfile);
        heightTextView = view.findViewById(R.id.heightProfile);

        /*Button buttonOverflow = view.findViewById(R.id.button_overflow);
        buttonOverflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), buttonOverflow);
                popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_1:
                                // Acción para el primer item del menú
                                Intent intent = new Intent(getContext(), ModifyProfileActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.menu_item_2:
                                // Acción para el segundo item del menú
                                ConfirmLogoutDialog confirmLogoutDialog = new ConfirmLogoutDialog(new ConfirmLogoutDialog.ConfirmLogoutDialogListener() {
                                    @Override
                                    public void onConfirmLogout() {
                                        //confirmLogout();
                                    }
                                });
                                confirmLogoutDialog.show(getParentFragmentManager(), "confirmLogoutDialog");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });*/

        /*
        Button buttonModifyWeighHeight = view.findViewById(R.id.modifyWeightHeight);
        buttonModifyWeighHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir la actividad para modificar peso y altura
                Intent intent = new Intent(getContext(), ModifyWeightHeightActivity.class);
                startActivity(intent);
            }
        });*/

        Gson gson = new Gson();
        String contenidoJSON;
        try {
            InputStreamReader archivo = new InputStreamReader(getActivity().openFileInput("usuario.json"));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            contenidoJSON = "";
            while (linea != null) {
                contenidoJSON = contenidoJSON + linea + "\n";
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        User user = gson.fromJson(contenidoJSON, User.class);

        String email = user.getEmail();

        StringBuilder stringBuilder = new StringBuilder();

        if (hasConsecutivePercent40(email)){
            int length = email.length();
            boolean previousIsPercent40 = false;
            for (int j = 0; j < length; j++) {
                char currentChar = email.charAt(j);
                if (currentChar == '%' && j + 2 < length && email.charAt(j + 1) == '4' && email.charAt(j + 2) == '0') {
                    if (!previousIsPercent40) {
                        stringBuilder.append("@");
                        previousIsPercent40 = true;
                    }
                    j += 2; // Salta los siguientes dos caracteres
                } else {
                    stringBuilder.append(currentChar);
                    previousIsPercent40 = false;
                }
            }
            email = stringBuilder.toString();
        }

        emailTextView.setText(email);
        //TODO: passwordTextView.setText();
        weightTextView.setText(String.valueOf((float) user.getWeight()));
        usernameTextView.setText(user.getName());
        heightTextView.setText(String.valueOf((float) user.getHeight()));

        return view;
    }

    public static boolean hasConsecutivePercent40(String input) {
        int length = input.length();

        for (int i = 0; i < length - 2; i++) {
            if (input.charAt(i) == '%' && input.charAt(i + 1) == '4' && input.charAt(i + 2) == '0') {
                return true;
            }
        }

        return false;
    }

}