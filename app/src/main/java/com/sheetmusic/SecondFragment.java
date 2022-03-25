package com.sheetmusic;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.Toast;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.sheetmusic.databinding.FragmentSecondBinding;




public class SecondFragment extends Fragment implements View.OnClickListener {
    private FragmentSecondBinding binding;
    private final String TAG = "Log: ";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        ((MainActivity) getActivity()).hideFab();

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting filename from args
        String caller = getArguments().getString("file");
        Log.d("Caller: ", caller);
        MusicXML musicXML = null;
        try {
            musicXML = new MusicXML(this.getContext(), this.getActivity(), caller);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Button treble_1 = getView().findViewById(R.id.treble_1);
        treble_1.setOnClickListener(this);

        Button bass_1 = getView().findViewById(R.id.bass_1);
        bass_1.setOnClickListener(this);

        Button time_1 = getView().findViewById(R.id.time_1);
        time_1.setOnClickListener(this);

        Button time_2 = getView().findViewById(R.id.time_2);
        time_2.setOnClickListener(this);

        Button eighth_rest_1 = getView().findViewById(R.id.eighth_rest_1);
        eighth_rest_1.setOnClickListener(this);

        Button eighth_note_1 = getView().findViewById(R.id.eighth_note_1);
        eighth_note_1.setOnClickListener(this);
        Button eighth_note_2 = getView().findViewById(R.id.eighth_note_2);
        eighth_note_2.setOnClickListener(this);
        Button eighth_note_3 = getView().findViewById(R.id.eighth_note_3);
        eighth_note_3.setOnClickListener(this);
        Button eighth_note_4 = getView().findViewById(R.id.eighth_note_4);
        eighth_note_4.setOnClickListener(this);
        Button eighth_note_5 = getView().findViewById(R.id.eighth_note_5);
        eighth_note_5.setOnClickListener(this);

        Button half_dot_1 = getView().findViewById(R.id.half_dot_1);
        half_dot_1.setOnClickListener(this);
        Button half_dot_2 = getView().findViewById(R.id.half_dot_2);
        half_dot_2.setOnClickListener(this);

        Button start_repeat = getView().findViewById(R.id.start_repeat);
        start_repeat.setOnClickListener(this);

//        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        switch(v.getId()) {
            case R.id.treble_1:
                Toast.makeText(getActivity().getBaseContext(), "Treble Clef", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bass_1:
                Toast.makeText(getActivity().getBaseContext(), "Bass Clef", Toast.LENGTH_SHORT).show();
                break;
            case R.id.time_1:
            case R.id.time_2:
                Toast.makeText(getActivity().getBaseContext(), "Time Signature \n" +
                        "12 beats\n" +
                        "8th note gets the beat", Toast.LENGTH_LONG).show();
                break;
            case R.id.eighth_rest_1:
                Toast.makeText(getActivity().getBaseContext(), "Eighth Rest\nbeat(s): 1", Toast.LENGTH_LONG).show();
                break;
            case R.id.eighth_note_1:
            case R.id.eighth_note_2:
            case R.id.eighth_note_4:
                Toast.makeText(getActivity().getBaseContext(), "Eighth Note\nNote: F\nbeat(s): 1", Toast.LENGTH_LONG).show();
                break;
            case R.id.eighth_note_3:
                Toast.makeText(getActivity().getBaseContext(), "Eighth Note\nNote: B-flat\nbeat(s): 1", Toast.LENGTH_LONG).show();
                break;
            case R.id.eighth_note_5:
                Toast.makeText(getActivity().getBaseContext(), "Eighth Note\nNote: E-flat\nbeat(s): 1", Toast.LENGTH_LONG).show();
                break;
            case R.id.half_dot_1:
            case R.id.half_dot_2:
                Toast.makeText(getActivity().getBaseContext(), "Dotted Half Note\nNote: E-flat\nbeat(s): 6", Toast.LENGTH_LONG).show();
                break;
            case R.id.start_repeat:
                Toast.makeText(getActivity().getBaseContext(), "Start of Repeat Section", Toast.LENGTH_LONG).show();
                break;
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).showFab();
        binding = null;
    }
}