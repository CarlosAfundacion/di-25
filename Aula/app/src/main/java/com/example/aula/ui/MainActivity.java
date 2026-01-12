package com.example.aula.ui;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.aula.R;
import com.example.aula.data.InMemoryNoticeRepository;
import com.example.aula.viewmodel.NoticeViewModel;
import com.example.aula.viewmodel.NoticeViewModelFactory;

public class MainActivity extends AppCompatActivity {

    private NoticeViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText etTitle = findViewById(R.id.etTitle);
        EditText etSubject = findViewById(R.id.etSubject);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnDeleteLast = findViewById(R.id.btnDeleteLast);
        TextView tvList = findViewById(R.id.tvList);

        InMemoryNoticeRepository repo = new InMemoryNoticeRepository();
        NoticeViewModelFactory factory = new NoticeViewModelFactory(repo);
        vm = new ViewModelProvider(this, factory).get(NoticeViewModel.class);

        vm.getListado().observe(this, tvList::setText);

        vm.getError().observe(this, err -> {
            if (err != null) Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
        });

        vm.getEventoToast().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                vm.consumirEventoToast(); // evento one-shot (no se repite al rotar)
            }
        });

        btnAdd.setOnClickListener(v -> {
            vm.addNotice(etTitle.getText().toString(), etSubject.getText().toString());
            etTitle.setText("");
        });

        btnDeleteLast.setOnClickListener(v -> vm.deleteLast());
    }
}
