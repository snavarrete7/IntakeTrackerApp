package com.example.intaketraackerapp.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.intaketraackerapp.R;
import com.example.intaketraackerapp.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSelectFromFolder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSelectFromFolder extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentSelectFromFolder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSelectFromFolder.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSelectFromFolder newInstance(String param1, String param2) {
        FragmentSelectFromFolder fragment = new FragmentSelectFromFolder();
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
        return inflater.inflate(R.layout.fragment_select_from_folder, container, false);
    }

    public String classifyPhoto(Bitmap imgBitmap, Context context) {

        try {

            //Load image from disk
            //Bitmap imgBitmap = BitmapFactory.decodeFile(rutaImagen);
            imgBitmap = Bitmap.createScaledBitmap(imgBitmap, 224, 224, true);

            Model model = Model.newInstance(context);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            TensorImage tensorImage = TensorImage.fromBitmap(imgBitmap);
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
            Toast.makeText(getContext(), "Error predicting label", Toast.LENGTH_LONG).show();
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

}