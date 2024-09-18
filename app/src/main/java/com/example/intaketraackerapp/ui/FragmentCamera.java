package com.example.intaketraackerapp.ui;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.intaketraackerapp.HomePageActivity;
import com.example.intaketraackerapp.IntakeClasses.JsonFunctions;
import com.example.intaketraackerapp.IntakeClasses.User;
import com.example.intaketraackerapp.IntakeClasses.Utils;
import com.example.intaketraackerapp.R;
import com.example.intaketraackerapp.ml.Model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCamera#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCamera extends Fragment {

    FragmentFoodSearched fragmentFoodSearched = new FragmentFoodSearched();
    Button takePictureButton;
    Button takeFromFolder;
    Button pictureHistory;

    String rutaImagen;
    Uri uriImagen;
    String bitMapString;
    User user;

    FragmentPicturesHistory fragmentPicturesHistory = new FragmentPicturesHistory();
    private ConstraintLayout cameraContainer;
    private FrameLayout chargingIcon;
    public FragmentCamera() {
        // Required empty public constructor
    }


    public static FragmentCamera newInstance(String param1, String param2) {
        FragmentCamera fragment = new FragmentCamera();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        takePictureButton = (Button) view.findViewById(R.id.buttonTakePicture);
        pictureHistory = (Button) view.findViewById(R.id.buttonPicturesHistory);
        cameraContainer = view.findViewById(R.id.camera);
        chargingIcon = view.findViewById(R.id.loadingContainer);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        pictureHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("rutaimagen", bitMapString);
                fragmentPicturesHistory.setArguments(bundle);
                loadFragment(fragmentPicturesHistory);
            }
        });

        return view;
    }


    private void openCamera(){
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //intent.setType("image/*");
            /*File imagenArchivo = crearImagen();

            Activity activity = getActivity();

            Uri fotoUri = FileProvider.getUriForFile(activity, "com.example.intaketraackerapp.fileprovider", imagenArchivo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            uriImagen = fotoUri;

            */
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, 1);
            }


            //startActivityForResult(intent, 1);
        } catch(Exception e) {
            Toast.makeText(getContext(), "Error opening camera, try restarting the app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (data != null && data.getExtras() != null) {

                //GET THE IMAGE
                Bitmap imgBitmap = (Bitmap) data.getExtras().get("data");
                if (imgBitmap != null) {

                    try {
                        String imagePath = saveImage(imgBitmap);

                        //SAVE PHOTO TO USER
                        JsonFunctions jsonFunctions = new JsonFunctions();
                        user = jsonFunctions.readJson(getActivity());
                        user.getPhotosTakenPath().add(imagePath);
                        jsonFunctions.writeJson(user, getActivity());

                    } catch (IOException e) {
                        Utils.showToast("Cold not save image to loacl memory: " + e.getMessage(), getActivity());
                    }

                    try {
                        Context context = getContext();
                        if (context != null) {
                            showLoadingIcon();

                            //PREDICT LABEL FROM IMAGE
                            String predictedLabel = classifyPhoto(imgBitmap, context);
                            if (predictedLabel != null) {

                                String[] res = {""};
                                final boolean[] searched = {false};
                                String url = Utils.serverUri + "/searchFoodInformation?query=" + predictedLabel;
                                OkHttpClient client = new OkHttpClient.Builder()
                                        .connectTimeout(2, TimeUnit.SECONDS)
                                        .writeTimeout(5, TimeUnit.SECONDS)
                                        .readTimeout(5, TimeUnit.SECONDS)
                                        .build();

                                Request request = new Request.Builder()
                                        .url(url)
                                        .build();

                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Utils.showToast("Could not connect with the server: " + e.getMessage(), getActivity());
                                        hideLoadingIcon();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        try {
                                            if (response.isSuccessful()) {
                                                res[0] = response.body().string();

                                                String result = res[0].trim();
                                                if (!Objects.equals(result, "Not Found")) {

                                                    String url = Utils.serverUri + "/addScannedFood";

                                                    OkHttpClient client = new OkHttpClient.Builder()
                                                            .connectTimeout(2, TimeUnit.SECONDS)
                                                            .writeTimeout(5, TimeUnit.SECONDS)
                                                            .readTimeout(5, TimeUnit.SECONDS)
                                                            .build();

                                                    RequestBody requestBody = new FormBody.Builder()
                                                            .add("param1", rutaImagen)
                                                            .add("param2", user.getId())
                                                            .build();

                                                    Request request = new Request.Builder()
                                                            .url(url)
                                                            .post(requestBody)
                                                            .build();

                                                    client.newCall(request).enqueue(new Callback() {
                                                        @Override
                                                        public void onFailure(Call call, IOException e) {
                                                            Utils.showToast("Can't insert image: " + e.getMessage(), getActivity());
                                                            hideLoadingIcon();
                                                        }

                                                        @Override
                                                        public void onResponse(Call call, Response response) throws IOException {
                                                            try {
                                                                if (response.isSuccessful()) {
                                                                    Type listType = new TypeToken<ArrayList<String>>() {
                                                                    }.getType();
                                                                    Gson gsonfood = new Gson();
                                                                    ArrayList<String> foodSearched = gsonfood.fromJson(res[0], listType);

                                                                    Bundle bundle = new Bundle();

                                                                    bundle.putString("foodClickedName", predictedLabel);
                                                                    bundle.putFloat("foodClickedPro", Float.parseFloat(foodSearched.get(0)));
                                                                    bundle.putFloat("foodClickedFats", Float.parseFloat(foodSearched.get(1)));
                                                                    bundle.putFloat("foodClickedCal", Float.parseFloat(foodSearched.get(3)));
                                                                    bundle.putFloat("foodClickedCarbs", Float.parseFloat(foodSearched.get(2)));
                                                                    fragmentFoodSearched.setArguments(bundle);
                                                                    loadFragment(fragmentFoodSearched);
                                                                }
                                                            } finally {
                                                                response.close();
                                                                hideLoadingIcon();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Utils.showToast("Please enter a correct food name", getActivity());
                                                    hideLoadingIcon();
                                                }
                                            } else {
                                                hideLoadingIcon();
                                            }
                                        } catch (Exception e) {
                                            hideLoadingIcon();
                                        } finally {
                                            response.close();
                                        }
                                    }
                                });
                            } else {
                                hideLoadingIcon();
                            }

                        } else {
                            Toast.makeText(getContext(), "Error getting the context", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error preparing the photo for prediction", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String saveImage(Bitmap bmp) throws IOException {

        String nombreImagen = "foto_";
        File directorio = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(nombreImagen, ".jpg", directorio);
        rutaImagen = imagen.getAbsolutePath();

        try (FileOutputStream out = new FileOutputStream(imagen)) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            Utils.showToast("Cannot save photo to local memory: " + e.getMessage(), getActivity());
        }

        return imagen.getAbsolutePath();
    }

    public void loadFragment(Fragment fragment){

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);

        transaction.commit();
    }

    public String classifyPhoto(Bitmap imgBitmap, Context context) {

        try {

            // Specify the bitmap format and scale the image to the desired dimensions.
            Bitmap btm = imgBitmap.copy(Bitmap.Config.ARGB_8888, true);
            btm = Bitmap.createScaledBitmap(btm, 224, 224, true);

            //Loads model
            Model model = Model.newInstance(context);

            // Loads bitmap into a Tensorflow buffer gets it ready to be used as input of the model
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            TensorImage tensorImage = TensorImage.fromBitmap(btm);
            TensorImage tensorImageFloat = TensorImage.createFrom(tensorImage, DataType.FLOAT32);
            inputFeature0.loadBuffer(tensorImageFloat.getBuffer());

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            String predictedLabel = getLabels(context).get(getMax(outputFeature0.getFloatArray()));

            // Releases model resources if no longer used.
            model.close();

            return predictedLabel;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error predicting label: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return null;
    }

    public int getMax(float[] arr) {
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > arr[max]) {
                max = i;
            }
        }
        return max;
    }

    public List<String> getLabels(Context context) throws IOException {
        String fileName = "labels.txt";
        List<String> result = new ArrayList<>();
        BufferedReader br = null;

        br = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));

        String line;
        while ((line = br.readLine()) != null) {
            result.add(line);
        }

        br.close();

        return result;
    }

    private void showLoadingIcon() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        chargingIcon.setVisibility(View.VISIBLE);
                        cameraContainer.setAlpha(0.5F);
                        chargingIcon.setAlpha(1F);
                    } catch (Exception e) {
                        Utils.showToast(e.getMessage(), getActivity());
                    }
                }
            });
        } catch (Exception e2) {
            Utils.showToast(e2.getMessage(), getActivity());
        }
    }

    private void hideLoadingIcon() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        cameraContainer.setAlpha(1F);
                        chargingIcon.setVisibility(View.GONE);
                    } catch (Exception e) {
                        Utils.showToast(e.getMessage(), getActivity());
                    }
                }
            });
        } catch (Exception e2) {
            Utils.showToast(e2.getMessage(), getActivity());
        }
    }
}