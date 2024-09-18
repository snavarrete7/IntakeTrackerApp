package com.example.intaketraackerapp.ui;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.intaketraackerapp.HomePageActivity;
import com.example.intaketraackerapp.IntakeClasses.JsonFunctions;
import com.example.intaketraackerapp.IntakeClasses.RecyclerViewAdapter;
import com.example.intaketraackerapp.IntakeClasses.User;
import com.example.intaketraackerapp.IntakeClasses.Utils;
import com.example.intaketraackerapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPicturesHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPicturesHistory extends Fragment {

    ImageView imagen;
    private String rutaImagen;

    private RecyclerView recyclerView;
    private ArrayList<Bitmap> bitmapArrayList;
    private RecyclerViewAdapter recyclerViewAdapter;

    public FragmentPicturesHistory() {
        // Required empty public constructor
    }


    public static FragmentPicturesHistory newInstance(String param1, String param2) {
        FragmentPicturesHistory fragment = new FragmentPicturesHistory();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           this.rutaImagen = (String) this.getArguments().getString("rutaimagen");
           String string = "";
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_pictures_history, container, false);

        //imagen = (ImageView) view.findViewById(R.id.imagenHistory);
        recyclerView = (RecyclerView) view.findViewById(R.id.listPhotosTaken);

        //LEER JSON
        /*
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

        User user = gson.fromJson(contenidoJSON, User.class);*/

        JsonFunctions jsonFunctions = new JsonFunctions();
        User user = jsonFunctions.readJson(getActivity());
        String useridCorrect = user.getId().replaceAll("\r", "").replaceAll("\n", "");
        //PATH TO BITMAP
        //Bitmap imgBitmap = BitmapFactory.decodeFile(user.getPhotosTakenPath().get(0));
        String[] resScannedConsumed = {""};
        final boolean[] petition = {false};
        String url2 = Utils.serverUri + "/getScannedFood?user_id=" + useridCorrect;
        OkHttpClient client2 = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();

        Request request2 = new Request.Builder()
                .url(url2)
                .build();

        client2.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        resScannedConsumed[0] = response.body().string();
                        petition[0] = true;
                        bitmapArrayList = new ArrayList<>();
                        Type listType2 = new TypeToken<ArrayList<ArrayList<String>>>() {
                        }.getType();
                        Gson gson = new Gson();
                        ArrayList<ArrayList<String>> pathArrayList = gson.fromJson(resScannedConsumed[0], listType2);
                        for (int i = 0; i < pathArrayList.size(); i++) {

                            String imagePath = pathArrayList.get(i).get(1).toString();
                            StringBuilder stringBuilder = new StringBuilder();

                            if (hasConsecutivePercent2F(imagePath)) {
                                int length = imagePath.length();
                                boolean previousIsPercent2F = false;
                                for (int j = 0; j < length; j++) {
                                    char currentChar = imagePath.charAt(j);
                                    if (currentChar == '%' && j + 2 < length && imagePath.charAt(j + 1) == '2' && imagePath.charAt(j + 2) == 'F') {
                                        if (!previousIsPercent2F) {
                                            stringBuilder.append("/");
                                            previousIsPercent2F = true;
                                        }
                                        j += 2; // Salta los siguientes dos caracteres
                                    } else {
                                        stringBuilder.append(currentChar);
                                        previousIsPercent2F = false;
                                    }
                                }
                                imagePath = stringBuilder.toString();
                            }
                            Bitmap imgBitmap = BitmapFactory.decodeFile(imagePath);
                            bitmapArrayList.add(imgBitmap);
                        }
                        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(),bitmapArrayList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(recyclerViewAdapter);
                    }


                        /*
                        for(int j=0; j< user.getPhotosTakenPath().size();j++){
                            Bitmap imgBitmap = BitmapFactory.decodeFile(user.getPhotosTakenPath().get(j));
                            bitmapArrayList.add(imgBitmap);
                        }
                        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(),bitmapArrayList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(recyclerViewAdapter);
                    }*/
                } finally {
                    response.close();
                }
            }
        });
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //STRING TO BITMAP
        //Bitmap imgBitmap = StringToBitMap(rutaImagen);

        //imagen.setImageBitmap(imgBitmap);

        return view;
    }

    public static boolean hasConsecutivePercent2F(String input) {
        int length = input.length();

        for (int i = 0; i < length - 2; i++) {
            if (input.charAt(i) == '%' && input.charAt(i + 1) == '2' && input.charAt(i + 2) == 'F') {
                return true;
            }
        }

        return false;
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}