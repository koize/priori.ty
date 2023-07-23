package com.koize.priority.ui.journal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.koize.priority.databinding.FragmentJournalBinding;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koize.priority.R;


public class JournalFragment extends Fragment {

    private FragmentJournalBinding binding;

    private FloatingActionButton addJournalButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        JournalViewModel journalViewModel =
                new ViewModelProvider(this).get(JournalViewModel.class);

        binding = FragmentJournalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        addJournalButton = root.findViewById(R.id.button_journal_add);
        addJournalButton.setOnClickListener(addJournalListener);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    View.OnClickListener addJournalListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JournalPopUp journalPopUp = new JournalPopUp();
            journalPopUp.showPopupWindow(v);        }
    };
}