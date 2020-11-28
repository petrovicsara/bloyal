package com.example.bloyal;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

public class HomeActivity extends AppCompatActivity {

    ImageView ivAccount, ivBarcode;
    ListView lvArticles;

    String mTitles[] = {"Zara sezonsko snizenje", "Akcija sakupljanja limenki", "Dupli poeni u Idea prodavnicama"};
    String mArticles[] = {"U svim Zara radnjama pocevsi od 10.10.2020 svaki korisnik moze ostvariti popust od 15 posto na staru kolekciju.",
                        "Opstina Vracar za mesec novembar organizuje humanitarnu akciju 'Ocistimo Vracar'",
                        "Uskoro! U svim prodajnim mestima, Idea Vam daje duplo poena za istu cenu!"};
    int mImages[] = {R.drawable.zaradisc, R.drawable.candisc, R.drawable.ideadisc};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ivAccount = findViewById(R.id.ivAccount);
        ivBarcode = findViewById(R.id.ivBarcode);
        lvArticles = findViewById(R.id.lvArticles);

        Intent intent = getIntent();
        final String email = intent.getStringExtra("email");

        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            Writer codeWriter;
            codeWriter = new Code128Writer();
            BitMatrix byteMatrix = codeWriter.encode(email, BarcodeFormat.CODE_128,500, 200, hintMap);
            int width = byteMatrix.getWidth();
            int height = byteMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, byteMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            ivBarcode.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        ivAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        MyAddapter adapter = new MyAddapter(this, mTitles, mArticles, mImages);
        lvArticles.setAdapter(adapter);
    }

    class MyAddapter extends ArrayAdapter<String> {
        Context context;
        String rTitles[];
        String rArticles[];
        int rImages[];

        MyAddapter (Context c, String title[], String article[], int image[]) {
            super (c, R.layout.article_row, R.id.tvTitle, title);
            this.context = context;
            this.rTitles = title;
            this.rArticles = article;
            this.rImages = image;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.article_row, parent, false);
            ImageView images = row.findViewById(R.id.ivArticlePicture);
            TextView myTitle = row.findViewById(R.id.tvTitle);
            TextView myArticle = row.findViewById(R.id.tvArticle);

            images.setImageResource(rImages[position]);
            myTitle.setText(rTitles[position]);
            myArticle.setText(rArticles[position]);


            return row;
        }
    }
}
